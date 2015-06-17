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

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.atwelm.aezwidget.data.PreferencesAccessor;
import com.atwelm.aezwidget.responses.interfaces.AEZCell;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WidgetService extends RemoteViewsService {

    private static Map<Integer, List<AEZCell>> mInstanceItemMap =
            new ConcurrentHashMap<Integer, List<AEZCell>>();

    public synchronized static void removeWidgetData(Integer widgetId) {
        if (mInstanceItemMap != null) {
            mInstanceItemMap.remove(widgetId);
        }
    }

    public synchronized static AEZCell getActionableItem(Context context, Integer widgetId, int itemIndex) {
        if (mInstanceItemMap == null) {
            mInstanceItemMap = new ConcurrentHashMap<Integer, List<AEZCell>>();
        }

        List<AEZCell> actionableItems = mInstanceItemMap.get(widgetId);
        if (actionableItems == null) {
            actionableItems = new LinkedList<AEZCell>();
            mInstanceItemMap.put(widgetId, actionableItems);
        }

        if (actionableItems.size() <= itemIndex) {
            AEZWidgetConfiguration configuration =
                    PreferencesAccessor.loadConfigurationItem(context, widgetId);
            actionableItems.clear();
            actionableItems.addAll(configuration.getLayout().getCells());
        }
        AEZCell item = actionableItems.get(itemIndex);

        return item;
    }


    public synchronized static void addActionableItems(Integer widgetId, List<AEZCell> items) {
        if (mInstanceItemMap == null) {
            mInstanceItemMap = new ConcurrentHashMap<Integer, List<AEZCell>>();
        }
        List<AEZCell> actionableItems = mInstanceItemMap.get(widgetId);
        if (actionableItems == null) {
            actionableItems = new LinkedList<AEZCell>();
            mInstanceItemMap.put(widgetId, actionableItems);
        }
        mInstanceItemMap.get(widgetId).addAll(items);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return new WidgetRemoteViewsFactory(this.getApplicationContext(), intent);
    }
}

class WidgetRemoteViewsFactory implements RemoteViewsService.RemoteViewsFactory {
    public static final String LOG_TAG = "WidgetRVF";

    private static final String COMPONENT_SIZE_SMALL = "Small";
    private static final String COMPONENT_SIZE_MEDIUM = "Medium";
    private static final String COMPONENT_SIZE_LARGE = "Large";
    private static final String COMPONENT_SIZE_EXTRA_LARGE = "Extra Large";
    private static final String COMPONENT_SIZE_FLEXIBLE = "Flexible";

    private final Context mContext;
    private final int mAppWidgetId;
    private final List<AEZCell> mItems = new LinkedList<AEZCell>();
    //    private int mItemCount;
    private int mTextSizeDp;
    private AEZWidgetConfiguration mConfiguration;
    private int mWidgetComponentId;
    private int mWidgetComponentLayoutId;

    private Integer mWidgetHeight = null;
    private Integer mWidgetMinHeight = null;
    private Integer mWidgetMaxHeight = null;

    WidgetRemoteViewsFactory(Context context, Intent intent) {
        mContext = context;
        mAppWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);

        WidgetService.addActionableItems(mAppWidgetId, mItems);
    }

    @Override
    public void onCreate() {
        updateConfiguration();
    }

    private void updateData() {

    }

    private void updateConfiguration() {
        mConfiguration = PreferencesAccessor.loadConfigurationItem(mContext, mAppWidgetId);

        if (mConfiguration != null) {
            if (mConfiguration.getTextSizeDp() != null) {
                mTextSizeDp = mConfiguration.getTextSizeDp();
            } else {
                mTextSizeDp = 12;
            }

            String widgetComponentSize = mConfiguration.getWidgetComponentSize();
            if (COMPONENT_SIZE_SMALL.equals(widgetComponentSize)) {
                mWidgetComponentId = R.id.widget_item_small;
                mWidgetComponentLayoutId = R.layout.widget_item_small;

            } else if (COMPONENT_SIZE_MEDIUM.equals(widgetComponentSize)) {
                mWidgetComponentId = R.id.widget_item_medium;
                mWidgetComponentLayoutId = R.layout.widget_item_medium;

            } else if (COMPONENT_SIZE_LARGE.equals(widgetComponentSize)) {
                mWidgetComponentId = R.id.widget_item_large;
                mWidgetComponentLayoutId = R.layout.widget_item_large;

            } else if (COMPONENT_SIZE_EXTRA_LARGE.equals(widgetComponentSize)) {
                mWidgetComponentId = R.id.widget_item_extra_large;
                mWidgetComponentLayoutId = R.layout.widget_item_extra_large;

            } else if (COMPONENT_SIZE_FLEXIBLE.equals(widgetComponentSize)) {
                mWidgetComponentId = R.id.widget_item_flexible;
                mWidgetComponentLayoutId = R.layout.widget_item_flexible;
                mWidgetHeight = mConfiguration.getItemHeight();

            } else {
                mWidgetComponentId = R.id.widget_item_medium;
                mWidgetComponentLayoutId = R.layout.widget_item_medium;
            }

            if (mConfiguration.getLayout().getCells() != null && mItems.size() == 0) {
                mItems.addAll(mConfiguration.getLayout().getCells());
            }
        }
    }

    @Override
    public void onDataSetChanged() {
        updateConfiguration();
    }

    @Override
    public void onDestroy() {
        mItems.clear();
    }

    @Override
    public int getCount() {
        return mItems.size();
    }

    @Override
    public RemoteViews getViewAt(int position) {
        RemoteViews rv = new RemoteViews(mContext.getPackageName(), mWidgetComponentLayoutId);

        AEZCell item = mItems.get(position);

        if (item.getIconUrl() == null) {
            rv.setViewVisibility(R.id.widgetitem_textview, View.VISIBLE);
            rv.setViewVisibility(R.id.widgetitem_imageview, View.GONE);
            rv.setTextViewText(R.id.widgetitem_textview, item.getLabel());
            rv.setTextViewTextSize(R.id.widgetitem_textview, TypedValue.COMPLEX_UNIT_DIP, mTextSizeDp);
        } else {
            rv.setViewVisibility(R.id.widgetitem_textview, View.GONE);
            rv.setViewVisibility(R.id.widgetitem_imageview, View.VISIBLE);
            rv.setImageViewBitmap(R.id.widgetitem_imageview, item.getIconBitmap());
        }

        if (mWidgetHeight != null) {
            rv.setInt(R.id.widgetitem_textview, "setHeight", mWidgetHeight);
            rv.setInt(R.id.widgetitem_imageview, "setMaxHeight", mWidgetHeight);
        }

        Bundle extras = new Bundle();
        extras.putInt(WidgetProvider.EXTRA_ITEM, position);

        Intent fillInIntent = new Intent();
        fillInIntent.putExtras(extras);

        rv.setOnClickFillInIntent(mWidgetComponentId, fillInIntent);

        return rv;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}
