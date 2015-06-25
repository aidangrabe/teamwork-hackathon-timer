package com.teamwork.timerwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * Created by aidangrabe on 25/06/15.
 *
 */
public class WidgetProvider extends AppWidgetProvider {

    private static final long UPDATE_INTERVAL = TimeUnit.SECONDS.toMillis(2);

    private static final String CLICK_PLAY_BUTTON       = "playButtonOnClick";
    private static final String CLICK_RESET_BUTTON      = "resetButtonOnClick";
    private static final String CLICK_LOG_TIME_BUTTON   = "logTimeButtonOnClick";

    private static int[] sWidgetIds = new int[0];
    private static Date sStartTime;
    private static Handler sHandler;
    private static long sTimerMillis = 0;
    private static TimerState sTimerState = TimerState.STOPPED;
    private static Runnable sTimerRunnable;

    enum TimerState {
        PAUSED, STARTED, STOPPED
    }

    public WidgetProvider() {

        if (sHandler == null) {
            sHandler = new Handler(Looper.getMainLooper());
        }

    }

    protected PendingIntent getPendingSelfIntent(Context context, String action) {
        Intent intent = new Intent(context, getClass());
        intent.setAction(action);
        return PendingIntent.getBroadcast(context, 0, intent, 0);
    }

    // shorter convenience method of onUpdate()
    public void onUpdate(Context context) {
        onUpdate(context, AppWidgetManager.getInstance(context), sWidgetIds);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        Log.d("tw", "onUpdate");

        sWidgetIds = appWidgetIds;
        Date now = Calendar.getInstance().getTime();

        // call onUpdate again in UPDATE_INTERVAL milliseconds
        if (isStarted()) {
            startUpdateTimer(context, appWidgetIds);
        }

        for (int widgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(),R.layout.widget_layout);

            if (isStarted()) {

                long deltaMillis = now.getTime() - sStartTime.getTime();
                deltaMillis += sTimerMillis;
                remoteViews.setImageViewResource(R.id.play_button, R.drawable.ic_pause_circle_filled_white_48dp);
                setTimeLabel(remoteViews, deltaMillis);
            }

            if (isStopped()) {
                setTimeLabel(remoteViews, 0);
            }

            if (isStopped() || isPaused()) {
                remoteViews.setImageViewResource(R.id.play_button, R.drawable.play_white_24dp);
            }

            if (isStopped() || isStarted()) {
                showExtraButtons(remoteViews, false);
            }

            if (isPaused()) {
                showExtraButtons(remoteViews, true);
            }

            // onclick listeners
            remoteViews.setOnClickPendingIntent(R.id.play_button,
                    getPendingSelfIntent(context, CLICK_PLAY_BUTTON));
            remoteViews.setOnClickPendingIntent(R.id.reset_button,
                    getPendingSelfIntent(context, CLICK_RESET_BUTTON));

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }

    }

    private void setTimeLabel(RemoteViews remoteViews, long millis) {
        int seconds = (int) (TimeUnit.MILLISECONDS.toSeconds(millis) % 60);
        int minutes = (int) (TimeUnit.MILLISECONDS.toMinutes(millis) % 60);
        int hours   = (int) (TimeUnit.MILLISECONDS.toHours(millis));
        remoteViews.setTextViewText(R.id.time_label, String.format("%d:%02d:%02d", hours, minutes, seconds));
    }

    private void showExtraButtons(RemoteViews remoteViews, boolean show) {
        int buttonsVisible  = show ? View.VISIBLE : View.GONE;
        int textVisible     = show ? View.GONE : View.VISIBLE;
        remoteViews.setViewVisibility(R.id.reset_button, buttonsVisible);
        remoteViews.setViewVisibility(R.id.log_time_button, buttonsVisible);

        // time labels
        remoteViews.setViewVisibility(R.id.time_label, textVisible);
        remoteViews.setViewVisibility(R.id.detail_label, textVisible);
    }

    private void startUpdateTimer(final Context context, final int[] appWidgetIds) {
        // prevent stacking of runnables
        if (sTimerRunnable != null) {
            sHandler.removeCallbacks(sTimerRunnable);
        }
        sTimerRunnable = new Runnable() {
            @Override
            public void run() {
                onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);
            }
        };
        sHandler.postDelayed(sTimerRunnable, UPDATE_INTERVAL);
    }

    public void startTimer() {
        sTimerState     = TimerState.STARTED;

        sStartTime      = Calendar.getInstance().getTime();
        sTimerMillis    = 0;
    }

    public void resumeTimer() {
        sTimerState = TimerState.STARTED;
        sStartTime  = Calendar.getInstance().getTime();
        Log.d("tw", "Resuming timer");
    }

    public void pauseTimer() {
        sTimerState = TimerState.PAUSED;

        sTimerMillis += Calendar.getInstance().getTimeInMillis() - sStartTime.getTime();
        Log.d("tw", "Paused at: " + TimeUnit.MILLISECONDS.toSeconds(sTimerMillis) + " seconds");
    }

    public void resetTimer() {
        sTimerState     = TimerState.STOPPED;
        sTimerMillis    = 0;
        sStartTime      = null;
    }

    public void onPlayButtonClicked(Context context) {

        Log.d("tw", "State: " + sTimerState.name());
        if (isPaused()) {
            Log.d("tw", "isPaused, resuming");
            resumeTimer();
        } else if (isStarted()) {
            Log.d("tw", "isStarted, pausing");
            pauseTimer();
        } else {
            Log.d("tw", "isStopped, starting");
            startTimer();
        }

        onUpdate(context);

    }

    public void onResetButtonClicked(Context context) {

        Log.d("tw", "resetting timer");
        resetTimer();

        onUpdate(context);

    }

    public void onLogTimeButtonClicked(Context context) {

        // TODO: log time on Teamwork.com
        resetTimer();

        onUpdate(context);

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);

        if (intent.getAction().equals(CLICK_PLAY_BUTTON)) {
            onPlayButtonClicked(context);
        } else if (intent.getAction().equals(CLICK_RESET_BUTTON)) {
            onResetButtonClicked(context);
        } else if (intent.getAction().equals(CLICK_LOG_TIME_BUTTON)) {
            onLogTimeButtonClicked(context);
        }

    }

    private boolean isStarted() {
        return sTimerState == TimerState.STARTED;
    }

    private boolean isPaused() {
        return sTimerState == TimerState.PAUSED;
    }

    private boolean isStopped() {
        return sTimerState == TimerState.STOPPED;
    }

}
