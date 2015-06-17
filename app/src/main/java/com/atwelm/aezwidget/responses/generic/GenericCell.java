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

import android.graphics.Bitmap;
import android.support.annotation.Nullable;

import com.atwelm.aezwidget.responses.interfaces.AEZCell;

import org.springframework.http.HttpHeaders;
import org.springframework.web.client.RestTemplate;

/**
 * A cell object that is meant to be contained in a {@link com.atwelm.aezwidget.responses.interfaces.AEZLayout} that
 * also contains logic to execute network requests based on what the cell is created with
 */
public class GenericCell extends AEZCell {
    private static RestTemplate mRestTemplate;

    /**
     * Base private constructor for a generic cell without null notation checks
     *
     * @param label The label of the cell
     * @param iconUrl The url of the icon for the cell
     * @param executionType The execution type (GET/PUT) when the cell is presed
     * @param executionUrl The execution url when the cell is pressed
     * @param executionBody The execution body when the cell is pressed
     * @param executionHeaders The execution headers when the cell is pressed
     */
    private GenericCell(String label, @Nullable String iconUrl, @Nullable String executionType,
                        @Nullable String executionUrl, @Nullable String executionBody,
                        @Nullable HttpHeaders executionHeaders) {
        this.label = label;
        this.iconUrl = iconUrl;
        this.executionType = executionType;
        this.executionUrl = executionUrl;
        this.executionBody = executionBody;
        this.executionHeaders = executionHeaders;
    }

    /**
     * The constructor for a non-executable cell
     * @param label The label of the cell
     * @param iconUrl The url of the icon for the cell
     * @param iconBitmap The bitmap value for the icon
     */
    public GenericCell(String label, @Nullable String iconUrl, @Nullable Bitmap iconBitmap) {
        this(label, iconUrl, null, null, null, null);
        this.mIconBitmap = iconBitmap;
    }

    // TODO: Add support for icon states

    /**
     * An executable cell
     * @param label The label of the cell
     * @param iconUrl The url of the icon for the cell
     * @param iconBitmap The bitmap value for the icon
     * @param executionType The execution type (GET/PUT) when the cell is presed
     * @param executionUrl The execution url when the cell is pressed
     * @param executionBody The execution body when the cell is pressed
     * @param executionHeaders The execution headers when the cell is pressed
     */
    public GenericCell(String label, @Nullable String iconUrl, @Nullable Bitmap iconBitmap, String executionType, String executionUrl, @Nullable String executionBody, @Nullable HttpHeaders executionHeaders) {
        this(label, iconUrl, executionType, executionUrl, executionBody, executionHeaders);
        this.mIconBitmap = iconBitmap;
    }

    private static RestTemplate getRestTemplate() {
        if (mRestTemplate == null) {
            mRestTemplate = new RestTemplate();
        }
        return mRestTemplate;
    }

    private String label;
    private String iconUrl;
    private String executionUrl;
    private HttpHeaders executionHeaders;
    private String executionType;
    private String executionBody;

    @Override
    public String getExecutionUrl() {
        return executionUrl;
    }

    @Override
    public String getExecutionType() {
        return executionType;
    }

    @Override
    public String getExecutionBody() {
        return executionBody;
    }

    @Override
    public HttpHeaders getExecutionHeaders() {
        return executionHeaders;

    }

    @Override
    public String getLabel() {
        return label;
    }

    @Override
    public String getIconUrl() {
        return iconUrl;
    }
}