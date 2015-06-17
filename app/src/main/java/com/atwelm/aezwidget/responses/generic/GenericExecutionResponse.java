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

package com.atwelm.aezwidget.responses.generic;

import com.atwelm.aezwidget.responses.interfaces.AEZExecutionResponseInterface;

/**
 * The response data generated from a generic cell execution
 */
public class GenericExecutionResponse implements AEZExecutionResponseInterface {
    public String label;
    public String iconUrl;

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }
}
