package com.example.android.popularmoviesapp.UI;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.popularmoviesapp.Data.MovieContract;
import com.example.android.popularmoviesapp.Data.MovieDBHelper;
import com.example.android.popularmoviesapp.Movie;
import com.example.android.popularmoviesapp.MovieAdapter;
import com.example.android.popularmoviesapp.MovieResponse;
import com.example.android.popularmoviesapp.R;
import com.example.android.popularmoviesapp.RestManager;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.example.android.popularmoviesapp.R.id.recyclerView;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private RecyclerView mRecyclerView;
    private RestManager mManager;
    private List<Movie> mMovieList;
    private MovieAdapter mMovieAdapter;
    private MovieDBHelper movieDBHelper;
    private AppCompatActivity activity = MainActivity.this;
    private static final String MOVIES_EXTRAS = "MOVIES_EXTRAS";
    private static final String ORDER_EXTRAS = "ORDER_EXTRAS";
    private static final int MOVIE_LOADER_ID = 1;
    private String sortBy = "top_rated";
    private ArrayList<Movie> arrMovies;
    private static final int FAV_LOADER_ID = 3;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        configViews();

        mManager = new RestManager();


        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        //String sortBy = sharedPref.getString("SORT_CRITERION_KEY", "top_rated");

    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies(sortBy);
    }

    @Override
    protected void onSaveInstanceState(Bundle savedInstanceState) {
        if (sortBy != null && !sortBy.isEmpty()) {
            savedInstanceState.putString("sort_criteria", sortBy);
        }
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("sort_criteria")) {
                sortBy = savedInstanceState.getString("sort_criteria");
                Log.e("sort_criteria", "->" + sortBy);
            }
        }
    }

    private void loadMovies(String sort_criteria) {

        Call<MovieResponse> listCall = mManager.getMovieService().getAllMovies(sort_criteria);
        listCall.enqueue(new Callback<MovieResponse>() {
            @Override
            public void onResponse(Call<MovieResponse> call, Response<MovieResponse> response) {

                if (response.isSuccessful()) {
                    List<Movie> movieList = response.body().getMovieList();

                    mMovieAdapter.clearMovies();

                    for (int i = 0; i < movieList.size(); i++) {
                        Movie movie = movieList.get(i);
                        mMovieAdapter.addMovie(movie);

                    }
                } else {
                }
            }

            @Override
            public void onFailure(Call<MovieResponse> call, Throwable t) {

            }
        });

    }

    public Activity getActivity() {
        Context context = this;
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void configViews() {
        mRecyclerView = (RecyclerView) findViewById(recyclerView);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setRecycledViewPool(new RecyclerView.RecycledViewPool());
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.VERTICAL, false));

        mMovieList = new ArrayList<>();
        mMovieAdapter = new MovieAdapter(mMovieList);

        mRecyclerView.setAdapter(mMovieAdapter);

        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
        } else {
            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
        }

        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setAdapter(mMovieAdapter);
        mMovieAdapter.notifyDataSetChanged();
        movieDBHelper = new MovieDBHelper(activity);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {


        switch (item.getItemId()) {

            case R.id.popular:
                item.setChecked(!item.isChecked());
                sortBy = "popular";
                loadMovies(sortBy);

                break;
            case R.id.top_rated:
                item.setChecked(!item.isChecked());
                sortBy = "top_rated";
                loadMovies(sortBy);
                break;
            case R.id.favorite:
                item.setChecked(!item.isChecked());
                sortBy = "favorites";

                getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI,
                        null,
                        null,
                        null,
                        MovieContract.MovieEntry.COLUMN_ID);
                mMovieAdapter.clearMovies();
                break;
        }
        return super.onOptionsItemSelected(item);

    }

