package com.example.android.popularmoviesapp;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by Lorenzo on 05/11/17.
 */

public interface MovieService {

    String API_KEY = "1f2f8438b87e9af5868a239428b0aa31";


    @GET("movie/{sort_criteria}?api_key=" + API_KEY)
    Call<MovieResponse> getAllMovies(@Path("sort_criteria") String sort_criteria );

    @GET("movie/{movie_id}/videos?api_key=" + API_KEY)
    Call<MoviesTrailer> getMoviesTrailer(@Path("movie_id") int id);

}
