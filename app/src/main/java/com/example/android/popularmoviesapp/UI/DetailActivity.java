package com.example.android.popularmoviesapp.UI;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.android.popularmoviesapp.Data.MovieContract;
import com.example.android.popularmoviesapp.Data.MovieDBHelper;
import com.example.android.popularmoviesapp.Movie;
import com.example.android.popularmoviesapp.MoviesTrailer;
import com.example.android.popularmoviesapp.R;
import com.example.android.popularmoviesapp.RestManager;
import com.example.android.popularmoviesapp.Trailer;
import com.example.android.popularmoviesapp.TrailerAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Lorenzo on 06/11/17.
 */

public class DetailActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor>{

    private TextView nameOfMovie, plotSynopsys, userRating, releaseDate;
    private ImageView mPosterPath;
    private RecyclerView recyclerView;
    private TrailerAdapter mTrailerAdapter;
    private List<Trailer> trailerList;
    private RestManager mManager;
    ToggleButton toggleButton;
    private MovieDBHelper movieDBHelper;
    private Movie favorite;
    private final AppCompatActivity activity = DetailActivity.this;
    Movie movie;
    private static final String TAG = DetailActivity.class.getSimpleName();
    private static final int FAVORITE_LOADER_ID = 0;




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        mManager = new RestManager();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        initCollapsingToolbar();


