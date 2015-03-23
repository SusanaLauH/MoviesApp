package com.example.android.moviesapp;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.android.moviesapp.Utility.BitmapFromURL;

/**
 * Created by SusanaLauH on 3/14/2015.
 */
public class MovieAdapter extends CursorAdapter {
    public static final String LOG_TAG = "TAG";

    public static class ViewHolder {
        public final ImageView posterView;
        public final TextView titleView;
        public final TextView scoreView;


        public ViewHolder(View view) {
            posterView = (ImageView) view.findViewById(R.id.list_item_icon);
            titleView = (TextView) view.findViewById(R.id.list_item_movie_title);
            scoreView = (TextView) view.findViewById(R.id.list_item_movie_score);

        }
    }

    public MovieAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }


    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        View view = LayoutInflater.from(context).inflate(R.layout.list_item_movie, parent, false);

        ViewHolder viewHolder = new ViewHolder(view);
        view.setTag(viewHolder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Log.d(LOG_TAG, "MovieAdapter bindView");
        ViewHolder viewHolder = (ViewHolder) view.getTag();
        BitmapFromURL bitmapFromURL = new BitmapFromURL();
        String moviePoster = cursor.getString(MovieFragment.COL_MOVIE_POSTER);
        Bitmap movieImage = bitmapFromURL.getBitmapFromURL(moviePoster);
        viewHolder.posterView.setImageBitmap(movieImage);


        String movieTitle = cursor.getString(MovieFragment.COL_MOVIE_TITLE);
        viewHolder.titleView.setText(movieTitle);

        String movieScore = cursor.getString(MovieFragment.COL_MOVIE_SCORE);
        viewHolder.scoreView.setText(movieScore);


    }


}
