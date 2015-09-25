package com.udacity.mrasulava.popularmovies.model;

import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.tojc.ormlite.android.annotation.AdditionalAnnotation;
import com.udacity.mrasulava.popularmovies.data.MovieContract;

/**
 * Created by mrasulava on 9/24/2015.
 */
@DatabaseTable(tableName = MovieContract.TrailerEntry.TABLE_NAME)
@AdditionalAnnotation.DefaultContentUri(authority = MovieContract.CONTENT_AUTHORITY, path = MovieContract.TrailerEntry.CONTENT_URI_PATH)
public class Trailer {

    @DatabaseField(columnName = MovieContract.TrailerEntry._ID, generatedId = true)
    @AdditionalAnnotation.DefaultSortOrder
    private transient int id;

    @DatabaseField(foreign = true, foreignAutoCreate = true, columnName = MovieContract.ReviewEntry.COLUMN_MOVIE)
    private Movie movie;

    @DatabaseField(columnName = MovieContract.TrailerEntry.COLUMN_VIDEO_KEY)
    private String key;

    @DatabaseField(columnName = MovieContract.TrailerEntry.COLUMN_NAME)
    private String name;

    public Trailer() {

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Movie getMovie() {
        return movie;
    }

    public void setMovie(Movie movie) {
        this.movie = movie;
    }

}
