package com.wolf32.cz3002.howlreal;

import android.os.AsyncTask;
import android.util.Log;

import com.wolf32.cz3002.howlreal.model.News;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

public class FetchNewsData extends AsyncTask<Void,Void,Void> {
    private static final String TAG = "FetchNewsData";
    String data;
    private RetrieveListener retrieveListener;
    private ArrayList<News> newsList = new ArrayList<>();

    public FetchNewsData(RetrieveListener retrieveListener) {
        this.retrieveListener = retrieveListener;
    }


    public void getData(){
        Log.e(TAG, "getData");
        try {
//            List<String> imageURL = new ArrayList<String>();
//            List<String> title = new ArrayList<String>();
            URL url = new URL("http://newsapi.org/v2/top-headlines?country=sg&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream inputStream = httpURLConnection.getInputStream();
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while (line != null){
                line = bufferedReader.readLine();
                data = data + line;
            }
            // Took a substring because buffered string had a null at the beginning
            JSONObject jsonObject = new JSONObject(data.substring(4));
            JSONArray jsonArray = jsonObject.getJSONArray("articles");

            Log.e(TAG, "length: " +jsonArray.length());
            for(int i=0; i<jsonArray.length(); i++){

                JSONObject article = jsonArray.getJSONObject(i);
//                imageURL.add(article.getString("urlToImage"));
//                title.add(article.getString("title"));
                News news = new News(article.getString("title"),article.getString("urlToImage"));
                newsList.add(news);
                //Log.e(TAG, ""+newsList.size());
            }

            if (newsList.size()!=0){
                Log.e(TAG, "retrieveListener.onSuccess");
                retrieveListener.onSuccess(newsList);
            }
            //NewsActivity.setImageURL(imageURL);
            //NewsActivity.setTitle(title);
            //Log.e(TAG, "title: " + title);


        } catch (IOException | JSONException e) {
            e.printStackTrace();
            retrieveListener.onFailure();
        }

    }

    @Override
    protected Void doInBackground(Void... voids) {
        getData();
        return null;
    }

    public interface RetrieveListener {
        void onSuccess(ArrayList<News> newsList);
        void onFailure();
    }


}
