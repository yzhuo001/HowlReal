package com.wolf32.cz3002.howlreal;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.wolf32.cz3002.howlreal.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    private int RC_SIGN_IN = 123;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private User model_user;
    boolean isGeneral = false;
    boolean isHealth = false;
    boolean isSports = false;
    boolean isScience = false;
    boolean isBusiness = false;
    boolean isTech = false;
    boolean isEntertain = false;
    private Intent drawerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);


        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build()
        );

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        drawerIntent = new Intent(this, MainActivity.class);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Log.e(TAG, "RC_SIGN_IN");
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                Log.e(TAG, "RESULT_OK");
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();
                assert user != null;
                final String name = user.getDisplayName();
                final String uid = user.getUid();
                final String email = user.getEmail();

                String photoUrl = "";
                if (user.getPhotoUrl() != null)
                    photoUrl = user.getPhotoUrl().toString();
                final boolean emailVerified = user.isEmailVerified();
                model_user = new User(name, uid, email, photoUrl, emailVerified);

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
//                Log.e(TAG, "name: " + name);
//                Log.e(TAG, "email: " + email);
//                Log.e(TAG, "photoUrl: " + photoUrl);
//                Log.e(TAG, "uid: " + uid);
//                Log.e(TAG, "emailVerified: " + emailVerified);


                DocumentReference docRef = db.collection("users").document(uid);
                final Map<String, Object> db_user = new HashMap<>();
                docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                Log.d(TAG, "User exists in database, logged in success!");
                                Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            } else {
                                Log.d(TAG, "User not in database, adding new user..");

                                // create a new user to add to firestore cloud
                                db_user.put("name", model_user.getName());
                                db_user.put("email", model_user.getEmail());
                                db_user.put("photoUrl", model_user.getUri());
                                db_user.put("uid", model_user.getUid());
                                db_user.put("emailVerified", model_user.isEmailVerified());

                                // add a new document with a generated ID
                                db.collection("users").document(uid)
                                        .set(db_user)
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Log.e(TAG, "Success ");

                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.e(TAG, "Error adding document", e);
                                            }
                                        });
                            }
                        } else {
                            Log.d(TAG, "get failed with ", task.getException());

                        }
                    }
                });


            } else {
                Log.e(TAG, "Sign in failed! " + response.getError());
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }

            // select preferences of news for reading
            Button btn_general = findViewById(R.id.btn_general);
            Button btn_health = findViewById(R.id.btn_health);
            Button btn_sports = findViewById(R.id.btn_sports);
            Button btn_science = findViewById(R.id.btn_science);
            Button btn_technology = findViewById(R.id.btn_technology);
            Button btn_business = findViewById(R.id.btn_business);
            Button btn_entertainment = findViewById(R.id.btn_entertainment);
            Button btn_done = findViewById(R.id.btn_done);

            btn_general.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isGeneral = !isGeneral;
                }
            });
            btn_health.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isHealth = !isHealth;
                }
            });
            btn_sports.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isSports = !isSports;
                }
            });
            btn_science.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isScience = !isScience;
                }
            });
            btn_technology.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isTech = !isTech;
                }
            });
            btn_business.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isBusiness = !isBusiness;
                }
            });
            btn_entertainment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isEntertain = !isEntertain;
                }
            });

            btn_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //save input to database
                    final Map<String, Object> categories = new HashMap<>();
                    categories.put("general", isGeneral);
                    categories.put("health", isHealth);
                    categories.put("sports", isSports);
                    categories.put("science", isScience);
                    categories.put("technology", isTech);
                    categories.put("business", isBusiness);
                    categories.put("entertainment", isEntertain);


                    db.collection("users").document(model_user.getUid())
                            .set(categories, SetOptions.merge());

                    startActivity(drawerIntent);
                }
            });
        }


    }


}
