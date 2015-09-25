package com.udacity.mrasulava.popularmovies;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;


public class MainActivity extends ActionBarActivity implements FilmsFragment.OnStateChangedListener {

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
    public void onFilmSelected() {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new DetailsFragment())
                .addToBackStack(null)
                .commit();

    }

}
