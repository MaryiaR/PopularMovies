package com.udacity.mrasulava.popularmovies;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.udacity.mrasulava.popularmovies.adapter.MoviesAdapter;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.service.MoviesService;
import com.udacity.mrasulava.popularmovies.util.MovieStorage;
import com.udacity.mrasulava.popularmovies.util.Utils;

import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class FilmsFragment extends Fragment {

    private static final String GRID_VIEW_STATE = "grid_view_state";

    @Bind(R.id.gv_films)
    GridView gvFilms;

    @Bind(R.id.ll_no_internet)
    LinearLayout llNoInternet;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    @Bind(R.id.empty_favorite_view)
    TextView tvEmptyFav;

    private MoviesAdapter adapter;

    private OnStateChangedListener mCallback;

    private List<Movie> movies;

    private Parcelable gridViewState;

    private MovieStorage movieStorage;

    private Activity activity;

    private BroadcastReceiver filmsLoadedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            movies = movieStorage.getMovies();
            if (movies != null) {
                updateViewForState(STATE.MOVIES);
                adapter.setMovies(movies);
            } else {
                updateViewForState(STATE.NO_CONNECTION);
            }
        }
    };

    public FilmsFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
        try {
            mCallback = (OnStateChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + " must implement OnStateChangedListener");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);
        movieStorage = MovieStorage.getInstance(activity);
        adapter = new MoviesAdapter(activity);
        gvFilms.setAdapter(adapter);

        if (savedInstanceState != null)
            gridViewState = savedInstanceState.getParcelable(GRID_VIEW_STATE);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        activity.registerReceiver(filmsLoadedReceiver, new IntentFilter(Utils.ACTION_FILMS_LOADED));
        updateMovies();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gridViewState != null)
            gvFilms.onRestoreInstanceState(gridViewState);
    }

    @Override
    public void onPause() {
        gridViewState = gvFilms.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(filmsLoadedReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (gvFilms != null)
            state.putParcelable(GRID_VIEW_STATE, gvFilms.onSaveInstanceState());
    }

    @OnItemClick(R.id.gv_films)
    void onItemClick(int position) {
        movieStorage.setSelectedMovie(movies.get(position));
        mCallback.onFilmSelected();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.films, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(activity, SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateViewForState(STATE state) {
        switch (state) {
            case MOVIES:
                gvFilms.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.GONE);
                tvEmptyFav.setVisibility(View.GONE);
                break;
            case LOADING:
                gvFilms.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                llNoInternet.setVisibility(View.GONE);
                tvEmptyFav.setVisibility(View.GONE);
                break;
            case NO_CONNECTION:
                gvFilms.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.VISIBLE);
                tvEmptyFav.setVisibility(View.GONE);
                break;
            case NO_FAVORITES:
                gvFilms.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.GONE);
                tvEmptyFav.setVisibility(View.VISIBLE);
                break;
        }
    }


    enum STATE {
        MOVIES,
        LOADING,
        NO_CONNECTION,
        NO_FAVORITES
    }

    @OnClick(R.id.btn_retry)
    public void retry(View view) {
        updateMovies();
    }

    public void updateMovies() {
        String sortBy = Utils.getSortBy(activity);
        if (getString(R.string.pref_sort_favorite).equals(sortBy)) {
            showFavoriteMovies();
        } else {
            if (Utils.haveInternetConnection(activity)) {
                updateViewForState(STATE.LOADING);
                MoviesService.startLoadingMovies(activity);
            } else {
                updateViewForState(STATE.NO_CONNECTION);
            }
        }
    }

    private void showFavoriteMovies() {
        movies = movieStorage.getFavoriteMovies();
        movieStorage.setMovies(movies);
        if (movies != null && !movies.isEmpty()) {
            updateViewForState(STATE.MOVIES);
            adapter.setMovies(movies);
            activity.sendBroadcast(new Intent(Utils.ACTION_FILMS_LOADED));
        } else {
            updateViewForState(STATE.NO_FAVORITES);
            movieStorage.setSelectedMovie(null);
            mCallback.updateDetailView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnStateChangedListener {
        void onFilmSelected();

        void updateDetailView();

        void onFavoriteRemoved();
    }

}
