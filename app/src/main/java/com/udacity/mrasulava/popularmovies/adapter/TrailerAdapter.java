package com.udacity.mrasulava.popularmovies.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.udacity.mrasulava.popularmovies.R;
import com.udacity.mrasulava.popularmovies.model.Trailer;

import java.util.ArrayList;
import java.util.List;

public class TrailerAdapter extends BaseAdapter {
    private Context mContext;
    private List<Trailer> trailers = new ArrayList<>();

    public TrailerAdapter(Context c) {
        mContext = c;
    }

    public int getCount() {
        return trailers.size();
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers.clear();
        this.trailers.addAll(trailers);
    }

    public Trailer getItem(int position) {
        return trailers.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final View view;
        if (convertView == null) {
            view = LayoutInflater.from(mContext).inflate(R.layout.trailer_list_item, null);
        } else {
            view = convertView;
        }
        TextView tvTitle = (TextView) view.findViewById(R.id.tv_name);
        tvTitle.setText(getItem(position).getName());
        return view;
    }

}