package choojeok.song2app.Timetable_Activity;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import choojeok.song2app.R;
import choojeok.song2app.SetBeacon_Activity.*;

public class TimetableSetting extends AppCompatActivity {

    boolean[] bool = new boolean[2];
    Context cont;
    public Switch sw;
    public String rangingBid = "1" ; // 현재 인식하고 있는 비콘 아이디 값 받아오는 변수

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        cont = this;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timetable_setting);

        sw = (Switch)findViewById(R.id.alarmSwitch);
        SharedPreferences sharedPrefs = getSharedPreferences("choojeok.song2app", MODE_PRIVATE);
        sw.setChecked(sharedPrefs.getBoolean("alarm", false));

        final Handler handler = new Handler();

        final DatabaseAccess databaseAccess2 = DatabaseAccess.getInstance(cont);
        final DatabaseWriteAccess databaseAccess3 = DatabaseWriteAccess.getInstance(cont);
        databaseAccess2.open();
        databaseAccess3.open();

        final List<String> className = databaseAccess2.getAlarmClass2();

        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getApplicationContext(), R.layout.timetable_alarmflagon_list);

        final ListView listView = (ListView) findViewById(R.id.listView2);
        for(int p = 0; p < className.size(); ++p)
            adapter.add(className.get(p)+"\n"+databaseAccess2.getClassNo(className.get(p)));
        if(adapter.isEmpty()) {
        }
        else
            listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(cont, AlertDialog.THEME_HOLO_LIGHT);
                builder.setTitle("등록된 알람입니다.");
                builder.setMessage("알람을 해제하겠습니까?");
                final int positionToRemove = position;
                builder.setPositiveButton("네", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        databaseAccess3.setLectureAlarmFlag(databaseAccess2.getClassid(className.get(position)), 0);
                        Toast.makeText(cont, "알람을 해제했습니다.", Toast.LENGTH_SHORT).show();
                        String temp = adapter.getItem(positionToRemove);
                        adapter.remove(temp);
                        adapter.notifyDataSetChanged();
                    }
                });
                builder.setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        sw.setOnCheckedChangeListener(new Switch.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                bool[0] = isChecked;

                Intent otherintent ;

                boolean sookmyungIsExist = getPackageList();

                if (sookmyungIsExist) {
                    otherintent = getPackageManager().getLaunchIntentForPackage("kr.ac.sookmyung.smartcampus");
                    otherintent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                }
                else {
                    String url = "market://details?id=" + "kr.ac.sookmyung.smartcampus";
                    otherintent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                }

                PendingIntent pend = PendingIntent.getActivity(cont, 0, otherintent, PendingIntent.FLAG_UPDATE_CURRENT);

                final NotificationCompat.Builder builder = new NotificationCompat.Builder(cont);
                builder.setTicker("출석 하세요!");
                builder.setWhen(System.currentTimeMillis());
                builder.setContentTitle("비콘을 이용하여 출석 하세요!");
                builder.setContentText("수업시간 5분 전 입니다.");
                builder.setAutoCancel(true);
                builder.setContentIntent(pend);
                builder.setSmallIcon(R.drawable.noti);
                //builder.build();

                if (isChecked) {

                    SharedPreferences.Editor editor = getSharedPreferences("choojeok.song2app", MODE_PRIVATE).edit();
                    editor.putBoolean("alarm", true);
                    editor.commit();

                    handler.post(new Runnable() {

                        @Override
                        public void run() {

                            handler.postDelayed(this, 1000);

                            rangingBid = Integer.toString(RecoBackgroundRangingService.minor);

                            NotificationManager nm = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

                            final DatabaseAccess databaseAccess = DatabaseAccess.getInstance(cont);
                            databaseAccess.open();

                            final int classTime[] = databaseAccess.getAlarmTime(); //디비의 수업 알람 시작 시간 받아오는 배열
                            final int[][] classTime2 = new int[classTime.length][1]; //시작시간의 길이

                            final int[] size = {databaseAccess.getAlarmNum()}; //디비의 알람 여부 개수
                            final String[] day = databaseAccess.getDay(); //디비의 수업 날짜

                            final String classPlace[] = databaseAccess.getClassBid();//알람이 켜져있는 bid값들

                            Calendar realcalender = Calendar.getInstance(); //달력 객체
                            final int time = realcalender.get(Calendar.HOUR_OF_DAY); //시간
                            final int min = realcalender.get(Calendar.MINUTE); //분
                            final int dayswitch = realcalender.get(Calendar.DAY_OF_WEEK); //요일

                            String alarmday = null; //알람이 울려야 하는 요일

                            switch (dayswitch) {
                                case 1:
                                    alarmday = "일"; //달력의 숫자가 1이면 일요일에 알람이 울려야
                                    break;
                                case 2:
                                    alarmday = "월";
                                    break;
                                case 3:
                                    alarmday = "화";
                                    break;
                                case 4:
                                    alarmday = "수";
                                    break;
                                case 5:
                                    alarmday = "목";
                                    break;
                                case 6:
                                    alarmday = "금";
                                    break;
                                case 7:
                                    alarmday = "토";
                                    break;
                            }

                            final String today = alarmday; //시스템이 인식한 오늘의 날짜

                            int alarmFlagIndex = size[0] - 1; //- 1; //디비의 알람 여부 size

                            while (alarmFlagIndex >= 0) { //0부터 반복
                                int classT = classTime[alarmFlagIndex];
                                int classM = classT % 100;
                                classT = classT / 100;

                                if (classM == 0)
                                    classM = 60;

                                int it = classT;
                                String st = String.valueOf(it);

                                int im = classM;
                                String sm = String.valueOf(im);

                                if (time == classT && min + 5 == classM && day[alarmFlagIndex].equals(today)) { //min +5
                                    if (classTime2[alarmFlagIndex][0] == 0) {
                                        if (classPlace[alarmFlagIndex].equals(rangingBid)) {
                                            nm.notify(1234567, builder.build());
                                            classTime2[alarmFlagIndex][0] = 1;
                                        }
                                    }
                                }
                                alarmFlagIndex--;
                            }
                        }
                    });
                }

                else {
                    SharedPreferences.Editor editor = getSharedPreferences("choojeok.song2app", MODE_PRIVATE).edit();
                    editor.putBoolean("alarm", false);
                    editor.commit();

                    handler.removeMessages(0);
                    Toast.makeText(cont, "자동 알람기능이 중단됩니다", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public boolean getPackageList() {

        boolean isExist = false;

        PackageManager pkgMgr = getPackageManager();
        List<ResolveInfo> mApps;
        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mApps = pkgMgr.queryIntentActivities(mainIntent, 0);

        try {
            for (int i = 0; i < mApps.size(); i++) {
                if(mApps.get(i).activityInfo.packageName.startsWith("kr.ac.sookmyung.smartcampus")){
                    isExist = true;
                    break;
                }
            }
        }
        catch (Exception e) {
            isExist = false;
        }
        return isExist;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putBooleanArray("save", bool);
        super.onSaveInstanceState(outState);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
