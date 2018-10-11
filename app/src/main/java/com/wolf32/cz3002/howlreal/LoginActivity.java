package com.wolf32.cz3002.howlreal;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

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
import com.google.gson.Gson;
import com.wolf32.cz3002.howlreal.model.BlacklistedNews;
import com.wolf32.cz3002.howlreal.model.User;
import com.wolf32.cz3002.howlreal.model.UserPreferences;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

    private int RC_SIGN_IN = 123;
    private FirebaseFirestore db;
    private FirebaseUser user;
    private User mUser = null;
    private Intent preferencesIntent;
    private Intent drawerIntent;
    private ProgressDialog nDialog;
    private boolean isBackPress = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(

                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build(),
                new AuthUI.IdpConfig.EmailBuilder().build()
                //new AuthUI.IdpConfig.PhoneBuilder().build(),

        );


        //receive intent data after logging in
        //mUser = (com.wolf32.cz3002.howlreal.model.User) getIntent().getSerializableExtra("user");
        //fromSettings = getIntent().getExtras().getBoolean("fromSettings");

        // Create and launch sign-in intent
        ///* // to disable login, uncomment.
            startActivityForResult(
                    AuthUI.getInstance()
                            .createSignInIntentBuilder()
                            .setLogo(R.mipmap.ic_logo)
                            .setTheme(R.style.LoginTheme)
                            .setAvailableProviders(providers)
                            .build(),
                    RC_SIGN_IN);
        //*/

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        preferencesIntent = new Intent(this, PreferencesActivity.class);
        drawerIntent = new Intent(this, MainActivity.class);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.e(TAG, "onActivityResult");

        ProgressBar progressBar = findViewById(R.id.progress_loader);
        progressBar.setVisibility(View.VISIBLE);
        nDialog = new ProgressDialog(this);
        nDialog.setMessage("Loading...");
        nDialog.setTitle("Logging in...");
        nDialog.setIndeterminate(false);
        nDialog.setCancelable(true);
        nDialog.show();


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
                mUser = new User(name, uid, email, photoUrl, emailVerified);

                // The user's ID, unique to the Firebase project. Do NOT use this value to
                // authenticate with your backend server, if you have one. Use
                // FirebaseUser.getIdToken() instead.
                // Log.e(TAG, "name: " + name);
                // Log.e(TAG, "email: " + email);
                // Log.e(TAG, "photoUrl: " + photoUrl);
                // Log.e(TAG, "uid: " + uid);
                // Log.e(TAG, "emailVerified: " + emailVerified);
                

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
                                Map<String, Object> data = document.getData();
                                boolean isHealth = (boolean) data.get("health");
                                boolean isSports = (boolean) data.get("sports");
                                boolean isScience = (boolean) data.get("science");
                                boolean isBusiness = (boolean) data.get("business");
                                boolean isTech = (boolean) data.get("technology");
                                boolean isEntertain = (boolean) data.get("entertainment");
                                UserPreferences preferences = new UserPreferences(isHealth,isSports,isScience,
                                                                                        isTech,isBusiness,isEntertain);
                                mUser.setPreferences(preferences);
                                startActivity(drawerIntent);
                                nDialog.dismiss();
                                finish();

                            }
                            else {
                                Log.d(TAG, "User not in database, adding new user..");

                                // create a new user to add to firestore cloud
                                db_user.put("name", mUser.getName());
                                db_user.put("email", mUser.getEmail());
                                db_user.put("photoUrl", mUser.getUri());
                                db_user.put("uid", mUser.getUid());
                                db_user.put("emailVerified", mUser.isEmailVerified());

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

                                startActivity(preferencesIntent);
                                nDialog.dismiss();
                                finish();

                            }
                            // save to shared preferences
                            Gson gson = new Gson();
                            String json = gson.toJson(mUser);
                            User.setDefaults(mUser.getUid(), json, getApplicationContext());
                            mUser.printUserInfo(TAG);

                        } else {
                            Log.d(TAG, "get failed with ", task.getException());

                        }
                    }
                });


            } else {
                if (response == null){
                    Log.e(TAG, "response == null");
                    // pressed back button, exit app
                    finish();
                    System.exit(0);
                }
                else{
                    Log.e(TAG, "Sign in failed! " + response.getError());

                }
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
            }


        }


    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Log.e(TAG, "onBackPressed");

        finish();
        System.exit(0);


    }

    //for auto login after opening app
    public void saveUserLocally(User mUser){
        // store to shared preferences
        Gson gson = new Gson();
        String json = gson.toJson(mUser);
        User.setDefaults(mUser.getUid(), json, this);
        mUser.printUserInfo(TAG);
    }


}
