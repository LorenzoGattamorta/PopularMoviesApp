package com.example.android.popularmoviesapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Lorenzo on 14/11/17.
 */

public class MovieResponse implements Parcelable{
    @SerializedName("page")
    @Expose
    private Long page;
    @SerializedName("total_results")
    @Expose
    private Long totalResults;
    @SerializedName("total_pages")
    @Expose
    private Long totalPages;
    @SerializedName("results")
    @Expose
    private List<Movie> movieList = null;
    public final static Parcelable.Creator<MovieResponse> CREATOR = new Parcelable.Creator<MovieResponse>() {


        @SuppressWarnings({
                "unchecked"
        })
        public MovieResponse createFromParcel(Parcel in) {
            return new MovieResponse(in);
        }

        public MovieResponse[] newArray(int size) {
            return (new MovieResponse[size]);
        }

    }
            ;

    protected MovieResponse(Parcel in) {
        this.page = ((Long) in.readValue((Long.class.getClassLoader())));
        this.totalResults = ((Long) in.readValue((Long.class.getClassLoader())));
        this.totalPages = ((Long) in.readValue((Long.class.getClassLoader())));
        in.readList(this.movieList, (com.example.android.popularmoviesapp.Movie.class.getClassLoader()));
    }

    public MovieResponse() {
    }

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getTotalResults() {
        return totalResults;
    }

    public void setTotalResults(Long totalResults) {
        this.totalResults = totalResults;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public List<Movie> getMovieList() {
        return movieList;
    }

    public void setMovieList(List<Movie> results) {
        this.movieList = results;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(page);
        dest.writeValue(totalResults);
        dest.writeValue(totalPages);
        dest.writeList(movieList);
    }

    public int describeContents() {
        return 0;
    }

}

