package com.example.wizi.popularmovies;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.wizi.popularmovies.data.Movie;
import com.example.wizi.popularmovies.data.MovieContract;
import com.example.wizi.popularmovies.data.Review;
import com.example.wizi.popularmovies.data.ReviewAdapter;
import com.example.wizi.popularmovies.data.Trailer;
import com.example.wizi.popularmovies.data.TrailerAdapter;
import com.example.wizi.popularmovies.utilities.QueryUtils;
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class DetailActivity extends AppCompatActivity {

    private static final String TAG = DetailActivity.class.getSimpleName();

    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;
    private RecyclerView mReviewsList;
    private RecyclerView mTrailersList;
    private Movie currentMovie;
    String movieId;
    ImageButton ibAddToFavorites;
    ImageButton ibRemoveFromFavorites;

    private static final String MOVIE_KEY = "movie";

    LinearLayoutManager reviewManager;

    List<Trailer> trailers;
    List<Review> reviews;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Context mContext = this;

        currentMovie = getIntent().getParcelableExtra(MOVIE_KEY);
        movieId = currentMovie.getId();

        ImageView detailPoster = findViewById(R.id.iv_detail_poster);
        TextView detailTitle = findViewById(R.id.tv_detail_title);
        TextView detailOverview = findViewById(R.id.tv_detail_overview);
        TextView detailAverageVote = findViewById(R.id.tv_detail_average_vote);
        TextView detailReleaseDate = findViewById(R.id.tv_detail_release_date);

        detailTitle.setText(currentMovie.getTitle());
        detailOverview.setText(currentMovie.getOverview());
        detailAverageVote.setText(currentMovie.getAverageVote());
        detailReleaseDate.setText(currentMovie.getReleaseDate());

        Picasso.with(mContext)
                .load(QueryUtils.buildPosterUri(currentMovie.getPoster()))
                .into(detailPoster);

        mReviewsList = findViewById(R.id.rv_reviews);

        reviewManager = new LinearLayoutManager(this);
        mReviewsList.setLayoutManager(reviewManager);
        mReviewsList.setHasFixedSize(true);


        reviews = new ArrayList<>();
        reviewAdapter = new ReviewAdapter(this, reviews);
        mReviewsList.setAdapter(reviewAdapter);

        mTrailersList = findViewById(R.id.rv_trailers);

        LinearLayoutManager trailerManager = new LinearLayoutManager(this);
        mTrailersList.setLayoutManager(trailerManager);
        mTrailersList.setHasFixedSize(true);

        trailers = new ArrayList<>();
        trailerAdapter = new TrailerAdapter(this, trailers);
        mTrailersList.setAdapter(trailerAdapter);

        ibAddToFavorites = findViewById(R.id.ib_add_to_favorites);
        ibRemoveFromFavorites = findViewById(R.id.ib_remove_from_favorites);

        if(isInFavorites()){
            displayRemoveButton();
        } else {
            displayAddButton();
        }

        boolean internetConnected = checkInternetConnection();

        if (savedInstanceState != null) {
            reviewAdapter.restoreInstanceState(savedInstanceState);
            trailerAdapter.restoreInstanceState(savedInstanceState);
        } else {
            if (internetConnected) {
                loadExtrasData();
            }
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        reviewAdapter.saveInstanceState(outState);
        trailerAdapter.saveInstanceState(outState);
    }

    private void loadExtrasData() {
        new GetReviewsTask().execute(movieId);
        new GetTrailersTask().execute(movieId);
    }

    private boolean checkInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public class GetReviewsTask extends AsyncTask<String, Void, List<Review>> {

        @Override
        protected List<Review> doInBackground(String... ids) {

            if (ids.length == 0) {
                return null;
            }

            String id = ids[0];
            URL reviewRequestUrl = QueryUtils.buildExtrasUrl(id, getString(R.string.movie_reviews_path));

            try {
                String jsonReviewResponse = QueryUtils.getResponseFromHttpUrl(reviewRequestUrl);

                return QueryUtils.getReviews(jsonReviewResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Review> reviews) {

            if (reviews != null) {
                reviewAdapter.setReviewData(reviews);
            }

        }


    }

    public class GetTrailersTask extends AsyncTask<String, Void, List<Trailer>> {

        @Override
        protected List<Trailer> doInBackground(String... ids) {

            if (ids.length == 0) {
                return null;
            }

            String id = ids[0];
            URL trailerRequestUrl = QueryUtils.buildExtrasUrl(id, getString(R.string.movie_trailers_path));

            try {
                String jsonTrailerResponse = QueryUtils.getResponseFromHttpUrl(trailerRequestUrl);

                return QueryUtils.getTrailers(jsonTrailerResponse);

            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(List<Trailer> trailers) {

            if (trailers != null) {
                trailerAdapter.setTrailerData(trailers);
            }
        }
    }


    public void addToFavorites(View view) {
        ContentValues contentValues = new ContentValues();

        contentValues.put(MovieContract.MovieFavorites.COLUMN_TITLE, currentMovie.getTitle());
        contentValues.put(MovieContract.MovieFavorites.COLUMN_POSTER, currentMovie.getPoster());
        contentValues.put(MovieContract.MovieFavorites.COLUMN_OVERVIEW, currentMovie.getOverview());
        contentValues.put(MovieContract.MovieFavorites.COLUMN_AVERAGE_VOTE, currentMovie.getAverageVote());
        contentValues.put(MovieContract.MovieFavorites.COLUMN_RELEASE_DATE, currentMovie.getReleaseDate());
        contentValues.put(MovieContract.MovieFavorites.COLUMN_MOVIE_ID, currentMovie.getId());

        getContentResolver().insert(MovieContract.MovieFavorites.CONTENT_URI, contentValues);

        displayRemoveButton();
    }

    public void removeFromFavorites(View view) {

        Uri uri = MovieContract.MovieFavorites.CONTENT_URI.buildUpon().appendPath(currentMovie.getId()).build();
        getContentResolver().delete(uri, null, null);

        displayAddButton();
    }

    public boolean isInFavorites() {

        Cursor cursor = getContentResolver().query(MovieContract.MovieFavorites.CONTENT_URI,
                null,
                MovieContract.MovieFavorites.COLUMN_MOVIE_ID + "=?",
                new String[]{currentMovie.getId()},
                null
        );

        cursor.close();
        return cursor.getCount() > 0;
    }

    public void displayAddButton() {
        ibRemoveFromFavorites.setVisibility(View.INVISIBLE);
        ibAddToFavorites.setVisibility(View.VISIBLE);
    }

    public void displayRemoveButton() {
        ibAddToFavorites.setVisibility(View.INVISIBLE);
        ibRemoveFromFavorites.setVisibility(View.VISIBLE);
    }
}
