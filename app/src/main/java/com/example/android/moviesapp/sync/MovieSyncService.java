package com.example.android.moviesapp.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by SusanaLauH on 3/21/2015.
 */
public class MovieSyncService extends Service {
    private static final Object sSyncAdapterLock = new Object();
    private static MovieSyncAdapter movieSyncAdapter = null;

    @Override
    public void onCreate() {
        Log.d("MovieSyncService", "onCreate - MovieSyncService");
        synchronized (sSyncAdapterLock) {
            if (movieSyncAdapter == null) {
                movieSyncAdapter = new MovieSyncAdapter(getApplicationContext(), true);
            }
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return movieSyncAdapter.getSyncAdapterBinder();
    }
}