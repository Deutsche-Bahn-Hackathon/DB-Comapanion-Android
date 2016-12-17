package com.dbhackathon.beacon.train;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Build;
import android.os.SystemClock;

import com.dbhackathon.beacon.AbsBeaconHandler;
import com.dbhackathon.beacon.BeaconStorage;
import com.dbhackathon.beacon.notification.CurrentTrip;
import com.dbhackathon.beacon.notification.TripNotification;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public final class TrainBeaconHandler extends AbsBeaconHandler {

    public static final String UUID = "e923b236-f2b7-4a83-bb74-cfb7fa44cab8";
    public static final String IDENTIFIER = "BUS";

    private static final int TIMEOUT;

    static {
        TIMEOUT = Build.DEVICE.startsWith("zeroflte") ? 20000 : 10000;
    }

    private static final int TIMER_INTERVAL = (int) TimeUnit.SECONDS.toMillis(150);
    private static final int BUS_LAST_SEEN_THRESHOLD = (int) TimeUnit.SECONDS.toMillis(180);

    private static final int MAX_BEACON_DISTANCE = 5;

    private final BeaconStorage mBeaconStorage;

    @SuppressLint("StaticFieldLeak")
    private static TrainBeaconHandler INSTANCE;

    private final Map<Integer, TrainBeacon> mBeaconMap = new ConcurrentHashMap<>();

    private byte mCycleCounter;


    private TrainBeaconHandler(Context context) {
        super(context);

        mBeaconStorage = BeaconStorage.getInstance(context);

        mBeaconMap.putAll(mBeaconStorage.getBeaconMap());
        deleteInvisibleBeacons();
        mBeaconStorage.writeBeaconMap(Collections.emptyMap());

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                Timber.i("Running timer");
                inspectBeacons();
            }
        }, 0, TIMER_INTERVAL);
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
            int minor = beacon.getId2().toInt();

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
            trainBeacon = new TrainBeacon(major);
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

            if (beacon.lastSeen + BUS_LAST_SEEN_THRESHOLD < System.currentTimeMillis()) {
                mBeaconMap.remove(entry.getKey());

                Timber.e("Removed beacon %d", entry.getKey());

                if (currentTrip != null && currentTrip.getId() == entry.getValue().id) {
                    mBeaconStorage.saveCurrentTrip(null);
                }
            } else if (beacon.lastSeen + TIMEOUT < System.currentTimeMillis()) {
                if (currentTrip != null && currentTrip.getId() == beacon.id) {
                    hideCurrentTrip(currentTrip);
                }
            }
        }
    }


    // ========================================= TRIP ==============================================

    private void updateCurrentTrip() {
        mCycleCounter++;

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

        if (currentTrip != null && currentTrip.beacon.id == beacon.id) {
            if (beacon.lastSeen + TIMEOUT >= System.currentTimeMillis()) {
                Timber.i("Seen: %s", beacon.lastSeen + TIMEOUT - System.currentTimeMillis());

                currentTrip.setBeacon(beacon);

                TripNotification.show(mContext, currentTrip);

                mBeaconStorage.saveCurrentTrip(currentTrip);
            }
        } else if (mCycleCounter % 3 == 0 && beacon.distance <= MAX_BEACON_DISTANCE) {
            mBeaconStorage.saveCurrentTrip(new CurrentTrip(mContext, beacon));

            mCycleCounter = 0;
        }
    }

    private void hideCurrentTrip(CurrentTrip trip) {
        Timber.i("hideCurrentTrip()");

        if (trip.isNotificationVisible()) {
            TripNotification.hide(mContext, trip);

            mBeaconStorage.saveCurrentTrip(trip);
        } else {
            Timber.i("Current trip has no notification.");
        }
    }
}