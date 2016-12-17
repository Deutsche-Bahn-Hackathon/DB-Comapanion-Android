package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

@AutoValue
public abstract class Poi implements Parcelable {

    public abstract String address();

    public abstract String lat();

    @SerializedName("lon")
    public abstract String lng();

    public abstract String photo();

    public abstract String url();

    public abstract String website();

    public static Poi create(String address, String lat, String lng, String photo, String url, String website) {
        return new AutoValue_Poi(address, lat, lng, photo, url, website);
    }

    public static TypeAdapter<Poi> typeAdapter(Gson gson) {
        return new AutoValue_Poi.GsonTypeAdapter(gson);
    }

}
