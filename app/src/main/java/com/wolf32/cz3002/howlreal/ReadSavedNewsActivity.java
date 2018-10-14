package com.wolf32.cz3002.howlreal;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.wolf32.cz3002.howlreal.model.News;

import java.util.ArrayList;

public class ReadSavedNewsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_saved_news);

        News news = new News();
        ArrayList<News> savedNewsList = news.loadNewsOffline(this);

        for (int i=0; i < savedNewsList.size(); i++){
            News currNews = savedNewsList.get(i);

        }

    }

}
