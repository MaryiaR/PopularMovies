package com.udacity.mrasulava.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

public class DetailsActivity extends ActionBarActivity implements FilmsFragment.OnStateChangedListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        if (savedInstanceState == null) {

            getSupportFragmentManager().beginTransaction()
                    .add(R.id.movie_detail_container, new DetailsFragment())
                    .commit();
        }
    }

    @Override
    public void onFilmSelected() {

    }

    @Override
    public void updateDetailView() {

    }

    @Override
    public void onFavoriteRemoved() {

    }
}