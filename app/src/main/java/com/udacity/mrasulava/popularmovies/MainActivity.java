package com.udacity.mrasulava.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;

import com.udacity.mrasulava.popularmovies.model.Film;


public class MainActivity extends ActionBarActivity implements FilmsFragment.OnStateChangedListener {

    public static final String POSTER_BASE_PATH = "http://image.tmdb.org/t/p/w185/";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState != null) {
            return;
        }
        FilmsFragment filmsFragment = new FilmsFragment();
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, filmsFragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public void onFilmSelected(Film selectedFilm) {

        DetailsFragment detailsFragment = new DetailsFragment();
        Bundle args = new Bundle();
        args.putParcelable(DetailsFragment.ARG_SELECTED_FILM, selectedFilm);
        detailsFragment.setArguments(args);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, detailsFragment)
                .addToBackStack(null)
                .commit();

    }

}
