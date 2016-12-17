package com.dbhackathon.beacon.station;

import com.dbhackathon.beacon.AbsBeacon;

/**
 * Model which represents a bus stop beacon and holds information about it.
 *
 * @author Alex Lardschneider
 */
public class StationBeacon extends AbsBeacon {

    boolean isNotificationShown;

    StationBeacon(int id) {
        super(id);
    }

    /**
     * Sets {@link #isNotificationShown} to {@code true}.
     */
    public void setNotificationShown() {
        isNotificationShown = true;
    }
}