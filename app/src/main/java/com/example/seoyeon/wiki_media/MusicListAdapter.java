package com.example.seoyeon.wiki_media;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

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

/**
 * Created by SeoYeon Choi on 2018-06-06.
 */

public class MusicListAdapter extends BaseAdapter{
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Music> musicList;
    private Bitmap bitmap;
    private String idUser = null;

    // ListViewAdapter의 생성자
    public MusicListAdapter() {
        musicList = new ArrayList<>();
    }

    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return musicList.size() ;
    }

    // position에 위치한 데이터를 화면에 출력하는데 사용될 View를 리턴. : 필수 구현
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final int pos = position;
        final Context context = parent.getContext();

        // "listview_item" Layout을 inflate하여 convertView 참조 획득.
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.main_music_item, parent, false);
        }

        // 화면에 표시될 View(Layout이 inflate된)으로부터 위젯에 대한 참조 획득
        ImageView iconImageView = (ImageView) convertView.findViewById(R.id.play_image) ;
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
        TextView descTextView = (TextView) convertView.findViewById(R.id.singer) ;
        ImageView btnPlay = (ImageView) convertView.findViewById(R.id.btn_play);
        ImageView btnAdd = (ImageView)convertView.findViewById(R.id.btn_add);

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Music listViewItem = musicList.get(position);

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

    // 지정한 위치(position)에 있는 데이터와 관계된 아이템(row)의 ID를 리턴. : 필수 구현
    @Override
    public long getItemId(int position) {
        return position ;
    }

    // 지정한 위치(position)에 있는 데이터 리턴 : 필수 구현
    @Override
    public Object getItem(int position) {
        return musicList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(Music music) {
        musicList.add(music);
    }

    public void getUserId(String userId){
        idUser = userId;
    }

    ImageView.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(final View view) {
            int position = Integer.parseInt(view.getTag().toString());
            final Music music = musicList.get(position);

            AlertDialog.Builder alert_confirm = new AlertDialog.Builder(view.getContext());
            alert_confirm.setMessage("플레이리스트에 담으시겠습니까?").setCancelable(false).setPositiveButton("확인",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            insertToDatabase(idUser, music.getIdMusic()+"", music.getTitle(), music.getSinger());
                            Toast.makeText(view.getContext(), "담기 완료", Toast.LENGTH_SHORT).show();
                        }
                    }).setNegativeButton("취소",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // 'No'
                            return;
                        }
                    });
            AlertDialog alert = alert_confirm.create();
            alert.show();

        }
    };
    private void insertToDatabase(String userId, String musicId, String title, String singer) {
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
                    String userId = (String) params[0];
                    String musicId = (String) params[1];
                    String title = (String) params[2];
                    String singer = (String) params[3];

                    String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/addMusic.php";
                    String data = URLEncoder.encode("userId", "UTF-8") + "=" + URLEncoder.encode(userId, "UTF-8");
                    data += "&" + URLEncoder.encode("musicId", "UTF-8") + "=" + URLEncoder.encode(musicId, "UTF-8");
                    data += "&" + URLEncoder.encode("title", "UTF-8") + "=" + URLEncoder.encode(title, "UTF-8");
                    data += "&" + URLEncoder.encode("singer", "UTF-8") + "=" + URLEncoder.encode(singer, "UTF-8");

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
        task.execute(userId, musicId, title, singer);
    }
}
