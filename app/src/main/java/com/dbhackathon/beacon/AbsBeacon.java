package com.dbhackathon.beacon;

import java.util.Date;

public abstract class AbsBeacon {

    public final int major;
    public final int minor;

    private final Date startDate;

    public long seenSeconds;
    public long lastSeen;

    public double distance;

    protected AbsBeacon(int major, int minor) {
        this.major = major;
        this.minor = minor;

        startDate = new Date();

        seen();
    }

    public void seen() {
        long millis = System.currentTimeMillis();

        seenSeconds = (millis - startDate.getTime()) / 1000;
        lastSeen = millis;
    }

    public Date getStartDate() {
        return startDate;
    }
}
