package com.example.wizi.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "favorites.db";

    private static final int DATABASE_VERSION = 2;

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        final String SQL_CREATE_FAVORITES_TABLE = "CREATE TABLE " +
                MovieContract.MovieFavorites.TABLE_NAME + " (" +
                MovieContract.MovieFavorites.COLUMN_MOVIE_ID + " TEXT NOT NULL PRIMARY KEY, " +
                MovieContract.MovieFavorites.COLUMN_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieFavorites.COLUMN_POSTER + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_OVERVIEW + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_AVERAGE_VOTE + " TEXT, " +
                MovieContract.MovieFavorites.COLUMN_RELEASE_DATE + " TEXT " +
                ");";

        db.execSQL(SQL_CREATE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieFavorites.TABLE_NAME);
        onCreate(db);
    }
}
