package com.wolf32.cz3002.howlreal.adapters;


import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

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

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.news_snippet_template,parent,false);

        News currentNews = newsList.get(position);

        ImageView imageView = listItem.findViewById(R.id.article_snippet_photo);
        URL url = null;

        Log.e(TAG,"currentNews.getImageUrl(): "+currentNews.getImageUrl());
        if (currentNews.getImageUrl() != null) {
            Picasso.get().load(currentNews.getImageUrl()).into(imageView);
        }

        TextView name = listItem.findViewById(R.id.article_snippet_title);
        name.setText(currentNews.getTitle());

        return listItem;
    }
}
