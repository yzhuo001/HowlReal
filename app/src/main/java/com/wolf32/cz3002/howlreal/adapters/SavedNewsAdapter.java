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

public class SavedNewsAdapter extends ArrayAdapter<News> {
    private static final String TAG = "SavedNewsAdapter";
    private Context mContext;
    private static List<News> savedNewsList = new ArrayList<>();


    public SavedNewsAdapter(@NonNull Context context, ArrayList<News> list) {
        super(context, 0 , list);
        mContext = context;
        savedNewsList = list;
    }


    @SuppressLint("DefaultLocale")
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Log.e(TAG,"getView");

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.news_snippet_template,parent,false);

        News currentNews = savedNewsList.get(position);

        ImageView imageView = listItem.findViewById(R.id.article_snippet_photo);
        imageView.setVisibility(View.GONE);

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


        return listItem;
    }
}
