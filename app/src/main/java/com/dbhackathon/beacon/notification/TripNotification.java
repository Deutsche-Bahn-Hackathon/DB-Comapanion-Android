package com.dbhackathon.beacon.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.DrawableRes;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.text.SpannableString;
import android.text.style.StyleSpan;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.RemoteViews;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.api.Api;
import com.dbhackathon.data.model.Station;
import com.dbhackathon.data.model.VdvStation;
import com.dbhackathon.ui.train.TrainActivity;
import com.dbhackathon.util.UIUtils;

import java.util.List;

import timber.log.Timber;

public final class TripNotification {

    private static final int NOTIFICATION_ID = 581044;

    private static final int[] BIG_VIEW_ROW_IDS = {
            R.id.notification_busstop_row_0,
            R.id.notification_busstop_row_1,
            R.id.notification_busstop_row_2,
            R.id.notification_busstop_row_3,
            R.id.notification_busstop_row_4,
            R.id.notification_busstop_row_5,
            R.id.notification_busstop_row_6
    };

    private static final int[] BIG_VIEW_ROUTE_IMAGE_IDS = {
            R.id.image_route_0,
            R.id.image_route1,
            R.id.image_route2,
            R.id.image_route3,
            R.id.image_route4,
            R.id.image_route5,
            R.id.image_route6
    };

    private static final int[] BIG_VIEW_TIME_TEXT_IDS = {
            R.id.txt_time_0,
            R.id.txt_time_1,
            R.id.txt_time_2,
            R.id.txt_time_3,
            R.id.txt_time_4,
            R.id.txt_time_5,
            R.id.txt_time_6
    };

    private static final int[] BIG_VIEW_TIME_DELAY_TEXT_IDS = {
            R.id.txt_time_delay_0,
            R.id.txt_time_delay_1,
            R.id.txt_time_delay_2,
            R.id.txt_time_delay_3,
            R.id.txt_time_delay_4,
            R.id.txt_time_delay_5,
            R.id.txt_time_delay_6
    };

    private static final int[] BIG_VIEW_BUS_STOP_TEXT_IDS = {
            R.id.txt_bus_stop_name_0,
            R.id.txt_bus_stop_name_1,
            R.id.txt_bus_stop_name_2,
            R.id.txt_bus_stop_name_3,
            R.id.txt_bus_stop_name_4,
            R.id.txt_bus_stop_name_5,
            R.id.txt_bus_stop_name_6
    };

    private TripNotification() {
    }

    public static void show(Context context, CurrentTrip trip) {
        trip.setNotificationVisible(true);

        Intent intent = new Intent(context, TrainActivity.class);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContent(getBaseNotificationView(context, trip))
                .setSmallIcon(R.drawable.ic_train_white_24dp)
                .setColor(ContextCompat.getColor(context, R.color.material_red_500))
                .setOngoing(true)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        Intent resultIntent = new Intent(context, TrainActivity.class);
        resultIntent.putExtra(Config.EXTRA_TRAIN, trip.toTrain());

        PendingIntent resultPendingIntent = PendingIntent.getActivity(context,
                NOTIFICATION_ID,
                resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        builder.setContentIntent(resultPendingIntent);

        Notification notification = builder.build();

        notification.bigContentView = getBigNotificationView(context, trip);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    public static void hide(Context context, @Nullable CurrentTrip trip) {
        Timber.e("Dismissing notification for bus %s", trip != null ? trip.getId() : "unknown");

        if (trip != null) {
            trip.setNotificationVisible(false);
        }

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.cancel(NOTIFICATION_ID);
    }

    private static void setCommonNotification(Context context, RemoteViews remoteViews, CurrentTrip trip) {
        remoteViews.setTextViewText(R.id.notification_bus_line, trip.beacon.getType());

        remoteViews.setImageViewBitmap(R.id.notification_bus_image, getNotificationIcon(context,
                ContextCompat.getColor(context, R.color.material_red_500)));

        int delay = trip.getDelay();
        String delayString;

        if (delay > 0) {
            delayString = context.getString(R.string.bottom_sheet_delayed, delay);
        } else if (delay < 0) {
            delayString = context.getString(R.string.bottom_sheet_early, delay * -1);
        } else {
            delayString = context.getString(R.string.bottom_sheet_punctual);
        }

        remoteViews.setTextColor(R.id.notification_bus_delay,
                UIUtils.getColorForDelay(context, delay));

        remoteViews.setTextViewText(R.id.notification_bus_delay, delayString);

    }

    private static RemoteViews getBaseNotificationView(Context context, CurrentTrip trip) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_current_trip_base);

        remoteViews.setTextViewText(R.id.notification_bus_description_collapsed,
                context.getString(R.string.line_details_current_stop, trip.beacon.getStation().name()));

        setCommonNotification(context, remoteViews, trip);

        return remoteViews;
    }

