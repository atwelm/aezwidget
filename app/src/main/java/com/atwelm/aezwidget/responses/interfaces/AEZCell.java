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

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.atwelm.aezwidget.data.ExecutionResponseCallback;
import com.atwelm.aezwidget.responses.generic.GenericExecutionResponse;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.io.BufferedInputStream;

/**
 * A core cell object that is meant to be contained in a {@link com.atwelm.aezwidget.responses.interfaces.AEZLayout} that
 * also contains logic to execute network requests based on what the cell is created with
 */
public abstract class AEZCell {
    private static final String LOG_TAG = "AEZCell";

    private static RestTemplate mRestTemplate;

    private static RestTemplate getRestTemplate() {
        if (mRestTemplate == null) {
            mRestTemplate = new RestTemplate();
        }
        return mRestTemplate;
    }

    protected Bitmap mIconBitmap;

    public abstract String getExecutionUrl();

    public abstract String getExecutionType();

    public abstract String getExecutionBody();

    public abstract HttpHeaders getExecutionHeaders();

    public abstract String getLabel();

    public abstract String getIconUrl();

    public final Bitmap getIconBitmap() {
        String iconUrl = getIconUrl();

        if (mIconBitmap == null && iconUrl != null) {

            Log.d(LOG_TAG, "getIconBitmap at url=\"" + iconUrl + "\"");

            try {
                ResponseEntity<Resource> responseEntity = getRestTemplate().getForEntity(iconUrl, Resource.class);
                BufferedInputStream bis = new BufferedInputStream(responseEntity.getBody().getInputStream());

                mIconBitmap = BitmapFactory.decodeStream(bis);
                return mIconBitmap;
            } catch (Exception e) {
                Log.e(LOG_TAG, e.toString());
                e.printStackTrace();
                mIconBitmap = null;
            }
        }
        return mIconBitmap;
    }

    public boolean isExecutable() {
        String executionType = getExecutionType();
        String executionUrl = getExecutionUrl();
        return executionType != null && executionUrl != null &&
                (executionType.equals("GET") || executionType.equals("POST") || executionType.equals("PUT"));
    }

    public final void execute(final ExecutionResponseCallback callback) {
        if (!isExecutable()) {
            return;
        }

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String executionType = getExecutionType();
                    String executionUrl = getExecutionUrl();
                    String executionBody = getExecutionBody();
                    HttpHeaders executionHeaders = getExecutionHeaders();

                    HttpEntity<String> requestEntity = new HttpEntity<String>(executionBody, executionHeaders);
                    RestTemplate restTemplate = new RestTemplate();
                    ResponseEntity<GenericExecutionResponse> responseEntity = restTemplate.exchange(executionUrl, HttpMethod.valueOf(executionType), requestEntity, GenericExecutionResponse.class);

                    int returnStatus = responseEntity.getStatusCode().value();

                    if (returnStatus <= 200 && returnStatus < 300) {
                        GenericExecutionResponse response = responseEntity.getBody();
                        if (callback != null) {
                            callback.success(response);
                        }
                    } else {
                        if (callback != null) {
                            callback.failure(returnStatus);
                        }
                    }

                } catch (HttpStatusCodeException hsce) {
                    callback.failure(hsce.getStatusCode().value());
                } catch (RestClientException rce) {
                    // TODO: Make this more specific since it includes scenarios such as when the network cannot be reached
                    callback.failure(-1);
                }
            }
        });
        t.start();
    }
}