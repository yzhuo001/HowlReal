package com.wolf32.cz3002.howlreal;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
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
import com.wolf32.cz3002.howlreal.model.User;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {
    private static final String TAG = "LoginActivity";

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
    private Switch btn_general;
    private Switch btn_health;
    private Switch btn_sports;
    private Switch btn_science;
    private Switch btn_technology;
    private Switch btn_business;
    private Switch btn_entertainment;
    private Button btn_done;
    private TextView textView_choose;

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

        // Create and launch sign-in intent
        ///* //to disable login, uncomment.
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
                                startActivity(drawerIntent);

                            }
                            else {
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

                                // new user select preferences of news for reading
                                initPreferencesButtons();
                                setupButtonsListener();
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


        }


    }

    public void initPreferencesButtons(){
        btn_health = findViewById(R.id.switch_health);
        btn_sports = findViewById(R.id.switch_sports);
        btn_science = findViewById(R.id.switch_science);
        btn_technology = findViewById(R.id.switch_tech);
        btn_business = findViewById(R.id.switch_business);
        btn_entertainment = findViewById(R.id.switch_entertainment);
        btn_done = findViewById(R.id.btn_done);
        textView_choose = findViewById(R.id.textView_choose);
        btn_health.setVisibility(View.VISIBLE);
        btn_sports.setVisibility(View.VISIBLE);
        btn_science.setVisibility(View.VISIBLE);
        btn_technology.setVisibility(View.VISIBLE);
        btn_business.setVisibility(View.VISIBLE);
        btn_entertainment.setVisibility(View.VISIBLE);
        btn_done.setVisibility(View.VISIBLE);
        textView_choose.setVisibility(View.VISIBLE);

    }

    public void setupButtonsListener(){

        btn_health.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isHealth = isChecked;
            }
        });

        btn_sports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isHealth = isChecked;
            }
        });
        btn_science.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isScience = isChecked;
            }
        });
        btn_technology.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isTech = isChecked;
            }
        });
        btn_business.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isBusiness = isChecked;
            }
        });
        btn_entertainment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                isEntertain = isChecked;
            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save input to database
                final Map<String, Object> categories = new HashMap<>();
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
