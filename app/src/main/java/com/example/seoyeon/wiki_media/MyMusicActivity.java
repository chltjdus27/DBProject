package com.example.seoyeon.wiki_media;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import java.net.URLEncoder;
import java.util.ArrayList;

public class MyMusicActivity extends AppCompatActivity {

    private SharedPreferences sPrefs;
    private String mJsonString, userId;
    ListView listview ;
    ListView groupList;
    //ListviewAdapter adapter;
    Bitmap bitmap;
    private MyMusicAdapter adapter;
    private MyGroupAdapter groupAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_music);

        listview = (ListView) findViewById(R.id.listview2);
        //groupList = (ListView)findViewById(R.id.listview1);
        adapter = new MyMusicAdapter();
        groupAdapter = new MyGroupAdapter();

        sPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        userId = sPrefs.getString("sessionId", "");


        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getUserPlayList.php");
        //getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getMyGroup.php");

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

                Listviewitem items = (Listviewitem) adapterView.getItemAtPosition(i) ;

                Intent intent = new Intent(Intent.ACTION_VIEW,Uri.parse(items.getUrl()));
                startActivity(intent);

            }
        });

//        groupList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                Group group = (Group)adapterView.getItemAtPosition(i);
//
//                Intent intent = new Intent(MyMusicActivity.this, GroupDetailActivity.class);
//                intent.putExtra("groupId", group.getIdGroup());
//                intent.putExtra("superUser", group.getSuperUserId());
//                intent.putExtra("groupName", group.getTitle());
//                intent.putExtra("img", 1);
//
//                startActivity(intent);
//            }
//        });

    }

    public void getData(String url) {
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
                //showResultGroup();
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

                Music music = new Music();

                music.setIdMusic(item.getInt("idMusic"));
                music.setTitle(item.getString("title"));
                music.setSinger(item.getString("singer"));
                music.setUrl(item.getString("url"));

                adapter.addItem(music);
                Log.i("ddddddddd", music.getSinger());
            }

            listview.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    private void showResultGroup(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("group");

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                Group group = new Group();
                group.setIdGroup(item.getInt("idGroup"));
                group.setSuperUserId(item.getString("superUser"));
                group.setTitle(item.getString("groupName"));

                groupAdapter.addItem(group);
            }

            groupList.setAdapter(groupAdapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    class MyMusicAdapter extends BaseAdapter {

        private ArrayList<Music> musicList;

        public MyMusicAdapter(){
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
                convertView = inflater.inflate(R.layout.group_music_item, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            ImageView iconImageView = (ImageView) convertView.findViewById(R.id.play_image) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
            TextView descTextView = (TextView) convertView.findViewById(R.id.singer) ;
            ImageView btnPlay = (ImageView) convertView.findViewById(R.id.btn_play);
            ImageView btnDelete = (ImageView)convertView.findViewById(R.id.btn_delete);

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

            btnPlay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(listViewItem.getUrl()));
                    context.startActivity(intent);
                }
            });

            return convertView;
        }

        public void addItem(Music music){
            musicList.add(music);
        }


    }

    class MyGroupAdapter extends BaseAdapter {

        private ArrayList<Group> groupList;

        public MyGroupAdapter(){
            groupList = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return groupList.size();
        }

        @Override
        public Object getItem(int i) {
            return groupList.get(i);
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
                convertView = inflater.inflate(R.layout.item_group, parent, false);
            }

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView tv = (TextView)findViewById(R.id.groupName);

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            final Group listViewItem = groupList.get(position);

            tv.setText(listViewItem.getTitle());

            return convertView;
        }

        public void addItem(Group group){
            groupList.add(group);
        }


    }


}

