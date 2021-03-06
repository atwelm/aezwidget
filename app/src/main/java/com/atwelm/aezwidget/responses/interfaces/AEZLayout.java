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

package com.atwelm.aezwidget.responses.interfaces;

import java.util.List;

/**
 * A base cell layout interface
 */

public interface AEZLayout<T extends AEZCell> {

    /**
     * Returns the layout title
     * @return the layout title
     */
    public abstract String getTitle();

    /**
     * Gets the cells contained in the layout
     * @return The cells
     */
    public abstract List<AEZCell> getCells();

    /**
     * @return The title of the layout
     */
    public abstract String toString();
}
