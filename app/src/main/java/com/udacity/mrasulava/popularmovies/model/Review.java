package com.udacity.mrasulava.popularmovies.model;

import com.google.gson.annotations.SerializedName;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;
import com.udacity.mrasulava.popularmovies.data.MovieContract;

@DatabaseTable(tableName = MovieContract.ReviewEntry.TABLE_NAME)
@AdditionalAnnotation.DefaultContentUri(authority = MovieContract.CONTENT_AUTHORITY, path = MovieContract.ReviewEntry.CONTENT_URI_PATH)
public class Review {

    @DatabaseField(columnName = MovieContract.ReviewEntry._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private transient int id;

    @DatabaseField(columnName = MovieContract.ReviewEntry.COLUMN_AUTHOR)
    @SerializedName("author")
    private String author;

    @DatabaseField(columnName = MovieContract.ReviewEntry.COLUMN_CONTENT)
    @SerializedName("content")
    private String content;

    @DatabaseField(foreign = true, foreignAutoCreate = true, columnName = MovieContract.ReviewEntry.COLUMN_MOVIE)
    private Movie movie;

    public Review() {

    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }
}
