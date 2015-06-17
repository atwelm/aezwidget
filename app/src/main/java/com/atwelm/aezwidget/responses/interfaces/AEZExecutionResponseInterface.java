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

package com.atwelm.aezwidget.responses.interfaces;

/**
 * Created by austin on 2/23/15.
 */
public interface AEZExecutionResponseInterface {

    // TODO: Implement layout updating
    /**
     * Returns the new label for the button
     * @return the new label
     */
    public String getLabel();

    // TODO: Implement icon updating
    /**
     * Returns a new icon for the button
     * @return The new icon
     */
    public String getIconUrl();
}
