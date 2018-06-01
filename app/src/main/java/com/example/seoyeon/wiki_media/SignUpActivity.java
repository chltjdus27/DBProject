package com.example.seoyeon.wiki_media;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class SignUpActivity extends AppCompatActivity {

    private EditText id, pw, name, sex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        id = findViewById(R.id.id);
        pw = findViewById(R.id.passwd);
        name = findViewById(R.id.name);
        sex = findViewById(R.id.sex);

    }
    public void insert(View view) {
        String userId = id.getText().toString();
        String userPw = pw.getText().toString();
        String userName = name.getText().toString();
        String userSex = sex.getText().toString();

        insertToDatabase(userId, userPw, userName, userSex);
    }
    private void insertToDatabase(String userId, String userPw, String userName, String userSex) {
        class InsertData extends AsyncTask<String, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(SignUpActivity.this, "Please Wait", null, true, true);
            }
            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();
                //Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
            }
            @Override
            protected String doInBackground(String... params) {

                try {
                    String id = (String) params[0];
                    String pw = (String) params[1];
                    String name = (String) params[2];
                    String sex = (String) params[3];

                    String link = "http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/sign_up.php";
                    String data = URLEncoder.encode("userID", "UTF-8") + "=" + URLEncoder.encode(id, "UTF-8");
                    data += "&" + URLEncoder.encode("userPW", "UTF-8") + "=" + URLEncoder.encode(pw, "UTF-8");
                    data += "&" + URLEncoder.encode("userName", "UTF-8") + "=" + URLEncoder.encode(name, "UTF-8");
                    data += "&" + URLEncoder.encode("userSex", "UTF-8") + "=" + URLEncoder.encode(sex, "UTF-8");

                    URL url = new URL(link);
                    URLConnection conn = url.openConnection();

                    conn.setDoOutput(true);
                    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());

                    wr.write(data);
                    wr.flush();

                    BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    StringBuilder sb = new StringBuilder();
                    String line = null;

                    Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
                    startActivity(intent);

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
        task.execute(userId, userPw, userName, userSex);
    }
}
