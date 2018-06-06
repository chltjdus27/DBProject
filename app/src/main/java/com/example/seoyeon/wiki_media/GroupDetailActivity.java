package com.example.seoyeon.wiki_media;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TabHost;
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

public class GroupDetailActivity extends AppCompatActivity {

    private TextView groupTitle, groupLeader, addMusic, join;
    private int image;
    private LinearLayout layout;
    private GroupMemberAdapter memberAdapter;
    private GroupMusicAdapter musicAdapter;
    private String mJsonString;
    private ListView listView1 ,listView2;
    private boolean isJoined;
    private SharedPreferences mPrefs;
    private String userId;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_detail);

        groupTitle = (TextView)findViewById(R.id.tv_groupTitle);
        groupLeader = (TextView)findViewById(R.id.tv_superUser);
        layout = (LinearLayout)findViewById(R.id.layout_groupDetail);
        listView1 = (ListView)findViewById(R.id.lv_groupMusic);
        listView2 = (ListView)findViewById(R.id.lv_groupMember);
        addMusic = (TextView) findViewById(R.id.btn_addMusic);
        join = (TextView)findViewById(R.id.btn_join);

        mPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        userId = mPrefs.getString("sessionId", "");

        isJoined = false;

        int[] img = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7};

        memberAdapter = new GroupMemberAdapter();
        musicAdapter = new GroupMusicAdapter();

        Intent intent = getIntent();
        groupTitle.setText(intent.getStringExtra("groupName"));
        groupLeader.setText(intent.getStringExtra("superUser"));
        image = intent.getIntExtra("img", 0);

        Drawable drawable = getResources().getDrawable(img[image]);
        layout.setBackground(drawable);

        TabHost tabHost = (TabHost) findViewById(android.R.id.tabhost) ;
        tabHost.setup() ;

        TabHost.TabSpec ts1 = tabHost.newTabSpec("Tab Spec 1") ;
        ts1.setContent(R.id.tab1) ;
        ts1.setIndicator("Playlist") ;
        tabHost.addTab(ts1) ;

        TabHost.TabSpec ts2 = tabHost.newTabSpec("Tab Spec 2") ;
        ts2.setContent(R.id.tab2) ;
        ts2.setIndicator("Member") ;
        tabHost.addTab(ts2) ;

        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getGroupMember.php");
        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getGroupMusic.php");

        findViewById(R.id.btn_addMusic).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(GroupDetailActivity.this, SelectMusicActivity.class);
                intent.putExtra("groupId", getIntent().getIntExtra("groupId",0));
                intent.putExtra("groupName", getIntent().getStringExtra("groupName"));
                intent.putExtra("superUser", getIntent().getStringExtra("superUser"));
                intent.putExtra("img", getIntent().getIntExtra("img", 0));
                intent.putExtra("type", 2);
                startActivity(intent);
                finish();
            }
        });

        join.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                insertToDatabase(getIntent().getIntExtra("groupId",0), userId);
                Toast.makeText(view.getContext(), "참여 완료!", Toast.LENGTH_SHORT).show();

                Intent intent = getIntent();
                finish();
                startActivity(intent);
            }
        });


    }
    public void getData(String url) {
        class GetDataJSON extends AsyncTask<String, Void, String> {

            @Override
            protected String doInBackground(String... params) {

                String uri = params[0];
                String groupId = params[1];

                BufferedReader bufferedReader = null;
                try {
                    String data = URLEncoder.encode("groupId", "UTF-8") + "=" + URLEncoder.encode(groupId, "UTF-8");

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
                showResultMember();
            }
        }
        GetDataJSON g = new GetDataJSON();
        g.execute(url, getIntent().getIntExtra("groupId",0)+"");
    }

    private void insertToDatabase(int idGroup, String userId) {
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
                    String superUser = (String) params[1];

                    String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/addMember.php";
                    String data = URLEncoder.encode("groupId", "UTF-8") + "=" + URLEncoder.encode(groupId, "UTF-8");
                    data += "&" + URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(superUser, "UTF-8");

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
        task.execute(idGroup+"", userId);
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

            listView1.setAdapter(musicAdapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    private void showResultMember(){
        try {
            JSONObject jsonObject = new JSONObject(mJsonString);
            JSONArray jsonArray = jsonObject.getJSONArray("member");

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

                memberAdapter.addMember(item.getString("idUser"));
                if(userId.equals(item.getString("idUser"))){
                    isJoined = true;

                    addMusic.setVisibility(View.VISIBLE);
                    join.setVisibility(View.GONE);
                }
            }

            listView2.setAdapter(memberAdapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    class GroupMemberAdapter extends BaseAdapter{

        private ArrayList<String> users;

        public GroupMemberAdapter(){
            users = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return users.size();
        }

        @Override
        public Object getItem(int i) {
            return users.get(i);
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
                convertView = inflater.inflate(R.layout.item_member, parent, false);
            }

            String id = users.get(position);

            // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
            TextView titleTextView = (TextView) convertView.findViewById(R.id.memberId);
            titleTextView.setText(id);

            return convertView;


        }

        public void addMember(String id){
            users.add(id);
        }
    }



    /////////////////
    class GroupMusicAdapter extends BaseAdapter{

        private ArrayList<Music> musicList;

        public GroupMusicAdapter(){
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

            if(isJoined){
                btnDelete.setTag(position);
                btnDelete.setOnClickListener(mClickListener);
            }else{
                btnDelete.setVisibility(View.GONE);
            }


            return convertView;
        }

        public void addItem(Music music){
            musicList.add(music);
        }

        ImageView.OnClickListener mClickListener = new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                int position = Integer.parseInt(view.getTag().toString());
                final Music music = musicList.get(position);
                Intent intent = getIntent();
                int groupId = intent.getIntExtra("groupId", 1);

                deleteData(groupId+"", music.getIdMusic()+"");
                Toast.makeText(view.getContext(), "삭제 완료", Toast.LENGTH_SHORT).show();

                finish();
                startActivity(intent);
            }
        };

        public void deleteData(String groupId, String musicId) {
            class GetDataJSON extends AsyncTask<String, Void, String> {

                @Override
                protected String doInBackground(String... params) {

                    String groupId = params[0];
                    String musicId = params[1];

                    BufferedReader bufferedReader = null;
                    try {
                        String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/deleteGroupMusic.php";
                        String data = URLEncoder.encode("groupId", "UTF-8") + "=" + URLEncoder.encode(groupId, "UTF-8");
                        data += "&" + URLEncoder.encode("musicId", "UTF-8") + "=" + URLEncoder.encode(musicId, "UTF-8");

                        URL url = new URL(link);
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
                    showResultMember();
                }
            }
            GetDataJSON g = new GetDataJSON();
            g.execute(groupId, musicId);
        }
    }


}
