package choojeok.song2app.SearchFriend_Activity;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import choojeok.song2app.R;

public class view extends Activity {

    String myJSON;
    String str_loginedID;
    Intent DeleteFriend = null;

    private static final String TAG_RESULTS="result";
    private static final String TAG_ID = "friend";
    private static final String TAG_ADD ="searchid";

    JSONArray peoples = null;

    ArrayList<HashMap <String, String>> friendlist;

    ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_friend_page);

        Intent intent = getIntent();
        str_loginedID = intent.getExtras().getString("loginedID");



        list = (ListView) findViewById(R.id.listView);
        friendlist = new ArrayList<HashMap<String,String>>();
        getData("http://203.252.195.99/viewfriend.php?ID=" + str_loginedID);
    }


    protected void showList(){
        try {
            JSONObject jsonObj = new JSONObject(myJSON);
            peoples = jsonObj.getJSONArray(TAG_RESULTS);

            for(int i=0;i<peoples.length();i++){
                JSONObject c = peoples.getJSONObject(i);
                String id = c.getString(TAG_ID);
                String address = c.getString(TAG_ADD);

                HashMap<String,String> persons = new HashMap<String,String>();

                persons.put(TAG_ID,id);
                persons.put(TAG_ADD,address);

                friendlist.add(persons);
            }

            ListAdapter adapter = new SimpleAdapter(
                    view.this, friendlist, R.layout.list_friend,
                    new String[]{TAG_ID,TAG_ADD},
                    new int[]{R.id.friend, R.id.bid}
            );

            list.setAdapter(adapter);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onClickDeleteFriend(View arg) {
        switch (arg.getId()) {
            case R.id.DelBtn:
                DeleteFriend = new Intent(view.this, delsearch.class);
                DeleteFriend.putExtra("loginedID", str_loginedID);
                startActivity(DeleteFriend);
                break;
        }
    }

    public void getData(String url){
        class GetDataJSON extends AsyncTask<String, Void, String>{

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];

                BufferedReader bufferedReader = null;
                try {
                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }



            }

            @Override
            protected void onPostExecute(String result){
                myJSON=result;
                showList();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url);
    }

}