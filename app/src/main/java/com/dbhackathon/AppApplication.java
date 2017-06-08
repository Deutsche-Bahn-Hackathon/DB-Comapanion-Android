package com.dbhackathon;

import android.app.Application;

import com.dbhackathon.beacon.BeaconHandler;
import com.dbhackathon.data.network.RestClient;
import com.google.firebase.messaging.FirebaseMessaging;

import timber.log.Timber;

/**
 * Main application which handles common app functionality like exception logging and
 * setting user preferences.
 *
 * @author Alex Lardschneider
 */
public class AppApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        initLogging();

        // Initialize the rest adapter and auth helper.
        RestClient.init(this);

        initBeacons();

        FirebaseMessaging.getInstance().subscribeToTopic("ice1206");
    }


    private void initLogging() {
        Timber.plant(new Timber.DebugTree());
    }

    public void initBeacons() {
        Timber.e("Starting beacons");

        BeaconHandler beaconHandler = BeaconHandler.get(getApplicationContext());
        beaconHandler.start();
    }
}