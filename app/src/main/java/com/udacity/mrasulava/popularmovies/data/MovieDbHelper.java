package com.udacity.mrasulava.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.RuntimeExceptionDao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.model.Review;
import com.udacity.mrasulava.popularmovies.model.Trailer;

import java.sql.SQLException;

public class MovieDbHelper extends OrmLiteSqliteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movies.db";

    private RuntimeExceptionDao<Movie, Integer> mMovieDao;

    private RuntimeExceptionDao<Trailer, Integer> mTrailerDao;

    private RuntimeExceptionDao<Review, Integer> mReviewDao;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Movie.class);
            TableUtils.createTable(connectionSource, Trailer.class);
            TableUtils.createTable(connectionSource, Review.class);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize database", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Movie.class, true);
            TableUtils.dropTable(connectionSource, Trailer.class, true);
            TableUtils.dropTable(connectionSource, Review.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

     public RuntimeExceptionDao<Movie, Integer> getMovieDao() {
        if (mMovieDao == null) {
            mMovieDao = getRuntimeExceptionDao(Movie.class);
        }
        return mMovieDao;
    }

    public RuntimeExceptionDao<Trailer, Integer> getTrailerDao() {
        if (mTrailerDao == null) {
            mTrailerDao = getRuntimeExceptionDao(Trailer.class);
        }
        return mTrailerDao;
    }

    public RuntimeExceptionDao<Review, Integer> getReviewDao() {
        if (mReviewDao == null) {
            mReviewDao = getRuntimeExceptionDao(Review.class);
        }
        return mReviewDao;
    }

}

