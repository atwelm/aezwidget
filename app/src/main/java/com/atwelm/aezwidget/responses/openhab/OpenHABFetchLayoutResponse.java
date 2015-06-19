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

package com.atwelm.aezwidget.responses.openhab;

import com.atwelm.aezwidget.responses.generic.GenericCell;
import com.atwelm.aezwidget.responses.generic.GenericLayout;
import com.atwelm.aezwidget.responses.interfaces.AEZFetchLayoutResponseInterface;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;

import org.springframework.http.HttpHeaders;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * A Generic Response received from a {@link com.atwelm.aezwidget.data.ConfigurationServer} item
 */
public class OpenHABFetchLayoutResponse implements AEZFetchLayoutResponseInterface {
    public static final String
            TYPE_FRAME = "Frame",
            TYPE_SWITCH = "Switch",
            TYPE_IMAGE = "Image",
            LABEL_IMAGES = "images",
            EXECUTION_TYPE = "POST",
            EXECUTION_BODY = "ON",
            EXECUTION_HEADER_1_KEY = "Content-Type",
            EXECUTION_HEADER_1_VALUE = "text/plain";


    public static List<OpenHABWidget> parseWidgets(JsonElement widget, Gson gson) {
        List<OpenHABWidget> widgets = new LinkedList<OpenHABWidget>();

        if (widget != null) {
            if (widget.isJsonObject()) {
                OpenHABWidget innerWidget = gson.fromJson(widget, OpenHABWidget.class);
                widgets.add(innerWidget);
            } else if (widget.isJsonArray()) {
                JsonArray array = widget.getAsJsonArray();

                for (JsonElement element : array) {
                    if (element.isJsonObject()) {
                        OpenHABWidget innerWidget = gson.fromJson(element, OpenHABWidget.class);
                        widgets.add(innerWidget);
                    } else {
                        // Log error
                    }
                }
            }
        }
        return widgets;
    }


    public class OpenHABSitemap {
        public String name;
        public String link;
        public OpenHABHomepage homepage;
    }


    public class OpenHABHomepage {
        public String id;
        public String link;
        public JsonElement widget;

        private List<OpenHABWidget> widgets;

        public List<OpenHABWidget> getWidgets(Gson gson) {
            if (widgets == null) {
                widgets = parseWidgets(widget, gson);
            }
            return widgets;
        }
    }


    public class OpenHABWidget {
        public String type;
        public String label;
        public String icon;
        public List<OpenHABMapping> mapping; // OpenHAB 1
        public List<OpenHABMapping> mappings; // OpenHAB 2
        public JsonElement widget;
        public OpenHABItem item;
        public String url;

        private List<OpenHABWidget> widgets;

        public List<OpenHABWidget> getWidgets(Gson gson) {
            if (widgets == null) {
                widgets = parseWidgets(widget, gson);
            }
            return widgets;
        }
    }


    public class OpenHABMapping {
        public String command;
        public String label;
    }


    public class OpenHABItem {
        public String type;
        public String name;
        public String state;
        public String label;
        public String link;
    }

    public String name;

    public String link;

    public OpenHABHomepage homepage;

    @Override
    public String getTitle() {
        return name;
    }

    @Override
    public List<AEZLayout> getLayouts() {

        Map<String,String> imageMap = new HashMap<>();

        List<AEZLayout> layouts = new LinkedList<>();

        Gson gson = new GsonBuilder().create();

        List<OpenHABWidget> homepageWidgets = homepage.getWidgets(gson);

        for( OpenHABWidget topWidget : homepageWidgets) {
            if (TYPE_FRAME.equals(topWidget.type) && LABEL_IMAGES.equals(topWidget.label)) {
                List<OpenHABWidget> topWidgetWidgets = topWidget.getWidgets(gson);
                for (OpenHABWidget innerWidget : topWidgetWidgets) {
                    if (TYPE_IMAGE.equals(innerWidget.type)) {
                        imageMap.put(innerWidget.label, innerWidget.url);
                    }
                }
            }
        }

        for( OpenHABWidget topWidget : homepageWidgets) {
            if (TYPE_FRAME.equals(topWidget.type) && !LABEL_IMAGES.equals(topWidget.label)) {
                List<GenericCell> itemList = new LinkedList<>();

                List<OpenHABWidget> topWidgetWidgets = topWidget.getWidgets(gson);
                for (OpenHABWidget innerWidget : topWidgetWidgets) {
                    if(TYPE_SWITCH.equals(innerWidget.type) && innerWidget.item != null) {

                        List<OpenHABMapping> innerMapping = null;
                        if (innerWidget.mapping != null && !innerWidget.mapping.isEmpty()) {
                            innerMapping = innerWidget.mapping;
                        } else if (innerWidget.mappings != null && !innerWidget.mappings.isEmpty()) {
                            innerMapping = innerWidget.mappings;
                        }

                        if (innerMapping != null) {
                            for (OpenHABMapping mapping : innerMapping) {
                                HttpHeaders headers = new HttpHeaders();
                                headers.add(EXECUTION_HEADER_1_KEY, EXECUTION_HEADER_1_VALUE);
                                GenericCell cell = new GenericCell(mapping.label, imageMap.get(mapping.label), null, EXECUTION_TYPE, innerWidget.item.link, mapping.command, headers);
                                itemList.add(cell);
                            }

                        } else {

                            HttpHeaders headers = new HttpHeaders();
                            headers.add(EXECUTION_HEADER_1_KEY, EXECUTION_HEADER_1_VALUE);

                            //     public GenericCell(String label, @Nullable String iconUrl, @Nullable Bitmap iconBitmap, String executionType, String executionUrl, @Nullable String executionBody, @Nullable HttpHeaders executionHeaders) {
                            GenericCell cell = new GenericCell(innerWidget.label, imageMap.get(innerWidget.label), null, EXECUTION_TYPE, innerWidget.item.link, EXECUTION_BODY, headers);
                            itemList.add(cell);
                        }
                    }
                }

                AEZLayout layout = new GenericLayout(topWidget.label, itemList);
                layouts.add(layout);
            }
        }

        return layouts;
    }
}
