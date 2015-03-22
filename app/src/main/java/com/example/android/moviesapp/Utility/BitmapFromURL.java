package com.example.android.moviesapp.Utility;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by SusanaLauH on 3/21/2015.
 */
public class BitmapFromURL {

    public Bitmap getBitmapFromURL(String src) {

        try

        {
            Log.e("src", src);
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            Log.e("Bitmap", "returned");
            return myBitmap;
        } catch (
                IOException e
                )

        {
            e.printStackTrace();
            Log.e("Exception", e.getMessage());
            return null;
        }
    }
}


