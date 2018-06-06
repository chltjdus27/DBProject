package com.example.seoyeon.wiki_media;

import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sPrefs;
    private MusicListAdapter musicAdapter;
    private String mJsonString;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

//        Toolbar toolbar = (Toolbar)findViewById(R.id.main_toolbar);
//        setSupportActionBar(toolbar);
//
//        ActionBar actionBar = getSupportActionBar();
//        actionBar.setDisplayShowCustomEnabled(true); //커스터마이징 하기 위해 필요
//        actionBar.setDisplayShowTitleEnabled(false);

        musicAdapter = new MusicListAdapter();
        listView = (ListView)findViewById(R.id.listview);

        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getAllMusic.php");

        sPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        String userId = sPrefs.getString("sessionId", "");
        musicAdapter.getUserId(userId);
    }

    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                BufferedReader bufferedReader = null;
                try {

                    URL url = new URL(uri);
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
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
        g.execute(url);
    }

    private void showResult(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("music");

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                Music music = new Music();

                music.setIdMusic(item.getInt("idMusic"));
                music.setTitle(item.getString("title"));
                music.setSinger(item.getString("singer"));
                music.setUrl(item.getString("url"));

                musicAdapter.addItem(music);
            }

            listView.setAdapter(musicAdapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }
}
