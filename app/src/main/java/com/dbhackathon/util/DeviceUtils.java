package com.dbhackathon.util;

import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;

/**
 * Utility class to help with identifying the current device and to get info about it
 * i.e if bluetooth is enabled or if the device is a tablet.
 *
 * @author Alex Lardschneider
 */
public final class DeviceUtils {

    private DeviceUtils() {
    }

    /**
     * Gets the devices bluetooth title
     *
     * @return bluetooth title, or {@code none} when no adapter is available
     */
    static boolean isBluetoothEnabled() {
        BluetoothAdapter bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        return bluetoothAdapter != null && bluetoothAdapter.isEnabled();
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    static boolean hasBle(Context context) {
        return context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE);
    }

    /**
     * Detects if the current device is a tablet by calculating it from
     * its screen width
     *
     * @param context the context to access resources
     * @return {@code true} if tablet, {@code false} otherwise
     */
    public static boolean isTablet(Context context) {
        Preconditions.checkNotNull(context, "isTablet() context == null");

        return (context.getResources().getConfiguration().screenLayout &
                Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    /**
     * Get the device's screen width
     *
     * @param context AppApplication context
     * @return screen width in pixels
     */
    public static int getScreenWidth(Context context) {
        Preconditions.checkNotNull(context, "getScreenWidth() context == null");

        return context.getResources().getDisplayMetrics().widthPixels;
    }

    /**
     * Get the device's screen height
     *
     * @param context AppApplication context
     * @return screen height in pixels
     */
    public static int getScreenHeight(Context context) {
        Preconditions.checkNotNull(context, "getScreenHeight() context == null");

        return context.getResources().getDisplayMetrics().heightPixels;
    }

    public static boolean hasPermission(Context context, String permission) {
        return ActivityCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
