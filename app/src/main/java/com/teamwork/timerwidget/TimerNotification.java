package com.teamwork.timerwidget;

import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Created by aidangrabe on 25/06/15.
 *
 */
public class TimerNotification {

    private static final int NOTIFICATION_ID = 1;

    public TimerNotification() {

    }

    public static void showNotification(Context context, String time) {

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Teamwork timer")
                .setContentText(time)
                .setSmallIcon(R.drawable.ic_pause_circle_filled_white_24dp)
                .setOngoing(true);

        NotificationManager notifManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());

    }

}
