package com.udacity.mrasulava.popularmovies.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.SerializedName;

/**
 * Created by mrasulava on 8/18/2015.
 */
public class Film implements Parcelable {

    @SerializedName("poster_path")
    private String posterPath;

    @SerializedName("original_title")
    private String originalTitle;

    @SerializedName("overview")
    private String overview;

    @SerializedName("vote_average")
    private double rating;

    @SerializedName("release_date")
    private String releaseDate;

    @SerializedName("video")
    private boolean hasVideo;

    public static final Creator<Film> CREATOR = new Creator<Film>() {
        public Film createFromParcel(Parcel in) {
            return new Film(in);
        }

        public Film[] newArray(int size) {
            return new Film[size];
        }
    };

    public Film(Parcel in) {
        this(in.readString(), in.readString(), in.readString(),
                in.readDouble(), in.readString(), in.readInt() == 1);
    }

    public Film(String posterPath, String originalTitle, String overview, double rating, String releaseDate, boolean hasVideo) {
        this.posterPath = posterPath;
        this.originalTitle = originalTitle;
        this.overview = overview;
        this.rating = rating;
        this.releaseDate = releaseDate;
        this.hasVideo = hasVideo;
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

    public boolean isHasVideo() {
        return hasVideo;
    }

    public void setHasVideo(boolean hasVideo) {
        this.hasVideo = hasVideo;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeString(posterPath);
        out.writeString(originalTitle);
        out.writeString(overview);
        out.writeDouble(rating);
        out.writeString(releaseDate);
        out.writeInt(hasVideo ? 1 : 0);
    }
}
