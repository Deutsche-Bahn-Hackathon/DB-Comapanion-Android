package com.dbhackathon.beacon.station;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;

import com.dbhackathon.beacon.AbsBeaconHandler;
import com.dbhackathon.beacon.notification.DeparturesNotification;

import org.altbeacon.beacon.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ConcurrentHashMap;

import timber.log.Timber;

public final class StationBeaconHandler extends AbsBeaconHandler {

    public static final String UUID = "e923b236-f2b7-4a83-bb74-cfb7fa44cab8";
    public static final String IDENTIFIER = "STATION";

    private final Map<Integer, StationBeacon> mBeaconMap = new ConcurrentHashMap<>();

    private static final int BEACON_REMOVAL_TIME = 10000;
    private static final int TIMER_INTERVAL = 5000;

    private StationBeacon mCurrentStation;

    @SuppressLint("StaticFieldLeak")
    private static StationBeaconHandler INSTANCE;

    private Timer TIMER;

    private final Handler HANDLER = new Handler();
    private final Runnable STOP_TIMER = new Runnable() {
        @Override
        public void run() {
            Timber.w("Stopped timer");

            if (TIMER != null) {
                TIMER.cancel();
                TIMER.purge();
            }

            mBeaconMap.clear();
        }
    };


    private StationBeaconHandler(Context context) {
        super(context);
    }

    public static StationBeaconHandler getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (StationBeaconHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StationBeaconHandler(context);
                }
            }
        }

        return INSTANCE;
    }


    @Override
    public void didEnterRegion() {
        Timber.w("Starting timer");

        mBeaconMap.clear();

        HANDLER.removeCallbacks(STOP_TIMER);

        if (TIMER != null) {
            TIMER.cancel();
            TIMER.purge();
        }

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Timber.i("Running timer");

                for (Map.Entry<Integer, StationBeacon> entry : mBeaconMap.entrySet()) {
                    StationBeacon beacon = entry.getValue();

                    if (beacon.lastSeen < System.currentTimeMillis() - BEACON_REMOVAL_TIME) {
                        if (mCurrentStation != null && mCurrentStation.major == beacon.major) {
                            Timber.w("Removed current bus stop %d", mCurrentStation.major);

                            DeparturesNotification.hide(mContext);

                            mCurrentStation = null;
                        }

                        Timber.e("Removed beacon %d", beacon.major);

                        mBeaconMap.remove(beacon.major);
                    }
                }
            }
        };

        TIMER = new Timer();
        TIMER.scheduleAtFixedRate(task, BEACON_REMOVAL_TIME, TIMER_INTERVAL);
    }

    @Override
    public void didExitRegion() {
        Timber.w("Stopping bus stop beacon handler");

        DeparturesNotification.hide(mContext);

        HANDLER.postDelayed(STOP_TIMER, BEACON_REMOVAL_TIME + TIMER_INTERVAL);
    }

    @Override
    public void didRangeBeacons(Collection<Beacon> beacons) {
        for (Beacon beacon : beacons) {
            int major = beacon.getId2().toInt();
            int minor = beacon.getId3().toInt();

            validateBeacon(beacon, major, minor);
        }

        List<StationBeacon> list = new ArrayList<>(mBeaconMap.values());
        Collections.sort(list, (lhs, rhs) -> (int) (lhs.distance - rhs.distance));

        if (!beacons.isEmpty()) {
            mCurrentStation = list.get(0);
        }

        showNotificationIfNeeded();
    }

    @Override
    public void validateBeacon(Beacon beacon, int major, int minor) {
        if (mBeaconMap.containsKey(major)) {
            StationBeacon beaconInfo = mBeaconMap.get(major);

            beaconInfo.seen();
            beaconInfo.distance = beacon.getDistance();

            Timber.i("Bus stop %d, seen: %d, distance: %f", major,
                    beaconInfo.seenSeconds, beaconInfo.distance);
        } else {
            mBeaconMap.put(major, new StationBeacon(major, minor));

            Timber.w("Added bus stop %d", major);
        }
    }


    private void showNotificationIfNeeded() {
        if (mCurrentStation != null) {
            DeparturesNotification.show(mContext, mCurrentStation);
        }
    }

    public StationBeacon getCurrentStation() {
        return mCurrentStation;
    }
}