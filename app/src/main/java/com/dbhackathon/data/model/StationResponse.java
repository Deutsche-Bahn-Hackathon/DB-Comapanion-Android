package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

import java.util.List;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

@AutoValue
public abstract class StationResponse implements Parcelable {

    public abstract List<Station> stations();

    public static StationResponse create(List<Station> stations) {
        return new AutoValue_StationResponse(stations);
    }

    public static TypeAdapter<StationResponse> typeAdapter(Gson gson) {
        return new AutoValue_StationResponse.GsonTypeAdapter(gson);
    }

}
