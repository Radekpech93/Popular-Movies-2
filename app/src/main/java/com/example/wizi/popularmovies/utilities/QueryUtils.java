package com.example.wizi.popularmovies.utilities;

import android.arch.lifecycle.BuildConfig;
import android.net.Uri;
import android.util.Log;

import com.example.wizi.popularmovies.R;
import com.example.wizi.popularmovies.data.Movie;
import com.example.wizi.popularmovies.data.Review;
import com.example.wizi.popularmovies.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static com.example.wizi.popularmovies.BuildConfig.API_KEY;

public final class QueryUtils {

    private static final String TAG = QueryUtils.class.getSimpleName();

    private static final String API_KEY = "VALID_KEY_Krappa";

    private static final String BASE_URL = "https://api.themoviedb.org/3";
    private static final String IMAGE_BASE_URL = "http://image.tmdb.org/t/p/w185";
    private static final String TRAILER_BASE_URL = "https://api.themoviedb.org/3/movie";

    private static final String MOVIE_PARAMETER = "movie";
    private static final String API_PARAMETER = "api_key";
    private static final String MOVIE_DEFAULT_SORT_METHOD = "popular";

    private static final String MOVIE_RESULTS = "results";
    private static final String MOVIE_TITLE = "title";
    private static final String MOVIE_POSTER = "poster_path";
    private static final String MOVIE_OVERVIEW = "overview";
    private static final String MOVIE_AVERAGE_VOTE = "vote_average";
    private static final String MOVIE_RELEASE_DATE = "release_date";
    private static final String MOVIE_ID = "id";

    private static final String VIDEOS_KEY = "key";
    private static final String VIDEOS_TYPE = "type";
    private static final String VIDEOS_SITE = "site";
    private static final String VIDEOS_TRAILER = "Trailer";
    private static final String VIDEOS_YOUTUBE = "YouTube";

    private static final String REVIEWS_AUTHOR = "author";
    private static final String REVIEWS_CONTENT = "content";

    private static String sortedBy = MOVIE_DEFAULT_SORT_METHOD;


    public static void setSortingMethod(String sortBy) {
        sortedBy = sortBy;
    }

    public static String getSortingMethod() {
        return sortedBy;
    }

    public static URL buildQueryUrl(String sortedBy) {
        Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                .appendPath(MOVIE_PARAMETER)
                .appendPath(sortedBy)
                .appendQueryParameter(API_PARAMETER, API_KEY)
                .build();

        return createUrl(builtUri);
    }

    //This method is kept because is is used by Picasso, which needs a Uri
    public static String buildPosterUri(String poster) {

        Uri.Builder builtUri = Uri.parse(IMAGE_BASE_URL).buildUpon()
                .appendEncodedPath(poster);

        return builtUri.toString();
    }

    //This method creates URLs for both trailers and posters - can be toggled with the "extrasType" parameter
    public static URL buildExtrasUrl(String trailerId, String extrasType) {

        Uri builtUri = Uri.parse(TRAILER_BASE_URL).buildUpon()
                .appendPath(trailerId)
                .appendPath(extrasType)
                .appendQueryParameter(API_PARAMETER, API_KEY)
                .build();

        return createUrl(builtUri);
    }


    private static URL createUrl(Uri uri) {
        URL url = null;
        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getResponseFromHttpUrl(URL url) throws IOException {
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        try {
            InputStream in = urlConnection.getInputStream();

            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");

            boolean hasInput = scanner.hasNext();
            if (hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }

    public static List<Movie> parseJson(String jsonString)
            throws JSONException {

        List<Movie> movies = new ArrayList<>();
        String title;
        String posterPath;
        String averageVote;
        String overview;
        String releaseDate;
        String id;

        JSONObject rootJson;

        if (jsonString != null) {
            rootJson = new JSONObject(jsonString);
        } else {
            return null;
        }
        JSONArray movieArray = rootJson.getJSONArray(MOVIE_RESULTS);

        for (int i = 0; i < movieArray.length(); i++) {

            JSONObject movieDetails = movieArray.getJSONObject(i);

            title = movieDetails.getString(MOVIE_TITLE);
            posterPath = movieDetails.getString(MOVIE_POSTER);
            overview = movieDetails.getString(MOVIE_OVERVIEW);
            averageVote = movieDetails.getString(MOVIE_AVERAGE_VOTE) + " /10";
            releaseDate = movieDetails.getString(MOVIE_RELEASE_DATE);
            id = movieDetails.getString(MOVIE_ID);

            movies.add(new Movie(title, posterPath, overview, averageVote, releaseDate, id));
        }
        return movies;
    }

    public static List<Trailer> getTrailers(String jsonString)
            throws JSONException {

        List<Trailer> trailers = new ArrayList<>();

        String key;
        String type;
        String site;

        JSONObject rootJson;

        if (jsonString != null) {
            rootJson = new JSONObject(jsonString);
        } else {
            return null;
        }

        JSONArray resultsArray = rootJson.getJSONArray(MOVIE_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject results = resultsArray.getJSONObject(i);

            key = results.getString(VIDEOS_KEY);
            type = results.getString(VIDEOS_TYPE);
            site = results.getString(VIDEOS_SITE);

            if (type.equals(VIDEOS_TRAILER) && site.equals(VIDEOS_YOUTUBE)) {
                trailers.add(new Trailer(key));
            }
        }
        return trailers;
    }

    public static List<Review> getReviews(String jsonString)
            throws JSONException {

        List<Review> reviews = new ArrayList<>();

        String author;
        String content;

        JSONObject rootJson;

        if (jsonString != null) {
            rootJson = new JSONObject(jsonString);
        } else {
            return null;
        }

        JSONArray resultsArray = rootJson.getJSONArray(MOVIE_RESULTS);

        for (int i = 0; i < resultsArray.length(); i++) {

            JSONObject results = resultsArray.getJSONObject(i);

            author = results.getString(REVIEWS_AUTHOR) + ":";
            content = results.getString(REVIEWS_CONTENT);

            reviews.add(new Review(author, content));
        }
        return reviews;
    }
}
