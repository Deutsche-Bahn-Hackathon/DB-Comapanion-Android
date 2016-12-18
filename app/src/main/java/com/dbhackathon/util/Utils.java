package com.dbhackathon.util;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.customtabs.CustomTabsIntent;

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

    public static void openCustomTab(String url, Context context) {
        if (isPackageInstalled("com.android.chrome", context.getPackageManager())) {
            CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                    .build();
            customTabsIntent.launchUrl(context, Uri.parse(url));
        } else {
            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            context.startActivity(intent);
        }
    }

    private static boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public interface ActionListener<T> {
        void onClick(T t);
    }
}
