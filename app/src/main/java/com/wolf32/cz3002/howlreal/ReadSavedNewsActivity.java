package com.wolf32.cz3002.howlreal;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.wolf32.cz3002.howlreal.model.News;

import java.util.ArrayList;

public class ReadSavedNewsActivity extends AppCompatActivity {

    private static final String TAG = "ReadSavedNewsActivity";
    private News news;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_saved_news);

        Intent intent = this.getIntent();
        Bundle bundle = intent.getExtras();

        if (bundle != null) {
            news = (News) bundle.getSerializable("news");

            if (news == null) {
                Log.e(TAG, "error: news == null");
            } else {
                Log.e(TAG, "displayed read news layout");

                //get toolbar
                ActionBar supportActionBar = getSupportActionBar();
                supportActionBar.setTitle(news.getSourceName().toLowerCase());

                //set menu item display on/off

                TextView newsTitle = findViewById(R.id.news_title);
                newsTitle.setText(news.getTitle());

                TextView newsBody = findViewById(R.id.news_body);
                newsBody.setText(news.getContent());

            }
        }


    }




}
