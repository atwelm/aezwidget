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

import android.util.Log;

import com.atwelm.aezwidget.responses.generic.GenericCell;
import com.atwelm.aezwidget.responses.generic.GenericFetchLayoutResponse;
import com.atwelm.aezwidget.responses.generic.GenericLayout;
import com.atwelm.aezwidget.responses.openhab.OpenHABFetchLayoutResponse;
import com.atwelm.aezwidget.responses.sonarr.SonarrCell;
import com.atwelm.aezwidget.responses.sonarr.SonarrFetchLayoutResponse;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * Created by austin on 5/4/15.
 */
public enum ServerTypes {
    Generic(GenericFetchLayoutResponse.class, GenericLayout.class, GenericCell.class),
    Sonarr(SonarrFetchLayoutResponse.class, GenericLayout.class, SonarrCell.class),
    OpenHAB(OpenHABFetchLayoutResponse.class, GenericLayout.class, GenericCell.class);

    private static final String LOG_TAG = "ServerTypes";

    private ServerTypes(Class responseClass, Class layoutClass, Class cellClass) {
        mResponseClass = responseClass;
        mLayoutClass = layoutClass;
        mCellClass = cellClass;
    }

    private final Class mResponseClass;
    private final Class mLayoutClass;
    private final Class mCellClass;

    public Class getResponseClass() {
        return mResponseClass;
    }

    public Class getLayoutClass() {
        return mLayoutClass;
    }

    public Class getCellClass() {
        return mCellClass;
    }

    public Type getLayoutType() {
        // TODO: Is there a better way to do this? Class declarations appear to not be usable as generic types...
        if (mCellClass.equals(GenericCell.class)) {
            return new TypeToken<GenericLayout<GenericCell>>(){}.getType();

        } else if (mCellClass.equals(SonarrCell.class)) {
            return new TypeToken<GenericLayout<SonarrCell>>(){}.getType();

        } else {
            Log.e(LOG_TAG, "getLayoutType() section not defined for the class \"" + mCellClass.toString() + "\"");
            return null;
        }
    }

    public Type getConfigurationType() {
        // TODO: Is there a better way to do this? Class declarations appear to not be usable as generic types...
        if (mCellClass.equals(GenericCell.class)) {
            return new TypeToken<AEZWidgetConfiguration<GenericLayout<GenericCell>>>(){}.getType();

        } else if (mCellClass.equals(SonarrCell.class)) {
            return new TypeToken<AEZWidgetConfiguration<GenericLayout<SonarrCell>>>(){}.getType();

        } else {
            Log.e(LOG_TAG, "getConfigurationType() section not defined for the class \"" + mCellClass.toString() + "\"");
            return null;
        }
    }
}
