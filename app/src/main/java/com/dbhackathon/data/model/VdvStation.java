package com.dbhackathon.data.model;

import com.dbhackathon.data.api.Api;

/**
 * Represents a bus stop.
 *
 * @author David Dejori
 */
public class VdvStation {

    private final long id;
    private int departure;

    public VdvStation(long id, int departure) {
        this.id = id;
        this.departure = departure;
    }

    public long getId() {
        return id;
    }

    public int getDeparture() {
        return departure;
    }

    public void setDeparture(int departure) {
        this.departure = departure;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        VdvStation busStop = (VdvStation) o;

        return id == busStop.id;
    }

    @Override
    public int hashCode() {
        return (int) id;
    }

    @Override
    public String toString() {
        return String.valueOf(id);
    }

    public String getTime() {
        return Api.Time.toTime(departure);
    }
}
