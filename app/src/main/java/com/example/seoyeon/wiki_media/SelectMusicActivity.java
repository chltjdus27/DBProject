package com.example.seoyeon.wiki_media;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;

public class SelectMusicActivity extends AppCompatActivity {

    private ListView listView;
    private SelectMusicListAdapter adapter;
    private String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_music);

        listView = (ListView)findViewById(R.id.lv_musicList);
        adapter = new SelectMusicListAdapter();

        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getAllMusic.php");

        findViewById(R.id.btn_done).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                int type = intent.getIntExtra("type", 1);
                if(type==1){
                    finish();
                    startActivity(new Intent(SelectMusicActivity.this, GroupMusicActivity.class));
                }else if(type==2){
                    finish();

                    Intent intent1 = new Intent(SelectMusicActivity.this, GroupDetailActivity.class);
                    intent1.putExtra("groupId", intent.getIntExtra("groupId",0));
                    intent1.putExtra("groupName", intent.getStringExtra("groupName"));
                    intent1.putExtra("superUser", intent.getStringExtra("superUser"));
                    intent1.putExtra("img", intent.getIntExtra("img", 0));
                    startActivity(intent1);
                }

            }
        });
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

                adapter.addMusic(music);
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    class SelectMusicListAdapter extends BaseAdapter{

        private ArrayList<Music> musicList;
        private Bitmap bitmap;

        public SelectMusicListAdapter(){
            musicList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return musicList.size();
        }

        @Override
        public Object getItem(int i) {
            return musicList.get(i);
        }

        @Override
        public long getItemId(int i) {
            return i;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int pos = position;
            final Context context = parent.getContext();

            // "listview_item" Layout을 inflate하여 convertView 참조 획득.
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.item_select_music, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.play_image) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
            TextView descTextView = (TextView) convertView.findViewById(R.id.singer) ;
            ImageView btnAdd = (ImageView)convertView.findViewById(R.id.btn_add);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            final Music listViewItem = musicList.get(position);

            String url = listViewItem.getUrl();
            final String musicId = url.split("=")[1];

            Thread mThread = new Thread(){

                @Override
                public void run(){
                    try{
                        URL url = new URL("https://img.youtube.com/vi/"+musicId+"/mqdefault.jpg");

                        HttpURLConnection conn = (HttpURLConnection)url.openConnection();
                        conn.setDoInput(true);
                        conn.connect();

                        InputStream is = conn.getInputStream();
                        bitmap = BitmapFactory.decodeStream(is);
                    }catch (MalformedURLException e){
                        e.printStackTrace();
                    }catch (IOException e){
                        e.printStackTrace();
                    }
                }
            };
            mThread.start();

            try{
                mThread.join();
                iconImageView.setImageBitmap(bitmap);
            }catch (InterruptedException e){
            }

            // 아이템 내 각 위젯에 데이터 반영
            titleTextView.setText(listViewItem.getTitle());
            descTextView.setText(listViewItem.getSinger());

            btnAdd.setTag(position);
            btnAdd.setOnClickListener(mClickListener);

            return convertView;
        }

        public void addMusic(Music music){
            musicList.add(music);
        }

        ImageView.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int position = Integer.parseInt(view.getTag().toString());
                final Music music = musicList.get(position);
                Intent intent = getIntent();
                int groupId = intent.getIntExtra("groupId", 1);

                insertToDatabase(groupId+"", music.getIdMusic()+"", music.getTitle(), music.getSinger(), music.getUrl());
                Toast.makeText(view.getContext(), "담기 완료", Toast.LENGTH_SHORT).show();

            }
        };

        private void insertToDatabase(String groupId, String musicId, String title, String singer, String url) {
            class InsertData extends AsyncTask<String, Void, String> {

                @Override
                protected void onPreExecute() {
                    super.onPreExecute();
                }
                @Override
                protected void onPostExecute(String s) {
                    super.onPostExecute(s);
                }
                @Override
                protected String doInBackground(String... params) {

                    try {
                        String groupId = (String) params[0];
                        String musicId = (String) params[1];
                        String title = (String) params[2];
                        String singer = (String) params[3];
                        String URL = (String )params[4];

                        String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/addGroupMusic.php";
                        String data = URLEncoder.encode("groupId", "UTF-8") + "=" + URLEncoder.encode(groupId, "UTF-8");
                        data += "&" + URLEncoder.encode("musicId", "UTF-8") + "=" + URLEncoder.encode(musicId, "UTF-8");
                        data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                        data += "&" + URLEncoder.encode("singer", "UTF-8") + "=" + URLEncoder.encode(singer, "UTF-8");
                        data += "&" + URLEncoder.encode("url", "UTF-8") + "=" + URLEncoder.encode(URL, "UTF-8");

                        URL url = new URL(link);
                        URLConnection conn = url.openConnection();

                        conn.setDoOutput(true);
                        OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                        wr.write(data);
                        wr.flush();

                        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                        StringBuilder sb = new StringBuilder();
                        String line = null;

                        // Read Server Response
                        while ((line = reader.readLine()) != null) {
                            sb.append(line);
                            break;
                        }
                        return sb.toString();
                    } catch (Exception e) {
                        return new String("Exception: " + e.getMessage());
                    }
                }

            }
            InsertData task = new InsertData();
            task.execute(groupId, musicId, title, singer, url);
        }
    }

}
