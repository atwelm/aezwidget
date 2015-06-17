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

package com.atwelm.aezwidget.responses.sonarr;

/**
 * Created by austin on 11/17/14.
 */

import com.atwelm.aezwidget.responses.interfaces.AEZCell;

import org.springframework.http.HttpHeaders;

/**
 * A cell containing tv show data from a calendar request against a Sonarr server
 */
public class SonarrCell extends AEZCell {

    public class SeriesClass {
        public String title;
        public String airTime;
    }

    public SonarrCell(String title, String airTime, String airDate) {
        series = new SeriesClass();
        series.title = title;
        series.airTime = airTime;
        this.airDate = airDate;
    }

    public SeriesClass series;

    public String airDate;

    @Override
    public String getExecutionUrl() {
        return null;
    }

    @Override
    public String getExecutionType() {
        return null;
    }

    @Override
    public String getExecutionBody() {
        return null;
    }

    @Override
    public HttpHeaders getExecutionHeaders() {
        return null;
    }

    @Override
    public String getLabel() {
        return series.title;
    }

    @Override
    public String getIconUrl() {
        return null;
    }
}