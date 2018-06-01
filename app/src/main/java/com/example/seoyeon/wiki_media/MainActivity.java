package com.example.seoyeon.wiki_media;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;

public class MainActivity extends TabActivity{

    final static String HOME = "홈";
    final static String INDIVIDUAL = "마이";
    final static String GROUP = "그룹";

    private TabHost tabHost;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tabHost = (TabHost)findViewById(android.R.id.tabhost);
        tabHost.setup();
        tabHost.getTabWidget().setDividerDrawable(null);

        setupTab(new TextView(this), HOME);
        setupTab(new TextView(this), INDIVIDUAL);
        setupTab(new TextView(this), GROUP);

        tabHost.setCurrentTab(0);
    }

    private void setupTab(final View view, final String tag)
    {
        View tabview = createTabView(tabHost.getContext(), tag);

        TabHost.TabSpec tabContent = tabHost.newTabSpec(tag).setIndicator(tabview);

        if(tag.equals(HOME)){
            tabContent.setContent(new Intent(this, HomeActivity.class));
        } else if(tag.equals(INDIVIDUAL)){
            tabContent.setContent(new Intent(this, MyMusicActivity.class));
        } else if(tag.equals(GROUP)){
            tabContent.setContent(new Intent(this, GroupMusicActivity.class));
        }

        tabHost.addTab(tabContent);

    }
    private static View createTabView(final Context context, final String text)
    {
        // layoutinflater를 이용해 xml 리소스를 읽어옴
        View view = LayoutInflater.from(context).inflate(R.layout.tabwiget_layout, null);
        ImageView img;

        if(text.equals(HOME))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            img.setImageResource(R.drawable.tab_01);
        }
        else if(text.equals(INDIVIDUAL))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            img.setImageResource(R.drawable.tab_02);
        }
        else if(text.equals(GROUP))
        {
            img = (ImageView)view.findViewById(R.id.tabs_image);
            img.setImageResource(R.drawable.tab_03);
        }

        TextView tv = (TextView) view.findViewById(R.id.tabs_text);
        tv.setText(text);

        return view;
    }

}
