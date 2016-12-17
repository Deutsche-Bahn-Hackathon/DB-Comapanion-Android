package com.dbhackathon.beacon;

import android.content.Context;

import com.dbhackathon.util.Preconditions;

import org.altbeacon.beacon.Beacon;

import java.util.Collection;

public abstract class AbsBeaconHandler {

    @SuppressWarnings("StaticFieldLeak")
    protected final Context mContext;

    protected AbsBeaconHandler(Context context) {
        Preconditions.checkNotNull(context, "context == null");
        mContext = context.getApplicationContext();
    }

    public void didEnterRegion() {
    }

    public void didExitRegion() {
    }

    public abstract void didRangeBeacons(Collection<Beacon> beacons);
    public abstract void validateBeacon(Beacon beacon, int major, int minor);
}
