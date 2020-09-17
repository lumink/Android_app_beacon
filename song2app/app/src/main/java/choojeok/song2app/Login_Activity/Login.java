package choojeok.song2app.Login_Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.google.firebase.iid.FirebaseInstanceId;
import android.content.SharedPreferences;


import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;

import java.util.ArrayList;
import java.util.List;

import choojeok.song2app.R;


public class Login extends Activity {

    Button b;
    EditText et,pass;
    TextView tv;
    HttpPost httppost;
    HttpResponse response;
    HttpClient httpclient;
    List<NameValuePair> nameValuePairs;
    ProgressDialog dialog = null;
    Intent intents = null;

    EditText txtUsername;
    EditText txtPassword;
    SessionManager session;
    Context thisContext;

    private long lastTimeBackPressed;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_page);


        b = (Button)findViewById(R.id.button);
        et = (EditText)findViewById(R.id.id);
        pass= (EditText)findViewById(R.id.password);
        tv = (TextView)findViewById(R.id.result);


        b.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(Login.this, "",
                        "로그인 중...", true);
                new Thread(new Runnable() {
                    public void run() {
                        login();
                    }
                }).start();
            }
        });
    }

    public void onClickJoin(View arg) {
        switch (arg.getId()) {
            case R.id.JoinBtn:
                intents = new Intent(Login.this, Join.class);
                startActivity(intents);
                break;
        }
    }


    public void onBackPressed() {
        if(System.currentTimeMillis() - lastTimeBackPressed < 2000){
            finish();
            return;
        }
        lastTimeBackPressed = System.currentTimeMillis();
        Toast.makeText(this,"'뒤로' 버튼을 한 번 더 누르면 종료됩니다.",Toast.LENGTH_SHORT).show();
    }



    public static DefaultHttpClient getThreadSafeClient()  {

        DefaultHttpClient client = new DefaultHttpClient();
        ClientConnectionManager mgr = client.getConnectionManager();
        HttpParams params = client.getParams();
        client = new DefaultHttpClient(new ThreadSafeClientConnManager(params,

                mgr.getSchemeRegistry()), params);
        return client;
    }

    void login(){
        try{

            httpclient = getThreadSafeClient();


            httppost= new HttpPost("http://203.252.195.99/login.php"); // make sure the url is correct.
            //add your data
            nameValuePairs = new ArrayList<NameValuePair>(2);
            // Always use the same variable name for posting i.e the android side variable name and php side variable name should be similar,
            nameValuePairs.add(new BasicNameValuePair("id",et.getText().toString().trim()));
            // $Edittext_value = $_POST['Edittext_value'];
            nameValuePairs.add(new BasicNameValuePair("password",pass.getText().toString().trim()));
            httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            //Execute HTTP Post Request

            response = httpclient.execute(httppost);
            ResponseHandler<String> responseHandler = new BasicResponseHandler();

            final String response2= httpclient.execute(httppost, responseHandler);

            runOnUiThread(new Runnable() {
                public void run() {
                    tv.setText("Response from PHP : " + response2);
                    dialog.dismiss();
                }
            });

            if(response2.equals("No Such User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Login.this,"아이디 혹은 비밀번호를 다시 확인해주세요", Toast.LENGTH_SHORT).show();
                    }
                });

            }

            else if(response2.equals("User Found")){
                runOnUiThread(new Runnable() {
                    public void run() {

                    }
                });

                thisContext = this;
                txtUsername = (EditText)findViewById(R.id.id);
                txtPassword = (EditText)findViewById(R.id.password);

                session = new SessionManager(getApplicationContext());
                session.createLoginSession(et.getText().toString());


                FirebaseInstanceId.getInstance().deleteInstanceId();


                intents = new Intent(Login.this, Userpage.class);
                intents.putExtra("loginedID", txtUsername.getText().toString());

                finish();
                startActivity(intents);
            }

            else{
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(Login.this,"로그인을 다시 시도해 주세요", Toast.LENGTH_SHORT).show();
                    }
                });
            }

        }catch(Exception e){
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }




}