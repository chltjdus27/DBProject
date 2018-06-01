package com.example.seoyeon.wiki_media;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Looper;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText userId ,userPw;
    private HttpPost httpPost;
    private HttpResponse response;
    private HttpClient httpclient;
    private List<NameValuePair> nameValuePairs;
    private ProgressDialog dialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        userId = (EditText)findViewById(R.id.userId);
        userPw = (EditText)findViewById(R.id.userPw);

        findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = ProgressDialog.show(LoginActivity.this, "",
                        "Validating user...", true);
                new Thread(new Runnable() {
                    public void run() {
                        Looper.prepare();
                        login();
                    }
                }).start();
            }
        });

        findViewById(R.id.btn_signUp).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
                startActivity(intent);
            }
        });
    }

    void login() {

        try {
            httpclient = new DefaultHttpClient();
            httpPost = new HttpPost("http://ec2-13-125-224-234.ap-northeast-2.compute.amazonaws.com/login.php");

            nameValuePairs = new ArrayList<>(2);
            nameValuePairs.add(new BasicNameValuePair("userId", userId.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("passwd", userPw.getText().toString()));

            httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            response = httpclient.execute(httpPost);

            ResponseHandler<String> responseHandler  = new BasicResponseHandler();
            final String str = responseHandler.handleResponse(response);

            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    dialog.dismiss();

                    if (str.equalsIgnoreCase("\nUser Found")) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(LoginActivity.this, "Welcome!", Toast.LENGTH_SHORT).show();
                            }
                        });
                        startActivity((new Intent(LoginActivity.this, MainActivity.class)));
                        finish();

                    } else {
                        Toast.makeText(LoginActivity.this, "Login Fail", Toast.LENGTH_SHORT).show();
                    }
                }
            });

        }
        catch(Exception e)
        {
            dialog.dismiss();
            System.out.println("Exception : " + e.getMessage());
        }
    }

}
