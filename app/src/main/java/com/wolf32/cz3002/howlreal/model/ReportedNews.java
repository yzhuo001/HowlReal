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

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

public class ReportedNews implements Serializable {
    private static final String TAG = "ReportedNews";
    private String newsId;
    private String userId;

    public ReportedNews(String newsId, String userId){
        this.newsId=newsId;
        this.userId=userId;
    }

    public void addToDatabase(){
        // todo: check if exists in database using url.
        // if exists, dont add, if doesnt exists add.

        // Access a Cloud Firestore instance from your Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);


        DocumentReference docRef = db.collection("reportedNews").document(newsId);
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
                        Log.d(TAG, "User not in database, adding new user..");

                        // create a new user to add to firestore cloud
                        db_news.put("newsId", newsId);
                        db_news.put("userId", userId);

                        // add a new document with a generated ID
                        db.collection("reportedNews").document(newsId)
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