    private static RemoteViews getBigNotificationView(Context context, CurrentTrip trip) {
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_current_trip_big);

        remoteViews.setTextViewText(R.id.notification_bus_description_expanded, trip.getTitle());

        setCommonNotification(context, remoteViews, trip);

        List<Station> path = trip.getPath();
        List<VdvStation> times = trip.getTimes();

        Station currentBusStop = trip.beacon.getStation();

        int index = 5;

        remoteViews.setViewVisibility(R.id.image_route_points, View.VISIBLE);

        int delayColor = UIUtils.getColorForDelay(context, trip.getDelay());

        // If the bus is not at the stop the notification will display the last bus stop
        // the bus passed by in the first row. The current bus stop will be displayed on the
        // second row instead.
        int tempIndex = index - 1;
        Station busStop = path.get(tempIndex);

        int timeSeconds = times.get(tempIndex).getDeparture();
        timeSeconds += trip.getDelay() * 60;

        Bitmap bitmap = getTintedBitmap(context, R.drawable.path_start, trip);
        remoteViews.setImageViewBitmap(BIG_VIEW_ROUTE_IMAGE_IDS[0], bitmap);

        remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[0], busStop.name());
        remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[0], times.get(tempIndex).getTime());

        remoteViews.setTextViewText(BIG_VIEW_TIME_DELAY_TEXT_IDS[0], Api.Time.toTime(timeSeconds));
        remoteViews.setTextColor(BIG_VIEW_TIME_DELAY_TEXT_IDS[0], delayColor);

        remoteViews.setTextColor(BIG_VIEW_BUS_STOP_TEXT_IDS[0], Color.GRAY);
        remoteViews.setTextColor(BIG_VIEW_TIME_TEXT_IDS[0], Color.GRAY);

        remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[0], View.VISIBLE);

        remoteViews.setImageViewBitmap(R.id.image_route_points,
                getTintedBitmap(context, R.drawable.path_etc, trip));

        int length = BIG_VIEW_ROW_IDS.length;
        for (int i = 1; i < length; i++) {
            if (index + i <= path.size()) {
                remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[i], View.VISIBLE);

                // Last bus stop
                if (i == 6 || index + i > path.size() - 1) {
                    tempIndex = path.size() - 1;
                    busStop = path.get(tempIndex);

                    timeSeconds = times.get(tempIndex).getDeparture();
                    timeSeconds += trip.getDelay() * 60;

                    bitmap = getTintedBitmap(context, R.drawable.path_end, trip);
                    remoteViews.setImageViewBitmap(BIG_VIEW_ROUTE_IMAGE_IDS[i], bitmap);

                    remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[i], busStop.name());
                    remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[i], times.get(tempIndex).getTime());

                    remoteViews.setTextViewText(BIG_VIEW_TIME_DELAY_TEXT_IDS[i], Api.Time.toTime(timeSeconds));
                    remoteViews.setTextColor(BIG_VIEW_TIME_DELAY_TEXT_IDS[i], delayColor);

                    remoteViews.setTextColor(BIG_VIEW_BUS_STOP_TEXT_IDS[i], Color.BLACK);
                    remoteViews.setTextColor(BIG_VIEW_TIME_TEXT_IDS[i], Color.BLACK);

                    continue;
                }

                if (i == 1) {
                    tempIndex = index;
                    busStop = path.get(tempIndex);

                    timeSeconds = times.get(tempIndex).getDeparture();
                    timeSeconds += trip.getDelay() * 60;

                    SpannableString busStopString =
                            new SpannableString(busStop.name());
                    SpannableString timeString =
                            new SpannableString(times.get(tempIndex).getTime());
                    SpannableString timeDelayString =
                            new SpannableString(Api.Time.toTime(timeSeconds));

                    bitmap = getTintedBitmap(context, R.drawable.path_bus, trip);
                    remoteViews.setImageViewBitmap(BIG_VIEW_ROUTE_IMAGE_IDS[1], bitmap);

                    busStopString.setSpan(new StyleSpan(Typeface.BOLD), 0, busStopString.length(), 0);
                    timeString.setSpan(new StyleSpan(Typeface.BOLD), 0, timeString.length(), 0);
                    timeDelayString.setSpan(new StyleSpan(Typeface.BOLD), 0, timeDelayString.length(), 0);

                    remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[1], busStopString);
                    remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[1], timeString);

                    remoteViews.setTextViewText(BIG_VIEW_TIME_DELAY_TEXT_IDS[1], timeDelayString);
                    remoteViews.setTextColor(BIG_VIEW_TIME_DELAY_TEXT_IDS[1], delayColor);

                    remoteViews.setTextColor(BIG_VIEW_BUS_STOP_TEXT_IDS[1], Color.BLACK);
                    remoteViews.setTextColor(BIG_VIEW_TIME_TEXT_IDS[1], Color.BLACK);

                    continue;
                }

                tempIndex = index + i - 1;
                busStop = path.get(tempIndex);

                timeSeconds = times.get(tempIndex).getDeparture();
                timeSeconds += trip.getDelay() * 60;

                bitmap = getTintedBitmap(context, R.drawable.path_bus_stop, trip);
                remoteViews.setImageViewBitmap(BIG_VIEW_ROUTE_IMAGE_IDS[i], bitmap);

                remoteViews.setTextViewText(BIG_VIEW_BUS_STOP_TEXT_IDS[i], busStop.name());
                remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[i], times.get(tempIndex).getTime());

                remoteViews.setTextViewText(BIG_VIEW_TIME_DELAY_TEXT_IDS[i], Api.Time.toTime(timeSeconds));
                remoteViews.setTextColor(BIG_VIEW_TIME_DELAY_TEXT_IDS[i], delayColor);

                remoteViews.setTextColor(BIG_VIEW_BUS_STOP_TEXT_IDS[i], Color.BLACK);
                remoteViews.setTextColor(BIG_VIEW_TIME_TEXT_IDS[i], Color.BLACK);
            } else {
                remoteViews.setViewVisibility(R.id.image_route_points, View.GONE);
                remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[i], View.GONE);
            }
        }

        return remoteViews;
    }

    private static Bitmap getNotificationIcon(Context context, int color) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        GradientDrawable circularImage = (GradientDrawable) ContextCompat.getDrawable(context,
                R.drawable.circle_image);

        circularImage.setStroke(Math.round(4 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT)),
                color);

        circularImage.setColor(color);

        int size = Math.round(64 * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));

        Bitmap circularBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(circularBitmap);
        circularImage.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        circularImage.draw(canvas);

        return circularBitmap;
    }

    private static Bitmap getTintedBitmap(Context context, @DrawableRes int drawable, CurrentTrip trip) {
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(),
                drawable);

        int color = ContextCompat.getColor(context, R.color.material_red_500);
        Paint paint = new Paint();
        paint.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
        Bitmap bitmapResult = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(),
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmapResult);
        canvas.drawBitmap(bitmap, 0, 0, paint);

        return bitmapResult;
    }
}
