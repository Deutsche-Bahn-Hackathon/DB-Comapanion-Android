package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

@AutoValue
public abstract class Station implements Parcelable {

    public abstract String id();

    public abstract String name();

    public abstract String lat();

    @SerializedName("lon")
    public abstract String lng();

    public static Station create(String id, String name, String lat, String lng) {
        return new AutoValue_Station(id, name, lat, lng);
    }

    public static TypeAdapter<Station> typeAdapter(Gson gson) {
        return new AutoValue_Station.GsonTypeAdapter(gson);
    }

}
