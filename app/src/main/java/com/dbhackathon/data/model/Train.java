package com.dbhackathon.data.model;

import android.os.Parcelable;
import android.support.annotation.Nullable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created on 12/16/16.
 *
 * @author Martin Fink
 */
@AutoValue
public abstract class Train implements Parcelable {

    public abstract String name();

    @Nullable
    public abstract String type();

    public abstract String stop_id();

    public abstract String stop();

    public abstract DateTime datetime();

    public abstract String track();

    public abstract String journey();


    public static Train create(String name, String type, String stop_id, String stop, DateTime datetime,
                               String track, String journey) {
        return new AutoValue_Train(name, type, stop_id, stop, datetime, track, journey);
    }

    public static TypeAdapter<Train> typeAdapter(Gson gson) {
        return new AutoValue_Train.GsonTypeAdapter(gson);
    }

}
