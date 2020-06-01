package com.example.wizi.popularmovies;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.wizi.popularmovies.data.FavoritesAdapter;
import com.example.wizi.popularmovies.data.Movie;
import com.example.wizi.popularmovies.data.MovieAdapter;
import com.example.wizi.popularmovies.utilities.QueryUtils;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<List<Movie>> {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String SORT_PREFERENCE = QueryUtils.getSortingMethod();

    private static final int MOVIE_SEARCH_LOADER = 7;

    private MovieAdapter movieAdapter;
    private RecyclerView mMoviesList;
    private ProgressBar mLoadingIndicator;
    private TextView noInternetTextView;
    private TextView noDataTextView;
    GridLayoutManager gridLayoutManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mMoviesList = findViewById(R.id.rv_movies);
        noInternetTextView = findViewById(R.id.tv_error_no_internet);
        noDataTextView = findViewById(R.id.tv_error_no_data);

        mLoadingIndicator = findViewById(R.id.pb_loading_indicator);

        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            gridLayoutManager = new GridLayoutManager(this, 2, LinearLayoutManager.VERTICAL, false);
        } else {
            gridLayoutManager = new GridLayoutManager(this, 4, LinearLayoutManager.VERTICAL, false);
        }

        mMoviesList.setLayoutManager(gridLayoutManager);
        mMoviesList.setHasFixedSize(true);

        List<Movie> movies = new ArrayList<>();
        movieAdapter = new MovieAdapter(this, movies);
        mMoviesList.setAdapter(movieAdapter);


        boolean internetConnected = checkInternetConnection();

        if (savedInstanceState != null) {
            movieAdapter.restoreInstanceState(savedInstanceState);
            mMoviesList.scrollToPosition(savedInstanceState.getInt(getString(R.string.bundle_scroll_position)));
        } else {
            if (internetConnected) {
                loadMovieData();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        movieAdapter.saveInstanceState(outState);
        int scrollPosition = gridLayoutManager.findFirstVisibleItemPosition();
        outState.putInt(getString(R.string.bundle_scroll_position), scrollPosition);
    }

    private void loadMovieData() {
        String sortedBy = QueryUtils.getSortingMethod();
        Bundle queryBundle = new Bundle();
        queryBundle.putString(SORT_PREFERENCE, sortedBy);

        LoaderManager loaderManager = getLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_SEARCH_LOADER);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_SEARCH_LOADER, queryBundle, this);
        }

    }

    @Override
    public Loader<List<Movie>> onCreateLoader(int id, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);
                forceLoad();
            }

            @Override
            public List<Movie> loadInBackground() {
                String sortedBy = args.getString(SORT_PREFERENCE);
                URL movieRequestUrl = QueryUtils.buildQueryUrl(sortedBy);

                try {
                    String jsonMovieResponse = QueryUtils.getResponseFromHttpUrl(movieRequestUrl);

                    return QueryUtils.parseJson(jsonMovieResponse);

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(Loader<List<Movie>> loader, List<Movie> movies) {
        mLoadingIndicator.setVisibility(View.INVISIBLE);

        if (movies != null) {
            movieAdapter.setMovieData(movies);
            showMovies();
        } else {
            showErrorNoData();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Movie>> loader) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_top_rated) {
            QueryUtils.setSortingMethod(getString(R.string.option_top_rated));
            checkInternetConnection();
            loadMovieData();
            return true;
        }

        if (id == R.id.action_popular) {
            QueryUtils.setSortingMethod(getString(R.string.option_popular));
            checkInternetConnection();
            loadMovieData();
            return true;
        }

        if (id == R.id.action_favorites) {
            Intent favorites = new Intent(this, FavoritesActivity.class);
            this.startActivity(favorites);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting()) {
            showMovies();
            return true;
        } else {
            showErrorNoInternet();
            return false;
        }
    }

    private void showErrorNoInternet() {
        mMoviesList.setVisibility(View.INVISIBLE);
        noInternetTextView.setVisibility(View.VISIBLE);
    }

    private void showErrorNoData() {
        mMoviesList.setVisibility(View.INVISIBLE);
        noDataTextView.setVisibility(View.VISIBLE);
    }

    private void showMovies() {
        noInternetTextView.setVisibility(View.INVISIBLE);
        noDataTextView.setVisibility(View.INVISIBLE);
        mMoviesList.setVisibility(View.VISIBLE);
    }


}


