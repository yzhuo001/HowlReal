package com.wolf32.cz3002.howlreal.model;

public class User {
    private String username;
    private String uid;
    private String email;
    private String uri;
    private boolean emailVerified;
    private UserPreferences preferences;

    public User() {
    //empty constructor
    }

    public User(String username, String uid, String email, String uri, boolean emailVerified) {
        this.username = username;
        this.uid = uid;
        this.email = email;
        this.uri = uri;
        this.emailVerified = emailVerified;
    }

    public String getName(){
        return username;
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


}
