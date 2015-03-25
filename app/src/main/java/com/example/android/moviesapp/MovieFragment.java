package com.example.android.moviesapp;

import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.android.moviesapp.data.MovieContract;
import com.example.android.moviesapp.sync.MovieSyncAdapter;

/**
 * Created by SusanaLauH on 3/14/2015.
 */
public class MovieFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String LOG_TAG = "TAG";
    private MovieAdapter mMovieAdapter;
    //private MovieSyncAdapter mMovieSyncAdapter;

    private ListView mListView;
    private int mPosition = ListView.INVALID_POSITION;

    private static final String SELECTED_KEY = "selected_position";

    private static final int MOVIE_LOADER = 0;


    private static final String[] MOVIE_COLUMNS = {
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

    // These indices are tied to FORECAST_COLUMNS.  If FORECAST_COLUMNS changes, these
    // must change.
    static final int COL_MOVIE_ID = 1;
    static final int COL_MOVIE_TITLE = 2;
    static final int COL_MOVIE_RELEASE_YEAR = 3;
    static final int COL_MOVIE_RATING = 4;
    static final int COL_MOVIE_DURATION = 5;
    static final int COL_MOVIE_SCORE = 6;
    static final int COL_MOVIE_SYNOPSIS = 7;
    static final int COL_MOVIE_POSTER = 8;


    public interface Callback {
        public void onItemSelected(Uri dateUri);
    }

    public MovieFragment() {
    }

    //called before onCreateView()
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Add this line in order for this fragment to handle menu events.
        setHasOptionsMenu(true);
    }


    public void updateMovie() {
        MovieSyncAdapter.syncImmediately(getActivity());
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        getLoaderManager().initLoader(MOVIE_LOADER, null, this);
        super.onActivityCreated(savedInstanceState);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.movie_fragment, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        /*int id = item.getItemId();

        if (id == R.id.action_refresh) {
            updateMovie();
            getLoaderManager().restartLoader(MOVIE_LOADER, null, this);
            return true;
        }*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mMovieAdapter = new MovieAdapter(getActivity(), null, 0);

        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        // The CursorAdapter will take data from our cursor and populate the ListView.


        mListView = (ListView) rootView.findViewById(R.id.listView_movies);
        mListView.setAdapter(mMovieAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                // CursorAdapter returns a cursor at the correct position for getItem(), or null
                // if it cannot seek to that position.
                Cursor cursor = (Cursor) adapterView.getItemAtPosition(position);
                if (cursor != null) {
                    ((Callback) getActivity()).onItemSelected(MovieContract.MovieEntry.buildMovieWithSpecificID(l));
                }
                mPosition = position;
                Log.d(LOG_TAG, "Value of position:: " + position);
                Log.d(LOG_TAG, "Value of l:: " + l);
                Log.d(LOG_TAG, "Cursor at position:  " + position + "is: " +  cursor.getString(COL_MOVIE_TITLE));
                Log.d(LOG_TAG, "contentUri No ID" + MovieContract.MovieEntry.buildMovieUri());

            }
        });

        // If there's instance state, mine it for useful information.
        // The end-goal here is that the user never knows that turning their device sideways
        // does crazy lifecycle related things.  It should feel like some stuff stretched out,
        // or magically appeared to take advantage of room, but data or place in the app was never
        // actually *lost*.
        if (savedInstanceState != null && savedInstanceState.containsKey(SELECTED_KEY)) {
            // The listview probably hasn't even been populated yet.  Actually perform the
            // swapout in onLoadFinished.
            mPosition = savedInstanceState.getInt(SELECTED_KEY);
        }

        // mMovieAdapter.setUseTodayLayout(mUseTodayLayout);
        Log.d(LOG_TAG, "OnCreateView- Movie Fragment");

        return rootView;
    }


    @Override
    public void onSaveInstanceState(Bundle outState) {
        // When tablets rotate, the currently selected list item needs to be saved.
        // When no item is selected, mPosition will be set to Listview.INVALID_POSITION,
        // so check for that before storing.
        if (mPosition != ListView.INVALID_POSITION) {
            outState.putInt(SELECTED_KEY, mPosition);
        }
        super.onSaveInstanceState(outState);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Log.d(LOG_TAG, "OnCreateLoader- Movie Fragment");

        Uri movieUri = MovieContract.MovieEntry.buildMovieUri();
        Log.d (LOG_TAG, "Movie Fragment OnCreate Loader" + movieUri);
        return new CursorLoader(getActivity(),
                movieUri,
                MOVIE_COLUMNS,
                null,
                null,
                null
        );

    }


    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mMovieAdapter.swapCursor(data);
        if (mPosition != ListView.INVALID_POSITION) {

            mListView.smoothScrollToPosition(mPosition);
        }
        Log.d(LOG_TAG, "OnLoaderFinished- Movie Fragment");

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mMovieAdapter.swapCursor(null);
    }


}
