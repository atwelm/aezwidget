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

package com.atwelm.aezwidget.data;

import com.atwelm.aezwidget.responses.interfaces.AEZExecutionResponseInterface;

/**
 * Callback function for executions
 */
public interface ExecutionResponseCallback {

    /**
     * A successful execution
     * @param responseData Data associated with the execution
     */
    public void success(AEZExecutionResponseInterface responseData);

    /**
     * Returned when an error occurs when loading the layouts
     * @param errorCode The status code returned from the server
     */
    public void failure(int errorCode);
}
