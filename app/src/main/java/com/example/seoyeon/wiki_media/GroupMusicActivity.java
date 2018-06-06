package com.example.seoyeon.wiki_media;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Random;

public class GroupMusicActivity extends AppCompatActivity {

    private GroupListAdapter adapter;
    private ListView listView;
    private String mJsonString;
    private SharedPreferences mPrefs;
    private int[] imgArray;
    private int groupCount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_music);

        listView = (ListView)findViewById(R.id.groupList);
        adapter = new GroupListAdapter();
        imgArray = new int[20];

        mPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        final String userId = mPrefs.getString("sessionId", "");

        getData("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/getAllGroup.php");

        findViewById(R.id.btn_createGroup).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder ad = new AlertDialog.Builder(GroupMusicActivity.this);

                ad.setTitle("그룹 생성");       // 제목 설정
                ad.setMessage("그룹명을 입력해주세요!");   // 내용 설정

                final EditText et = new EditText(GroupMusicActivity.this);
                ad.setView(et);

                ad.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String value = et.getText().toString();

                        insertToDatabase(groupCount+1, userId, value);
                        insertTo_Database(groupCount+1, userId);

                        Intent intent = new Intent(GroupMusicActivity.this, SelectMusicActivity.class);
                        intent.putExtra("groupId", groupCount+1);
                        intent.putExtra("type", 1);
                        startActivity(intent);
                        finish();

                        dialog.dismiss();     //닫기
                        // Event
                    }
                });

// 취소 버튼 설정
                ad.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();     //닫기
                        // Event
                    }
                });
// 창 띄우기
                ad.show();
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Group group = (Group)adapterView.getItemAtPosition(i);

                Intent intent = new Intent(GroupMusicActivity.this, GroupDetailActivity.class);
                intent.putExtra("groupId", group.getIdGroup());
                intent.putExtra("superUser", group.getSuperUserId());
                intent.putExtra("groupName", group.getTitle());
                intent.putExtra("img", imgArray[i]);

                startActivity(intent);
            }
        });
    }

    private void insertToDatabase(int idGroup, String userId, String groupName) {
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
                    String groupName = (String) params[2];

                    String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/createGroup.php";
                    String data = URLEncoder.encode("groupId", "UTF-8") + "=" + URLEncoder.encode(groupId, "UTF-8");
                    data += "&" + URLEncoder.encode("superUser", "UTF-8") + "=" + URLEncoder.encode(superUser, "UTF-8");
                    data += "&" + URLEncoder.encode("groupName", "UTF-8") + "=" + URLEncoder.encode(groupName, "UTF-8");
                    data += "&" + URLEncoder.encode("limitNum", "UTF-8") + "=" + URLEncoder.encode("20", "UTF-8");

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
        task.execute(idGroup+"", userId, groupName);
    }


    private void insertTo_Database(int idGroup, String userId) {
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

                Group group = new Group();

                group.setIdGroup(item.getInt("groupId"));
                group.setSuperUserId(item.getString("superUser"));
                group.setTitle(item.getString("title"));

                adapter.addGroup(group);
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
            groupCount = groupList.size();
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

            imgArray[position] = num;
            Drawable drawable = getResources().getDrawable(img[num]);
            layout.setBackground(drawable);

            titleTextView.setText(item.getTitle());

            return convertView;
        }

        public void addGroup(Group group){
            groupList.add(group);
        }
    }

}
