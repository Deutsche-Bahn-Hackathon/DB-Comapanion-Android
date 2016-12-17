package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Facility implements Parcelable {

    @SerializedName("to_go")
    public abstract int toGo();

    public abstract int wagon();

    public abstract boolean free();

    @SerializedName("driving_direction")
    public abstract boolean drivingDirection();

    public static Facility create(int toGo, int wagon, boolean free, boolean drivingDirections) {
        return new AutoValue_Facility(toGo, wagon, free, drivingDirections);
    }

    public static TypeAdapter<Facility> typeAdapter(Gson gson) {
        return new AutoValue_Facility.GsonTypeAdapter(gson);
    }
}
