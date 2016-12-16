package com.dbhackathon.data.model;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created on 12/16/16.
 *
 * @author Martin Fink
 */
public class Train implements Parcelable {
    protected Train(Parcel in) {
    }

    public static final Creator<Train> CREATOR = new Creator<Train>() {
        @Override
        public Train createFromParcel(Parcel in) {
            return new Train(in);
        }

        @Override
        public Train[] newArray(int size) {
            return new Train[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
    }
}
