package com.wolf32.cz3002.howlreal.model;


import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.gson.Gson;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class News implements Serializable {
    private static final String TAG = "News";
    private String title;
    private String imageUrl;
    private String sourceName;
    private String author;
    private String description;
    private String content;
    private String publishedAt;
    private String newsUrl;
    private String newsId;

    public News(){

    }

    public void setImageUrl(String imageUrl){
        this.imageUrl = imageUrl;
    }

    public void setTitle(String title){
        this.title = title;
    }

    public void setSourceName(String sourceName){
        this.sourceName = sourceName;
    }

    public void setAuthor(String author){
        this.author = author;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setPublishedAt(String publishedAt){
        this.publishedAt = publishedAt;
    }

    public void setUrl(String url){
        this.newsUrl = url;
    }

    public void setNewsId(String url){
        this.newsId = url;
    }

    public String getAuthor(){
        return author;
    }

    public String getDescription(){
        return description;
    }

    public String getPublishedAt(){
        return publishedAt;
    }

    public String getContent(){
        return content;
    }

    public String getUrl(){
        return newsUrl;
    }

    public String getSourceName(){
        return sourceName;
    }

    public String getTitle(){
        return title;
    }

    public String getImageUrl(){
        return imageUrl;
    }

    public String getNewsId(){
        return newsId;
    }

    public void addToDatabase(final String url){
        // todo: check if exists in database using url.
        // if exists, dont add, if doesnt exists add.

        // Access a Cloud Firestore instance from your Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        newsId = newsId.replace("/","");
        final String finalNewsId = newsId;

        DocumentReference docRef = db.collection("news").document(newsId);
        final Map<String, Object> db_news = new HashMap<>();

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        /*
                        Map<String, Object> data = document.getData();

                        title = (String) data.get("title");
                        imageUrl = (String) data.get("imageUrl");
                        sourceName = (String) data.get("sourceName");
                        author = (String) data.get("author");
                        description = (String) data.get("description");
                        content = (String) data.get("content");
                        newsUrl = (String) data.get("url");
                        publishedAt = (String) data.get("publishedAt");
                        */

                    }
                    else {
                        Log.d(TAG, "News not in database, adding new entry..");

                        // create a new user to add to firestore cloud
                        db_news.put("title", title);
                        db_news.put("imageUrl", imageUrl);
                        db_news.put("sourceName", sourceName);
                        db_news.put("author", author);
                        db_news.put("description", description);
                        db_news.put("content", content);
                        db_news.put("publishedAt", publishedAt);
                        db_news.put("url", newsUrl);

                        // add a new document with a generated ID
                        db.collection("news").document(finalNewsId)
                                .set(db_news)
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.e(TAG, "Success ");

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e(TAG, "Error adding news", e);
                                    }
                                });



                    }


                } else {
                    Log.d(TAG, "get failed with ", task.getException());

                }
            }
        });

    }

}
