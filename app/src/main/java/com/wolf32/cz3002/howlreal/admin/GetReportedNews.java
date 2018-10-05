package com.wolf32.cz3002.howlreal.admin;

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


public class GetReportedNews extends AsyncTask<Void, Void, Void> {
    private static final String TAG = "GetReportedNews";
    String data;
    private FirebaseFirestore db;
    private com.wolf32.cz3002.howlreal.admin.GetReportedNews.RetrieveListener retrieveListener;
    private ArrayList<News> newsList = new ArrayList<>();

    public ArrayList<News> getNewsList() {
        return newsList;
    }

    public GetReportedNews(com.wolf32.cz3002.howlreal.admin.GetReportedNews.RetrieveListener retrieveListener) {
        this.retrieveListener = retrieveListener;
    }


    public void getData(String category, int userType) {
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
                                                assert mUrl != null;
                                                news.setNewsId(mUrl.replace("/", ""));
                                                Log.e(TAG, news.getUrl());
                                                newsList.add(news);
                                                Log.e(TAG, "added news");
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


}
