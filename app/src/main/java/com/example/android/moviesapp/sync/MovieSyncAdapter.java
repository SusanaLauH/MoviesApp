package com.example.android.moviesapp.sync;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncRequest;
import android.content.SyncResult;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.example.android.moviesapp.R;
import com.example.android.moviesapp.data.MovieContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Vector;

/**
 * Created by SusanaLauH on 3/21/2015.
 */
public class MovieSyncAdapter extends AbstractThreadedSyncAdapter {
    public final String LOG_TAG = MovieSyncAdapter.class.getSimpleName();

    // Interval at which to sync with the weather, in seconds.
    // 60 seconds (1 minute) * 180 = 3 hours
    public static final int SYNC_INTERVAL = 60 * 60;
    public static final int SYNC_FLEXTIME = SYNC_INTERVAL / 3;

    public MovieSyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {
        Log.d(LOG_TAG, "Starting sync");
        String movieJsonString = null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String page_limit = "50";

        //Add the country to the settings menu.
        String country = "us";
        String apiKey = "v2v8k9sdnrc7nux8smg439ye";

        try

        {

            final String BASE_URL =
                    "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json";

            final String PAGE_LIMIT_PARAM = "page_limit";
            final String COUNTRY_PARAM = "country";
            final String API_KEY_PARAM = "apikey";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(API_KEY_PARAM, apiKey)
                    .appendQueryParameter(PAGE_LIMIT_PARAM, page_limit)
                    .appendQueryParameter(COUNTRY_PARAM, country)
                    .build();

            URL url = new URL(builtUri.toString());

            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Read the input stream into a String
            InputStream inputStream = urlConnection.getInputStream();
            StringBuffer buffer = new StringBuffer();
            if (inputStream == null) {
                // Nothing to do.
                return;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                // But it does make debugging a *lot* easier if you print out the completed
                // buffer for debugging.
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0) {
                // Stream was empty.  No point in parsing.
                return;
            }
            movieJsonString = buffer.toString();
            getMoviesFromJson(movieJsonString);
        } catch (
                IOException e
                )

        {
            Log.e(LOG_TAG, "Error ", e);
        } catch (
                JSONException e
                )

        {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        } finally

        {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(LOG_TAG, "Error closing stream", e);
                }
            }
        }
        return;
    }


    private void getMoviesFromJson(String movieJsonString) throws JSONException {


        final String MOVIE_MOVIES = "movies";
        final String MOVIE_ID = "id";
        final String MOVIE_TITLE = "title";
        final String MOVIE_RELEASE_YEAR = "year";
        final String MOVIE_MPAA_RATING = "mpaa_rating";
        final String MOVIE_DURATION = "runtime";

        final String MOVIE_RATINGS = "ratings";
        final String MOVIE_RATINGS_AUDIENCE = "audience_score";

        final String MOVIE_SYNOPSIS = "synopsis";

        final String MOVIE_POSTER = "posters";
        final String MOVIE_POSTER_THUMBNAIL = "thumbnail";


        try {
            JSONObject movieJson = new JSONObject(movieJsonString);

            JSONArray movieArray = movieJson.getJSONArray(MOVIE_MOVIES);

            Vector<ContentValues> cVVector = new Vector<ContentValues>(movieArray.length());


            for (int i = 0; i < movieArray.length(); i++) {
                String id;
                String title;
                String releaseYear;
                String mpaaRating;
                String duration;
                String ratingScore;
                String synopsis;
                String posterLink;

                JSONObject movie = movieArray.getJSONObject(i);

                id = movie.getString(MOVIE_ID);
                title = movie.getString(MOVIE_TITLE);
                Log.d(LOG_TAG, title);

                releaseYear = movie.getString(MOVIE_RELEASE_YEAR);
                Log.d(LOG_TAG, releaseYear);

                mpaaRating = movie.getString(MOVIE_MPAA_RATING);
                duration = movie.getString(MOVIE_DURATION);

                JSONObject rating = movie.getJSONObject(MOVIE_RATINGS);
                ratingScore = rating.getString(MOVIE_RATINGS_AUDIENCE);

                synopsis = movie.getString(MOVIE_SYNOPSIS);

                JSONObject poster = movie.getJSONObject(MOVIE_POSTER);
                posterLink = poster.getString(MOVIE_POSTER_THUMBNAIL);

                ContentValues movieValues = new ContentValues();

                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_ID, id);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_TITLE, title);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR, releaseYear);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_RATING, mpaaRating);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_DURATION, duration);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SCORE, ratingScore);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS, synopsis);
                movieValues.put(MovieContract.MovieEntry.COLUMN_MOVIE_POSTER, posterLink);

                cVVector.add(movieValues);


            }

            int inserted = 0;
            // add to database
            if (cVVector.size() > 0) {
                ContentValues[] cvArray = new ContentValues[cVVector.size()];
                cVVector.toArray(cvArray);
                getContext().getContentResolver().bulkInsert(MovieContract.MovieEntry.CONTENT_URI, cvArray);


            }

            Log.d(LOG_TAG, "Sync Complete. " + cVVector.size() + " Inserted");

        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


    }


    /**
     * Helper method to schedule the sync adapter periodic execution
     */
    public static void configurePeriodicSync(Context context, int syncInterval, int flexTime) {
        Account account = getSyncAccount(context);
        String authority = context.getString(R.string.content_authority);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            // we can enable inexact timers in our periodic sync
            SyncRequest request = new SyncRequest.Builder().
                    syncPeriodic(syncInterval, flexTime).
                    setSyncAdapter(account, authority).
                    setExtras(new Bundle()).build();
            ContentResolver.requestSync(request);
        } else {
            ContentResolver.addPeriodicSync(account,
                    authority, new Bundle(), syncInterval);
        }
    }

    /**
     * Helper method to have the sync adapter sync immediately
     *
     * @param context The context used to access the account service
     */
    public static void syncImmediately(Context context) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
        bundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
        ContentResolver.requestSync(getSyncAccount(context),
                context.getString(R.string.content_authority), bundle);
    }

    /**
     * Helper method to get the fake account to be used with SyncAdapter, or make a new one
     * if the fake account doesn't exist yet.  If we make a new account, we call the
     * onAccountCreated method so we can initialize things.
     *
     * @param context The context used to access the account service
     * @return a fake account.
     */
    public static Account getSyncAccount(Context context) {
        // Get an instance of the Android account manager
        AccountManager accountManager =
                (AccountManager) context.getSystemService(Context.ACCOUNT_SERVICE);

        // Create the account type and default account
        Account newAccount = new Account(
                context.getString(R.string.app_name), context.getString(R.string.sync_account_type));

        // If the password doesn't exist, the account doesn't exist
        if (null == accountManager.getPassword(newAccount)) {

        /*
         * Add the account and account type, no password or user data
         * If successful, return the Account object, otherwise report an error.
         */
            if (!accountManager.addAccountExplicitly(newAccount, "", null)) {
                return null;
            }
            /*
             * If you don't set android:syncable="true" in
             * in your <provider> element in the manifest,
             * then call ContentResolver.setIsSyncable(account, AUTHORITY, 1)
             * here.
             */

            onAccountCreated(newAccount, context);
        }
        return newAccount;
    }

    private static void onAccountCreated(Account newAccount, Context context) {

        MovieSyncAdapter.configurePeriodicSync(context, SYNC_INTERVAL, SYNC_FLEXTIME);

        ContentResolver.setSyncAutomatically(newAccount, context.getString(R.string.content_authority), true);

        syncImmediately(context);
    }

    public static void initializeSyncAdapter(Context context) {
        getSyncAccount(context);
    }
}

