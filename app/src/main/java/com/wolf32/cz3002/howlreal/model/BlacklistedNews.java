package com.wolf32.cz3002.howlreal.model;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class BlacklistedNews {
    private static final String TAG = "BlacklistedNews";
    private String newsID;

    public BlacklistedNews(){
    }


    public void setNewsID(String newsID) {
        this.newsID = newsID;
    }

    public String getNewsID() {
        return newsID;
    }

    public void addToDatabase() {

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        // add a new document with a generated ID
        db.collection("blacklistedNews").document(newsID)
                .set(this)
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


    public ArrayList<String> getBlacklistedNews(){
        final ArrayList<String> blackListedNews = new ArrayList<>();

        // Access a Cloud Firestore instance from your Activity
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        final boolean[] oktoreturn = {false};
        db.collection("blacklistedNews").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        document.getString("newsID");
                        blackListedNews.add(document.getString("newsID"));
                    }
                    Log.e(TAG, "blacklisted list" + blackListedNews.toString());
                    oktoreturn[0] = true;
                } else {
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });

        while(true){
            if (oktoreturn[0])
                break;
        }

        return blackListedNews;

    }


}