//    private void queryData() {
//        new DataQueryTask().execute(MovieContract.MovieEntry.CONTENT_URI);
//    }
//
//    public class DataQueryTask extends AsyncTask<Uri, Void, ArrayList<Movie>> {
//
//        @Override
//        protected void onPreExecute(){
//            super.onPreExecute();
//        }
//
//
//        @Override
//        protected ArrayList<Movie> doInBackground(Uri... params) {
//            Cursor data = getBaseContext().getContentResolver().query(params[0], null, null, null, null);
//            int titleData = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_NAME);
//            int overviewData = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_SYNOPSIS);
//            int ratingData = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_RATE);
//            int posterpathData = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);
//            int idData = data.getColumnIndex(MovieContract.MovieEntry.COLUMN_ID);
//
//            ArrayList<Movie> movies = new ArrayList<>();
//            while (data.moveToNext()){
//                String title = data.getString(titleData);
//                String overview = data.getString(overviewData);
//                double rate = data.getDouble(ratingData);
//                int id = data.getInt(idData);
//                String posterpath = data.getString(posterpathData);
//
//                movies.add(new Movie(posterpath, overview, title, id, rate));
//            }
//
//            arrMovies = movies;
//            return arrMovies;
//
//        }
//
//        @Override
//        protected void onPostExecute(ArrayList<Movie> movies){
//            if (movies != null){
//                Log.d(TAG, "No films");
//                if (mMovieAdapter == null){
//                    mMovieAdapter = new MovieAdapter(arrMovies);
//                } else {
//                    mMovieAdapter.notifyDataSetChanged();
//                }
//            } else {
//                Log.d(TAG, "No films");
//            }
//        }
//
//    };


    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        return new AsyncTaskLoader<Cursor>(getApplicationContext()) {

            Cursor fav = null;

            @Override
            protected void onStartLoading() {
                if (fav != null) {
                    deliverResult(fav);
                } else {
                    forceLoad();
                }

            }

            @Override
            public Cursor loadInBackground() {
                try {
                    return getContentResolver().query(
                            MovieContract.MovieEntry.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieEntry.COLUMN_ID
                    );
                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }

            @Override
            public void deliverResult(Cursor data) {
                fav = data;
                super.deliverResult(data);
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {


    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){

    }

}


//    private void favoriteLoader() {
//        ConnectivityManager cm = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        final NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
//        if (activeNetwork != null && activeNetwork.isConnectedOrConnecting()) {
//
//
//            getSupportLoaderManager().initLoader(FAV_LOADER_ID, null, favLoaders).forceLoad();
//        } else {
//            Toast.makeText(this, getString(R.string.internet), Toast.LENGTH_SHORT).show();
//        }
//    }


//    private ArrayList<Movie> LoadFavoriteDB(){
//        Cursor cursor = getContentResolver().query(MovieContract.MovieEntry.CONTENT_URI, null, null, null, null);
//
//        int faMovie = cursor.getCount();
//
//        Movie movie;
//
//        ArrayList<Movie> movieToAdapter = new ArrayList<>(faMovie);
//        for (int i = 0; cursor.moveToNext(); i++){
//            movie = new Movie();
//
//
//            movieToAdapter.add(i, movie);
//        }
//
//        cursor.close();
//
//        return movieToAdapter;
//    }

////    private void loadFavorite() {
////       mRecyclerView = (RecyclerView) findViewById(recyclerView);
////        mMovieList = new ArrayList<>();
////        mMovieAdapter = new MovieAdapter(mMovieList);
////
////        if (getActivity().getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT){
////            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 2));
////        } else {
////            mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
////        }
////
////        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
////        mRecyclerView.setAdapter(mMovieAdapter);
////        mMovieAdapter.notifyDataSetChanged();
////        movieDBHelper = new MovieDBHelper(activity);
////
////        getAllFavorite();
////    }
////
////    private void getAllFavorite() {
////        new AsyncTask<Void, Void, Void>(){
////
////            @Override
////            protected Void doInBackground(Void... params) {
////                mMovieList.clear();
////                return null;
////            }
////
////            @Override
////            protected void onPostExecute(Void aVoid){
////                super.onPostExecute(aVoid);
////                mMovieAdapter.notifyDataSetChanged();
////            }
////        }.execute();
////    }
//
////    @Override
////    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
////
////        Log.d(TAG, "Preference updated");
////        LoadFavoriteDB();
////    }
////
////    @NonNull
////    private String getPreference() {
////        SharedPreferences shared = getSharedPreferences("sort", MODE_PRIVATE);
////        String pref = (shared.getString("sort_by", ""));
////        Log.v("Main", "value is: " + pref);
////        return pref;
////    }
//}