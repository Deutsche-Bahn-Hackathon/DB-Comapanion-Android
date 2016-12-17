package com.dbhackathon.data.api;

import org.joda.time.DateTimeZone;

import java.util.Locale;

/**
 * This is the main offline API where the app gets data from. This API tells us specific information
 * about departures and trips. It uses the SASA SpA-AG offline stored open data.
 *
 * @author David Dejori
 */
public final class Api {

    private Api() {
    }

    public static final class Time {

        private Time() {
        }

        static long addOffset(long seconds) {
            seconds += DateTimeZone.forID("Europe/Rome").getOffset(seconds);
            return seconds;
        }

        static long now() {
            long now = System.currentTimeMillis();
            return now + DateTimeZone.forID("Europe/Rome").getOffset(now);
        }

        public static String toTime(long seconds) {
            return String.format(Locale.ROOT, "%02d:%02d", seconds / 3600 % 24, seconds % 3600 / 60);
        }
    }
}