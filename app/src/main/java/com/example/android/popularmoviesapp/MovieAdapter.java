package com.example.android.popularmoviesapp;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Lorenzo on 05/11/17.
 */

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.Holder>{

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private Context mContext;
    private List<Movie> mMovies;

    public MovieAdapter(List<Movie> movies) {
        this.mMovies = movies;

    }


    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        mContext = parent.getContext();


        View row = View.inflate(parent.getContext(), R.layout.movie_item, null);

        return new Holder(row);
    }

    @Override
    public void onBindViewHolder(Holder holder, int position) {

        final Movie currMovie = mMovies.get(position);

        Context mContext = holder.mPhoto.getContext();

        holder.mName.setText(currMovie.getTitle());
        String vote = Double.toString(mMovies.get(position).getVoteAverage());
        holder.mRating.setText(vote);

        String poster = "http://image.tmdb.org/t/p/w185" + currMovie.getPosterPath();
        Picasso.with(mContext)
                .load(poster)
                .placeholder(R.drawable.load2)
                .into(holder.mPhoto);

    }

    @Override
    public int getItemCount() {
        return mMovies!=null? mMovies.size():0;
    }

    public void addMovie(Movie movie) {

        Log.d(TAG, movie.getPosterPath());
        mMovies.add(movie);
        notifyDataSetChanged();

    }

    public void clearMovies() {
        mMovies.clear();
        notifyDataSetChanged();
    }

    public class Holder extends RecyclerView.ViewHolder{


        private ImageView mPhoto;
        private TextView mName;
        private TextView mRating;

        public Holder(View itemView) {
            super(itemView);
            mPhoto = (ImageView) itemView.findViewById(R.id.thumbnail);
            mName = (TextView) itemView.findViewById(R.id.title);
            mRating = (TextView) itemView.findViewById(R.id.movieRating);
            itemView.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v) {
                    int pos = getAdapterPosition();
                    if (pos != RecyclerView.NO_POSITION) {
                        Movie clickDataItem = mMovies.get(pos);
                        Intent intent = new Intent(mContext, DetailActivity.class);
                        intent.putExtra("MOVIE", clickDataItem);
                        //intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        mContext.startActivity(intent);
                        Toast.makeText(v.getContext(), "You clciked" + clickDataItem.getOriginalTitle(),
                                Toast.LENGTH_SHORT).show();
                    }
                }

                });
            }
        }


}
