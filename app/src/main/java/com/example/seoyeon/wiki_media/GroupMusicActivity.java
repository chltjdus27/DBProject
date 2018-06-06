package com.example.seoyeon.wiki_media;

import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;

public class GroupMusicActivity extends AppCompatActivity {

    private GroupListAdapter adapter;
    private ListView listView;
    private String mJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_music);

        listView = (ListView)findViewById(R.id.groupList);
        adapter = new GroupListAdapter();

        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getAllGroup.php");
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
            JSONArray jsonArray = jsonObject.getJSONArray("group");

            for(int i=0;i<jsonArray.length();i++){

                JSONObject item = jsonArray.getJSONObject(i);

//                Group group = new Group();
//
//                group.setIdGroup(item.getInt("groupId"));
//                group.setSuperUserId(item.getString("superUser"));
//                group.setTitle(item.getString("title"));

                adapter.addGroup(item.getInt("groupId"), item.getString("superUser"), item.getString("title"));
            }

            listView.setAdapter(adapter);

        } catch (JSONException e) {
            Log.d("error", "showResult : ", e);
        }

    }

    class GroupListAdapter extends BaseAdapter{

        private ArrayList<Group> groupList;
        int[] img = {R.drawable.bg1, R.drawable.bg2, R.drawable.bg3, R.drawable.bg4, R.drawable.bg5, R.drawable.bg6, R.drawable.bg7};

        public GroupListAdapter(){
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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            final int pos = position;
            final Context context = viewGroup.getContext();

            Random ram = new Random();
            int num = ram.nextInt(img.length);

            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.listview_group, viewGroup, false);
            }

            LinearLayout layout = (LinearLayout) convertView.findViewById(R.id.layout_groupMusic) ;
            TextView titleTextView = (TextView) convertView.findViewById(R.id.groupTitle) ;
            TextView countTextView = (TextView) convertView.findViewById(R.id.musicCount) ;

            // Data Set(listViewItemList)에서 position에 위치한 데이터 참조 획득
            Group item = groupList.get(position);

            Drawable drawable = getResources().getDrawable(img[num]);
            layout.setBackground(drawable);

            titleTextView.setText(item.getTitle());

            return convertView;
        }

        public void addGroup(int groupId, String userId, String title){
            Group group = new Group();
            group.setIdGroup(groupId);
            group.setSuperUserId(userId);
            group.setTitle(title);

            groupList.add(group);
        }
    }
}
