package com.udacity.mrasulava.popularmovies.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.dao.ForeignCollection;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.field.ForeignCollectionField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;
import com.udacity.mrasulava.popularmovies.data.MovieContract.MovieEntry;

import java.util.ArrayList;
import java.util.List;


@DatabaseTable(tableName = MovieEntry.TABLE_NAME)
public class Movie {

    @DatabaseField(columnName = MovieEntry._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private transient int id;

    @SerializedName("id")
    @DatabaseField(columnName = MovieEntry.COLUMN_VIDEO_ID)
    private long videoId;

    @SerializedName("poster_path")
    @DatabaseField(columnName = MovieEntry.COLUMN_POSTER_PATH)
    private String posterPath;

    @SerializedName("original_title")
    @DatabaseField(columnName = MovieEntry.COLUMN_ORIGINAL_TITLE)
    private String originalTitle;

    @DatabaseField(columnName = MovieEntry.COLUMN_OVERVIEW)
    private String overview;

    @SerializedName("vote_average")
    @DatabaseField(columnName = MovieEntry.COLUMN_RATING)
    private double rating;

    @SerializedName("release_date")
    @DatabaseField(columnName = MovieEntry.COLUMN_RELEASE_DATE)
    private String releaseDate;

    @ForeignCollectionField
    private ForeignCollection<Trailer> trailers;

    @ForeignCollectionField
    private ForeignCollection<Review> reviews;

    private boolean isFavorite;

    public Movie() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public long getVideoId() {
        return videoId;
    }

    public void setVideoId(long videoId) {
        this.videoId = videoId;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public void setPosterPath(String posterPath) {
        this.posterPath = posterPath;
    }

    public String getOriginalTitle() {
        return originalTitle;
    }

    public void setOriginalTitle(String originalTitle) {
        this.originalTitle = originalTitle;
    }

    public String getOverview() {
        return overview;
    }

    public void setOverview(String overview) {
        this.overview = overview;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public ForeignCollection<Trailer> getTrailers() {
        return trailers;
    }

    public ArrayList<Trailer> getTrailersAsList() {
        return trailers == null ? null : new ArrayList<Trailer>(trailers);
    }

    public ForeignCollection<Review> getReviews() {
        return reviews;
    }

    public List<Review> getReviewsAsList() {
        return reviews == null ? null : new ArrayList<Review>(reviews);
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Movie movie = (Movie) o;

        return videoId == movie.videoId;

    }

    @Override
    public int hashCode() {
        return (int) (videoId ^ (videoId >>> 32));
    }
}
