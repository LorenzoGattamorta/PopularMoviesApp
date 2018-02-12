package com.example.android.popularmoviesapp.Adapter;

import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.android.popularmoviesapp.Data.MovieContract;
import com.example.android.popularmoviesapp.R;
import com.squareup.picasso.Picasso;

/**
 * Created by Lorenzo on 12/02/18.
 */

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.MyViewHolder> {

    private Cursor mCursor;
    private final Context mContext;

    public FavoritesAdapter(Context mContext){
        this.mContext = mContext;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(mContext).inflate(R.layout.movie_item, parent, false );

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {

        int posterPath = mCursor.getColumnIndex(MovieContract.MovieEntry.COLUMN_POSTER_PATH);

        mCursor.moveToPosition(position);

        String poster = mCursor.getString(posterPath);

        Picasso.with(mContext)
                .load(poster)
                .placeholder(R.drawable.load2)
                .into(holder.mPhoto);

    }

    @Override
    public int getItemCount() {
        if (mCursor == null){
            return 0;
        }
        return mCursor.getCount();
    }

    public Cursor swapCursor(Cursor c){
        if (mCursor == c){
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c!= null){
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        final ImageView mPhoto;


        public MyViewHolder(View view) {
            super(view);
            mPhoto = (ImageView) view.findViewById(R.id.thumbnail);
        }
    }
}
