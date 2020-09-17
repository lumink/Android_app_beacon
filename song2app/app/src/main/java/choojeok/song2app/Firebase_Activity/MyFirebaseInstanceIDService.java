package choojeok.song2app.Firebase_Activity;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import java.io.IOException;


import choojeok.song2app.Login_Activity.SessionManager;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;



public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";


    SessionManager session;

    String sessionid;

    @Override
    public void onCreate(){

        session = new SessionManager(getApplicationContext());

    }

    @Override
    public void onTokenRefresh() {

        String token = FirebaseInstanceId.getInstance().getToken();
        Log.d(TAG, "Refreshed token: " + token);

        sessionid = session.getUserDetails().get(SessionManager.KEY_USERNAME).toString();
        sendRegistrationToServer(token, sessionid);

    }



    private void sendRegistrationToServer(String token, String sessionid) {
        // Add custom implementation, as needed.



        OkHttpClient client = new OkHttpClient();
        RequestBody body = new FormBody.Builder()
                .add("Token", token)
                .add("Sessionid", sessionid)
                .build();

        //request
        Request request = new Request.Builder()
                .url("http://203.252.195.99/fcm/register.php")
                .post(body)
                .build();

        try {
            client.newCall(request).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}