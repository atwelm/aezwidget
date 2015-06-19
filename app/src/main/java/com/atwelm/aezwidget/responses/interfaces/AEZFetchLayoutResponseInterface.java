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
 * An interface for layout fetch responses
 */
public interface AEZFetchLayoutResponseInterface {

    /**
     * Returns the title of the server
     * @return The server title
     */
    public String getTitle();

    /**
     * Contains a list of the layouts the server returns
     * @return The layouts
     */
    public List<AEZLayout> getLayouts();
}
