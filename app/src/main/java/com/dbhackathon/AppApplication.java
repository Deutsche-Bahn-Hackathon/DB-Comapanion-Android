package com.dbhackathon;

import android.app.Application;

import com.dbhackathon.beacon.BeaconHandler;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.realm.RealmHelper;
import com.google.firebase.messaging.FirebaseMessaging;

import io.realm.Realm;
import io.realm.log.LogLevel;
import io.realm.log.RealmLog;
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
        initRealm();

        // Initialize the rest adapter and auth helper.
        RestClient.init(this);

        initBeacons();

        FirebaseMessaging.getInstance().subscribeToTopic("ice1206");
    }


    private void initLogging() {
        Timber.plant(new Timber.DebugTree());
    }

    private void initRealm() {
        Realm.init(this);

        RealmLog.setLevel(LogLevel.INFO);
        RealmLog.add((level, tag, throwable, message) -> {
            Timber.tag(tag);
            Timber.log(level, throwable, message);
        });

        RealmHelper.init();
    }

    public void initBeacons() {
        Timber.e("Starting beacons");

        BeaconHandler beaconHandler = BeaconHandler.get(getApplicationContext());
        beaconHandler.start();
    }
}