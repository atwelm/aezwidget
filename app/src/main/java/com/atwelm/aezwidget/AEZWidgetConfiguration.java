/*
 * Copyright (c) 2015.
 *
 * The author of these files, Austin Wellman, allows modification, reproduction and private use of them. It, derived works, and works including derived files cannot be published to an app store, packaged and redistributed, used commercially, or sublicensed.
 *
 * The author is not responsible for anything this software causes to happen and is provided as-is with no guarantee of anything.
 *
 * During the building process, gradle pulls the following files covered under the Apache License 2.0 which is located in the root of this repository as the file "included_licenses/APACHE_2.0.txt":
 * gson
 * spring-android-core
 * spring-android-rest-template
 * support-annotations
 */

package com.atwelm.aezwidget;

import com.atwelm.aezwidget.data.ConfigurationServer;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

/**
 * An item that contains the configuration details for a widget instance
 */
public class AEZWidgetConfiguration<T extends AEZLayout> {
    private final int mTextSizeDp;
    private T mLayout;
    private final int mColumnCount;
    private final int mRowCount;
    private final String mWidgetComponentSize;
    private int mItemHeight;
    private final ConfigurationServer mConfigurationServer;

    public int getItemHeight() {
        return mItemHeight;
    }

    public void setItemHeight(int itemHeight) {
        this.mItemHeight = itemHeight;
    }

    public AEZWidgetConfiguration(ConfigurationServer configurationServer, T layout, int textSizeDp, int columnCount, int rowCount, String widgetComponentSize) {
        mConfigurationServer = configurationServer;
        mLayout = layout;
        mTextSizeDp = textSizeDp;
        mColumnCount = columnCount;
        mRowCount = rowCount;
        mWidgetComponentSize = widgetComponentSize;
    }

    public Integer getTextSizeDp() {
        return mTextSizeDp;
    }

    public Integer getColumnCount() {
        return mColumnCount;
    }

    public Integer getRowCount() {
        return mRowCount;
    }

    public String getWidgetComponentSize() {
        return mWidgetComponentSize;
    }

    public void setLayout(T layout) {
        mLayout = layout;
    }

    public AEZLayout getLayout() {
        return mLayout;
    }

    public ServerTypes getServerType() {
        return mConfigurationServer.getServerType();
    }

}
