package com.wolf32.cz3002.howlreal.model;

public class Admin {

    private String email = "admin@howlnews.com";

    public Admin(){

    }

    private boolean isAdmin(String email){
        return email.equals(this.email);
    }

}
