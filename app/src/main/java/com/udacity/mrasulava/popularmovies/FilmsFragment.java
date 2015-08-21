package com.udacity.mrasulava.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.google.gson.Gson;
import com.squareup.picasso.Picasso;
import com.udacity.mrasulava.popularmovies.model.Film;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


/**
 * A placeholder fragment containing a simple view.
 */
public class FilmsFragment extends Fragment {

    private static final String GRID_VIEW_STATE = "grid_view_state";
    private static final String API_KEY = "<INSERT YOUR API KEY>";

    @Bind(R.id.gv_films)
    GridView gvFilms;

    @Bind(R.id.ll_no_internet)
    LinearLayout llNoInternet;

    @Bind(R.id.progress)
    ProgressBar progressBar;

    private FilmsAdapter adapter;

    private OnStateChangedListener mCallback;

    private List<Film> films = new ArrayList<>();

    private Parcelable gridViewState;

    public FilmsFragment() {
        setHasOptionsMenu(true);
    }

    public void setFilms(List<Film> films) {
        this.films.clear();
        this.films.addAll(films);
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnStateChangedListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }

    @Override
    public void onPause() {
        gridViewState = gvFilms.onSaveInstanceState();
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (gridViewState != null)
            gvFilms.onRestoreInstanceState(gridViewState);
    }

    @Override
    public void onSaveInstanceState(Bundle state) {
        super.onSaveInstanceState(state);
        if (gvFilms != null)
            state.putParcelable(GRID_VIEW_STATE, gvFilms.onSaveInstanceState());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        ButterKnife.bind(this, rootView);

        adapter = new FilmsAdapter(getActivity());
        gvFilms.setAdapter(adapter);

        if (savedInstanceState != null)
            gridViewState = savedInstanceState.getParcelable(GRID_VIEW_STATE);

        return rootView;
    }

    @OnItemClick(R.id.gv_films)
    void onItemClick(int position) {
        mCallback.onFilmSelected(films.get(position));
    }

    @Override
    public void onStart() {
        super.onStart();
        updateFilms();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.films, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(getActivity(), SettingsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateViewForState(STATE state) {
        switch (state) {
            case FILMS:
                gvFilms.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.GONE);
                break;
            case LOADING:
                gvFilms.setVisibility(View.GONE);
                progressBar.setVisibility(View.VISIBLE);
                llNoInternet.setVisibility(View.GONE);
                break;
            case NO_CONNECTION:
                gvFilms.setVisibility(View.GONE);
                progressBar.setVisibility(View.GONE);
                llNoInternet.setVisibility(View.VISIBLE);
                break;
        }
    }

    enum STATE {
        FILMS,
        LOADING,
        NO_CONNECTION
    }

    @OnClick(R.id.btn_retry)
    public void retry(View view) {
        updateFilms();
    }

    private void updateFilms() {
        if (Utils.haveInternetConnection(getActivity())) {
            updateViewForState(STATE.LOADING);
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getActivity());
            String sortBy = sharedPref.getString(getString(R.string.pref_key_sort_by), getString(R.string.pref_sort_by_default));
            new LoadFilmsTask().execute(sortBy, API_KEY);
        } else {
            updateViewForState(STATE.NO_CONNECTION);
        }
    }

    public class FilmsAdapter extends BaseAdapter {
        private Context mContext;
        private Picasso picasso;
        private List<Film> films = new ArrayList<>();

        public void setFilms(List<Film> films) {
            this.films.clear();
            this.films.addAll(films);
        }

        public FilmsAdapter(Context c) {
            mContext = c;
            picasso = Picasso.with(mContext);
        }

        public int getCount() {
            return films.size();
        }

        public Film getItem(int position) {
            return films.get(position);
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
            picasso.load(MainActivity.POSTER_BASE_PATH + getItem(position).getPosterPath())
                    .placeholder(R.drawable.placeholder)
                    .error(R.drawable.ic_error)
                    .into(ivPoster);
            return ivPoster;
        }

    }

    private class LoadFilmsTask extends AsyncTask<String, Void, List<Film>> {
        private final String LOG_TAG = LoadFilmsTask.class.getSimpleName();

        @Override
        protected List<Film> doInBackground(String... params) {

            if (params.length == 0 && params.length < 2)
                return null;

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            try {
                final String BASE_URL =
                        "http://api.themoviedb.org/3/discover/movie?";

                final String SORT_PARAM = "sort_by";
                final String KEY_PARAM = "api_key";

                Uri uri = Uri.parse(BASE_URL).buildUpon()
                        .appendQueryParameter(SORT_PARAM, params[0])
                        .appendQueryParameter(KEY_PARAM, params[1])
                        .build();

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
                return getFilmsDataFromJson(builder.toString());
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error " + e);
                return null;
            } catch (JSONException e) {
                e.printStackTrace();
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
            return null;
        }

        @Override
        protected void onPostExecute(List<Film> result) {
            if (result != null) {
                updateViewForState(STATE.FILMS);
                setFilms(result);
                adapter.setFilms(films);
                adapter.notifyDataSetChanged();
            } else {
                updateViewForState(STATE.NO_CONNECTION);
            }
        }

        private List<Film> getFilmsDataFromJson(String jsonStr)
                throws JSONException {

            String filmsJson = new JSONObject(jsonStr).getString("results");
            return Arrays.asList(new Gson().fromJson(filmsJson, Film[].class));
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }

    public interface OnStateChangedListener {
        void onFilmSelected(Film film);
    }

}
