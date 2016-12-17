package com.dbhackathon.fcm;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;

/**
 * Settings about Google Cloud Messaging.
 *
 * @author Alex Lardschneider
 */
public final class FcmSettings {

    private static final String PREF_GCM_TOKEN = "pref_gcm_token";

    private FcmSettings() {
    }

    /**
     * Saved the user's gcm token.
     *
     * @param context Context to be used to edit the {@link SharedPreferences}.
     * @param token   The token to save.
     */
    static void setGcmToken(Context context, String token) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);
        sp.edit().putString(PREF_GCM_TOKEN, token).apply();
    }

    /**
     * Returns the saved gcm token.
     *
     * @param context Context to be used to edit the {@link SharedPreferences}.
     * @return the saved gcm token, or {@code null} if it hasn't been saved yet.
     */
    @Nullable
    public static String getGcmToken(Context context) {
        return PreferenceManager.getDefaultSharedPreferences(context).getString(PREF_GCM_TOKEN, null);
    }
}
