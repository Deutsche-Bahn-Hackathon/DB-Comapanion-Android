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
public abstract class PoiResponse implements Parcelable {

    public abstract List<Poi> results();

    public static PoiResponse create(List<Poi> results) {
        return new AutoValue_PoiResponse(results);
    }

    public static TypeAdapter<PoiResponse> typeAdapter(Gson gson) {
        return new AutoValue_PoiResponse.GsonTypeAdapter(gson);
    }

}
