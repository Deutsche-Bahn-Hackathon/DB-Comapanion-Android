package com.sampleapplication;

import android.app.Application;

import com.sampleapplication.data.network.RestClient;
import com.sampleapplication.data.realm.RealmHelper;

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
    }


    private void initLogging() {
        Timber.plant(new Timber.DebugTree());
    }

    private void initRealm() {
        // Initialize realms. We do not need to init the user realm helper here as it will get
        // initialized as soon as it is needed.
        Realm.init(this);

        RealmLog.setLevel(LogLevel.INFO);
        RealmLog.add((level, tag, throwable, message) -> {
            Timber.tag(tag);
            Timber.log(level, throwable, message);
        });

        RealmHelper.init();
    }

    public void initBeacons() {
        //if (Utils.areBeaconsEnabled(this)) {
        Timber.e("Starting beacons");

        //BeaconHandler beaconHandler = BeaconHandler.get(getApplicationContext());
        //beaconHandler.start();
        /*} else {
            Timber.w("Beacons are disabled");
        }*/
    }
}