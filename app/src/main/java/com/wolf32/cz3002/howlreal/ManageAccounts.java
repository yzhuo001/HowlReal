package com.wolf32.cz3002.howlreal;

import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.wolf32.cz3002.howlreal.model.User;

import java.util.ArrayList;

public class ManageAccounts {

    private static final String TAG = "ManageAccounts";
    private RetrieveListener retrieveListener;

    public ManageAccounts(RetrieveListener retrieveListener) {
        this.retrieveListener = retrieveListener;
    }

    public interface RetrieveListener {
        void onSuccess(ArrayList<User> newsList);

        void onFailure();
    }

    public void getAllAccounts() {

        final ArrayList<User> userList = new ArrayList<>();

        // Access a Cloud Firestore instance from your Activity
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                String name = document.getString("name");
                                String email = document.getString("email");
                                String photoUrl = document.getString("photoUrl");
                                String uid = document.getString("uid");
                                boolean emailVerified = document.getBoolean("emailVerified");
                                User currUser = new User(name,uid,email,photoUrl,emailVerified);
                                // add retrieved user into list
                                userList.add(currUser);

                            }

                            Log.e(TAG, "Retrieve users added all");

                            if (userList.size() != 0) {
                                Log.e(TAG, "userList size != 0");

                                retrieveListener.onSuccess(userList);
                            }

                            Log.e(TAG, "Retrieve users ended.");

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            retrieveListener.onFailure();
                        }
                    }
                });
    }

}

