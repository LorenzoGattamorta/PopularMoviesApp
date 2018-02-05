package com.example.android.popularmoviesapp;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lorenzo on 28/12/17.
 */
public class MoviesTrailer {
    @SerializedName("id")
    @Expose
    private long id_trailer;
    @SerializedName("results")
    private List<Trailer> results;

    public Long getIdTrailer(){
        return id_trailer;
    }

    public void setIdTrailer(long id_trailer) {
        this.id_trailer = id_trailer;
    }

    public List<Trailer> getResults(){
        return results;
    }
}
