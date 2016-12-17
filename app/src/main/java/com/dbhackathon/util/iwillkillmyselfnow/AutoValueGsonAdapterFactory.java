package com.dbhackathon.util.iwillkillmyselfnow;

import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.StationResponse;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

public class AutoValueGsonAdapterFactory implements TypeAdapterFactory {

    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();

        if (rawType.equals(StationResponse.class)) {
            return (TypeAdapter<T>) StationResponse.typeAdapter(gson);
        } else if (rawType.equals(Station.class)) {
            return (TypeAdapter<T>) Station.typeAdapter(gson);
        }

        return null;
    }
}