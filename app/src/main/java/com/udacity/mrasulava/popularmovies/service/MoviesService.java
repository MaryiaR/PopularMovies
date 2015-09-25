package com.udacity.mrasulava.popularmovies.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.gson.Gson;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.model.Review;
import com.udacity.mrasulava.popularmovies.model.Trailer;
import com.udacity.mrasulava.popularmovies.util.MovieStorage;
import com.udacity.mrasulava.popularmovies.util.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

public class MoviesService extends IntentService {

    private static final String API_KEY = "<INSERT YOUR API KEY>";
    private static final String LOG_TAG = MoviesService.class.getSimpleName();

    private static final String BASE_URL = "http://api.themoviedb.org/3/";
    private static final String KEY_PARAM = "api_key";

    private static final String ACTION_LOAD_MOVIES = "load_movies";
    private static final String ACTION_LOAD_TRAILERS = "load_trailers";
    private static final String ACTION_LOAD_REVIEWS = "load_reviews";

    private MovieStorage movieStorage = MovieStorage.getInstance(this);

    public MoviesService() {
        super(LOG_TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null)
            return;

        final String action = intent.getAction();
        Movie movie = movieStorage.getSelectedMovie();
        long videoId = 0;
        if (movie != null)
            videoId = movie.getVideoId();

        if (ACTION_LOAD_MOVIES.equals(action)) {
            loadMovies();
        } else if (ACTION_LOAD_TRAILERS.equals(action)) {
            loadTrailers(videoId);
        } else if (ACTION_LOAD_REVIEWS.equals(action)) {
            loadReviews(videoId);
        }
    }

    private void loadReviews(long videoId) {
        if (videoId <= 0)
            return;

        String url = BASE_URL + "movie/" + videoId + "/reviews?";

        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();

        String jsonResponse = executeGetRequest(uri);
        if (jsonResponse != null)
            try {
                String filmsJson = new JSONObject(jsonResponse).getString("results");
                List<Review> reviews = Arrays.asList(new Gson().fromJson(filmsJson, Review[].class));
                if (movieStorage.getSelectedMovie() != null) {
                    movieStorage.setSelectedReviews(reviews);
                    sendBroadcast(new Intent(Utils.ACTION_REVIEWS_LOADED));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing reviews: " + e.getMessage());
                e.printStackTrace();
            }
    }

    private void loadTrailers(long videoId) {
        if (videoId <= 0)
            return;
        String url = BASE_URL + "movie/" + videoId + "/videos?";

        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();

        String jsonResponse = executeGetRequest(uri);
        if (jsonResponse != null)
            try {
                String filmsJson = new JSONObject(jsonResponse).getString("results");
                List<Trailer> trailers = Arrays.asList(new Gson().fromJson(filmsJson, Trailer[].class));
                if (movieStorage.getSelectedMovie() != null) {
                    movieStorage.setSelectedTrailers(trailers);
                    sendBroadcast(new Intent(Utils.ACTION_TRAILERS_LOADED));
                }
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing trailers: " + e.getMessage());
                e.printStackTrace();
            }
    }

    private void loadMovies() {
        String sortBy = Utils.getSortBy(this);

        final String url = BASE_URL + "discover/movie?";

        final String SORT_PARAM = "sort_by";
        final String KEY_PARAM = "api_key";

        Uri uri = Uri.parse(url).buildUpon()
                .appendQueryParameter(SORT_PARAM, sortBy)
                .appendQueryParameter(KEY_PARAM, API_KEY)
                .build();

        String jsonResponse = executeGetRequest(uri);
        if (jsonResponse != null)
            try {
                String filmsJson = new JSONObject(jsonResponse).getString("results");
                List<Movie> movies = Arrays.asList(new Gson().fromJson(filmsJson, Movie[].class));
                List<Movie> favoriteMovies = movieStorage.getFavoriteMovies();
                for (Movie movie : movies) {
                    if (favoriteMovies.contains(movie)) {
                        Movie favorite = favoriteMovies.get(favoriteMovies.indexOf(movie));
                        movie.setFavorite(true);
                        movie.setId(favorite.getId());
                    }
                }
                movieStorage.setMovies(movies);
                sendBroadcast(new Intent(Utils.ACTION_FILMS_LOADED));
            } catch (JSONException e) {
                Log.e(LOG_TAG, "Error parsing movies: " + e.getMessage());
                e.printStackTrace();
            }
    }

    private String executeGetRequest(Uri uri) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(uri.toString());
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder builder = new StringBuilder();
            if (inputStream == null) {
                return null;
            }
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line + "\n");
            }

            if (builder.length() == 0) {
                return null;
            }
            return builder.toString();
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error " + e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream " + e);
                }
            }
        }
    }

    public static void startLoadingMovies(Context context) {
        Intent intent = new Intent(context, MoviesService.class);
        intent.setAction(MoviesService.ACTION_LOAD_MOVIES);
        context.startService(intent);
    }

    public static void startLoadingTrailers(Context context) {
        Intent intent = new Intent(context, MoviesService.class);
        intent.setAction(MoviesService.ACTION_LOAD_TRAILERS);
        context.startService(intent);
    }

    public static void startLoadingReviews(Context context) {
        Intent intent = new Intent(context, MoviesService.class);
        intent.setAction(MoviesService.ACTION_LOAD_REVIEWS);
        context.startService(intent);
    }


}
