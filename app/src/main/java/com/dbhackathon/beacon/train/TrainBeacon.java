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
        return "ICE 1206";
    }

    public String getType() {
        return "ICE";
    }

    public Station getStation() {
        return Station.create("8000228", "Lichtenfels", "50.145994", "11.059966");
    }
}