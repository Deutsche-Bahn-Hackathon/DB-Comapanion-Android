package com.dbhackathon.beacon.train;

import com.dbhackathon.beacon.AbsBeacon;

/**
 * Model which represents a bus stop beacon and holds information about it.
 *
 * @author Alex Lardschneider
 */
public class TrainBeacon extends AbsBeacon {

    boolean isNotificationShown;

    TrainBeacon(int id) {
        super(id);
    }

    /**
     * Sets {@link #isNotificationShown} to {@code true}.
     */
    public void setNotificationShown() {
        isNotificationShown = true;
    }
}