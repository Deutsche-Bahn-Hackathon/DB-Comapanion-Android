package com.dbhackathon.util;

import android.content.Context;
import android.preference.PreferenceManager;

public class Settings {

    public static final String PREF_HAS_ALARMS = "pref_has_alarms";

    public static void setHasAlarms(Context context, boolean value) {
        PreferenceManager.getDefaultSharedPreferences(context).edit()
                .putBoolean(PREF_HAS_ALARMS, value).apply();
    }

    public static boolean hasAlarms(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context)
                .getBoolean(PREF_HAS_ALARMS, false);
    }
}
