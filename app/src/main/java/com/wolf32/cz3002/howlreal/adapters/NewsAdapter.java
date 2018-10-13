package com.wolf32.cz3002.howlreal.adapters;


import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;
import com.wolf32.cz3002.howlreal.R;
import com.wolf32.cz3002.howlreal.model.News;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class NewsAdapter extends ArrayAdapter<News> {
    private static final String TAG = "NewsAdapter";
    private Context mContext;
    private static List<News> newsList = new ArrayList<>();


    public NewsAdapter(@NonNull Context context, ArrayList<News> list) {
        super(context, 0 , list);
        mContext = context;
        newsList = list;
    }


    public static void addNews(News news){
        newsList.add(news);
    }

    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.e(TAG,"getView");

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.news_snippet_template,parent,false);

        News currentNews = newsList.get(position);

        ImageView imageView = listItem.findViewById(R.id.article_snippet_photo);

        TextView title = listItem.findViewById(R.id.article_snippet_title);
        title.setText(currentNews.getTitle());

        TextView source = listItem.findViewById(R.id.article_snippet_info);
        source.setText(currentNews.getSourceName());

        TextView newsCredibilityScore = listItem.findViewById(R.id.article_snippet_credibility);
        Random r = new Random();
        double randomValue = 77 + (96 - 77) * r.nextDouble();
        newsCredibilityScore.setText(String.format("%.1f", randomValue));

        Log.e(TAG,"index: " + position);
        Log.e(TAG,"currentNews.getImageUrl(): " + currentNews.getImageUrl());

        if (!currentNews.getImageUrl().isEmpty() || !currentNews.getImageUrl().contains("null")) {
            Picasso.get().load(currentNews.getImageUrl()).resize(120,100).into(imageView);
        }

        if (currentNews.getImageUrl().contains("null")){
            //imageView.setVisibility(View.GONE);
            //todo: add default image to news without ImageUrl
        }

        return listItem;
    }
}
