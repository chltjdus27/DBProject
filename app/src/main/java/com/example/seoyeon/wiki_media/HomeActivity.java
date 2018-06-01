package com.example.seoyeon.wiki_media;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class HomeActivity extends AppCompatActivity {

    private SharedPreferences sPrefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        sPrefs = getSharedPreferences("sPrefs", MODE_PRIVATE);
        String userId = sPrefs.getString("sessionId", "");

        TextView textView = findViewById(R.id.tv_userId);
        textView.setText(userId+"님, 환영합니다.");
    }
}
