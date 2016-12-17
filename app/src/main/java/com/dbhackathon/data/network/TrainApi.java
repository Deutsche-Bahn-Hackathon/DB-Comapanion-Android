package com.dbhackathon.data.network;

import com.dbhackathon.data.model.StationResponse;
import com.dbhackathon.data.model.TrainResponse;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface TrainApi {

    @GET(Endpoint.STATIONS)
    Observable<StationResponse> getStations();

    @GET(Endpoint.DEPARTURES_ARRIVALS)
    Observable<TrainResponse> getArrivals(@Path("id") String id, @Path("departures_arrivals") String departures_arrivals);

}
