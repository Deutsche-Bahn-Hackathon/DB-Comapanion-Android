package com.dbhackathon.fcm.command;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.dbhackathon.BuildConfig;
import com.dbhackathon.R;
import com.dbhackathon.util.Utils;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;
import java.util.Map;

import timber.log.Timber;

/**
 * General purpose command which can display a highly customizable notification. The notification
 * can be targeted to only very specific devices by using {@link NotificationCommandModel#audience}
 * and {@link NotificationCommandModel#minVersion}.
 *
 * A expiry time can also be specified. If the notification command arrives after the specified time,
 * either because the device was offline or not reachable by GCM, it will ignored. The notification
 * will be hidden after the expiry time.
 *
 * A invalid notification will be ignored.
 *
 * @author Alex Lardschneider
 */
public class NotificationCommand implements FcmCommand {

    private static class NotificationCommandModel {

        int id;
        int minVersion;
        int maxVersion;
        int expiry;

        String color;
        String audience;
        String url;

        String iconType;

        @SerializedName("package")
        String packageName;

        String titleIt;
        String titleDe;

        String messageIt;
        String messageDe;

        String dialogTitleIt;
        String dialogTitleDe;

        String dialogTextIt;
        String dialogTextDe;

        String dialogYesIt;
        String dialogYesDe;

        String dialogNoIt;
        String dialogNoDe;
    }

    @Override
    public void execute(Context context, @NonNull Map<String, String> data) {
        Timber.w("Received GCM notification message");
        Timber.w("Parsing GCM notification command: %s", data);

        JSONObject json = new JSONObject();

        for (Map.Entry<String, String> entry : data.entrySet()) {
            try {
                json.put(entry.getKey(), entry.getValue());
            } catch (JSONException e) {
                Utils.logException(e);
            }
        }

        Gson gson = new Gson();
        NotificationCommandModel command;

        try {
            command = gson.fromJson(json.toString(), NotificationCommandModel.class);

            Timber.w("Id: %d", command.id);
            Timber.w("Audience: %s", command.audience);
            Timber.w("TitleIt: %s", command.titleIt);
            Timber.w("TitleDe: %s", command.titleDe);
            Timber.w("MessageIt: %s", command.messageIt);
            Timber.w("MessageDe: %s", command.messageDe);
            Timber.w("Expiry: %s", command.expiry);
            Timber.w("URL: %s", command.url);
            Timber.w("Dialog titleIt: %s", command.dialogTitleIt);
            Timber.w("Dialog titleDe: %s", command.dialogTitleDe);
            Timber.w("Dialog textIt: %s", command.dialogTextIt);
            Timber.w("Dialog textDe: %s", command.dialogTextDe);
            Timber.w("Dialog yesIt: %s", command.dialogYesIt);
            Timber.w("Dialog yesDe: %s", command.dialogYesDe);
            Timber.w("Dialog noIt: %s", command.dialogNoIt);
            Timber.w("Dialog noDe: %s", command.dialogNoDe);
            Timber.w("Min version code: %s", command.minVersion);
            Timber.w("Max version code: %s", command.maxVersion);
            Timber.w("Color: %s", command.color);
        } catch (Exception e) {
            Utils.logException(e);

            Timber.e("Failed to parse GCM notification command.");
            return;
        }

        Timber.i("Processing notification command.");
        processCommand(context, command);
    }

