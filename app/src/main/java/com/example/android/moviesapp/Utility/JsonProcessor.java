package com.example.android.moviesapp.Utility;

import android.content.ContentValues;
import android.net.Uri;
import android.util.Log;

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
 * Created by SusanaLauH on 3/20/2015.
 */
public class JsonProcessor {

    public final String LOG_TAG = JsonProcessor.class.getSimpleName();
    String movieJsonString = null;

    public void processJson() {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String page_limit = "1";
        String page = "1";
        String country = "us";

        try

        {

            final String BASE_URL =
                    "http://api.rottentomatoes.com/api/public/v1.0/lists/movies/in_theaters.json?";

            final String PAGE_LIMIT_PARAM = "page_limit";
            final String PAGE_PARAM = "page";
            final String COUNTRY_PARAM = "country";

            Uri builtUri = Uri.parse(BASE_URL).buildUpon()
                    .appendQueryParameter(PAGE_LIMIT_PARAM, page_limit)
                    .appendQueryParameter(PAGE_PARAM, page)
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
            // If the code didn't successfully get the weather data, there's no point in attempting
            // to parse it.
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
    }


    /**
     * Take the String representing the complete forecast in JSON Format and
     * pull out the data we need to construct the Strings needed for the wireframes.
     * <p/>
     * Fortunately parsing is easy:  constructor takes the JSON string and converts it
     * into an Object hierarchy for us.
     */
    private void getMoviesFromJson(String forecastJsonStr) throws JSONException {


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

            // Insert the new weather information into the database
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
                releaseYear = movie.getString(MOVIE_RELEASE_YEAR);
                mpaaRating = movie.getString(MOVIE_MPAA_RATING);
                duration = movie.getString(MOVIE_DURATION);

                JSONObject rating = movie.getJSONObject(MOVIE_RATINGS);
                ratingScore = rating.getString(MOVIE_RATINGS_AUDIENCE);

                synopsis = movie.getString(MOVIE_SYNOPSIS);

                JSONObject poster = movie.getJSONObject(MOVIE_POSTER);
                posterLink = rating.getString(MOVIE_POSTER_THUMBNAIL);

                Log.d(LOG_TAG, id);
                Log.d(LOG_TAG, title);
                Log.d(LOG_TAG, releaseYear);
                Log.d(LOG_TAG, mpaaRating);
                Log.d(LOG_TAG, duration);
                Log.d(LOG_TAG, ratingScore);
                Log.d(LOG_TAG, synopsis);
                Log.d(LOG_TAG, ratingScore);
                Log.d(LOG_TAG, posterLink);

            }


        } catch (JSONException e) {
            Log.e(LOG_TAG, e.getMessage(), e);
            e.printStackTrace();
        }


    }
}