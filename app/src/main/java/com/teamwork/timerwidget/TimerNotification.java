package com.teamwork.timerwidget;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;

/**
 * Created by aidangrabe on 25/06/15.
 *
 */
public class TimerNotification {

    private static final int NOTIFICATION_ID = 1;

    // private constructor for singleton
    private TimerNotification() {

    }

    public static void showNotification(Context context, String time, WidgetProvider.TimerState state) {

        NotificationCompat.Builder notifBuilder = new NotificationCompat.Builder(context)
                .setContentTitle("Teamwork timer")
                .setContentText(time)

                .setSmallIcon(R.drawable.ic_pause_circle_filled_white_24dp)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_pause_circle_filled_white_24dp));
//                .setOngoing(true);

        if (isStarted(state)) {
            notifBuilder.addAction(R.drawable.pause_white_24dp, "Pause",
                    getWidgetPendingIntent(context, WidgetProvider.CLICK_PLAY_BUTTON));
        } else if (isPaused(state)) {
            notifBuilder.addAction(R.drawable.play_white_24dp, "Play",
                    getWidgetPendingIntent(context, WidgetProvider.CLICK_PLAY_BUTTON));
        }

        if (!isStopped(state)) {
            notifBuilder.addAction(R.drawable.refresh_white_24dp, "Reset",
                    getWidgetPendingIntent(context, WidgetProvider.CLICK_RESET_BUTTON));
        }

        NotificationManagerCompat notifManager = NotificationManagerCompat.from(context);
        notifManager.notify(NOTIFICATION_ID, notifBuilder.build());

    }

    private static PendingIntent getWidgetPendingIntent(Context context, String action) {
        Intent intent = new Intent(context, WidgetProvider.class);
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    // convenience methods
    private static boolean isStarted(WidgetProvider.TimerState state) {
        return state == WidgetProvider.TimerState.STARTED;
    }

    private static boolean isPaused(WidgetProvider.TimerState state) {
        return state == WidgetProvider.TimerState.PAUSED;
    }

    private static boolean isStopped(WidgetProvider.TimerState state) {
        return state == WidgetProvider.TimerState.STOPPED;
    }

}
