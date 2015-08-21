package com.udacity.mrasulava.popularmovies;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.udacity.mrasulava.popularmovies.model.Film;

import java.text.DecimalFormat;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class DetailsFragment extends android.support.v4.app.Fragment {

    public static final String ARG_SELECTED_FILM = "selected_film";

    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Bind(R.id.iv_poster)
    ImageView ivPoster;

    @Bind(R.id.tv_date)
    TextView tvDate;

    @Bind(R.id.tv_rating)
    TextView tvRating;

    @Bind(R.id.tv_description)
    TextView tvDescription;

    public DetailsFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        Bundle arguments = getArguments();
        if (arguments != null && arguments.size() > 0) {
            Film film = (Film) arguments.getParcelable(ARG_SELECTED_FILM);
            updateView(film);
        }
        return rootView;
    }

    @OnClick(R.id.btn_fav)
    public void markAsFavorite(View view) {
        Toast.makeText(getActivity(), "Not implemented yet", Toast.LENGTH_SHORT).show();
    }

    public void updateView(Film film) {
        if (film == null)
            return;
        tvTitle.setText(film.getOriginalTitle());
        tvDate.setText(film.getReleaseDate());

        tvRating.setText(getString(R.string.rating, new DecimalFormat("#.#").format(film.getRating())));
        tvDescription.setText(film.getOverview());
        Picasso.with(getActivity())
                .load(MainActivity.POSTER_BASE_PATH + film.getPosterPath())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.ic_error)
                .into(ivPoster);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
    }
}
