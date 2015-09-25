package com.udacity.mrasulava.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.udacity.mrasulava.popularmovies.R;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.util.Utils;

import java.util.ArrayList;
import java.util.List;

public class MoviesAdapter extends BaseAdapter {
    private Context mContext;
    private List<Movie> movies = new ArrayList<>();

    public void setMovies(List<Movie> movies) {
        this.movies.clear();
        this.movies.addAll(movies);
    }

    public MoviesAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return movies.size();
    }

    public Movie getItem(int position) {
        return movies.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ImageView ivPoster;
        if (convertView == null) {
            ivPoster = (ImageView) LayoutInflater.from(mContext).inflate(R.layout.grid_item, null);
        } else {
            ivPoster = (ImageView) convertView;
        }
        Utils.loadImage(mContext, getItem(position), ivPoster);

        return ivPoster;
    }

}
