package com.dbhackathon.util;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;

import com.dbhackathon.Config;
import com.dbhackathon.R;
import com.dbhackathon.data.model.Coffee;
import com.dbhackathon.ui.coffee.CoffeeActivity;

public class Notifications {

    public static void coffee(Context context, Coffee coffee) {
        Preconditions.checkNotNull(context, "context == null");
        Preconditions.checkNotNull(coffee, "coffee == null");

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.ic_local_cafe_white_24dp)
                .setContentTitle("Do you want a coffee?")
                .setContentText("Click here to order one")
                .setAutoCancel(true)
                .setColor(ContextCompat.getColor(context, R.color.material_brown_500));

        Intent resultIntent = new Intent(context, CoffeeActivity.class);
        resultIntent.putExtra(Config.EXTRA_COFFEE, coffee.id);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, coffee.id.hashCode(), resultIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);

        mBuilder.setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(coffee.id.hashCode(), mBuilder.build());
    }
}
