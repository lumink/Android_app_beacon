/**
 * The MIT License (MIT)
 *
 * Copyright (c) 2014-2015 Perples, Inc.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package choojeok.song2app.SetBeacon_Activity;


import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.perples.recosdk.RECOBeacon;
import com.perples.recosdk.RECOBeaconManager;
import com.perples.recosdk.RECOBeaconRegion;
import com.perples.recosdk.RECOBeaconRegionState;
import com.perples.recosdk.RECOErrorCode;
import com.perples.recosdk.RECOMonitoringListener;
import com.perples.recosdk.RECORangingListener;
import com.perples.recosdk.RECOServiceConnectListener;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

import choojeok.song2app.Login_Activity.MainActivity;
import choojeok.song2app.Login_Activity.SessionManager;
import choojeok.song2app.Login_Activity.Popup;

import android.os.Handler;



/**
 * RECOBackgroundRangingService is to monitor regions and range regions when the device is inside in the BACKGROUND.
 *
 * RECOBackgroundMonitoringService는 백그라운드에서 monitoring을 수행하며, 특정 region 내부로 진입한 경우 백그라운드 상태에서 ranging을 수행합니다.
 */
public class RecoBackgroundRangingService extends Service implements RECORangingListener, RECOMonitoringListener, RECOServiceConnectListener {
    /**
     * We recommend 1 second for scanning, 10 seconds interval between scanning, and 60 seconds for region expiration time.
     * 1초 스캔, 10초 간격으로 스캔, 60초의 region expiration time은 당사 권장사항입니다.
     */
    private long mScanDuration = 1*1000L;
    private long mSleepDuration = 10*1000L;
    private long mRegionExpirationTime = 60*1000L;

    public static int minor = 0;

    private RECOBeaconManager mRecoManager;
    private ArrayList<RECOBeaconRegion> mRegions;
    private ArrayList<RECOBeacon> mRangedBeacons;

    private Handler mHandler;
    private Runnable mRunnable;

    Intent popup = null;

    String message;
    String loginedID;

    SessionManager session;





    @Override
    public void onCreate() {
        Log.i("BackRangingService", "onCreate()");
        super.onCreate();

        session = new SessionManager(getApplicationContext());
        loginedID = session.getUserDetails().get(SessionManager.KEY_USERNAME).toString();


        message = loginedID+"님이 학교에 왔습니다";

        mRunnable = new Runnable() {
            @Override
            public void run() {
                startMonitoring();
            }
        };

        mHandler = new Handler();
        mHandler.postDelayed(mRunnable, 75000);

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i("BackRangingService", "onStartCommand");
        /**
         * Create an instance of RECOBeaconManager (to set scanning target and ranging timeout in the background.)
         * If you want to scan only RECO, and do not set ranging timeout in the backgournd, create an instance:
         * 		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), true, false);
         * WARNING: False enableRangingTimeout will affect the battery consumption.
         *
         * RECOBeaconManager 인스턴스틀 생성합니다. (스캔 대상 및 백그라운드 ranging timeout 설정)
         * RECO만을 스캔하고, 백그라운드 ranging timeout을 설정하고 싶지 않으시다면, 다음과 같이 생성하시기 바랍니다.
         * 		mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(), true, false);
         * 주의: enableRangingTimeout을 false로 설정 시, 배터리 소모량이 증가합니다.
         */

        mRecoManager = RECOBeaconManager.getInstance(getApplicationContext(),SetBeaconActivity.SCAN_RECO_ONLY, SetBeaconActivity.ENABLE_BACKGROUND_RANGING_TIMEOUT);
        this.bindRECOService();
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        Log.i("BackRangingService", "onDestroy()");

        this.tearDown();
        super.onDestroy();
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        Log.i("BackRangingService", "onTaskRemoved()");
        super.onTaskRemoved(rootIntent);
    }

    private void bindRECOService() {
        Log.i("BackRangingService", "bindRECOService()");

        mRegions = new ArrayList<RECOBeaconRegion>();
        this.generateBeaconRegion();

        mRecoManager.setMonitoringListener(this);
        mRecoManager.setRangingListener(this);
        mRecoManager.bind(this);
    }

