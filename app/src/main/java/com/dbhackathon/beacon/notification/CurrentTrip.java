package com.dbhackathon.beacon.notification;

import android.content.Context;

import com.dbhackathon.beacon.train.TrainBeacon;
import com.dbhackathon.data.model.DateTime;
import com.dbhackathon.data.model.JsonSerializable;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.model.VdvStation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import timber.log.Timber;

public class CurrentTrip implements JsonSerializable {

    public TrainBeacon beacon;

    private transient Context mContext;

    private final AtomicBoolean mNotificationVisible = new AtomicBoolean();

    private final List<Station> path;
    private List<VdvStation> times;

    public CurrentTrip(Context context, TrainBeacon beacon) {
        mContext = context;
        this.beacon = beacon;

        path = new ArrayList<>();

        init();
    }


    private void init() {
        path.clear();

        if (times != null) {
            times.clear();
        }

        // 8000261 München Hbf
        // 8000183 Ingolstadt Hbf
        // 8096025 Nürnberg
        // 8001844 Erlangen
        // 8000025 Bamberg
        // 8000228 Lichtenfels
        // 8011956 Jena Paradies
        // 8010240 Naumburg(Saale)Hbf
        // 8010205 Leipzig Hbf
        // 8010050 Bitterfeld
        // 8010222 Lutherstadt Wittenberg Hbf
        // 8011113 Berlin Südkreuz
        // 8011160 Berlin Hbf

        times = new ArrayList<>();
        times.add(new VdvStation(8000261, 46620));
        times.add(new VdvStation(8000183, 50040));
        times.add(new VdvStation(8096025, 52080));
        times.add(new VdvStation(8001844, 53160));
        times.add(new VdvStation(8000025, 54420));
        times.add(new VdvStation(8000228, 55740));
        times.add(new VdvStation(8011956, 61560));
        times.add(new VdvStation(8010240, 63120));
        times.add(new VdvStation(8010205, 65400));
        times.add(new VdvStation(8010050, 66720));
        times.add(new VdvStation(8010222, 67800));
        times.add(new VdvStation(8011113, 69960));
        times.add(new VdvStation(8011160, 70380));

        path.add(Station.create("8000261", "München Hbf", "", ""));
        path.add(Station.create("8000183", "Ingolstadt Hbf", "", ""));
        path.add(Station.create("8096025", "Nürnberg", "", ""));
        path.add(Station.create("8001844", "Erlangen", "", ""));
        path.add(Station.create("8000025", "Bamberg", "", ""));
        path.add(Station.create("8000228", "Lichtenfels", "", ""));
        path.add(Station.create("8011956", "Jena Paradies", "", ""));
        path.add(Station.create("8010240", "Naumburg (Saale) Hbf", "", ""));
        path.add(Station.create("8010205", "Leipzig Hbf", "", ""));
        path.add(Station.create("8010050", "Bitterfeld", "", ""));
        path.add(Station.create("8010222", "Lutherstadt Wittenberg Hbf", "", ""));
        path.add(Station.create("8011113", "Berlin Südkreuz", "", ""));
        path.add(Station.create("8011160", "Berlin Hbf", "", ""));
    }

    public void reset() {
        Timber.e("Trip reset");
        init();
    }


    public int getId() {
        return beacon.major;
    }

    int getDelay() {
        return 2;
    }

    public void setBeacon(TrainBeacon beacon) {
        this.beacon = beacon;
    }

    public void update() {
        if (isNotificationVisible()) {
            TripNotification.show(mContext, this);
        }
    }

    public void setContext(Context context) {
        mContext = context;
    }

    public String getWagon() {
        String minor = String.valueOf(beacon.minor);
        return minor.substring(0, 2);
    }


    public List<VdvStation> getTimes() {
        return times;
    }

    public CharSequence getTitle() {
        return "ICE 1206 to Berlin Hbf";
    }

    public List<Station> getPath() {
        return path;
    }


    public Train toTrain() {
        return Train.create(
                beacon.getLine(),
                beacon.getType(),
                String.valueOf(beacon.getStation().id()),
                beacon.getStation().name(),
                DateTime.create("", 0, ""),
                "",
                ""
        );
    }


    // ==================================== NOTIFICATION ===========================================

    void setNotificationVisible(boolean visible) {
        mNotificationVisible.set(visible);
    }

    public boolean isNotificationVisible() {
        return mNotificationVisible.get();
    }
}
