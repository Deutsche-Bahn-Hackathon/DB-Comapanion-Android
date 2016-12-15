package com.dbhackathon.util;

import android.content.Context;
import android.os.Build;

/**
 * Utility class which holds various methods to help with things like logging exceptions.
 *
 * @author Alex Lardschneider
 */
public final class Utils {

    private Utils() {
    }

    public static String locale(Context context) {
        Preconditions.checkNotNull(context, "context == null");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.getResources().getConfiguration().getLocales().get(0).getLanguage();
        }

        //noinspection deprecation
        return context.getResources().getConfiguration().locale.getLanguage();
    }

    @SuppressWarnings("ChainOfInstanceofChecks")
    public static void logException(Throwable t) {
        t.printStackTrace();
    }
}
