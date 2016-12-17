package com.dbhackathon.beacon;

import android.Manifest;
import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.RemoteException;

import com.dbhackathon.beacon.station.StationBeaconHandler;
import com.dbhackathon.beacon.train.TrainBeaconHandler;
import com.dbhackathon.util.DeviceUtils;
import com.dbhackathon.util.Utils;

import org.altbeacon.beacon.BeaconConsumer;
import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.BeaconParser;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;

import java.util.Arrays;

import timber.log.Timber;

/**
 * Beacon handler which scans for beacons in range. This scanner will be automatically stated
 * by the {@code AltBeacon} library as soon as it detects either a bus or bus stop beacon.
 *
 * @author Alex Lardschneider
 */
public final class BeaconHandler implements BeaconConsumer, BootstrapNotifier {

    private final Context mContext;

    private BeaconManager mBeaconManager;

    private Region mRegionTrain;
    private Region mRegionBusStop;

    private TrainBeaconHandler mTrainBeaconHandler;
    private StationBeaconHandler mBusStopBeaconHandler;

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private RegionBootstrap mBootstrap;

    @SuppressLint("StaticFieldLeak")
    private static BeaconHandler INSTANCE;

    private static boolean isListening;


    private BeaconHandler(Context context) {
        Timber.e("Creating beacon handlers");

        mContext = context.getApplicationContext();
    }

    public static BeaconHandler get(Context context) {
        if (INSTANCE == null) {
            synchronized (BeaconHandler.class) {
                if (INSTANCE == null) {
                    INSTANCE = new BeaconHandler(context);
                }
            }
        }

        return INSTANCE;
    }


    @Override
    public void onBeaconServiceConnect() {
        Timber.e("onBeaconServiceConnect()");

        mBeaconManager.addRangeNotifier((beacons, region) -> {
            if (!isListening) {
                Timber.e("didRangeBeaconsInRegion(): not listening");
                return;
            }

            Timber.i("didRangeBeaconsInRegion(): %s %s", region.getUniqueId(), beacons.size());

            switch (region.getUniqueId()) {
                case TrainBeaconHandler.IDENTIFIER:
                    mTrainBeaconHandler.didRangeBeacons(beacons);
                    break;
                case StationBeaconHandler.IDENTIFIER:
                    mBusStopBeaconHandler.didRangeBeacons(beacons);
                    break;
                default:
                    Timber.e("Unknown region id: %s", region.getUniqueId());
            }
        });
    }

    @Override
    public void didEnterRegion(Region region) {
        Timber.e("didEnterRegion() %s", region.getUniqueId());

        try {
            switch (region.getUniqueId()) {
                case TrainBeaconHandler.IDENTIFIER:
                    mBeaconManager.startRangingBeaconsInRegion(mRegionTrain);
                    mTrainBeaconHandler.didEnterRegion();
                    break;
                case StationBeaconHandler.IDENTIFIER:
                    mBeaconManager.startRangingBeaconsInRegion(mRegionBusStop);
                    mBusStopBeaconHandler.didEnterRegion();
                    break;
                default:
                    Timber.e("Unknown region id: %s", region.getUniqueId());
            }
        } catch (RemoteException e) {
            Utils.logException(e);
        }
    }

    @Override
    public void didExitRegion(Region region) {
        Timber.e("didExitRegion() %s", region.getUniqueId());

        try {
            switch (region.getUniqueId()) {
                case TrainBeaconHandler.IDENTIFIER:
                    mBeaconManager.stopRangingBeaconsInRegion(mRegionTrain);
                    mTrainBeaconHandler.didExitRegion();
                    break;
                case StationBeaconHandler.IDENTIFIER:
                    mBeaconManager.stopRangingBeaconsInRegion(mRegionBusStop);
                    mBusStopBeaconHandler.didExitRegion();
                    break;
                default:
                    Timber.e("Unknown region id: %s", region.getUniqueId());
            }
        } catch (RemoteException e) {
            Utils.logException(e);
        }
    }


    @Override
    public Context getApplicationContext() {
        return mContext;
    }

    @Override
    public void didDetermineStateForRegion(int state, Region region) {
        Timber.i("didDetermineStateForRegion(): state=%d", state);
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection connection, int flags) {
        return mContext.bindService(service, connection, flags);
    }

    @Override
    public void unbindService(ServiceConnection conn) {
        mContext.unbindService(conn);
    }


    public void start() {
        Timber.e("start()");

        if (isListening) {
            Timber.w("Already listening for beacons");
            return;
        }

        if (!DeviceUtils.hasPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION)) {
            Timber.e("Missing location permission");
            return;
        }

        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();
        if (adapter == null) {
            Timber.e("Unable to find a valid bluetooth adapter");
            return;
        }

        if (!adapter.isEnabled()) {
            Timber.e("Bluetooth adapter is disabled");
            return;
        }

        if (adapter.getState() == BluetoothAdapter.STATE_TURNING_OFF) {
            Timber.e("Bluetooth adapter is turning off");
            return;
        }

        if (adapter.getState() != BluetoothAdapter.STATE_ON) {
            Timber.e("Bluetooth adapter is not in state STATE_ON");
            return;
        }

        startInternal();
    }

    private void startInternal() {
        Timber.e("startInternal()");

        mTrainBeaconHandler = TrainBeaconHandler.getInstance(mContext);
        mBusStopBeaconHandler = StationBeaconHandler.getInstance(mContext);

        mBeaconManager = BeaconManager.getInstanceForApplication(mContext);
        mBeaconManager.setRegionStatePeristenceEnabled(false);

        mBeaconManager.getBeaconParsers().add(new BeaconParser()
                .setBeaconLayout("m:2-3=0215,i:4-19,i:20-21,i:22-23,p:24-24"));

        mBeaconManager.setForegroundScanPeriod(3000);
        mBeaconManager.setForegroundBetweenScanPeriod(0);

        mBeaconManager.setBackgroundScanPeriod(3000);
        mBeaconManager.setBackgroundBetweenScanPeriod(0);

        mRegionTrain = new Region(TrainBeaconHandler.IDENTIFIER, Identifier.parse(TrainBeaconHandler.UUID), null, null);
        mRegionBusStop = new Region(StationBeaconHandler.IDENTIFIER, Identifier.parse(StationBeaconHandler.UUID), null, null);

        mBootstrap = new RegionBootstrap(this, Arrays.asList(
                mRegionTrain,
                mRegionBusStop
        ));

        mBeaconManager.bind(this);

        isListening = true;
    }
}