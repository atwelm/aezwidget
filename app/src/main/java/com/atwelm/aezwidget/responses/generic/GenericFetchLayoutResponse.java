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

package com.atwelm.aezwidget.responses.generic;

import com.atwelm.aezwidget.responses.interfaces.AEZFetchLayoutResponseInterface;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A Generic Response received from a {@link com.atwelm.aezwidget.data.ConfigurationServer} item
 */
public class GenericFetchLayoutResponse implements AEZFetchLayoutResponseInterface {

    /**
     * The title to identify the server
     */
    public String title;

    /**
     * A list of available layouts
     */
    public List<GenericLayout<GenericCell>> layouts;

    @Override
    public String getTitle() {
        return title;
    }

    @Override
    public List<AEZLayout> getLayouts() {
        if (layouts == null) {
            return new ArrayList<AEZLayout>(0);
        } else {
            List<AEZLayout> layoutList = new ArrayList<AEZLayout>(layouts.size());
            layoutList.addAll(layouts);
            return layoutList;
        }

    }
}
