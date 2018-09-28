package com.wolf32.cz3002.howlreal.model;


import java.io.Serializable;

public class UserPreferences implements Serializable {
    private boolean health;
    private boolean sports;
    private boolean science;
    private boolean technology;
    private boolean business;
    private boolean entertainment;

    public UserPreferences() {

    }

    public UserPreferences(boolean health, boolean sports, boolean science,
                           boolean technology, boolean business, boolean entertainment) {

        this.health = health;
        this.sports = sports;
        this.science = science;
        this.technology = technology;
        this.business = business;
        this.entertainment = entertainment;
    }

    public boolean isHealth(){
        return health;
    }

    public boolean isSports() {
        return sports;
    }

    public boolean isBusiness() {
        return business;
    }

    public boolean isEntertainment() {
        return entertainment;
    }

    public boolean isScience() {
        return science;
    }

    public boolean isTechnology() {
        return technology;
    }

    public void setHealth(boolean health){
        this.health = health;
    }

    public void setBusiness(boolean business) {
        this.business = business;
    }

    public void setEntertainment(boolean entertainment) {
        this.entertainment = entertainment;
    }

    public void setScience(boolean science) {
        this.science = science;
    }

    public void setSports(boolean sports) {
        this.sports = sports;
    }

    public void setTechnology(boolean technology) {
        this.technology = technology;
    }
}