    private void processCommand(Context context, NotificationCommandModel command) {
        String locale = Utils.locale(context);

        String title;
        String message;
        String dialogTitle;
        String dialogText;
        String dialogYes;
        String dialogNo;

        // Select proper language
        switch (locale) {
            case "de":
                title = command.titleDe;
                message = command.messageDe;
                dialogTitle = command.dialogTitleDe;
                dialogText = command.dialogTextDe;
                dialogYes = command.dialogYesDe;
                dialogNo = command.dialogNoDe;
                break;
            case "it":
                title = command.titleIt;
                message = command.messageIt;
                dialogTitle = command.dialogTitleIt;
                dialogText = command.dialogTextIt;
                dialogYes = command.dialogYesIt;
                dialogNo = command.dialogNoIt;
                break;
            default:
                title = command.titleIt;
                message = command.messageIt;
                dialogTitle = command.dialogTitleIt;
                dialogText = command.dialogTextIt;
                dialogYes = command.dialogYesIt;
                dialogNo = command.dialogNoIt;
        }

        // Check if required fields are empty
        if (TextUtils.isEmpty(title)) {
            Timber.e("Title is missing");
            return;
        }

        // Check if required fields are empty
        if (TextUtils.isEmpty(message)) {
            Timber.e("Message is missing");
            return;
        }

        // Check package
        if (!TextUtils.isEmpty(command.packageName) && !command.packageName.equals(BuildConfig.APPLICATION_ID)) {
            Timber.e("Skipping command because of wrong package name, is %s, should be %s",
                    command.packageName, BuildConfig.APPLICATION_ID);
            return;
        }

        // Check app version
        if (command.minVersion != 0 || command.maxVersion != 0) {
            Timber.i("Command has version range.");

            int minVersion = command.minVersion;
            int maxVersion = command.maxVersion != 0 ? command.maxVersion : Integer.MAX_VALUE;

            try {
                Timber.i("Version range: %d - %d", minVersion, maxVersion);
                Timber.i("My version code: %d", BuildConfig.VERSION_CODE);

                if (BuildConfig.VERSION_CODE < minVersion) {
                    Timber.e("Skipping command because our version is too old, %d < %d",
                            BuildConfig.VERSION_CODE, minVersion);
                    return;
                }
                if (BuildConfig.VERSION_CODE > maxVersion) {
                    Timber.e("Skipping command because our version is too new, %d > %d",
                            BuildConfig.VERSION_CODE, maxVersion);
                    return;
                }
            } catch (NumberFormatException ex) {
                Timber.e("Version spec badly formatted: min = %d, max = %d",
                        command.minVersion, command.maxVersion);
                return;
            } catch (Exception e) {
                Timber.e(e, "Unexpected problem doing version check.");
                return;
            }
        }

        // Check if we are the right audience
        if ("all".equals(command.audience)) {
            Timber.i("Relevant (audience is 'all').");
        } else if ("debug".equals(command.audience)) {
            if (!BuildConfig.DEBUG) {
                Timber.e("App is not in debug mode");
                return;
            }

            Timber.i("Relevant (audience is 'debug').");
        } else {
            Timber.e("Invalid audience on GCM notification command: %s", command.audience);
            return;
        }

        // Check if it expired
        Date expiry = new Date(command.expiry * 1000L);

        if (expiry.getTime() < System.currentTimeMillis()) {
            Timber.e("Got expired GCM notification command. Expiry: %s", expiry);
            return;
        } else {
            Timber.i("Message is still valid (expiry is in the future: %s)", expiry);
        }

        // Decide the intent that will be fired when the user clicks the notification
        /*Intent intent;
        if (TextUtils.isEmpty(dialogTitle) || TextUtils.isEmpty(dialogText)) {
            // notification leads directly to the URL, no dialog
            if (TextUtils.isEmpty(command.url)) {
                intent = new Intent(context, MapActivity.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |
                        Intent.FLAG_ACTIVITY_SINGLE_TOP);
            } else {
                intent = new Intent(Intent.ACTION_VIEW, Uri.parse(command.url));
            }
        } else {
            // Use a dialog
            intent = new Intent(context, MapActivity.class).setFlags(
                    Intent.FLAG_ACTIVITY_CLEAR_TOP |
                            Intent.FLAG_ACTIVITY_SINGLE_TOP |
                            Intent.FLAG_ACTIVITY_CLEAR_TASK);

            intent.putExtra(MapActivity.EXTRA_DIALOG_TITLE, dialogTitle);
            intent.putExtra(MapActivity.EXTRA_DIALOG_MESSAGE, dialogText);

            intent.putExtra(MapActivity.EXTRA_DIALOG_YES,
                    TextUtils.isEmpty(dialogYes) ? "OK" : dialogYes);
            intent.putExtra(MapActivity.EXTRA_DIALOG_NO,
                    TextUtils.isEmpty(dialogNo) ? "" : dialogNo);
            intent.putExtra(MapActivity.EXTRA_DIALOG_URL,
                    TextUtils.isEmpty(command.url) ? "" : command.url);
        }*/

        String notificationTitle = TextUtils.isEmpty(title) ?
                context.getString(R.string.app_name) : title;

        String notificationMessage = TextUtils.isEmpty(message) ? "" : message;

        // Select color for the notification background
        int color = ContextCompat.getColor(context, R.color.colorPrimary);
        try {
            if (!TextUtils.isEmpty(command.color)) {
                color = Color.parseColor('#' + command.color);
            }
        } catch (Exception e) {
            Timber.e("Color spec badly formatted: color=%s, using default", command.color);
        }

        // Select notification icon
        int iconResource = R.drawable.ic_info_outline_white_24dp;
        if (!TextUtils.isEmpty(command.iconType)) {
            switch (command.iconType) {
                case "info":
                    iconResource = R.drawable.ic_info_outline_white_24dp;
                    break;
                case "warning":
                    iconResource = R.drawable.ic_warning_white_24dp;
                    break;
            }
        }

        NotificationManager notificationManager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);

        Notification notification = new NotificationCompat.Builder(context)
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(iconResource)
                .setTicker(notificationMessage)
                .setContentTitle(notificationTitle)
                .setContentText(notificationMessage)
                .setColor(color)
                /*.setContentIntent(PendingIntent.getActivity(context, command.id, intent,
                        PendingIntent.FLAG_CANCEL_CURRENT))*/
                .setAutoCancel(true)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notificationMessage))
                .build();

        notificationManager.notify(command.id, notification);
    }
}
