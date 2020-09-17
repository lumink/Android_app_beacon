package choojeok.song2app.SearchFriend_Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import android.widget.Toast;

import choojeok.song2app.R;


public class search extends Activity {

    TextView txtview;
    private EditText editTextSearchId;
    String str_loginedID;
    String str_searchID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_friend_page);

        Intent intent = getIntent();
        str_loginedID = intent.getExtras().getString("loginedID");

        editTextSearchId = (EditText) findViewById(R.id.id);
        txtview = (TextView) findViewById(R.id.txtView);

        View.OnClickListener listener = new View.OnClickListener(){

            @Override
            public void onClick(View v){
                switch(v.getId()){
                    case R.id.SearchBtn:
                        String friend_search = editTextSearchId.getText().toString();
                        insertToDatabase(friend_search);
                        break;

                    case R.id.AddFriendBtn:
                        beFriend(str_searchID, str_loginedID);
                        break;
                }
            }
        };

        Button btnSearch = (Button) findViewById(R.id.SearchBtn);
        Button btnAdd = (Button)findViewById(R.id.AddFriendBtn);
        btnSearch.setOnClickListener(listener);
        btnAdd.setOnClickListener(listener);

    }


    private void insertToDatabase(String friend_search) {

        class InsertData extends AsyncTask<String, Void, String> {

            @Override
            protected void onPreExecute() {
            }

            @Override
            protected void onPostExecute(String result) {
                txtview.setText(result);
            }

            @Override
            protected String doInBackground(String... params) {

                try {
                    String friend_search = (String) params[0];

                    String link = "http://203.252.195.99/search.php?ID=" + friend_search;
                    URL url = new URL(link);
                    HttpClient client = new DefaultHttpClient();
                    HttpGet request = new HttpGet();
                    request.setURI(new URI(link));
                    HttpResponse response = client.execute(request);
                    BufferedReader in = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

                    StringBuffer sb = new StringBuffer("");
                    String line = "";

                    while ((line = in.readLine()) != null) {
                        sb.append(line);
                        str_searchID = friend_search;
                        break;
                    }

                    in.close();
                    return sb.toString();

                } catch (Exception e) {
                    return new String("Exception: " + e.getMessage());

                }
            }
        }

        InsertData task = new InsertData();
        task.execute(friend_search);

    }


    public void beFriend(String friend_search, String friend_logined) {

        class InsertDatas extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(search.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params){

                try {
                    String friend_search = (String)params[0];
                    String friend_logined = (String)params[1];

                    String link="http://203.252.195.99/makefriend.php";
                    String data  = URLEncoder.encode("friend_search", "UTF-8") + "=" + URLEncoder.encode(friend_search, "UTF-8");
                    data += "&" + URLEncoder.encode("friend_logined", "UTF-8") + "=" + URLEncoder.encode(friend_logined, "UTF-8");

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

        InsertDatas task = new InsertDatas();
        task.execute(friend_search,friend_logined);
        Toast.makeText(this,"친구 추가 되었습니다.",Toast.LENGTH_SHORT).show();
    }
}
