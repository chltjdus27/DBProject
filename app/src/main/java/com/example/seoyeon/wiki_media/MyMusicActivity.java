package com.example.seoyeon.wiki_media;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

public class MyMusicActivity extends AppCompatActivity {

    private SharedPreferences sPrefs;
    private String mJsonString;
    ListView listview ;
    ListviewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);

        listview = (ListView) findViewById(R.id.listview);
        adapter = new ListviewAdapter();

        sPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        String userId = sPrefs.getString("sessionId", "");


        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getUserPlayList.php", userId);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Listviewitem items = (Listviewitem) adapterView.getItemAtPosition(i) ;

                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(items.getUrl()));
                startActivity(intent);

            }
        });

    }

    public void getData(String url, final String userId){
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String userId = params[1];

                BufferedReader bufferedReader = null;
                try {
                    String data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();

                    con.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    StringBuilder sb = new StringBuilder();

                    bufferedReader = new BufferedReader(new InputStreamReader(con.getInputStream()));

                    String json = null;
                    while((json = bufferedReader.readLine())!= null){
                        sb.append(json+"\n");
                    }

                    return sb.toString().trim();

                }catch(Exception e){
                    return null;
                }
            }

            @Override
            protected void onPostExecute(String result) {
                mJsonString = result;
                showResult();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url, userId);
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("music");

            for(int i=0;i<jsonArray.length();i++){
                JSONObject item = jsonArray.getJSONObject(i);
                adapter.addItem(item.getInt("idMusic"),item.getString("title"),
                        item.getString("singer"),item.getString("url"));
            }
            listview.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);

        }
    }


}