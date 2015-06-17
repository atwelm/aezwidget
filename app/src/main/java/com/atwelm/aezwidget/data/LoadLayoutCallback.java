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

package com.atwelm.aezwidget.data;

import android.support.annotation.Nullable;

import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

import java.util.List;

/**
 * Defines the callback response for
 * {@link com.atwelm.aezwidget.data.ConfigurationServer#loadLayouts(LoadLayoutCallback)}
 */
public interface LoadLayoutCallback {
    /**
     * @param layouts A list of layouts provided by the server
     */
    public void success(List<AEZLayout> layouts);

    /**
     * Returned when an error occurs when loading the layouts
     * @param errorCode The status code returned from the server. Will be a proper HTTP status code or -1
     * @param errorString String with more error information if available
     */
    public void failure(int errorCode, @Nullable String errorString);
}
