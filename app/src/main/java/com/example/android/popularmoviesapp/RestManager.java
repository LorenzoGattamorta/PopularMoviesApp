package com.example.android.popularmoviesapp;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Lorenzo on 05/11/17.
 */

public class RestManager {

    private MovieService mMovieService;

    public MovieService getMovieService() {
        if(mMovieService == null) {

            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(Constants.HTTP.BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(new OkHttpClient.Builder()
                            .addInterceptor(new HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY))
                            .build())
                    .build();

            mMovieService = retrofit.create(MovieService.class);

        }

        return mMovieService;
    }
}
