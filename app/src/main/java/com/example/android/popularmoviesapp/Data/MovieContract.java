package com.example.android.popularmoviesapp.Data;

import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by Lorenzo on 29/12/17.
 */

public class MovieContract {

    public static final String AUTHORITY = "com.example.android.popularmoviesapp";

    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + AUTHORITY);

    public static final String PATH_FAVOURITE = "favorites";


    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_FAVOURITE).build();

        public static final String TABLE_NAME = "favorites";

        public static final String COLUMN_NAME = "movieName";
        public static final String COLUMN_POSTER_PATH = "posterpath";
        public static final String COLUMN_RATE = "movieRate";
        public static final String COLUMN_ID = "movieID";
        public static final String COLUMN_SYNOPSIS = "overview";


    }

}