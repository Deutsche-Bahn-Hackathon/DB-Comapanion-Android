package com.dbhackathon.data.network;

public final class Endpoint {

    public static final String API_URL = "http://deutsche-bahn-api.appspot.com";
    public static final String STATIONS = API_URL + "/stations";
    public static final String DEPARTURES_ARRIVALS = STATIONS + "/{id}/{departures_arrivals}";

    private Endpoint() {
    }
}
