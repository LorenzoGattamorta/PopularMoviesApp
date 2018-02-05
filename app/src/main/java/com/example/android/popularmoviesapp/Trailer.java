package com.example.android.popularmoviesapp;

import com.google.gson.annotations.SerializedName;

/**
 * Created by Lorenzo on 28/12/17.
 */

public class Trailer {
    @SerializedName("name")
    private String name;
    @SerializedName("key")
    private String key;


    public Trailer(String key, String name){
        this.key = key;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
