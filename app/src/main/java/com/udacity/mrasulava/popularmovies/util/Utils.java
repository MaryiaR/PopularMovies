package com.udacity.mrasulava.popularmovies.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.udacity.mrasulava.popularmovies.R;
import com.udacity.mrasulava.popularmovies.model.Movie;

import java.io.File;
import java.io.FileOutputStream;

/**
 * Created by mrasulava on 8/21/2015.
 */
public class Utils {

    public static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

    public static final String ACTION_FILMS_LOADED = "films_loaded";

    public static final String ACTION_TRAILERS_LOADED = "trailers_loaded";

    public static final String ACTION_REVIEWS_LOADED = "reviews_loaded";

    public static boolean haveInternetConnection(Context context) {
        ConnectivityManager manager = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    public static String getSortBy(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(context.getString(R.string.pref_key_sort_by),
                context.getString(R.string.pref_sort_by_default));
    }

    public static void watchYoutubeVideo(Context context, String key) {
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + key));
        String title = context.getResources().getString(R.string.chooser_title);
        Intent chooser = Intent.createChooser(intent, title);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(chooser);
        }
    }

    public static void storeImage(Context context, final Movie movie) {
        Target target = new Target() {
            @Override
            public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        File file = new File(Environment.getExternalStorageDirectory().getPath()
                                + "/" + movie.getOriginalTitle() + ".jpg");
                        try {
                            file.createNewFile();
                            FileOutputStream ostream = new FileOutputStream(file);
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                            ostream.close();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            }

            @Override
            public void onBitmapFailed(Drawable errorDrawable) {
            }

            @Override
            public void onPrepareLoad(Drawable placeHolderDrawable) {
            }
        };
        Picasso.with(context).load(POSTER_BASE_PATH + movie.getPosterPath()).into(target);
    }

    public static void loadImage(Context context, final Movie movie, ImageView imageView) {
        if (movie.isFavorite() && !Utils.haveInternetConnection(context)) {
            Uri uri = Uri.fromFile(new File(Environment.getExternalStorageDirectory().getPath()
                    + "/" + movie.getOriginalTitle() + ".jpg"));
            Picasso.with(context).load(uri).into(imageView);
        } else {
            Picasso.with(context).load(POSTER_BASE_PATH + movie.getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_error)
                    .into(imageView);
        }
    }
}
