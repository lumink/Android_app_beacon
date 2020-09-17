package choojeok.song2app.Login_Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

import choojeok.song2app.R;

public class Join extends Activity {

    private EditText editTextJoinId;
    private EditText editTextJoinPassword;
    private EditText editTextJoinNickname;
    Context cont;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_join_page);
        cont = this;

        editTextJoinId = (EditText) findViewById(R.id.joinId);
        editTextJoinPassword = (EditText) findViewById(R.id.joinPassword);
        editTextJoinNickname = (EditText) findViewById(R.id.joinNickname);
    }

    public void insert(View view){
        String joinId = editTextJoinId.getText().toString();
        String joinPassword = editTextJoinPassword.getText().toString();
        String joinNickname = editTextJoinNickname.getText().toString();

        if (joinId.equals("")) {
            Toast.makeText(cont, "아이디를 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (joinNickname.equals("")){
            Toast.makeText(cont, "닉네임을 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        else if (joinPassword.length() < 5 || joinPassword.length() > 12){
            Toast.makeText(cont, "비밀번호는 5 ~ 12자로 입력하세요", Toast.LENGTH_SHORT).show();
            return;
        }

        insertToDatabase(joinId, joinPassword, joinNickname);
        finish();
    }

    private void insertToDatabase(String joinId, String joinPassword, String joinNickname){

        class InsertData extends AsyncTask<String, Void, String>{
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Join.this, "Please Wait", null, true, true);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
            }

            @Override
            protected String doInBackground(String... params) {

                try{
                    String joinId = (String)params[0];
                    String joinPassword = (String)params[1];
                    String joinNickname = (String)params[2];

                    String link="http://203.252.195.99/join.php";
                    String data  = URLEncoder.encode("joinId", "UTF-8") + "=" + URLEncoder.encode(joinId, "UTF-8");
                    data += "&" + URLEncoder.encode("joinPassword", "UTF-8") + "=" + URLEncoder.encode(joinPassword, "UTF-8");
                    data += "&" + URLEncoder.encode("joinNickname", "UTF-8") + "=" + URLEncoder.encode(joinNickname, "UTF-8");

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
        task.execute(joinId,joinPassword,joinNickname);
    }
}