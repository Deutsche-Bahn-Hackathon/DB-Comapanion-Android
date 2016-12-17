package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/16/16.
 *
 * @author Martin Fink
 */

@AutoValue
public abstract class Station implements Parcelable {

    public abstract int id();

    public abstract String state();

    public abstract String name();

    public abstract String street();

    @SerializedName("postal_code")
    public abstract int postalCode();

    public abstract String city();

    public static TypeAdapter<Station> typeAdapter(Gson gson) {
        return new AutoValue_Station.GsonTypeAdapter(gson);
    }

}
