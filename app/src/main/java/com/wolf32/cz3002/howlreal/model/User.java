package com.wolf32.cz3002.howlreal.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import java.io.Serializable;

public class User implements Serializable {
    private String name;
    private String uid;
    private String email;
    private String uri;
    private boolean emailVerified;
    private UserPreferences preferences;
    private int type; // 0- admin, 1-user

    public User() {
    //empty constructor
    }

    public User(String name, String uid, String email, String uri, boolean emailVerified) {
        this.name = name;
        this.uid = uid;
        this.email = email;
        this.uri = uri;
        this.emailVerified = emailVerified;
    }

    public int getType(){
        return type;
    }
    public String getName(){
        return name;
    }

    public String getUid(){
        return uid;
    }

    public String getEmail(){
        return email;
    }

    public String getUri(){
        return uri;
    }

    public boolean isEmailVerified(){
        return emailVerified;
    }

    public UserPreferences getPreferences() {
        return preferences;
    }

    public void setType(int type){
        this.type = type;
    }

    public void setPreferences(UserPreferences preferences) {
        this.preferences = preferences;
    }

    public static void setDefaults(String key, String value, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, value);
        editor.apply();
    }

    public static String getDefaults(String key, Context context) {
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        return preferences.getString(key, null);
    }

    public void printUserInfo(String TAG) {

            Log.e(TAG, "name: " + name);
            Log.e(TAG, "email: " + email);
            Log.e(TAG, "photoUrl: " + uri);
            Log.e(TAG, "uid: " + uid);
            Log.e(TAG, "emailVerified: " + emailVerified);

            if (preferences != null) {
                Log.e(TAG, "isHealth: " + preferences.isHealth());
                Log.e(TAG, "isSports: " + preferences.isSports());
                Log.e(TAG, "isScience: " + preferences.isScience());
                Log.e(TAG, "isTech: " + preferences.isTechnology());
                Log.e(TAG, "isBusiness: " + preferences.isBusiness());
                Log.e(TAG, "isEntertain: " + preferences.isEntertainment());

            }
            else {
                Log.e(TAG, "preferences == null");

            }


    }

    public boolean isAdmin(){
        String adminEmails[] = new String[]{"admin@howlreal.com", "a@g.com", "zyuezheng@gmail.com"};
        for (String adminEmail : adminEmails) {
            if (email.equals(adminEmail)) {
                return email.equals(adminEmail);
            }
        }

        return false;
    }
}
