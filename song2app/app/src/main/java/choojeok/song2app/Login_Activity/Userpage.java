package choojeok.song2app.Login_Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.iid.FirebaseInstanceId;

import choojeok.song2app.R;
import choojeok.song2app.SearchFriend_Activity.search;
import choojeok.song2app.SearchFriend_Activity.view;
import choojeok.song2app.SetBeacon_Activity.SetBeaconActivity;
import choojeok.song2app.Timetable_Activity.TimeTableActivity;
import choojeok.song2app.Timetable_Activity.TimetableSetting;
import choojeok.song2app.todo_Activity.Ex11_CalendarActivity;

public class Userpage extends Activity {

    Intent todo = null;
    Intent friendSearch = null;
    Intent Setting = null;
    String str_loginedID=null;
    Intent ViewFriendlist = null;
    String token= null;
    Intent timetable = null;
    Intent logoutpopup = null;
    TextView presentIdView;

    private long lastTimeBackPressed;
    SessionManager session;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_page);

        session = new SessionManager(getApplicationContext());
        str_loginedID = session.getUserDetails().get(SessionManager.KEY_USERNAME).toString();
        token = FirebaseInstanceId.getInstance().getToken();

        final Button btnLogout = (Button) findViewById(R.id.BtnLogout);

        btnLogout.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                session.logoutUser();
                finish();
            }
        });

        presentIdView = (TextView)findViewById(R.id.present);
        presentIdView.setText(str_loginedID+" 님");
    }

    @Override
    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000){
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
    }

    public void onClicktodo(View arg) {
        switch (arg.getId()) {
            case R.id.todoBtn:
                todo= new Intent(Userpage.this, Ex11_CalendarActivity.class);
                startActivity(todo);
                break;
        }
    }

    public void onClickFriendSearch(View arg) {
        switch (arg.getId()) {
            case R.id.FriendSearchBtn:
                friendSearch = new Intent(Userpage.this, search.class);
                friendSearch.putExtra("loginedID", str_loginedID);
                startActivity(friendSearch);
                break;
        }
    }

    public void onClickSet(View arg) {
        switch (arg.getId()) {
            case R.id.SetBtn:
                Setting = new Intent(Userpage.this, SetBeaconActivity.class);
                startActivity(Setting);
                break;
        }
    }

    public void onClickViewFriendlist(View arg) {
        switch (arg.getId()) {
            case R.id.ViewFriendlist:
                ViewFriendlist = new Intent(Userpage.this, view.class);
                ViewFriendlist.putExtra("loginedID", str_loginedID.toString());
                startActivity(ViewFriendlist);
                break;
        }
    }

    public void onClickTimetable(View arg) {
        switch (arg.getId()) {
            case R.id.Btntimetable:
                timetable = new Intent(Userpage.this, TimeTableActivity.class);
                startActivity(timetable);
                break;
        }
    }
}