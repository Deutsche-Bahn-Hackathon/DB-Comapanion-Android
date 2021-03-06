package com.dbhackathon.beacon.train;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.dbhackathon.beacon.AbsBeaconHandler;
import com.dbhackathon.beacon.BeaconStorage;
import com.dbhackathon.beacon.notification.CurrentTrip;
import com.dbhackathon.beacon.notification.TripNotification;
import com.dbhackathon.data.model.Poi;
import com.dbhackathon.data.model.PoiResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.util.DeviceUtils;
import com.dbhackathon.util.NextObserver;
import com.dbhackathon.util.Notifications;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public final class TrainBeaconHandler extends AbsBeaconHandler implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    public static final String UUID = "1f771fca-e25a-3a7f-af4e-1745a7be89af";
    public static final String IDENTIFIER = "BUS";

    private static final int TIMEOUT;

    static {
        TIMEOUT = Build.DEVICE.startsWith("zeroflte") ? 20000 : 10000;
    }

    private static final int BEACON_TIMER_INTERVAL = (int) TimeUnit.SECONDS.toMillis(150);
    private static final int TRAIN_LAST_SEEN_THRESHOLD = (int) TimeUnit.SECONDS.toMillis(180);

    private static final int POI_TIMER_INTERVAL = (int) TimeUnit.MINUTES.toMillis(2);

    private final BeaconStorage mBeaconStorage;

    @SuppressLint("StaticFieldLeak")
    private static TrainBeaconHandler INSTANCE;

    private final Map<Integer, TrainBeacon> mBeaconMap = new ConcurrentHashMap<>();

    private Subscription mSubscription;

    private GoogleApiClient mApiClient;

    private LatLng mPos;

    private TrainBeaconHandler(Context context) {
        super(context);

        mBeaconStorage = BeaconStorage.getInstance(context);

        mBeaconMap.putAll(mBeaconStorage.getBeaconMap());
        deleteInvisibleBeacons();
        mBeaconStorage.writeBeaconMap(Collections.emptyMap());

        mApiClient = new GoogleApiClient.Builder(mContext)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();

        Timer beaconTimer = new Timer();
        beaconTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timber.i("Running timer");
                inspectBeacons();
            }
        }, 0, BEACON_TIMER_INTERVAL);

        Timer poiTimer = new Timer();
        poiTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkPois();
            }
        }, 0, POI_TIMER_INTERVAL);
    }

    public static TrainBeaconHandler getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (TrainBeaconHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new TrainBeaconHandler(context);
                }
            }
        }

        return INSTANCE;
    }


    @Override
    public void didExitRegion() {
        Timber.w("didExitRegion()");

        // Clear all beacon maps
        mBeaconStorage.writeBeaconMap(Collections.emptyMap());

        deleteInvisibleBeacons();

        CurrentTrip currentTrip = mBeaconStorage.getCurrentTrip();
        if (currentTrip != null) {
            hideCurrentTrip(currentTrip);
        }
    }

    @Override
    public void didRangeBeacons(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            int major = beacon.getId2().toInt();
            int minor = beacon.getId3().toInt();

            validateBeacon(beacon, major, minor);
        }

        deleteInvisibleBeacons();
        updateCurrentTrip();

        mBeaconStorage.writeBeaconMap(mBeaconMap);
    }

    @Override
    public void validateBeacon(Beacon beacon, int major, int minor) {
        TrainBeacon trainBeacon;

        if (mBeaconMap.containsKey(major)) {
            trainBeacon = mBeaconMap.get(major);

            trainBeacon.seen();
            trainBeacon.distance = beacon.getDistance();

            Timber.w("Vehicle %d, seen: %d, distance: %f",
                    major, trainBeacon.seenSeconds, trainBeacon.distance);
        } else {
            trainBeacon = new TrainBeacon(major, minor);
            mBeaconMap.put(major, trainBeacon);

            Timber.e("Added vehicle %d", major);
        }
    }


    private void inspectBeacons() {
        didRangeBeacons(Collections.emptyList());

        new Thread(() -> {
            SystemClock.sleep(5_000);
            didRangeBeacons(Collections.emptyList());
            SystemClock.sleep(20_000);
            didRangeBeacons(Collections.emptyList());
        }).start();
    }

    private void deleteInvisibleBeacons() {
        Timber.i("deleteInvisibleBeacons()");

        CurrentTrip currentTrip = mBeaconStorage.getCurrentTrip();

        for (Map.Entry<Integer, TrainBeacon> entry : mBeaconMap.entrySet()) {
            TrainBeacon beacon = entry.getValue();

            if (beacon.lastSeen + TRAIN_LAST_SEEN_THRESHOLD < System.currentTimeMillis()) {
                mBeaconMap.remove(entry.getKey());

                Timber.e("Removed beacon %d", entry.getKey());

                if (currentTrip != null && currentTrip.getId() == entry.getValue().major) {
                    mBeaconStorage.saveCurrentTrip(null);
                }
            } else if (beacon.lastSeen + TIMEOUT < System.currentTimeMillis()) {
                if (currentTrip != null && currentTrip.getId() == beacon.major) {
                    hideCurrentTrip(currentTrip);
                }
            }
        }
    }


    // ========================================= TRIP ==============================================

    private void updateCurrentTrip() {
        TrainBeacon beacon = null;

        for (Map.Entry<Integer, TrainBeacon> entry : mBeaconMap.entrySet()) {
            TrainBeacon value = entry.getValue();

            if ((beacon == null || value.getStartDate().before(beacon.getStartDate()))
                    && value.lastSeen + 30000 > System.currentTimeMillis()) {
                beacon = value;
            }
        }

        if (beacon == null) {
            return;
        }

        CurrentTrip currentTrip = mBeaconStorage.getCurrentTrip();

        if (currentTrip != null && currentTrip.beacon.major == beacon.major) {
            if (beacon.lastSeen + TIMEOUT >= System.currentTimeMillis()) {
                Timber.i("Seen: %s", beacon.lastSeen + TIMEOUT - System.currentTimeMillis());

                currentTrip.setBeacon(beacon);

                TripNotification.show(mContext, currentTrip);

                mBeaconStorage.saveCurrentTrip(currentTrip);
            }
        } else {
            mBeaconStorage.saveCurrentTrip(new CurrentTrip(mContext, beacon));
        }
    }

    private void hideCurrentTrip(CurrentTrip trip) {
        Timber.i("hideCurrentTrip()");

        TripNotification.hide(mContext, trip);

        mBeaconStorage.saveCurrentTrip(trip);
    }


    // ========================================= POI ===============================================

    private void checkPois() {
        if (mSubscription != null) {
            mSubscription.unsubscribe();
        }

        if (mPos == null) {
            return;
        }


        TrainApi trainApi = RestClient.ADAPTER.create(TrainApi.class);
        mSubscription = trainApi.getPois(mPos.latitude, mPos.longitude)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextObserver<PoiResponse>() {
                    @Override
                    public void onNext(PoiResponse poiResponse) {
                        for (Poi poi : poiResponse.results()) {
                            Notifications.poi(mContext, poi);
                        }
                    }
                });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (!DeviceUtils.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Timber.e("Missing permission!!!!1111!");
            return;
        }

        //noinspection MissingPermission
        Location location = LocationServices.FusedLocationApi.getLastLocation(mApiClient);
        mPos = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {
        mApiClient.connect();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Timber.e("Could not connect!");
    }
}