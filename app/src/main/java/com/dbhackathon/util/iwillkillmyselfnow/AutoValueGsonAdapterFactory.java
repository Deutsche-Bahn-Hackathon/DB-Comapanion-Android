package com.dbhackathon.util.iwillkillmyselfnow;

import com.dbhackathon.data.model.DateTime;
import com.dbhackathon.data.model.Facility;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.StationResponse;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.model.TrainResponse;
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

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        Class<? super T> rawType = type.getRawType();

        if (rawType.equals(StationResponse.class)) {
            return (TypeAdapter<T>) StationResponse.typeAdapter(gson);
        } else if (rawType.equals(Station.class)) {
            return (TypeAdapter<T>) Station.typeAdapter(gson);
        } else if (rawType.equals(Train.class)) {
            return (TypeAdapter<T>) Train.typeAdapter(gson);
        } else if (rawType.equals(TrainResponse.class)) {
            return (TypeAdapter<T>) TrainResponse.typeAdapter(gson);
        } else if (rawType.equals(DateTime.class)) {
            return (TypeAdapter<T>) DateTime.typeAdapter(gson);
        } else if (rawType.equals(Facility.class)) {
            return (TypeAdapter<T>) Facility.typeAdapter(gson);
        }

        return null;
    }
}