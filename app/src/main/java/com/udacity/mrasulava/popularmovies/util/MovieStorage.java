package com.udacity.mrasulava.popularmovies.util;

import android.content.Context;

import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.udacity.mrasulava.popularmovies.data.MovieDbHelper;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.model.Review;
import com.udacity.mrasulava.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class MovieStorage {

    private static MovieStorage INSTANCE;

    private volatile MovieDbHelper dBHelper;

    private List<Movie> movies = new ArrayList<>();

    private Movie selectedMovie;

    private List<Trailer> selectedTrailers = new ArrayList<>();

    private List<Review> selectedReviews = new ArrayList<>();

    private MovieStorage(Context context) {
        dBHelper = OpenHelperManager.getHelper(context, MovieDbHelper.class);
    }

    public static MovieStorage getInstance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new MovieStorage(context);
        }
        return INSTANCE;
    }

    public void saveFavorite() {
        selectedMovie.setFavorite(true);
        for (Trailer trailerItem : selectedTrailers)
            trailerItem.setMovie(selectedMovie);

        for (Review reviewItem : selectedReviews)
            reviewItem.setMovie(selectedMovie);

        dBHelper.getMovieDao().createOrUpdate(selectedMovie);

        for (Review review : selectedReviews)
            dBHelper.getReviewDao().createOrUpdate(review);

        for (Trailer trailer : selectedTrailers)
            dBHelper.getTrailerDao().createOrUpdate(trailer);
    }

    public List<Movie> getFavoriteMovies() {
        List<Movie> favoriteMovies = dBHelper.getMovieDao().queryForAll();
        if (favoriteMovies != null && !favoriteMovies.isEmpty()) {
            for (Movie movie : favoriteMovies)
                movie.setFavorite(true);
        }
        return favoriteMovies;
    }

    public List<Movie> getMovies() {
        return movies;
    }

    public void setMovies(List<Movie> movies) {
        this.movies = movies;
    }

    public Movie getSelectedMovie() {
        return selectedMovie;
    }

    public void setSelectedMovie(Movie selectedMovie) {
        this.selectedMovie = selectedMovie;
    }

    public List<Trailer> getSelectedTrailers() {
        return (selectedTrailers == null || selectedTrailers.isEmpty()) ? selectedMovie.getTrailersAsList() : selectedTrailers;
    }

    public void setSelectedTrailers(List<Trailer> selectedTrailers) {
        this.selectedTrailers = selectedTrailers;
    }

    public List<Review> getSelectedReviews() {
        return (selectedReviews == null || selectedReviews.isEmpty()) ? selectedMovie.getReviewsAsList() : selectedReviews;
    }

    public void setSelectedReviews(List<Review> selectedReviews) {
        this.selectedReviews = selectedReviews;
    }

    public void removeFavorite() {
        selectedMovie.setFavorite(false);
        dBHelper.getMovieDao().deleteById(selectedMovie.getId());
    }
}
