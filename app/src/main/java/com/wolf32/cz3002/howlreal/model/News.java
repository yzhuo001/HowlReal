package com.wolf32.cz3002.howlreal.model;


import android.util.Log;

public class News {
    private static final String TAG = "NewsModel";
    private String mTitle;
    private String mImageUrl;

    public News(String mTitle, String mImageUrl){
        this.mTitle = mTitle;
        this.mImageUrl = mImageUrl;
        Log.e(TAG, "mTitle: " + mTitle);
        Log.e(TAG, "mImageUrl:" + mImageUrl);
    }

    public String getImageUrl(){
        return mImageUrl;
    }
    public void setImageUrl(String imageUrl){
        this.mImageUrl = imageUrl;
    }
    public String getTitle(){
        return mTitle;
    }
    public void setTitle(String title){
        this.mTitle = title;
    }


}
