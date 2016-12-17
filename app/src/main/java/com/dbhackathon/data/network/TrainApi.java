package com.dbhackathon.data.network;

import com.dbhackathon.data.model.StationResponse;

import retrofit2.http.GET;
import rx.Observable;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public interface TrainApi {

    @GET(Endpoint.STATIONS)
    Observable<StationResponse> getStations();

}
