package com.example.wizi.popularmovies.data;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.wizi.popularmovies.DetailActivity;
import com.example.wizi.popularmovies.R;
import com.example.wizi.popularmovies.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MovieAdapter extends RecyclerView.Adapter<MovieAdapter.MovieHolder> {

    private static final String TAG = MovieAdapter.class.getSimpleName();
    private static final String MOVIE_ADAPTER_KEY = "MOVIE_ADAPTER";

    private List<Movie> mMovies;

    private final Context mContext;

    public MovieAdapter(Context context, List<Movie> movies) {
        mContext = context;
        mMovies = movies;
    }

    @NonNull
    @Override
    public MovieHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new MovieHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieHolder movieHolder, int position) {

        String picassoUrl = QueryUtils.buildPosterUri(mMovies.get(position).getPoster());

        Picasso.with(mContext)
                .load(picassoUrl)
                .into(movieHolder.movieView);
    }


    @Override
    public int getItemCount() {
        return mMovies.size();
    }

    class MovieHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView movieView;

        public MovieHolder(View view) {
            super(view);

            movieView = view.findViewById(R.id.iv_movie);

            view.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(mContext, DetailActivity.class);
            int position = getAdapterPosition();

            Movie intentMovie;
            intentMovie = mMovies.get(position);
            intent.putExtra("movie", intentMovie);

            mContext.startActivity(intent);
        }
    }

    public void setMovieData(List<Movie> movieData) {
        mMovies = movieData;
        notifyDataSetChanged();
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(MOVIE_ADAPTER_KEY, (ArrayList<? extends Parcelable>) mMovies);
    }

    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(MOVIE_ADAPTER_KEY)) {
            List<Movie> savedMovies = savedInstanceState.getParcelableArrayList(MOVIE_ADAPTER_KEY);
            mMovies.clear();
            mMovies.addAll(savedMovies);
            notifyDataSetChanged();
        }
    }
}
