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

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.Nullable;
import android.util.Log;

import com.atwelm.aezwidget.AEZWidgetConfiguration;
import com.atwelm.aezwidget.ServerTypes;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

/**
 * Used to simplify and synchronize access to data storage
 */
public class PreferencesAccessor {
    private static final String LOG_TAG = "PreferencesAccessor";

    private static Gson mGson;

    private static final String PREFS_NAME = "com.atwelm.aezwidget";
    private static final String PREF_WIDGET_GSON_CLASS_TYPE_PREFIX_KEY = "appwidget_classtype_";
    private static final String PREF_WIDGET_DATA_PREFIX_KEY = "appwidget_data_";
    private static final String PREF_WIDGET_LAYOUT_PREFIX_KEY = "appwidget_layout_";

    private static final String PREF_SERVER_LIST_KEY = "SERVER_LIST";

    private static Gson gson() {
        if (mGson == null) {
            mGson = (new GsonBuilder()).create();
        }
        return mGson;
    }

    /**
     * Adds a configuration server of the given type and returns the associated
     * {@link com.atwelm.aezwidget.data.ConfigurationServer}
     * @param context The current context
     * @param serverType The type of server to add
     * @param serverUrl The Server URL
     * @param serverName The identifier for the server. If null, the serverUrl will be used
     * @return The created ConfigurationServer
     */
    public static synchronized ConfigurationServer addServer(Context context, ServerTypes serverType,
                                                             String serverUrl, @Nullable String serverName) {
        if (serverName == null) {
            serverName = serverUrl;
        }

        ConfigurationServer newServer = new ConfigurationServer(serverType, serverUrl, serverName);

        SharedPreferences appPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String serverListJson = appPreferences.getString(PREF_SERVER_LIST_KEY, null);

        List<ConfigurationServer> serverList;

        if (serverListJson == null) {
            serverList = new ArrayList<ConfigurationServer>();
        } else {
            Type listType = new TypeToken<ArrayList<ConfigurationServer>>() {}.getType();
            serverList = gson().fromJson(serverListJson, listType);
        }

        serverList.add(newServer);

        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(PREF_SERVER_LIST_KEY, gson().toJson(serverList));
        editor.commit();

        return newServer;
    }

    /**
     * Removes the given {@link com.atwelm.aezwidget.data.ConfigurationServer} from storage
     *
     * @param context The current context
     * @param server The {@link com.atwelm.aezwidget.data.ConfigurationServer} to remove
     */
    public static synchronized void removeServer(Context context, ConfigurationServer server) {
        SharedPreferences appPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        String serverListJson = appPreferences.getString(PREF_SERVER_LIST_KEY, null);

        if (serverListJson == null) {
            return;
        }

        Type listType = new TypeToken<ArrayList<ConfigurationServer>>() {}.getType();
        List<ConfigurationServer> serverList = gson().fromJson(serverListJson, listType);
        serverList.remove(server);

        SharedPreferences.Editor editor = appPreferences.edit();
        editor.putString(PREF_SERVER_LIST_KEY, gson().toJson(serverList));
        editor.commit();
    }

    /**
     * Gets a list of all the {@link com.atwelm.aezwidget.data.ConfigurationServer} items available to the application
     * @param context The current context
     * @return A {@link java.util.List} containing the {@link com.atwelm.aezwidget.data.ConfigurationServer} objects
     */
    public static synchronized List<ConfigurationServer> getServerList(Context context) {
        SharedPreferences appPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);

        List<ConfigurationServer> serverList;

        String serverListJson = appPreferences.getString(PREF_SERVER_LIST_KEY, null);

        if (serverListJson == null) {
            serverList = new ArrayList<ConfigurationServer>();
        } else {
            Type listType = new TypeToken<ArrayList<ConfigurationServer>>() {}.getType();
            serverList = gson().fromJson(serverListJson, listType);
        }

