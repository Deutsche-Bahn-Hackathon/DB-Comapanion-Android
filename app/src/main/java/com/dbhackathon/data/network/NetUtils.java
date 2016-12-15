package com.dbhackathon.data.network;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dbhackathon.util.Preconditions;

/**
 * Utility class to check the current network status.
 *
 * @author Alex Lardschneider
 */
public final class NetUtils {

    private NetUtils() {
    }

    public static final String HOST = "https://sasa-bus.appspot.com";

    /**
     * Indicates whether a network connection exists and can be used to fetch data.
     *
     * @param context AppApplication context
     * @return {@code true} if connectivity exists, {@code false} otherwise.
     */
    public static boolean isOnline(Context context) {
        Preconditions.checkNotNull(context, "context == null");

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager != null) {
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            return activeNetworkInfo != null && activeNetworkInfo.isConnectedOrConnecting();
        }

        return false;
    }
}
