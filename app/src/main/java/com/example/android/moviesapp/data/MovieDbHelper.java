package com.example.android.moviesapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by SusanaLauH on 3/14/2015.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        final String SQL_CREATE_MOVIE_TABLE = "CREATE TABLE " + MovieContract.MovieEntry.TABLE_NAME + " (" +
                MovieContract.MovieEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                MovieContract.MovieEntry.COLUMN_MOVIE_ID + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_DURATION + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SCORE + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS + " TEXT NOT NULL, " +
                MovieContract.MovieEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL " +
                " );";

        sqLiteDatabase.execSQL(SQL_CREATE_MOVIE_TABLE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + MovieContract.MovieEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

}
