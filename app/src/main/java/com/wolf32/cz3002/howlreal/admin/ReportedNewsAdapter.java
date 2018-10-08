package com.wolf32.cz3002.howlreal.admin;


import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
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

public class ReportedNewsAdapter extends ArrayAdapter<News> {
    private static final String TAG = "ReportedNewsAdapter";
    private Context mContext;
    private static List<News> newsList = new ArrayList<>();


    public ReportedNewsAdapter(@NonNull Context context, ArrayList<News> list) {
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
        Log.e(TAG,"getView");

        View listItem = convertView;
        if(listItem == null)
            listItem = LayoutInflater.from(mContext).inflate(R.layout.reported_news_snippet, parent,false);

        News currentNews = newsList.get(position);

        ImageView imageView = listItem.findViewById(R.id.snippet_photo);
        //imageView.setColorFilter(ContextCompat.getColor(getContext(), R.color.com_facebook_button_background_color_disabled), android.graphics.PorterDuff.Mode.SRC_IN);

        TextView title = listItem.findViewById(R.id.snippet_title);
        title.setText(currentNews.getTitle());

        TextView source = listItem.findViewById(R.id.snippet_info);
        source.setText(currentNews.getSourceName());

        TextView count = listItem.findViewById(R.id.snippet_count);
        count.setText("1");

        Log.e(TAG,"index: " + position);
        Log.e(TAG,"currentNews.getImageUrl(): " + currentNews.getImageUrl());
        //if (userEmail != null && !userEmail.isEmpty() && !userEmail.equals("null"))
        if (!currentNews.getImageUrl().isEmpty() && !currentNews.getImageUrl().contains("null") && !currentNews.getImageUrl().equals("null")
                ) {
            Log.e(TAG,"exists: " + currentNews.getImageUrl());
            Picasso.get().load(currentNews.getImageUrl()).resize(120,100).into(imageView);
        }
        else{
            Log.e(TAG,"null: " + currentNews.getImageUrl());
            //todo: change this to black or grey image if null.
            Picasso.get().load("http://wallpicel.com/wp-content/uploads/2017/10/Grey-Color-Wallpaper-Photos-High-Quality-For-Laptop-The-All-Encompassing.jpg").resize(120,100).into(imageView);
        }



        if (currentNews.getImageUrl().contains("null")){
            //imageView.setVisibility(View.GONE);
            //todo: add default image to news without ImageUrl
        }




        return listItem;
    }
}