        return serverList;
    }

    /**
     * Loads the configuration item for the provided widgetId
     * @param context The current context
     * @param appWidgetId The widget ID
     * @return A configuration item used to load the widget instance with data
     */
    public static synchronized AEZWidgetConfiguration loadConfigurationItem(Context context, int appWidgetId) {
        Log.d(LOG_TAG, "loadConfigurationItem: loading for widget #" + appWidgetId);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);

        String classIdentifier = prefs.getString(PREF_WIDGET_GSON_CLASS_TYPE_PREFIX_KEY + appWidgetId, null);

        if (classIdentifier == null) {
            Log.e(LOG_TAG, "loadConfigurationItem: No classIdentifier set for widget #" + appWidgetId);
            return null;

        } else {
            Type configurationType = ServerTypes.valueOf(classIdentifier).getConfigurationType();
            String configurationItemJson = prefs.getString(PREF_WIDGET_DATA_PREFIX_KEY + appWidgetId, null);
            String layoutJson = prefs.getString(PREF_WIDGET_LAYOUT_PREFIX_KEY + appWidgetId, null);

            AEZWidgetConfiguration config = gson().fromJson(configurationItemJson, configurationType);

            Type layoutType = config.getServerType().getLayoutType();
            AEZLayout layout = gson().fromJson(layoutJson, layoutType);
            config.setLayout(layout);

            return config;
        }

    }

    /**
     * Saves the configuration item for a widget
     * @param context The current context
     * @param appWidgetId The widget ID
     * @param configurationItem The configuratino item to save
     */
    public static synchronized void saveConfigurationItem(Context context, int appWidgetId, AEZWidgetConfiguration configurationItem) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();

        prefs.putString(PREF_WIDGET_GSON_CLASS_TYPE_PREFIX_KEY + appWidgetId, configurationItem.getServerType().toString());

        String configurationItemJsonString = gson().toJson(configurationItem, AEZWidgetConfiguration.class);
        String layoutJsonString = gson().toJson(configurationItem.getLayout(), configurationItem.getServerType().getLayoutType());

        prefs.putString(PREF_WIDGET_DATA_PREFIX_KEY + appWidgetId, configurationItemJsonString);
        prefs.putString(PREF_WIDGET_LAYOUT_PREFIX_KEY + appWidgetId, layoutJsonString);
        prefs.commit();
    }

    /**
     * Removes the configuration for a widget
     * @param context The current context
     * @param appWidgetId The widget ID
     */
    public static synchronized  void deleteConfigurationItem(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(PREF_WIDGET_GSON_CLASS_TYPE_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_WIDGET_DATA_PREFIX_KEY + appWidgetId);
        prefs.remove(PREF_WIDGET_LAYOUT_PREFIX_KEY + appWidgetId);
        prefs.commit();
    }

//    public static synchronized void loadTestServers(Context context) {
//        List<ConfigurationServer> serverList = getServerList(context);
//
//        String badProtocol = "BadProtocol", badUrl = "BadUrl", badAddress = "BadAddress", badData = "BadData", badPort = "BadPort";
//        boolean badProtocolB = false, baddUrlB = false, badAddressB = false, badDataB = false, badPortB = false;
//
//        for(ConfigurationServer server : serverList) {
//            if (badProtocol.equals(server.getServerName())) {
//                badProtocolB = true;
//
//            } else if (badUrl.equals(server.getServerName())) {
//                baddUrlB = true;
//
//            } else if (badAddress.equals(server.getServerName())) {
//                badAddressB = true;
//
//            } else if (badData.equals(server.getServerName())) {
//                badDataB = true;
//
//            } else if (badPort.equals(server.getServerName())) {
//                badPortB = true;
//            }
//        }
//
//        if (!badProtocolB) {
//            addServer(context, Constants.SERVER_TYPES.Generic, "https://192.168.11.164:4070/aezwidget/openhab", badProtocol);
//        }
//
//        if (!baddUrlB) {
//            addServer(context, Constants.SERVER_TYPES.Generic, "http://192.168.11.164:4070/aeezwidget", badUrl);
//        }
//
//        if (!badAddressB) {
//            addServer(context, Constants.SERVER_TYPES.Generic, "http://192.168.11.199:4070/aezwidget/openhab", badAddress);
//        }
//
//        if (!badDataB) {
//            addServer(context, Constants.SERVER_TYPES.Generic, "http://192.168.11.164:8090/rest/sitemaps/widgets?type=json", badData);
//        }
//
//        if (!badPortB) {
//            addServer(context, Constants.SERVER_TYPES.Generic, "http://192.168.11.164:3225/aezwidget/openhab", badPort);
//        }
//    }
}
