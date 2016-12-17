package com.dbhackathon.data.network;

public final class Endpoint {

    public static final String API_URL = "http://deutsche-bahn-api.appspot.com/";

    public static final String STATIONS = "stations";

    public static final String DEPARTURES_ARRIVALS = "stations/{id}/{departures_arrivals}";

    public static final String FACILITY_TOILETS = "train/{train}/wagon/{wagon}/next/toilet";
    public static final String FACILITY_RESTAURANT = "train/{train}/wagon/{wagon}/next/restaurant";
    public static final String POIS = "pois/{lat}/{lng}";

    private Endpoint() {
    }
}
