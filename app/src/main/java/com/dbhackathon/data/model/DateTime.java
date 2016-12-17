package com.dbhackathon.data.model;

import android.os.Parcelable;

import com.google.auto.value.AutoValue;
import com.google.gson.Gson;
import com.google.gson.TypeAdapter;

/**
 * Created on 12/17/16.
 *
 * @author Martin Fink
 */

@AutoValue
public abstract class DateTime implements Parcelable {

    public abstract String date();
    public abstract int timezone_type();
    public abstract String timezone();

    public static DateTime create(String date, int timezone_type, String timezone) {
        return new AutoValue_DateTime(date, timezone_type, timezone);
    }

    public static TypeAdapter<DateTime> typeAdapter(Gson gson) {
        return new AutoValue_DateTime.GsonTypeAdapter(gson);
    }
}
