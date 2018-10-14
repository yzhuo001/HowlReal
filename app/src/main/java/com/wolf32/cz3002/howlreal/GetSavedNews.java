package com.wolf32.cz3002.howlreal;

import android.content.Context;
import android.util.Log;

import com.wolf32.cz3002.howlreal.model.News;

import java.util.ArrayList;

public class GetSavedNews {
    private static final String TAG = "GetSavedNews";
    private RetrieveListener retrieveListener;
    private ArrayList<News> savedNewsList = new ArrayList<>();


    public GetSavedNews(RetrieveListener retrieveListener){
        this.retrieveListener = retrieveListener;
    }

    public void loadSavedNews(Context context){
       /* News news = new News();
        String url = "http://statestimesreview.com/2018/10/03/ho-ching-to-bail-out-friend-olivia-lum-with-more-than-s1-billion-taxes/";
        news.setUrl(url);
        news.setNewsId(url.replace("/",""));
        news.setTitle("Ho Ching to bail out friend Olivia Lum with more than S$1 billion taxes");
        news.setSourceName("statestimesreview.com");
        news.setImageUrl("http://statestimesreview.com/wp-content/uploads/2017/11/23511219_1659818277414205_5318980212209748063_o.jpg");
*/
        News news = new News();
        ArrayList<News> newsList = news.loadNewsOffline(context);
        if (newsList != null)
            savedNewsList.addAll(newsList);
        else
            Log.e(TAG, "loadnewsoffline error");


        // add to savedNewsList
        if (savedNewsList.size() != 0) {
            Log.e(TAG, "retrieveListener.onSuccess");
            retrieveListener.onSuccess(savedNewsList);
        }


        //set failure listener.
    }

    public interface RetrieveListener {
        void onSuccess(ArrayList<News> newsList);

        void onFailure();
    }
}
