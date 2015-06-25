package com.teamwork.timerwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aidangrabe on 25/06/15.
 *
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final String CLICK_PLAY_BUTTON = "playButtonOnClick";

    private static boolean sTimerStarted = false;
    private static int[] sWidgetIds;
    private static Date sStartTime;
    private static Handler sHandler;

    public WidgetProvider() {

        sHandler = new Handler(Looper.getMainLooper());


    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        final Context c = context;
        final int[] ids = appWidgetIds;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d("tw", "Handler calling!");
                onUpdate(c, AppWidgetManager.getInstance(c), ids);
            }
        };
        sHandler.removeCallbacks(r);
        sHandler.postDelayed(r, 10000);

        Log.d("tw", "onUpdate");

        sWidgetIds = appWidgetIds;

        for (int widgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(),R.layout.widget_layout);

            if (sTimerStarted) {

                Date now = Calendar.getInstance().getTime();
                long deltaMillis = now.getTime() - sStartTime.getTime();
                int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(deltaMillis) % 60);
                int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(deltaMillis) % 60);
                int hours   = (int) (TimeUnit.MILLISECONDS.toHours(deltaMillis));
                Log.d("tw", "deltaMillis: " + deltaMillis);
                Log.d("tw", "minutes: " + (TimeUnit.MILLISECONDS.toMinutes(deltaMillis)));
                Log.d("tw", "hours: " + hours);

                remoteViews.setImageViewResource(R.id.play_button, R.drawable.ic_pause_circle_filled_white_48dp);
                remoteViews.setTextViewText(R.id.time_label, String.format("%d:%02d:%02d", hours, minutes, seconds));
            } else {
                remoteViews.setImageViewResource(R.id.play_button, R.drawable.ic_play_arrow_white_24dp);
            }

            remoteViews.setOnClickPendingIntent(R.id.play_button,
                    getPendingSelfIntent(context, CLICK_PLAY_BUTTON));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    public void startTimer() {
        sStartTime = Calendar.getInstance().getTime();
    }

    public void onPlayButtonClicked(Context context) {

        startTimer();
        sTimerStarted = !sTimerStarted;

        onUpdate(context, AppWidgetManager.getInstance(context), sWidgetIds);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_PLAY_BUTTON)) {
            onPlayButtonClicked(context);
        }

    }
}
