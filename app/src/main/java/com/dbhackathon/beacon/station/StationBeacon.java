package com.dbhackathon.beacon.station;

import com.dbhackathon.beacon.AbsBeacon;
import com.dbhackathon.data.model.Station;

/**
 * Model which represents a bus stop beacon and holds information about it.
 *
 * @author Alex Lardschneider
 */
public class StationBeacon extends AbsBeacon {

    private Station station;

    StationBeacon(int major, int minor) {
        super(major, minor);

        station = Station.create("008011160", "Berlin Bhf", "52.525589", "13.369548");
    }

    public Station station() {
        return station;
    }
}