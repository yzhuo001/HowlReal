package com.wolf32.cz3002.howlreal;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.SetOptions;
import com.google.gson.Gson;
import com.wolf32.cz3002.howlreal.model.User;
import com.wolf32.cz3002.howlreal.model.UserPreferences;

import java.util.HashMap;
import java.util.Map;

public class PreferencesActivity extends AppCompatActivity {

    private Switch switch_health;
    private Switch switch_sports;
    private Switch switch_science;
    private Switch switch_technology;
    private Switch switch_business;
    private Switch switch_entertainment;
    private Button btn_done;
    private TextView textView_choose;
    private static final String TAG = "PreferencesActivity";
    private FirebaseFirestore db;
    private FirebaseUser user;
    private User mUser;
    private UserPreferences mPref;
    private Intent drawerIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preferences);

        // Access a Cloud Firestore instance from your Activity
        db = FirebaseFirestore.getInstance();

        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                .setTimestampsInSnapshotsEnabled(true)
                .build();
        db.setFirestoreSettings(settings);

        drawerIntent = new Intent(this, MainActivity.class);

        initButtons();
        setupButtonsListener();

    }


    public void initButtons() {
        switch_health = findViewById(R.id.switch_health);
        switch_sports = findViewById(R.id.switch_sports);
        switch_science = findViewById(R.id.switch_science);
        switch_technology = findViewById(R.id.switch_tech);
        switch_business = findViewById(R.id.switch_business);
        switch_entertainment = findViewById(R.id.switch_entertainment);
        btn_done = findViewById(R.id.btn_done);
        textView_choose = findViewById(R.id.textView_choose);

        // get user from FirebaseAuth
        user = FirebaseAuth.getInstance().getCurrentUser();
        String uid = "";
        if (user != null) {
            uid = user.getUid();

            // get shared preferences
            Gson gson = new Gson();
            String json = User.getDefaults(uid, this);
            mUser = gson.fromJson(json, User.class);
            mUser.printUserInfo(TAG);
        }

        // if not a new user,
        if (mUser != null) {
            Log.e(TAG, "existing user: name: " + mUser.getName());
            mPref = mUser.getPreferences();

            if (mPref != null) {
                Log.e(TAG, "mPref exists");
                switch_health.setChecked(mPref.isHealth());
                switch_sports.setChecked(mPref.isSports());
                switch_entertainment.setChecked(mPref.isEntertainment());
                switch_business.setChecked(mPref.isBusiness());
                switch_technology.setChecked(mPref.isTechnology());
                switch_science.setChecked(mPref.isScience());

            }
            else {
                Log.e(TAG, "mPref is null, not saved before on this phone");
                switch_health.setChecked(false);
                switch_entertainment.setChecked(false);
                switch_business.setChecked(false);
                switch_technology.setChecked(false);
                switch_science.setChecked(false);
                switch_sports.setChecked(false);
                mPref = new UserPreferences(false,false,false,
                                            false,false,false);
            }

        } else {
            Log.e(TAG, "mUser is null, error.");
        }


    }

    public void setupButtonsListener() {

        switch_health.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setHealth(isChecked);
                Log.e(TAG, "onChecked isHealth: " + mPref.isHealth());
            }
        });

        switch_sports.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setSports(isChecked);
                Log.e(TAG, "onChecked isSports: " + mPref.isSports());

            }
        });
        switch_science.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setScience(isChecked);
                Log.e(TAG, "isScience: " + mPref.isScience());

            }
        });
        switch_technology.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setTechnology(isChecked);
                Log.e(TAG, "isTech: " + mPref.isTechnology());

            }
        });
        switch_business.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setBusiness(isChecked);
                Log.e(TAG, "isBusiness: " + mPref.isBusiness());

            }
        });
        switch_entertainment.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // do something, the isChecked will be
                // true if the switch is in the On position
                mPref.setEntertainment(isChecked);
                Log.e(TAG, "isEntertain: " + mPref.isEntertainment());

            }
        });

        btn_done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //save input to database
                final Map<String, Object> categories = new HashMap<>();
                categories.put("health", mPref.isHealth());
                categories.put("sports", mPref.isSports());
                categories.put("science", mPref.isScience());
                categories.put("technology", mPref.isTechnology());
                categories.put("business", mPref.isBusiness());
                categories.put("entertainment", mPref.isEntertainment());

                db.collection("users").document(mUser.getUid())
                        .set(categories, SetOptions.merge());

                // add preferences to mUser
                mUser.setPreferences(mPref);
                drawerIntent.putExtra("user", mUser);

                // store to shared preferences
                Gson gson = new Gson();
                String json = gson.toJson(mUser);
                User.setDefaults(mUser.getUid(), json, btn_done.getContext());
                mUser.printUserInfo(TAG);

                startActivity(drawerIntent);
            }
        });
    }
}


