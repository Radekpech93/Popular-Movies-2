package com.example.wizi.popularmovies.data;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoritesHolder> {

    private List<Movie> mMovies = new ArrayList<>();
    private Context mContext;
    private Cursor mCursor;

    private static final String FAVORITES_KEY = "FAVORITES_KEY";
    private static final String MOVIE_KEY = "movie";

    public FavoritesAdapter(Context context) {
        mContext = context;
    }

    @NonNull
    @Override
    public FavoritesHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        int layoutIdForListItem = R.layout.movie_list_item;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutIdForListItem, parent, shouldAttachToParentImmediately);

        return new FavoritesHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FavoritesHolder favoritesHolder, int position) {

        mCursor.moveToPosition(position);

        int titleIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_TITLE);
        int posterIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_POSTER);
        int overviewIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_OVERVIEW);
        int averageVoteIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_AVERAGE_VOTE);
        int releaseDateIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_RELEASE_DATE);
        int movieIdIndex = mCursor.getColumnIndex(MovieContract.MovieFavorites.COLUMN_MOVIE_ID);

        Movie current = new Movie(mCursor.getString(titleIndex),
                mCursor.getString(posterIndex),
                mCursor.getString(overviewIndex),
                mCursor.getString(averageVoteIndex),
                mCursor.getString(releaseDateIndex),
                mCursor.getString(movieIdIndex));

        mMovies.add(current);

        String picassoUrl = QueryUtils.buildPosterUri(mMovies.get(position).getPoster());

        Picasso.with(mContext)
                .load(picassoUrl)
                .into(favoritesHolder.movieView);
    }

    @Override
    public int getItemCount() {
        if (mCursor == null) {
            return 0;
        }
        return mCursor.getCount();
    }

    class FavoritesHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        private ImageView movieView;

        FavoritesHolder(View view) {
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
            intent.putExtra(MOVIE_KEY, intentMovie);

            mContext.startActivity(intent);
        }
    }

    public Cursor swapCursor(Cursor c) {
        if (mCursor == c) {
            return null;
        }
        Cursor temp = mCursor;
        this.mCursor = c;

        if (c != null) {
            this.notifyDataSetChanged();
        }
        return temp;
    }

    public void saveInstanceState(Bundle outState) {
        outState.putParcelableArrayList(FAVORITES_KEY, (ArrayList<? extends Parcelable>) mMovies);
    }



    public void restoreInstanceState(Bundle savedInstanceState) {
        if (savedInstanceState.containsKey(FAVORITES_KEY)) {
            List<Movie> savedMovies = savedInstanceState.getParcelableArrayList(FAVORITES_KEY);
            mMovies.clear();
            mMovies.addAll(savedMovies);
            notifyDataSetChanged();
        }
    }
}
