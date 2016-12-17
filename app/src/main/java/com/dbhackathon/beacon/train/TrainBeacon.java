package com.dbhackathon.beacon.train;

import com.dbhackathon.beacon.AbsBeacon;
import com.dbhackathon.data.model.JsonSerializable;
import com.dbhackathon.data.model.Station;

public class TrainBeacon extends AbsBeacon implements JsonSerializable {

    TrainBeacon(int id) {
        super(id);

        seen();
    }


    // ======================================== LISTS ==============================================

    public String getLine() {
        return "ICE";
    }

    public Station getStation() {
        return Station.create(8000228, "", "Lichtenfels", "", 0, "");
    }
}