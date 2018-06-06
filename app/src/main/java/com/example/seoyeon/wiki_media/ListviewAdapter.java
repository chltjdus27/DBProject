package com.example.seoyeon.wiki_media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

public class ListviewAdapter extends BaseAdapter {
    // Adapter에 추가된 데이터를 저장하기 위한 ArrayList
    private ArrayList<Listviewitem> listViewItemList = new ArrayList<Listviewitem>() ;
    private Bitmap bitmap;

    // ListViewAdapter의 생성자
    public ListviewAdapter() {

    }
    // Adapter에 사용되는 데이터의 개수를 리턴. : 필수 구현
    @Override
    public int getCount() {
        return listViewItemList.size() ;
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
        ImageView imageView = (ImageView)convertView.findViewById(R.id.play_image);
        TextView titleTextView = (TextView) convertView.findViewById(R.id.title) ;
        TextView singerTextView = (TextView) convertView.findViewById(R.id.singer) ;

        // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
        Listviewitem listViewItem = listViewItemList.get(position);

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
            imageView.setImageBitmap(bitmap);
        }catch (InterruptedException e){
        }
        titleTextView.setText(listViewItem.getTitle());
        singerTextView.setText(listViewItem.getDesc());

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
        return listViewItemList.get(position) ;
    }

    // 아이템 데이터 추가를 위한 함수. 개발자가 원하는대로 작성 가능.
    public void addItem(int idMusic, String title, String singer,String Url) {

        Listviewitem item = new Listviewitem();

        item.setidMusic(idMusic);
        item.setTitle(title);
        item.setSinger(singer);
        item.setUrl(Url);

        listViewItemList.add(item);
    }
}