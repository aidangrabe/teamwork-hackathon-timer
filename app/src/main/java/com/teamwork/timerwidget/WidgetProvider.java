package com.teamwork.timerwidget;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.widget.RemoteViews;

/**
 * Created by aidangrabe on 25/06/15.
 *
 */
public class WidgetProvider extends AppWidgetProvider {

    public WidgetProvider() {

    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);

        for (int widgetId : appWidgetIds) {

            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(),R.layout.widget_layout);

            appWidgetManager.updateAppWidget(widgetId, remoteViews);

        }
    }

}
