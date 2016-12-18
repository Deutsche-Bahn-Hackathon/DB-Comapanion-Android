package com.dbhackathon.beacon;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;

import com.dbhackathon.BuildConfig;
import com.dbhackathon.beacon.notification.CurrentTrip;
import com.dbhackathon.beacon.notification.TripNotification;
import com.dbhackathon.beacon.station.StationBeacon;
import com.dbhackathon.beacon.train.TrainBeacon;
import com.dbhackathon.util.AutoValueGsonAdapterFactory;
import com.dbhackathon.util.Utils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import timber.log.Timber;

public final class BeaconStorage {

    /**
     * Preferences which contain the saved bus beacons to keep the trip progress
     * in case the user quits the app. The saved beacons get restored as soon as
     * the beacon handler starts.
     */
    private static final String STORAGE_NAME = BuildConfig.APPLICATION_ID + "_beacons";

    private static final String PREF_BEACON_CURRENT_TRIP = "pref_beacon_current_trip";
    private static final String PREF_BEACON_CURRENT_BUS_STOP = "pref_beacon_current_bus_stop";
    private static final String PREF_BUS_BEACON_MAP = "pref_bus_beacon_map";
    private static final String PREF_BUS_BEACON_MAP_LAST = "pref_bus_beacon_map_last";

    private static final long BEACON_MAP_TIMEOUT = TimeUnit.SECONDS.toMillis(240);

    private final SharedPreferences mPrefs;
    private final Context mContext;

    @SuppressLint("StaticFieldLeak")
    private static BeaconStorage INSTANCE;

    private CurrentTrip mCurrentTrip;
    private StationBeacon mCurrentBusStop;

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapterFactory(new AutoValueGsonAdapterFactory())
            .create();

    private BeaconStorage(Context context) {
        mContext = context;

        mPrefs = context.getSharedPreferences(STORAGE_NAME, Context.MODE_PRIVATE);
    }

    public static BeaconStorage getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconStorage.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BeaconStorage(context);
                }
            }
        }

        return INSTANCE;
    }

    public void clear() {
        Timber.e("Clearing beacon storage");

        mPrefs.edit().remove(PREF_BEACON_CURRENT_TRIP).apply();
        mPrefs.edit().remove(PREF_BUS_BEACON_MAP).apply();
        mPrefs.edit().remove(PREF_BUS_BEACON_MAP_LAST).apply();
    }


    // =================================== CURRENT TRIP ============================================

    public void saveCurrentTrip(CurrentTrip trip) {
        mCurrentTrip = trip;

        if (trip == null) {
            Timber.e("trip == null, cancelling notification");
            TripNotification.hide(mContext, null);

            mPrefs.edit().remove(PREF_BEACON_CURRENT_TRIP).apply();
        } else {
            try {
                String json = GSON.toJson(trip);
                mPrefs.edit().putString(PREF_BEACON_CURRENT_TRIP, json).apply();
            } catch (Exception e) {
                Utils.logException(e);
            }
        }
    }

    public CurrentTrip getCurrentTrip() {
        if (mCurrentTrip == null) {
            mCurrentTrip = readCurrentTrip();
        }

        return mCurrentTrip;
    }

    private CurrentTrip readCurrentTrip() {
        String json = mPrefs.getString(PREF_BEACON_CURRENT_TRIP, null);
        if (json == null) {
            return null;
        }

        try {
            CurrentTrip trip = GSON.fromJson(json, CurrentTrip.class);

            if (trip != null) {
                trip.setContext(mContext);
                trip.update();

                if (trip.getPath() != null && trip.getPath().isEmpty() ||
                        trip.getTimes() != null && trip.getTimes().isEmpty()) {
                    Timber.e("Path or times empty for current trip, will reset...");

                    trip.reset();
                }
            }

            return trip;
        } catch (Exception e) {
            Utils.logException(e);
        }

        return null;
    }

    public boolean hasCurrentTrip() {
        return getCurrentTrip() != null;
    }


    // ================================= CURRENT BUS STOP ==========================================

    /*public void saveCurrentBusStop(BusStopBeacon beacon) {
        mCurrentBusStop = beacon;

        if (beacon == null) {
            mPrefs.edit().remove(PREF_BEACON_CURRENT_BUS_STOP).apply();
        } else {
            try {
                String json = GSON.toJson(beacon);
                mPrefs.edit().putString(PREF_BEACON_CURRENT_BUS_STOP, json).apply();
            } catch (Exception e) {
                Utils.logException(e);
            }
        }
    }

    public BusStopBeacon getCurrentBusStop() {
        if (mCurrentBusStop == null) {
            mCurrentBusStop = readCurrentBusStop();
        }

        return mCurrentBusStop;
    }

    private BusStopBeacon readCurrentBusStop() {
        String json = mPrefs.getString(PREF_BEACON_CURRENT_BUS_STOP, null);
        if (json == null) {
            return null;
        }

        try {
            BusStopBeacon beacon = GSON.fromJson(json, BusStopBeacon.class);

            if (beacon != null) {
                com.davale.sasabus.data.realm.busstop.BusStop station =
                        BusStopRealmHelper.getStation(beacon.id);

                if (station == null) {
                    return null;
                }

                beacon.station = new BusStop(station);
            }

            return beacon;
        } catch (Exception e) {
            Utils.logException(e);
        }

        return null;
    }*/


    // ==================================== BEACON MAP =============================================

    public void writeBeaconMap(Map<Integer, TrainBeacon> map) {
        if (map == null) {
            mPrefs.edit().remove(PREF_BUS_BEACON_MAP_LAST).apply();
        } else {
            try {
                mPrefs.edit().putString(PREF_BUS_BEACON_MAP, GSON.toJson(map)).apply();
                mPrefs.edit().putLong(PREF_BUS_BEACON_MAP_LAST, System.currentTimeMillis()).apply();
            } catch (Exception e) {
                Utils.logException(e);
            }
        }
    }

    public Map<Integer, TrainBeacon> getBeaconMap() {
        long lastMapSave = 0;

        if (mPrefs.getLong(PREF_BUS_BEACON_MAP_LAST, -999) != -999) {
            lastMapSave = mPrefs.getLong(PREF_BUS_BEACON_MAP_LAST, -999);
        }

        if (lastMapSave != 0) {
            long difference = System.currentTimeMillis() - lastMapSave;

            if (difference < BEACON_MAP_TIMEOUT) {
                try {
                    String json = mPrefs.getString(PREF_BUS_BEACON_MAP, null);
                    if (json == null) {
                        return Collections.emptyMap();
                    }

                    Type type = new TypeToken<Map<Integer, TrainBeacon>>() {
                    }.getType();
                    return GSON.fromJson(json, type);
                } catch (Exception e) {
                    Utils.logException(e);
                }
            } else {
                saveCurrentTrip(null);
            }
        }

        return Collections.emptyMap();
    }
}
