package com.udacity.mrasulava.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.udacity.mrasulava.popularmovies.util.Utils;


public class MainActivity extends ActionBarActivity implements FilmsFragment.OnStateChangedListener {

    private static final String DETAIL_FRAGMENT_TAG = DetailsFragment.class.getSimpleName();
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
            if (savedInstanceState == null) {
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.movie_detail_container, new DetailsFragment(), DETAIL_FRAGMENT_TAG)
                        .commit();
            }
        } else {
            mTwoPane = false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onFilmSelected() {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailsFragment(), DETAIL_FRAGMENT_TAG)
                    .commit();
        } else {
            Intent intent = new Intent(this, DetailsActivity.class);
            startActivity(intent);
        }
    }

    @Override
    public void updateDetailView() {
        if (mTwoPane) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, new DetailsFragment(), DETAIL_FRAGMENT_TAG)
                    .commit();
        }
    }

    @Override
    public void onFavoriteRemoved() {
        if (mTwoPane) {
            FilmsFragment filmsFragment = (FilmsFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_movies);
            if (null != filmsFragment && getString(R.string.pref_sort_favorite).equals(Utils.getSortBy(this)))
                filmsFragment.updateMovies();
        }
    }

}
