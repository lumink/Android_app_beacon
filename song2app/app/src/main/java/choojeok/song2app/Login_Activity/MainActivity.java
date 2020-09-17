package choojeok.song2app.Login_Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;

import choojeok.song2app.R;


public class MainActivity extends Activity {


    Intent intents;
    String loginedID;
    SessionManager session;

    public static int firstState = 0;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        session = new SessionManager(getApplicationContext());

        if(session.isLoggedIn()){

            intents = new Intent(MainActivity.this, Userpage.class);
            intents.putExtra("loginedID", loginedID);

            finish();
            startActivity(intents);
        }else {

            ImageButton icon1 = (ImageButton) findViewById(R.id.img_app_mark);
            icon1.setOnClickListener(new ImageButton.OnClickListener() {
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, Login.class);
                    finish();
                    startActivity(intent);
                }
            });
        }



    }
}

