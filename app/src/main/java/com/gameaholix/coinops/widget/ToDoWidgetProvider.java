package com.gameaholix.coinops.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.todo.ToDoDetailActivity;

/**
 * Implementation of App Widget functionality.
 */
public class ToDoWidgetProvider extends AppWidgetProvider {

    private static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                        int appWidgetId) {

        // code here used from:
        // https://github.com/laaptu/appwidget-listview/tree/appwidget-listview1

        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_list);

        // RemoteViews Service needed to provide adapter for ListView
        Intent svcIntent = new Intent(context, ToDoWidgetService.class);

        // passing app widget id to that RemoteViews Service
        svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        // setting a unique Uri to the intent
        // don't know its purpose to me right now
        svcIntent.setData(Uri.parse(
                svcIntent.toUri(Intent.URI_INTENT_SCHEME)));

        // setting adapter to listview of the widget
        views.setRemoteAdapter(R.id.lv_widget, svcIntent);

        // setting an empty view in case of no data
        views.setTextViewText(R.id.tv_list_empty, context.getString(R.string.to_do_list_empty));
        views.setEmptyView(R.id.lv_widget, R.id.tv_list_empty);

        views.setTextViewText(R.id.tv_widget_title, context.getString(R.string.to_do_list_widget_title));

        // Setup PendingIntent for the ListView
        Intent intent = new Intent(context, ToDoDetailActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        views.setPendingIntentTemplate(R.id.lv_widget, pendingIntent);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ComponentName widget = new ComponentName(context.getPackageName(),
                ToDoWidgetProvider.class.getName());
        int[] appWidgetIds = AppWidgetManager.getInstance(context).getAppWidgetIds(widget);
        onUpdate(context, AppWidgetManager.getInstance(context), appWidgetIds);

        super.onReceive(context, intent);
    }

}