    private void generateBeaconRegion() {
        Log.i("BackRangingService", "generateBeaconRegion()");

        RECOBeaconRegion recoRegion;

        recoRegion = new RECOBeaconRegion(SetBeaconActivity.RECO_UUID, "RECO Sample Region");
        recoRegion.setRegionExpirationTimeMillis(this.mRegionExpirationTime);
        mRegions.add(recoRegion);
    }

    private void startMonitoring() {
        Log.i("BackRangingService", "startMonitoring()");

        mRecoManager.setScanPeriod(this.mScanDuration);
        mRecoManager.setSleepPeriod(this.mSleepDuration);

        for(RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.startMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.e("BackRangingService", "RemoteException has occured while executing RECOManager.startMonitoringForRegion()");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e("BackRangingService", "NullPointerException has occured while executing RECOManager.startMonitoringForRegion()");
                e.printStackTrace();
            }
        }
    }

    private void stopMonitoring() {
        Log.i("BackRangingService", "stopMonitoring()");

        for(RECOBeaconRegion region : mRegions) {
            try {
                mRecoManager.stopMonitoringForRegion(region);
            } catch (RemoteException e) {
                Log.e("BackRangingService", "RemoteException has occured while executing RECOManager.stopMonitoringForRegion()");
                e.printStackTrace();
            } catch (NullPointerException e) {
                Log.e("BackRangingService", "NullPointerException has occured while executing RECOManager.stopMonitoringForRegion()");
                e.printStackTrace();
            }
        }
    }

    private void startRangingWithRegion(RECOBeaconRegion region) {
        Log.i("BackRangingService", "startRangingWithRegion()");

        /**
         * There is a known android bug that some android devices scan BLE devices only once. (link: http://code.google.com/p/android/issues/detail?id=65863)
         * To resolve the bug in our SDK, you can use setDiscontinuousScan() method of the RECOBeaconManager.
         * This method is to set whether the device scans BLE devices continuously or discontinuously.
         * The default is set as FALSE. Please set TRUE only for specific devices.
         *
         * mRecoManager.setDiscontinuousScan(true);
         */

        try {
            mRecoManager.startRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e("BackRangingService", "RemoteException has occured while executing RECOManager.startRangingBeaconsInRegion()");
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e("BackRangingService", "NullPointerException has occured while executing RECOManager.startRangingBeaconsInRegion()");
            e.printStackTrace();
        }
    }

    private void stopRangingWithRegion(RECOBeaconRegion region) {
        Log.i("BackRangingService", "stopRangingWithRegion()");

        try {
            mRecoManager.stopRangingBeaconsInRegion(region);
        } catch (RemoteException e) {
            Log.e("BackRangingService", "RemoteException has occured while executing RECOManager.stopRangingBeaconsInRegion()");
            e.printStackTrace();
        } catch (NullPointerException e) {
            Log.e("BackRangingService", "NullPointerException has occured while executing RECOManager.stopRangingBeaconsInRegion()");
            e.printStackTrace();
        }
    }

    private void tearDown() {
        Log.i("BackRangingService", "tearDown()");
        this.stopMonitoring();

        try {
            mRecoManager.unbind();
        } catch (RemoteException e) {
            Log.e("BackRangingService", "RemoteException has occured while executing unbind()");
            e.printStackTrace();
        }
    }

    @Override
    public void onServiceConnect() {
        Log.i("BackRangingService", "onServiceConnect()");
        this.startMonitoring();
        //Write the code when RECOBeaconManager is bound to RECOBeaconService
    }

    @Override
    public void didDetermineStateForRegion(RECOBeaconRegionState state, RECOBeaconRegion region) {
        Log.i("BackRangingService", "didDetermineStateForRegion()");
        //Write the code when the state of the monitored region is changed




    }

    @Override
    public void didEnterRegion(RECOBeaconRegion region, Collection<RECOBeacon> beacons) {
        /**
         * For the first run, this callback method will not be called.
         * Please check the state of the region using didDetermineStateForRegion() callback method.
         *
         * 최초 실행시, 이 콜백 메소드는 호출되지 않습니다.
         * didDetermineStateForRegion() 콜백 메소드를 통해 region 상태를 확인할 수 있습니다.
         */

        //Get the region and found beacon list in the entered region
        Log.i("BackRangingService", "didEnterRegion() - " + region.getUniqueIdentifier());
        this.popupNotification("Inside of " + region.getUniqueIdentifier());
        //Write the code when the device is enter the region

        this.startRangingWithRegion(region); //start ranging to get beacons inside of the region
        //from now, stop ranging after 10 seconds if the device is not exited


    }

    @Override
    public void didExitRegion(RECOBeaconRegion region) {
        /**
         * For the first run, this callback method will not be called.
         * Please check the state of the region using didDetermineStateForRegion() callback method.
         *
         * 최초 실행시, 이 콜백 메소드는 호출되지 않습니다.
         * didDetermineStateForRegion() 콜백 메소드를 통해 region 상태를 확인할 수 있습니다.
         */

        Log.i("BackRangingService", "didExitRegion() - " + region.getUniqueIdentifier());
        //this.popupNotification("Outside of " + region.getUniqueIdentifier());
        //Write the code when the device is exit the region

        this.stopRangingWithRegion(region); //stop ranging because the device is outside of the region from now
    }

    @Override
    public void didStartMonitoringForRegion(RECOBeaconRegion region) {
        Log.i("BackRangingService", "didStartMonitoringForRegion() - " + region.getUniqueIdentifier());
        //Write the code when starting monitoring the region is started successfully
        this.startRangingWithRegion(region);
    }

    @Override
    public void didRangeBeaconsInRegion(Collection<RECOBeacon> beacons, RECOBeaconRegion region) {
        Log.i("BackRangingService", "didRangeBeaconsInRegion() - " + region.getUniqueIdentifier() + " with " + beacons.size() + " beacons");
        //Write the code when the beacons inside of the region is received

        updateAllBeacons(beacons);


        for(int j=0; j<mRangedBeacons.size(); j++){
            minor = mRangedBeacons.get(j).getMinor();
        }

        mRangedBeacons.clear();

        beacontoDatabase(Integer.toString(minor));
        popup = new Intent(this, Popup.class);
        popup.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        if(MainActivity.firstState == 0 & minor !=0) {
            informFriends();
            startActivity(popup);
            MainActivity.firstState++;
        }


        this.stopRangingWithRegion(region);
        this.startRangingWithRegion(region);

    }

    private void popupNotification(String msg) {

    }


    @Override
    public IBinder onBind(Intent intent) {
        //This method is not used
        return null;
    }

    @Override
    public void onServiceFail(RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed.
        //See the RECOErrorCode in the documents.
        return;
    }

    @Override
    public void monitoringDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed to monitor the region.
        //See the RECOErrorCode in the documents.
        return;
    }

    @Override
    public void rangingBeaconsDidFailForRegion(RECOBeaconRegion region, RECOErrorCode errorCode) {
        //Write the code when the RECOBeaconService is failed to range beacons in the region.
        //See the RECOErrorCode in the documents.
        return;
    }

    public void updateAllBeacons(Collection<RECOBeacon> beacons) {
        synchronized (beacons) {
            mRangedBeacons = new ArrayList<RECOBeacon>(beacons);
        }

    }


    private void beacontoDatabase(String bid){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String bid = (String)params[0];

                    String link="http://203.252.195.99/updateBID.php";
                    String data  = URLEncoder.encode("bid", "UTF-8") + "=" + URLEncoder.encode(bid, "UTF-8");
                    data += "&" + URLEncoder.encode("loginedID", "UTF-8") + "=" + URLEncoder.encode(loginedID, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(bid, loginedID);
    }



    private void informFriends(){

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
            }

            @Override
            protected String doInBackground(String... params) {

                try{

                    String link="http://203.252.195.99/fcm/push_notification.php";
                    String data  = URLEncoder.encode("loginedID", "UTF-8") + "=" + URLEncoder.encode(loginedID, "UTF-8");
                    data += "&" + URLEncoder.encode("message", "UTF-8") + "=" + URLEncoder.encode(message, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write( data );
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    // Read Server Response
                    while((line = reader.readLine()) != null)
                    {
                        sb.append(line);
                        break;
                    }
                    return sb.toString();
                }
                catch(Exception e){
                    return new String("Exception: " + e.getMessage());
                }

            }
        }

        InsertData task = new InsertData();
        task.execute(loginedID, message);
    }


}