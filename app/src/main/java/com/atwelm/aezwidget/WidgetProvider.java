/*
 * Copyright (c) 2015.
 *
 * The author of these files, Austin Wellman, allows modification, reproduction and private use of them. It, derived works, and works including derived files cannot be published to an app store, packaged and redistributed, used commercially, or sublicensed.
 *
 * During the building process, gradle pulls the following files covered under the Apache License 2.0 which is located in the root of this repository as the file "included_licenses/APACHE_2.0.txt":
 * gson
 * spring-android-core
 * spring-android-rest-template
 * support-annotations
 */

package com.atwelm.aezwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.widget.RemoteViews;

import com.atwelm.aezwidget.data.ExecutionResponseCallback;
import com.atwelm.aezwidget.data.PreferencesAccessor;
import com.atwelm.aezwidget.responses.interfaces.AEZCell;
import com.atwelm.aezwidget.responses.interfaces.AEZExecutionResponseInterface;


/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link WidgetConfigurationActivity TestWidgetConfigureActivity}
 */
public class WidgetProvider extends AppWidgetProvider {
    private static final String LOG_TAG = "WidgetProvider";

    public static final String TAP_ACTION = "com.atwelm.aezwidget.TAP_ACTION";
    public static final String EXTRA_ITEM = "com.atwelm.aezwidget.EXTRA_ITEM";

    /**
     * Layout id's. Multiple layouts are necessary because widget column counts cannot be modified on the fly.
     */
    private static final int[] COLUMN_COUNT_ID_ARRAY = {
            R.id.widget_3_column, // Default Column Count
            R.id.widget_1_column,
            R.id.widget_2_column,
            R.id.widget_3_column,
            R.id.widget_4_column,
            R.id.widget_5_column,
            R.id.widget_6_column,
            R.id.widget_7_column,
            R.id.widget_8_column,
            R.id.widget_9_column};


    /**
     * Layout id's. Multiple layouts are necessary because widget column counts cannot be modified on the fly.
     */
    private static final int[] COLUMN_COUNT_LAYOUT_ARRAY = {
            R.layout.widget_3_column, // Default Column Count
            R.layout.widget_1_column,
            R.layout.widget_2_column,
            R.layout.widget_3_column,
            R.layout.widget_4_column,
            R.layout.widget_5_column,
            R.layout.widget_6_column,
            R.layout.widget_7_column,
            R.layout.widget_8_column,
            R.layout.widget_9_column};

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        for (int appWidgetId : appWidgetIds) {
            configureWidget(context, appWidgetManager, appWidgetId);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

    @Override
    public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
        int minWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_WIDTH);
        int maxWidth = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_WIDTH);
        int minHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
        int maxHeight = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MAX_HEIGHT);

        AEZWidgetConfiguration configuration = PreferencesAccessor.loadConfigurationItem(context, appWidgetId);

        int height = ((int)Resources.getSystem().getDisplayMetrics().density)*maxHeight;

        configuration.setItemHeight((height)/configuration.getRowCount());

        PreferencesAccessor.saveConfigurationItem(context, appWidgetId, configuration);

        super.onAppWidgetOptionsChanged(context, appWidgetManager, appWidgetId, newOptions);

        // Is this necessary?
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetId, COLUMN_COUNT_ID_ARRAY[configuration.getColumnCount()]);
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        if (intent.getAction().equals(TAP_ACTION)) {
            final int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
            final int viewIndex = intent.getIntExtra(EXTRA_ITEM, 0);

            Thread t = new Thread(new Runnable() {
                @Override
                public void run() {
                    AEZCell item = WidgetService.getActionableItem(context, appWidgetId, viewIndex);
                    item.execute(new ExecutionResponseCallback() {
                        @Override
                        public void success(AEZExecutionResponseInterface responseData) {
                            // TODO: do something
                        }

                        @Override
                        public void failure(int errorCode) {
                            // TODO: do something
                        }
                    });
                }
            });

            t.start();
        }
        super.onReceive(context, intent);
    }

//    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {

        // When the user deletes the widget, delete the preference associated with it.
        final int N = appWidgetIds.length;
        for (int i=0; i<N; i++) {
            PreferencesAccessor.deleteConfigurationItem(context, appWidgetIds[i]);
            WidgetService.removeWidgetData(appWidgetIds[i]);
        }
        super.onDeleted(context, appWidgetIds);
    }

    static protected void configureWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {

        AEZWidgetConfiguration configuration =
                PreferencesAccessor.loadConfigurationItem(context, appWidgetId);

        if (configuration == null) {
            return;
        }

        Intent populateIntent = new Intent(context, WidgetService.class);
        populateIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        populateIntent.setData(Uri.parse(populateIntent.toUri(Intent.URI_INTENT_SCHEME)));

        int widgetTypeId = (configuration == null ? COLUMN_COUNT_ID_ARRAY[0] : COLUMN_COUNT_ID_ARRAY[configuration.getColumnCount()]);
        int widgetLayoutId = (configuration == null ? COLUMN_COUNT_LAYOUT_ARRAY[0] : COLUMN_COUNT_LAYOUT_ARRAY[configuration.getColumnCount()]);

        RemoteViews rv = new RemoteViews(context.getPackageName(), widgetLayoutId);
        rv.setRemoteAdapter(widgetTypeId, populateIntent);

        Intent selectionIntent = new Intent(context, WidgetProvider.class);
        selectionIntent.setAction(WidgetProvider.TAP_ACTION);
        selectionIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);

        PendingIntent selectionPendingIntent = PendingIntent.getBroadcast(context, appWidgetId, selectionIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        rv.setPendingIntentTemplate(widgetTypeId, selectionPendingIntent);

        appWidgetManager.updateAppWidget(appWidgetId, rv);
    }
}


