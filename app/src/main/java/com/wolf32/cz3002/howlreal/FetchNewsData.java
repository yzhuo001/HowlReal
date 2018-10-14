package com.wolf32.cz3002.howlreal;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wolf32.cz3002.howlreal.model.News;
import com.wolf32.cz3002.howlreal.model.User;

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

public class FetchNewsData extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "FetchNewsData";
    String data;
    private FirebaseFirestore db;
    private RetrieveListener retrieveListener;
    private ArrayList<News> newsList = new ArrayList<>();

    public ArrayList<News> getNewsList() {
        return newsList;
    }

    public FetchNewsData(RetrieveListener retrieveListener) {
        this.retrieveListener = retrieveListener;
    }


    public void getData(final String category, int userType) {
        Log.e(TAG, "getData");
        if (userType == 1) { // admin
            if (category.equals("reportedNews")) {
                Log.e(TAG, "reported news");
                getReportedNews();

                Log.e(TAG," after getReportedNews, newsList.size(): " + newsList.size());

                if (newsList.size() != 0) {
                    Log.e(TAG, "retrieveListener.onSuccess");
                    retrieveListener.onSuccess(newsList);
                }
            }

        }
        if (userType == 0 || category.equals("notification")) { // user or admin push notification
            try {

                URL url = null;

                // load pageSize only works for <20 articles
                if (category.equals("general")) {
                    Log.e(TAG, "general");
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("health")) {
                    Log.e(TAG, "health");
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=health&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("sports")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=sports&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("science")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=science&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("technology")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=technology&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("business")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=business&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("entertainment")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&category=entertainment&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("saved")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?pageSize=5&q=crypto&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                } else if (category.equals("notification")) {
                    url = new URL("http://newsapi.org/v2/top-headlines?country=sg&pageSize=20&apiKey=812d6b6b470a4b17a228bdf0361f0d46");
                    newsList.add(getPushNotifNews());
                }

                // open connection to retrieve news from URL
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String line = "";
                while (line != null) {
                    line = bufferedReader.readLine();
                    data = data + line;
                }

                // Took a substring because buffered string had a null at the beginning
                JSONObject jsonObject = new JSONObject(data.substring(4));
                final JSONArray jsonArray = jsonObject.getJSONArray("articles");

                // query blacklisted news
                db = FirebaseFirestore.getInstance();

                FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                        .setTimestampsInSnapshotsEnabled(true)
                        .build();
                db.setFirestoreSettings(settings);

                News blacklistedNews = getFakeNews();
                DocumentReference docRef = db.collection("blacklistedNews").document(blacklistedNews.getNewsId());
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.e(TAG, "Blacklisted News: " + document.getData());
                                Log.e(TAG, "length: " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {

                                    try {
                                        JSONObject article = jsonArray.getJSONObject(i);
                                        JSONObject source = article.getJSONObject("source");
                                        String mSourceName = source.getString("name").toLowerCase();
                                        String mTitle = article.getString("title");
                                        String mUrlToImage = article.getString("urlToImage");
                                        String mAuthor = article.getString("author");
                                        String mPublishedAt = article.getString("publishedAt");
                                        String mContent = article.getString("content");
                                        String mDescription = article.getString("description");
                                        String mUrl = article.getString("url");

                                        Log.e(TAG,"mUrlToImage : " + mUrlToImage);
                                        if (mUrlToImage.contains("null")){
                                            Log.e(TAG,"mUrlToImage.contains(null) continue");
                                            continue;
                                        }

                                        News news = new News();
                                        news.setSourceName(mSourceName);
                                        news.setAuthor(mAuthor);
                                        news.setContent(mContent);
                                        news.setImageUrl(mUrlToImage);
                                        news.setDescription(mDescription);
                                        news.setTitle(mTitle);
                                        news.setPublishedAt(mPublishedAt);
                                        news.setUrl(mUrl);
                                        news.setNewsId(mUrl.replace("/", ""));
                                        newsList.add(news);
                                        news.addToDatabase(mUrl);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                        retrieveListener.onFailure();

                                    }

                                }

                                if (newsList.size() != 0) {
                                    Log.e(TAG, "retrieveListener.onSuccess");
                                    retrieveListener.onSuccess(newsList);
                                }
                            } else {
                                Log.d(TAG, "No such document");
                                if (category.equals("general")) {
                                    newsList.add(getFakeNews());
                                }
                                Log.e(TAG, "length: " + jsonArray.length());
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    try {
                                        JSONObject article = jsonArray.getJSONObject(i);
                                        JSONObject source = article.getJSONObject("source");

                                        String mSourceName = source.getString("name").toLowerCase();
                                        String mTitle = article.getString("title");
                                        String mUrlToImage = article.getString("urlToImage");
                                        String mAuthor = article.getString("author");
                                        String mPublishedAt = article.getString("publishedAt");
                                        String mContent = article.getString("content");
                                        String mDescription = article.getString("description");
                                        String mUrl = article.getString("url");

                                        Log.e(TAG,"mUrlToImage : " + mUrlToImage);

                                        if (mUrlToImage.contains("null")) {
                                            Log.e(TAG,"mUrlToImage.contains(null) continue");
                                            continue;

                                        }

                                        News news = new News();
                                        news.setSourceName(mSourceName);
                                        news.setAuthor(mAuthor);
                                        news.setContent(mContent);
                                        news.setImageUrl(mUrlToImage);
                                        news.setDescription(mDescription);
                                        news.setTitle(mTitle);
                                        news.setPublishedAt(mPublishedAt);
                                        news.setUrl(mUrl);
                                        news.setNewsId(mUrl.replace("/", ""));

                                        newsList.add(news);
                                        news.addToDatabase(mUrl);
                                    }
                                    catch (JSONException e) {
                                        e.printStackTrace();
                                        retrieveListener.onFailure();

                                    }

                                }

                                if (newsList.size() != 0) {
                                    Log.e(TAG, "retrieveListener.onSuccess");
                                    retrieveListener.onSuccess(newsList);
                                }


                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());
                        }
                    }
                });



            } catch (IOException | JSONException e) {
                e.printStackTrace();
                retrieveListener.onFailure();
            }


        }


    }

    @Override
    protected Void doInBackground(Void... voids) {
        //Log.e(TAG,"doInBackground");
        return null;
    }

    public interface RetrieveListener {
        void onSuccess(ArrayList<News> newsList);

        void onFailure();
    }


    public void getReportedNews() {
        Log.e(TAG, "getReportedNews");

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("reportedNews")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                String newsId = document.getString("newsId");
                                String userId = document.getString("userId");

                                DocumentReference newsRef = db.collection("news").document(newsId);
                                newsRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful()) {
                                            DocumentSnapshot document = task.getResult();
                                            if (document.exists()) {
                                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                                                String mSourceName = document.getString("sourceName");
                                                String mAuthor = document.getString("author");
                                                String mContent = document.getString("content");
                                                String mUrlToImage = document.getString("imageUrl");
                                                String mDescription = document.getString("description");
                                                String mTitle = document.getString("title");
                                                String mPublishedAt = document.getString("publishedAt");
                                                String mUrl = document.getString("url");

                                                News news = new News();
                                                news.setSourceName(mSourceName);
                                                news.setAuthor(mAuthor);
                                                news.setContent(mContent);
                                                news.setImageUrl(mUrlToImage);
                                                news.setDescription(mDescription);
                                                news.setTitle(mTitle);
                                                news.setPublishedAt(mPublishedAt);
                                                news.setUrl(mUrl);
                                                Log.e(TAG, news.getUrl());
                                                if (mUrl != null){
                                                    news.setNewsId(mUrl.replace("/", ""));
                                                }
                                                else {
                                                    Log.e(TAG, "news mUrl is null.");
                                                }


                                                if (newsList.size() != 0) {
                                                    Log.e(TAG, "retrieveListener.onSuccess");
                                                    retrieveListener.onSuccess(newsList);
                                                }

                                            } else {
                                                Log.d(TAG, "No such document");
                                                retrieveListener.onFailure();

                                            }
                                        } else {
                                            Log.d(TAG, "get failed with ", task.getException());
                                            retrieveListener.onFailure();

                                        }
                                    }
                                });

                                Log.e(TAG, "after onComplete");

                                /*// Create a reference to the user collection
                                CollectionReference userRef = db.collection("user");
                                // Create a query against the collection.
                                Query userQuery = newsRef.whereEqualTo("userId", userId);
                                userQuery.get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        Log.d(TAG, document.getId() + " => " + document.getData());
                                                        String name = document.getString("name");
                                                        Log.e(TAG, "name : " + name);
                                                    }
                                                } else {
                                                    Log.d(TAG, "Error getting documents: ", task.getException());
                                                }
                                            }
                                        });*/

                            }
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }

                    }
                });

        Log.e(TAG, "getReportedNews ended");


    }

    public boolean getBlacklisted(News news){
        //check if its in blacklist. if yes, dont display to user
        //get blacklisted news id.
        String newsId = news.getNewsId();
        final boolean[] blacklisted = {false};

        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        DocumentReference docRef = db.collection("blacklistedNews").document(newsId);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.e(TAG, "Blacklisted News: " + document.getData());
                        blacklisted[0] = true;
                    } else {
                        Log.d(TAG, "No such document");
                        blacklisted[0] = false;

                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return blacklisted[0];
    }


    private News getFakeNews(){
        News news = new News();
        String url = "http://statestimesreview.com/2018/10/03/ho-ching-to-bail-out-friend-olivia-lum-with-more-than-s1-billion-taxes/";
        news.setUrl(url);
        news.setNewsId(url.replace("/",""));
        news.setTitle("Ho Ching to bail out friend Olivia Lum with more than S$1 billion taxes");
        news.setSourceName("statestimesreview.com");
        news.setImageUrl("http://statestimesreview.com/wp-content/uploads/2017/11/23511219_1659818277414205_5318980212209748063_o.jpg");

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection("news").document(news.getNewsId()).set(news);
        Log.e(TAG,"added fake news");
        return news;
    }


    private News getPushNotifNews(){

        // todo: edit push notification
        // add the hardcoded news for push notification for the first one.
        News newsToRead = new News();
        newsToRead.setImageUrl("https://www.straitstimes.com/sites/default/files/styles/article_pictrure_780x520_/public/articles/2018/10/12/colin-hks-12.jpg?itok=CyzzS8-R&timestamp=1539348256");
        newsToRead.setUrl("https://www.straitstimes.com/singapore/outcomes-matter-heng-swee-keat-says-in-response-to-controversial-oxfam-report");
        newsToRead.setSourceName("straitstimes.com");
        newsToRead.setTitle("Outcomes matter, Heng Swee Keat says in response to controversial Oxfam report");
        String url = "https://www.straitstimes.com/singapore/outcomes-matter-heng-swee-keat-says-in-response-to-controversial-oxfam-report";
        newsToRead.setNewsId(url.replace("/", ""));

        db = FirebaseFirestore.getInstance();
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);
        db.collection("news").document(newsToRead.getNewsId()).set(newsToRead);
        Log.e(TAG,"added push notif news");

        return newsToRead;
    }



}
