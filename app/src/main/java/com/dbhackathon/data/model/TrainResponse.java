package com.dbhackathon.data.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

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
public abstract class TrainResponse implements Parcelable {

    @Nullable
    public abstract List<Train> arrivals();

    @Nullable
    public abstract List<Train> departures();

    public static TrainResponse create(List<Train> arrivals, List<Train> departures) {
        return new AutoValue_TrainResponse(arrivals, departures);
    }

    public static TypeAdapter<TrainResponse> typeAdapter(Gson gson) {
        return new AutoValue_TrainResponse.GsonTypeAdapter(gson);
    }
}
