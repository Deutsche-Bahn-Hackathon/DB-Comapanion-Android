package com.dbhackathon.util;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.support.annotation.StringRes;

import com.dbhackathon.R;

/**
 * Created on 12/15/16.
 *
 * @author Martin Fink
 */

public class DialogFactory {

    private DialogFactory() {}


    public static Dialog createErrorDialog(Context context, String title, String message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .create();
    }

    public static Dialog createErrorDialog(Context context, @StringRes int title, @StringRes int message) {
        return new AlertDialog.Builder(context)
                .setTitle(title)
                .setMessage(message)
                .setNeutralButton(android.R.string.ok, null)
                .create();
    }

    public static Dialog createErrorDialog(Context context, String message) {
        return createErrorDialog(context, context.getString(R.string.sorry), message);
    }

    public static Dialog createErrorDialog(Context context, @StringRes int message) {
        return createErrorDialog(context, R.string.sorry, message);
    }


}
