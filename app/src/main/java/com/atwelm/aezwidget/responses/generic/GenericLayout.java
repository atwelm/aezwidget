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

import com.atwelm.aezwidget.responses.interfaces.AEZCell;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * A Generic layout returned within the {@link GenericFetchLayoutResponse}
 */

public class GenericLayout<T extends AEZCell> implements AEZLayout {

    public GenericLayout(String title, List<T> cells) {
        this.title = title;
        this.cells = cells;
    }

    /**
     * The title of the layout
     */
    public String title;

    /**
     * The cells contained in the layout
     */
    public List<T> cells;

    public String getTitle() {
        return title;
    }

    public List<AEZCell> getCells() {
        if (cells == null) {
            return new ArrayList<AEZCell>(0);
        } else {
            List<AEZCell> itemList = new ArrayList<>(cells.size());
            itemList.addAll(cells);
            return itemList;
        }
    }

    /**
     * @return The title of the layout
     */
    public String toString() {
        return title;
    }
}
