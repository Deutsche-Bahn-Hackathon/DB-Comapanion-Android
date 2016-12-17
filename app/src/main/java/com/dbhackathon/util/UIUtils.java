package com.dbhackathon.util;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.annotation.StringRes;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.inputmethod.InputMethodManager;

import com.dbhackathon.R;

import timber.log.Timber;

public final class UIUtils {

    private static Bitmap sIcon;

    private UIUtils() {
    }


    public static String getRawColorForDelay(int delay) {
        if (delay > 3) {
            return "#F44336";
        } else if (delay > 0) {
            return "#FFA000";
        } else {
            return "#4CAF50";
        }
    }

    public static int getColorForDelay(Context context, int delay) {
        if (delay > 3) {
            return ContextCompat.getColor(context, R.color.material_red_500);
        } else if (delay > 0) {
            return ContextCompat.getColor(context, R.color.material_amber_700);
        } else {
            return ContextCompat.getColor(context, R.color.material_green_500);
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public static void styleRecentTasksEntry(Activity activity, int color) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            return;
        }

        Resources resources = activity.getResources();
        String label = resources.getString(activity.getApplicationInfo().labelRes);

        if (sIcon == null) {
            // Cache to avoid decoding the same bitmap on every Activity change
            sIcon = BitmapFactory.decodeResource(resources, R.mipmap.ic_launcher);
        }

        activity.setTaskDescription(new ActivityManager.TaskDescription(label, sIcon, color));
    }

    public static void hideKeyboard(Activity activity) {
        View focus = activity.getCurrentFocus();

        if (focus == null) {
            Timber.e("Tried to hide keyboard but there is no focused window");
            return;
        }

        InputMethodManager inputMethodManager = (InputMethodManager)
                activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(focus.getWindowToken(), 0);
    }

    // ====================================== DIALOGS ==============================================

    public static void okDialog(Context context, @StringRes int title, @StringRes int message) {
        okDialog(context, title, message, (dialogInterface, i) -> dialogInterface.dismiss());
    }

    public static void okDialog(Context context, @StringRes int title, @StringRes int message,
                                DialogInterface.OnClickListener listener) {
        new AlertDialog.Builder(context, R.style.DialogStyle)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, listener)
                .create()
                .show();
    }

    public static void forceWrapContent(View v) {
        // Start with the provided view
        View current = v;

        // Travel up the tree until fail, modifying the LayoutParams
        do {
            // Get the parent
            ViewParent parent = current.getParent();

            // Check if the parent exists
            if (parent != null) {
                // Get the view
                try {
                    current = (View) parent;
                } catch (ClassCastException e) {
                    // This will happen when at the top view, it cannot be cast to a View
                    break;
                }

                // Modify the layout
                current.getLayoutParams().width = ViewGroup.LayoutParams.WRAP_CONTENT;
            }
        } while (current.getParent() != null);

        // Request a layout to be re-done
        current.requestLayout();
    }
}