        mPosterPath = (ImageView) findViewById(R.id.thumbnail_image_header);
        nameOfMovie = (TextView) findViewById(R.id.titleFilm);
        plotSynopsys = (TextView) findViewById(R.id.plotSynopsis);
        userRating = (TextView) findViewById(R.id.userRating);
        releaseDate = (TextView) findViewById(R.id.releaseDate);

        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("MOVIE")) {

            movie = intentThatStartedThisActivity.getParcelableExtra("MOVIE");

            String poster = "https://image.tmdb.org/t/p/w185" + movie.getPosterPath();

            Picasso.with(getApplicationContext())
                    .load(poster)
                    .placeholder(R.drawable.load2)
                    .into(mPosterPath);

            nameOfMovie.setText(movie.getOriginalTitle());
            plotSynopsys.setText(movie.getOverview());
            userRating.setText(String.valueOf(movie.getVoteAverage()));
            releaseDate.setText(movie.getReleaseDate());

        } else {
            Toast.makeText(this, "No API data", Toast.LENGTH_SHORT).show();

        }

        initViews();

        toggleButton = (ToggleButton) findViewById(R.id.toggleButton);
        toggleButton.setChecked(false);
        toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star));
    }


    public void addFavorite(View view){

        String imageString = movie.getPosterPath();
        String favMovieId = movie.getId().toString();
        String favTitle = movie.getOriginalTitle();
        String favRate = String.valueOf(movie.getVoteAverage());
        String favOverview = movie.getOverview();

        long id = 0;
        if (favMovieId != null){
            id = Long.parseLong(favMovieId);
        }


        ContentValues contentValues = new ContentValues();
        contentValues.put(MovieContract.MovieEntry.COLUMN_ID, id);
        contentValues.put(MovieContract.MovieEntry.COLUMN_RATE, favRate);
        contentValues.put(MovieContract.MovieEntry.COLUMN_NAME, favTitle);
        contentValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, imageString);
        contentValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, favOverview);


        Uri uri = getContentResolver().insert(MovieContract.MovieEntry.CONTENT_URI, contentValues);

        if (uri != null){
            Toast.makeText(DetailActivity.this, "Added to favorites", Toast.LENGTH_LONG).show();
        }
        finish();
    }


    private void initCollapsingToolbar(){
        final CollapsingToolbarLayout collapsingToolbarLayout =
                (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle(" ");
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        appBarLayout.setExpanded(true);

        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener(){
            boolean isShow = false;
            int scrollRange = -1;

            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffSet) {
                if (scrollRange == -1) {
                    scrollRange = appBarLayout.getTotalScrollRange();
                }
                if (scrollRange + verticalOffSet == 0) {
                    collapsingToolbarLayout.setTitle(getString(R.string.details));
                    isShow = true;
                } else if (isShow) {
                    collapsingToolbarLayout.setTitle(" ");
                    isShow = false;
                }
            }
        });
    }

    private void initViews(){
        trailerList = new ArrayList<>();
        mTrailerAdapter = new TrailerAdapter(this, trailerList);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView2);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setAdapter(mTrailerAdapter);
        mTrailerAdapter.notifyDataSetChanged();

        loadJSON();
    }

    private void loadJSON() {

        if(movie!=null){


            Call<MoviesTrailer> call = mManager.getMovieService().getMoviesTrailer(movie.getId());
            call.enqueue(new Callback<MoviesTrailer>() {
                @Override
                public void onResponse(Call<MoviesTrailer> call, Response<MoviesTrailer> response) {
                    List<Trailer> trailer = response.body().getResults();
                    recyclerView.setAdapter(new TrailerAdapter(getApplicationContext(), trailer));
                    recyclerView.smoothScrollToPosition(0);
                }

                @Override
                public void onFailure(Call<MoviesTrailer> call, Throwable t) {
                    Log.d("Errore", t.getMessage());
                    Toast.makeText(DetailActivity.this, "error fetching trailer", Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading(){
                if (mTaskData != null){
                    deliverResult(mTaskData);

                } else{
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {
                    return getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_ID);
                } catch (Exception e){
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data){
                mTaskData = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

    }

    //    private void deleteFav(){
//        ContentResolver contentResolver = getContentResolver();
//        String selection = MovieContract.MovieEntry.COLUMN_ID + "=?";
//        String favId = movie.getId().toString();
//        long id = Long.parseLong(favId);
//        Log.v(TAG, "Movie id to be deleted");
//
//        String[] args = new String[]{
//                String.valueOf(ContentUris.parseId(uri))
//        }
//    }


//        toggleButton.setOnCheckedChangeListener(
//                new ToggleButton.OnCheckedChangeListener(){
//
//                    @Override
//                    public void onCheckedChanged(CompoundButton buttonView, boolean favorite) {
//                        if (favorite){
//                            SharedPreferences.Editor editor = getSharedPreferences("com.example.android.popularmoviesapp.UI.DetailActivity", MODE_PRIVATE).edit();
//                            editor.putBoolean("Favorite added", true);
//                            editor.commit();
////                            saveFavorite();
//                            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star));
//                            Snackbar.make(buttonView, "Added to favorite",
//                                    Snackbar.LENGTH_SHORT).show();
//                        } else {
//                            movieDBHelper = new MovieDBHelper(DetailActivity.this);
//                            String stringId = Integer.toString(id);
//                            Uri uri = MovieContract.MovieEntry.CONTENT_URI;
//                            uri = uri.buildUpon().appendPath(stringId).build();
//                            getContentResolver().delete(uri, null, null);
//                            toggleButton.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.star_copia));
//                            SharedPreferences.Editor editor = getSharedPreferences("com.example.android.popularmoviesapp.UI.DetailActivity", MODE_PRIVATE).edit();
//                            editor.putBoolean("Favorite removed", true);
//                            editor.commit();
//                            Snackbar.make(buttonView, "Removed from favorite",
//                                    Snackbar.LENGTH_SHORT).show();
//                        }
//                    }
//                }
//        );

//    public void saveFavorite() {
//        movieDBHelper = new MovieDBHelper(activity);
//        favorite = new Movie();
//            ContentValues movieValues = new ContentValues();
//            movieValues.put(MovieContract.MovieEntry.COLUMN_NAME, movie.getTitle());
//            movieValues.put(MovieContract.MovieEntry.COLUMN_POSTER_PATH, movie.getTitle());
//            movieValues.put(MovieContract.MovieEntry.COLUMN_RATE, movie.getVoteAverage());
//            movieValues.put(MovieContract.MovieEntry.COLUMN_ID, movie.getId());
//            movieValues.put(MovieContract.MovieEntry.COLUMN_SYNOPSIS, movie.getOverview());
//            ContentResolver moviesContentResolver = getContentResolver();
//
//            moviesContentResolver.insert(
//                    MovieContract.MovieEntry.CONTENT_URI,
//                    movieValues);
//            Toast.makeText(this, "Saved to favorites!", Toast.LENGTH_SHORT).show();
//        }
}