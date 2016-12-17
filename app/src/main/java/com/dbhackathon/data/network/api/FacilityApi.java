package com.dbhackathon.data.network.api;

import com.dbhackathon.data.model.Facility;
import com.dbhackathon.data.network.Endpoint;

import java.util.List;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface FacilityApi {

    @GET(Endpoint.FACILITY_TOILETS)
    Observable<List<Facility>> toilets(@Path("train") String train, @Path("wagon") String wagon);

    @GET(Endpoint.FACILITY_RESTAURANT)
    Observable<List<Facility>> restaurant(@Path("train") String train, @Path("wagon") String wagon);
}
