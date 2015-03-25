package com.example.android.moviesapp;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.ShareActionProvider;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.data.MovieContract;
import com.koushikdutta.ion.Ion;

/**
 * Created by SusanaLauH on 3/21/2015.
 */
public class MovieDetailFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final String LOG_TAG = MovieDetailFragment.class.getSimpleName();

    static final String DETAIL_URI = "URI";
    private static final String FORECAST_SHARE_HASHTAG = " #MovieBuddy App";

    private ShareActionProvider mShareActionProvider;
    private String mMovie;
    private Uri mUri;

    private static final int DETAIL_LOADER = 0;


    private static final String[] DETAIL_COLUMNS = {
            MovieContract.MovieEntry.TABLE_NAME + "." + MovieContract.MovieEntry._ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_ID,
            MovieContract.MovieEntry.COLUMN_MOVIE_TITLE,
            MovieContract.MovieEntry.COLUMN_MOVIE_RELEASE_YEAR,
            MovieContract.MovieEntry.COLUMN_MOVIE_RATING,
            MovieContract.MovieEntry.COLUMN_MOVIE_DURATION,
            MovieContract.MovieEntry.COLUMN_MOVIE_SCORE,
            MovieContract.MovieEntry.COLUMN_MOVIE_SYNOPSIS,
            MovieContract.MovieEntry.COLUMN_MOVIE_POSTER
    };


    // These indices are tied to DETAIL_COLUMNS.  If DETAIL_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 0;
    static final int COL_MOVIE_ROTTEN_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_RELEASE_YEAR = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_DURATION = 5;
    static final int COL_MOVIE_SCORE = 6;
    static final int COL_MOVIE_SYNOPSIS = 7;
    static final int COL_MOVIE_POSTER = 8;

    private ImageView mMoviePosterView;
    private TextView mMovieTitleView;
    private TextView mMovieYearView;
    private TextView mMovieDurationView;
    private TextView mMovieRatingView;
    private TextView mMovieScoreView;
    private TextView mMovieSynopsisView;


    public MovieDetailFragment() {
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Bundle arguments = getArguments();
        if (arguments != null) {
            mUri = arguments.getParcelable(MovieDetailFragment.DETAIL_URI);
            Log.d(LOG_TAG, "MovieDetail Fragment, getting arguments" + mUri);
        }

        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        mMoviePosterView = (ImageView) rootView.findViewById(R.id.movie_detail_image);
        mMovieTitleView = (TextView) rootView.findViewById(R.id.movie_detail_title);
        mMovieYearView = (TextView) rootView.findViewById(R.id.movie_detail_year);
        mMovieDurationView = (TextView) rootView.findViewById(R.id.movie_detail_duration);
        mMovieRatingView = (TextView) rootView.findViewById(R.id.movie_detail_rating);
        mMovieScoreView = (TextView) rootView.findViewById(R.id.movie_detail_score);
        mMovieSynopsisView = (TextView) rootView.findViewById(R.id.movie_detail_synopsis);
        return rootView;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        // Inflate the menu; this adds items to the action bar if it is present.
        inflater.inflate(R.menu.detail_fragment, menu);

        // Retrieve the share menu item
        MenuItem menuItem = menu.findItem(R.id.action_share);

        // Get the provider and hold onto it to set/change the share intent.
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menuItem);

        // If onLoadFinished happens before this, we can go ahead and set the share intent now.
        if (mMovie != null) {
            mShareActionProvider.setShareIntent(createShareMovieIntent());
        }
    }

    private Intent createShareMovieIntent() {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, mMovie + FORECAST_SHARE_HASHTAG);
        return shareIntent;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(DETAIL_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (null != mUri) {
            Log.d(LOG_TAG, "onCreateLoader mUri" + mUri);
            // Now create and return a CursorLoader that will take care of
            // creating a Cursor for the data being displayed.
            return new CursorLoader(
                    getActivity(),
                    mUri,
                    DETAIL_COLUMNS,
                    null,
                    null,
                    null
            );
        }

        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null && data.moveToFirst()){
            //if (data != null) {
            Log.d(LOG_TAG, "On Load Finished  " + data.getString(COL_MOVIE_TITLE));
            Log.d(LOG_TAG, "Size  "+ data.getCount());

            String moviePoster = data.getString(COL_MOVIE_POSTER);

            Ion.with(mMoviePosterView)
                    .placeholder(R.drawable.movies_app_icon)
                    .error(R.drawable.movies_app_icon)
                            //.animateLoad(spinAnimation)
                            //.animateIn(fadeInAnimation)
                    .load(moviePoster);

            String movieTitle = data.getString(COL_MOVIE_TITLE);
            mMovieTitleView.setText(movieTitle);
            mMovie = movieTitle + " is now available on theaters!";

            String movieYear = data.getString(COL_MOVIE_RELEASE_YEAR);
            mMovieYearView.setText(movieYear);

            String movieDuration = data.getString(COL_MOVIE_DURATION);
            mMovieDurationView.setText(movieDuration);

            String movieRating = data.getString(COL_MOVIE_RATING);
            mMovieRatingView.setText(movieRating);

            String movieScore = data.getString(COL_MOVIE_SCORE);
            mMovieScoreView.setText(movieScore);

            String movieSynopsis = data.getString(COL_MOVIE_SYNOPSIS);
            mMovieSynopsisView.setText(movieSynopsis);



            // If onCreateOptionsMenu has already happened, we need to update the share intent now.
            if (mShareActionProvider != null) {
                mShareActionProvider.setShareIntent(createShareMovieIntent());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }


}
