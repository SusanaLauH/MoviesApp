package com.example.android.moviesapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by SusanaLauH on 3/14/2015.
 */
public class MovieContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.moviesapp";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_MOVIE = "movie";

    public static final class MovieEntry implements BaseColumns {

        public static final Uri CONTENT_URI =
                BASE_CONTENT_URI.buildUpon().appendPath(PATH_MOVIE).build();

        public static final String CONTENT_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_MOVIE;

        // Table name
        public static final String TABLE_NAME = "movie";

        //Column names
        public static final String COLUMN_MOVIE_ID = "movieID";
        public static final String COLUMN_MOVIE_TITLE = "title";
        public static final String COLUMN_MOVIE_RELEASE_YEAR = "releaseYear";
        public static final String COLUMN_MOVIE_RATING = "mpaaRating";
        public static final String COLUMN_MOVIE_DURATION = "duration";
        public static final String COLUMN_MOVIE_SCORE = "ratingScore";
        public static final String COLUMN_MOVIE_SYNOPSIS = "synopsis";
        public static final String COLUMN_MOVIE_POSTER = "posterLink";


        public static Uri buildMovieUri() {
            return CONTENT_URI.buildUpon().build();
            //return ContentUris.withAppendedId(CONTENT_URI, id);
        }

        public static Uri buildMovieWithSpecificID(long movieId) {
            // return CONTENT_URI.buildUpon().appendPath(Long.toString(movieId)).build();
            return ContentUris.withAppendedId(CONTENT_URI, movieId);
        }

    }

}
