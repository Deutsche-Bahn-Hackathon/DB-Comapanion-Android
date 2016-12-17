package com.dbhackathon.beacon.notification;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.RemoteViews;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.beacon.station.StationBeacon;
import com.dbhackathon.data.model.Train;
import com.dbhackathon.data.model.TrainResponse;
import com.dbhackathon.data.network.RestClient;
import com.dbhackathon.data.network.TrainApi;
import com.dbhackathon.ui.station.StationDetailsActivity;
import com.dbhackathon.util.NextObserver;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import timber.log.Timber;

public final class DeparturesNotification {

    private static final int NOTIFICATION_ID = 652423;

    private static final SimpleDateFormat INPUT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSSSSS");
    private static final SimpleDateFormat OUTPUT = new SimpleDateFormat("HH:mm");

    private static final int[] BIG_VIEW_ROW_IDS = {
            R.id.notification_departures_row_0,
            R.id.notification_departures_row_1,
            R.id.notification_departures_row_2,
            R.id.notification_departures_row_3,
            R.id.notification_departures_row_4,
            R.id.notification_departures_row_5,
            R.id.notification_departures_row_6,
    };

    private static final int[] BIG_VIEW_LINE_TEXT_IDS = {
            R.id.notification_departures_row_0_line,
            R.id.notification_departures_row_1_line,
            R.id.notification_departures_row_2_line,
            R.id.notification_departures_row_3_line,
            R.id.notification_departures_row_4_line,
            R.id.notification_departures_row_5_line,
            R.id.notification_departures_row_6_line,
    };

    private static final int[] BIG_VIEW_TIME_TEXT_IDS = {
            R.id.notification_departures_row_0_time,
            R.id.notification_departures_row_1_time,
            R.id.notification_departures_row_2_time,
            R.id.notification_departures_row_3_time,
            R.id.notification_departures_row_4_time,
            R.id.notification_departures_row_5_time,
            R.id.notification_departures_row_6_time,
    };

    private static final int[] BIG_VIEW_DESTINATION_TEXT_IDS = {
            R.id.notification_departures_row_0_destination,
            R.id.notification_departures_row_1_destination,
            R.id.notification_departures_row_2_destination,
            R.id.notification_departures_row_3_destination,
            R.id.notification_departures_row_4_destination,
            R.id.notification_departures_row_5_destination,
            R.id.notification_departures_row_6_destination,
    };

    private DeparturesNotification() {
    }

    public static void show(Context context, StationBeacon beacon) {
        Timber.e("Showing notification for bus stop %d", beacon.major);

        TrainApi trainApi = RestClient.ADAPTER.create(TrainApi.class);
        trainApi.getDepArrs(String.valueOf(beacon.station().id()), "departures")
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new NextObserver<TrainResponse>() {
                    @Override
                    public void onNext(TrainResponse response) {
                        showNotification(context, beacon, response.departures());
                    }
                });
    }

    public static void hide(Context context) {
        Timber.i("Hiding departure notification");

        NotificationManager manager = (NotificationManager)
                context.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.cancel(NOTIFICATION_ID);
    }

    private static void showNotification(Context context, StationBeacon beacon, List<Train> departures) {
        String contentTitle = beacon.station().name();

        String contentText = context.getString(R.string.notification_bus_stop_sub_pull);

        Intent intent = new Intent(context, StationDetailsActivity.class);
        intent.putExtra(Config.EXTRA_STATION, beacon.station());

        PendingIntent pendingIntent = PendingIntent.getActivity(context, beacon.major,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.ic_store_white_24dp)
                .setAutoCancel(false)
                .setLights(Color.RED, 500, 5000)
                .setColor(ContextCompat.getColor(context, R.color.material_red_500))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(pendingIntent);

        Notification notification = builder.build();

        notification.bigContentView = getBigNotificationView(context, beacon, departures);

        NotificationManager notificationManager = (NotificationManager) context
                .getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(NOTIFICATION_ID, notification);
    }

    private static RemoteViews getBigNotificationView(Context context, StationBeacon beacon,
                                                      List<Train> trains) {

        RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                R.layout.notification_departures);

        String subtitle = context.getString(R.string.notification_expanded_title,
                beacon.station().name());

        remoteViews.setTextViewText(R.id.notification_departures_title, subtitle);

        int length = BIG_VIEW_ROW_IDS.length;
        for (int i = 0; i < length; i++) {
            if (i < trains.size()) {
                Train train = trains.get(i);

                remoteViews.setTextViewText(BIG_VIEW_LINE_TEXT_IDS[i], train.name());
                remoteViews.setTextViewText(BIG_VIEW_DESTINATION_TEXT_IDS[i], train.stop());

                try {
                    remoteViews.setTextViewText(BIG_VIEW_TIME_TEXT_IDS[i], OUTPUT.format(INPUT.parse(train.datetime().date())));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            } else {
                remoteViews.setViewVisibility(BIG_VIEW_ROW_IDS[i], View.GONE);
            }
        }

        return remoteViews;
    }
}
