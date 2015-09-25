package com.udacity.mrasulava.popularmovies;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.udacity.mrasulava.popularmovies.adapter.TrailerAdapter;
import com.udacity.mrasulava.popularmovies.model.Movie;
import com.udacity.mrasulava.popularmovies.model.Review;
import com.udacity.mrasulava.popularmovies.model.Trailer;
import com.udacity.mrasulava.popularmovies.service.MoviesService;
import com.udacity.mrasulava.popularmovies.util.MovieStorage;
import com.udacity.mrasulava.popularmovies.util.Utils;

import java.text.DecimalFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnItemClick;


public class DetailsFragment extends android.support.v4.app.Fragment {

    @Bind(R.id.tv_title)
    TextView tvTitle;

    @Bind(R.id.iv_poster)
    ImageView ivPoster;

    @Bind(R.id.tv_date)
    TextView tvDate;

    @Bind(R.id.btn_fav)
    Button btnFavorite;

    @Bind(R.id.tv_rating)
    TextView tvRating;

    @Bind(R.id.tv_description)
    TextView tvDescription;

    @Bind(R.id.lv_trailers)
    ListView lvTrailers;

    @Bind(R.id.ll_trailers)
    LinearLayout llTrailers;

    @Bind(R.id.ll_reviews_container)
    LinearLayout llReviewsContainer;

    @Bind(R.id.ll_reviews)
    LinearLayout llReviews;

    @Bind(R.id.ll_no_internet)
    LinearLayout llNoInternet;

    private MovieStorage movieStorage;

    private Movie movie;

    private TrailerAdapter trailerAdapter;

    private Activity activity;

    private BroadcastReceiver loadingFinishedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(Utils.ACTION_REVIEWS_LOADED)) {
                updateReviewsView();
            } else if (action.equals(Utils.ACTION_TRAILERS_LOADED)) {
                updateTrailersView();
            }
        }
    };

    public DetailsFragment() {

    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_details, container, false);
        ButterKnife.bind(this, rootView);
        movieStorage = MovieStorage.getInstance(activity);
        movie = movieStorage.getSelectedMovie();
        updateMovieDetails();
        trailerAdapter = new TrailerAdapter(activity);
        lvTrailers.setAdapter(trailerAdapter);
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateTrailersAndReviews();
        IntentFilter intentFilter = new IntentFilter(Utils.ACTION_TRAILERS_LOADED);
        intentFilter.addAction(Utils.ACTION_REVIEWS_LOADED);
        activity.registerReceiver(loadingFinishedReceiver, intentFilter);
    }

    @Override
    public void onStop() {
        super.onStop();
        activity.unregisterReceiver(loadingFinishedReceiver);
    }

    @OnClick(R.id.btn_fav)
    public void markAsFavorite(View view) {
        if (movie.isFavorite()) {
            movieStorage.removeFavorite();
            btnFavorite.setText(getString(R.string.mark_favorite));
        } else {
            movieStorage.saveFavorite();
            Utils.storeImage(activity, movie);
            btnFavorite.setText(getString(R.string.delete_from_favorite));
        }
    }

    @OnItemClick(R.id.lv_trailers)
    void onItemClick(int position) {
        Utils.watchYoutubeVideo(activity, trailerAdapter.getItem(position).getKey());
    }

    @OnClick(R.id.btn_retry)
    public void retry(View view) {
        updateTrailersAndReviews();
    }

    public void updateMovieDetails() {
        if (movie == null)
            return;

        tvTitle.setText(movie.getOriginalTitle());
        tvDate.setText(movie.getReleaseDate());

        tvRating.setText(getString(R.string.rating, new DecimalFormat("#.#").format(movie.getRating())));
        tvDescription.setText(movie.getOverview());

        Utils.loadImage(activity, movie, ivPoster);

        if (movie.isFavorite())
            btnFavorite.setText(getString(R.string.delete_from_favorite));
        else
            btnFavorite.setText(getString(R.string.mark_favorite));

    }

    private void updateTrailersAndReviews() {
        if (Utils.haveInternetConnection(activity)) {
            llNoInternet.setVisibility(View.GONE);

            MoviesService.startLoadingTrailers(activity);
            MoviesService.startLoadingReviews(activity);
        } else {
            if (movie.isFavorite()) {
                updateTrailersView();
                updateReviewsView();
            } else {
                llNoInternet.setVisibility(View.VISIBLE);
                llReviews.setVisibility(View.GONE);
                llTrailers.setVisibility(View.GONE);
            }
        }
    }

    private void updateTrailersView() {
        List<Trailer> trailers = movieStorage.getSelectedTrailers();
        if (trailers != null && !trailers.isEmpty()) {
            Collections.sort(trailers, new Comparator<Trailer>() {
                public int compare(Trailer t1, Trailer t2) {
                    return t1.getName().compareToIgnoreCase(t2.getName());
                }
            });
            llTrailers.setVisibility(View.VISIBLE);
            trailerAdapter.setTrailers(trailers);
            trailerAdapter.notifyDataSetChanged();
            setListViewHeightBasedOnChildren(lvTrailers);
        } else llTrailers.setVisibility(View.GONE);
    }

    private void updateReviewsView() {
        List<Review> reviews = movieStorage.getSelectedReviews();
        if (reviews != null && !reviews.isEmpty()) {
            llReviews.setVisibility(View.VISIBLE);
            llReviewsContainer.removeAllViews();
            LayoutInflater inflater = LayoutInflater.from(activity);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(10, 10, 10, 10);
            for (Review review : reviews) {
                View view = inflater.inflate(R.layout.review_item, null, false);
                TextView tvTitle = (TextView) view.findViewById(R.id.tv_name);
                TextView tvContent = (TextView) view.findViewById(R.id.tv_content);
                tvTitle.setText(review.getAuthor());
                tvContent.setText(review.getContent());
                view.setLayoutParams(params);
                llReviewsContainer.addView(view);
            }
        } else llReviews.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        movieStorage.setSelectedMovie(null);
        ButterKnife.unbind(this);
    }

    public void setListViewHeightBasedOnChildren(ListView listView) {
        ListAdapter adapter = listView.getAdapter();
        int totalHeight = 0;
        for (int i = 0; i < listView.getCount(); i++) {
            View listItem = adapter.getView(i, null, listView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = listView.getLayoutParams();
        params.height = totalHeight + (listView.getDividerHeight() * (adapter.getCount() - 1));
        listView.setLayoutParams(params);
        listView.requestLayout();
    }
}
