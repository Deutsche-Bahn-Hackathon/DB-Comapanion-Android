package com.dbhackathon.data.model;

import android.os.Parcel;
import android.os.Parcelable;

public class Coffee implements Parcelable {

    public String id;
    public String name;

    public double price;

    public Coffee() {
    }

    protected Coffee(Parcel in) {
        id = in.readString();
        name = in.readString();
        price = in.readDouble();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeDouble(price);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Coffee> CREATOR = new Creator<Coffee>() {
        @Override
        public Coffee createFromParcel(Parcel in) {
            return new Coffee(in);
        }

        @Override
        public Coffee[] newArray(int size) {
            return new Coffee[size];
        }
    };
}
