package com.wolf32.cz3002.howlreal.model;


public class UserPreferences {
    private boolean general;
    private boolean health;
    private boolean sports;
    private boolean science;
    private boolean technology;
    private boolean business;
    private boolean entertainment;

    public UserPreferences(){}

    public UserPreferences(boolean general, boolean health, boolean sports,
                           boolean science, boolean technology, boolean business, boolean entertainment){

        this.general = general;
        this.health = health;
        this.sports = sports;
        this.science = science;
        this.technology = technology;
        this.business = business;
        this.entertainment = entertainment;
    }

}
