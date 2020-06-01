package com.example.wizi.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.res.Configuration;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.wizi.popularmovies.data.FavoritesAdapter;
import com.example.wizi.popularmovies.data.Movie;
import com.example.wizi.popularmovies.data.MovieAdapter;
import com.example.wizi.popularmovies.data.MovieContract;
import com.example.wizi.popularmovies.data.MovieDbHelper;

import java.util.List;

public class FavoritesActivity extends AppCompatActivity implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor> {

    private static final String TAG = FavoritesActivity.class.getSimpleName();
    private static final int TASK_LOADER_ID = 1;

    private FavoritesAdapter mAdapter;
    private RecyclerView mFavoritesList;
    GridLayoutManager gridLayoutManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mFavoritesList = findViewById(R.id.rv_movies);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }
        mFavoritesList.setLayoutManager(gridLayoutManager);
        mFavoritesList.setHasFixedSize(true);

        mAdapter = new FavoritesAdapter(this);
        mFavoritesList.setAdapter(mAdapter);

        if (savedInstanceState != null) {
            mAdapter.restoreInstanceState(savedInstanceState);
            mFavoritesList.scrollToPosition(savedInstanceState.getInt(getString(R.string.bundle_scroll_position)));
        } else {
            getSupportLoaderManager().initLoader(TASK_LOADER_ID, null, this);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        getSupportLoaderManager().restartLoader(TASK_LOADER_ID, null, this);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mAdapter.saveInstanceState(outState);
        int scrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(getString(R.string.bundle_scroll_position), scrollPosition);
    }

    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, final Bundle loaderArgs) {

        return new AsyncTaskLoader<Cursor>(this) {

            Cursor mTaskData = null;

            @Override
            protected void onStartLoading() {
                if (mTaskData != null) {
                    deliverResult(mTaskData);
                } else {
                    forceLoad();
                }
            }

            @Override
            public Cursor loadInBackground() {

                try {

                    return getContentResolver().query(MovieContract.MovieFavorites.CONTENT_URI,
                            null,
                            null,
                            null,
                            MovieContract.MovieFavorites.COLUMN_MOVIE_ID);

                } catch (Exception e) {
                    Log.e(TAG, "Failed to asynchronously load data.");
                    e.printStackTrace();
                    return null;
                }
            }

            public void deliverResult(Cursor data) {
                mTaskData = data;
                super.deliverResult(data);
            }
        };

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mAdapter.swapCursor(data);
    }


    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mAdapter.swapCursor(null);
    }
}


