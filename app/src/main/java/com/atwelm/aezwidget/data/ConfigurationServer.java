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

package com.atwelm.aezwidget.data;

import android.util.Log;

import com.atwelm.aezwidget.ServerTypes;
import com.atwelm.aezwidget.responses.interfaces.AEZFetchLayoutResponseInterface;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.GsonHttpMessageConverter;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.UUID;

/**
 * Item that represents a server which the widget will receive data from.
 */
public class ConfigurationServer {
    private static final String LOG_IDENTIFIER = "ConfigurationServer";

    public enum FetchMode {
        HYBRID,
        ONLINE,
        CACHED
    }

    private final String mId;
    private final ServerTypes mServerType;
    private final String mServerName;
    private final String mServerAddress;

    @Override
    public boolean equals(Object o) {
        if (o == null || o.getClass()!= this.getClass()) {
            return false;
        }

        ConfigurationServer configurationServer = (ConfigurationServer) o;

        return mId.equals(configurationServer.getServerId());
    }

    /**
     * Constructor
     *
     * @param serverType the class to use toDecode the received cells
     * @param serverAddress The address of the server
     * @param serverName String to identify the server by the user
     */
    public ConfigurationServer(ServerTypes serverType, String serverAddress, String serverName) {
        mId = UUID.randomUUID().toString();
        mServerType = serverType;
        mServerAddress = serverAddress;
        mServerName = serverName;
    }

    public String getServerId() {
        return mId;
    }

    public ServerTypes getServerType() {
        return mServerType;
    }

    public String getServerAddress() {
        return mServerAddress;
    }

    public String getServerName() {
        return mServerName;
    }

    /**
     * Loads the layouts from the server and provides them in the callback if provided
     * @param callback Contains the layouts or error information
     */
    public void loadLayouts(final LoadLayoutCallback callback) {
        final ConfigurationServer self = this;
        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    RestTemplate restTemplate = new RestTemplate();
                    restTemplate.getMessageConverters().add(new GsonHttpMessageConverter());

                    ResponseEntity<AEZFetchLayoutResponseInterface> responseEntity = restTemplate.getForEntity(mServerAddress, mServerType.getResponseClass());

                    int returnStatus = responseEntity.getStatusCode().value();

                    if (returnStatus <= 200 && returnStatus < 300) {
                        AEZFetchLayoutResponseInterface response = responseEntity.getBody();

                        List<AEZLayout> receivedLayouts = response.getLayouts();

                        callback.success(receivedLayouts);

                    } else {
                        callback.failure(returnStatus, null);
                    }

                } catch (HttpStatusCodeException rsce) {
                    Log.e(LOG_IDENTIFIER, rsce.toString());
                    callback.failure(rsce.getStatusCode().value(), rsce.toString());

                } catch (RestClientException rce) {
                    Log.e(LOG_IDENTIFIER, rce.toString());
                    callback.failure(-1, rce.toString());
                }
            }
        });
        t.start();
    }

    @Override
    public String toString() {
        return mServerName;
    }
}
