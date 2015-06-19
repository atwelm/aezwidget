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

package com.atwelm.aezwidget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.atwelm.aezwidget.data.ConfigurationServer;
import com.atwelm.aezwidget.data.LoadLayoutCallback;
import com.atwelm.aezwidget.data.PreferencesAccessor;
import com.atwelm.aezwidget.responses.interfaces.AEZLayout;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;

/**
 * The configuration screen for the {@link WidgetProvider Atwelmwidget} AppWidget.
 */
public class WidgetConfigurationActivity extends Activity {
    private static final String LOG_TAG = "WidgetConfigActivity";

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    static Gson mGson = (new GsonBuilder()).create();

    private List<ConfigurationServer> mServerList;
    private ArrayAdapter<ConfigurationServer> mServerAdapter;
    private Spinner mServerSpinner;


    private LinearLayout mLayoutSelectorLayout;
    private List<AEZLayout> mLayoutList;
    private ArrayAdapter<AEZLayout> mLayoutAdapter;
    private Spinner mLayoutSpinner;

    Spinner mTextSizeSpinner;
    TextView mTextSample;

    Spinner mColumnCountSpinner;
    Spinner mRowCountSpinner;
    Spinner mComponentHeightSpinner;

    LinearLayout mConfigurationLayout;
    LinearLayout mLoadingSplash;
    LinearLayout mLoadingFailedSplash;
    LinearLayout mSelectServer;

    public WidgetConfigurationActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        setContentView(R.layout.atwelmwidget_configure);

        final Context context = this;

        // Set up the server list
        mServerSpinner = (Spinner) findViewById(R.id.widget_server_spinner);
        mSelectServer = (LinearLayout) findViewById(R.id.widget_server_selector);

        mServerList = PreferencesAccessor.getServerList(this);

        if (mServerList.isEmpty()) {
            Log.e(LOG_TAG, "No server list");
            // TODO: show pop-up instructing to add servers
        } else {
            mServerAdapter = new ArrayAdapter<ConfigurationServer>(this, android.R.layout.simple_list_item_1, mServerList);

            mServerSpinner.setAdapter(mServerAdapter);

            mServerSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    setViewMode(ViewMode.LAYOUT_LOADING);

                    ConfigurationServer configServer = mServerAdapter.getItem(mServerSpinner.getSelectedItemPosition());

                    configServer.loadLayouts(new LoadLayoutCallback() {
                        @Override
                        public void success(final List<AEZLayout> layouts) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    mLayoutList = layouts;
                                    mLayoutAdapter = new ArrayAdapter<AEZLayout>(context, android.R.layout.simple_list_item_1, mLayoutList);
                                    mLayoutSpinner.setAdapter(mLayoutAdapter);

                                    setViewMode(ViewMode.LAYOUT_LOADING_SUCCESS);
                                }
                            });
                        }

                        @Override
                        public void failure(int errorCode, String errorString) {
                            Log.e(LOG_TAG, errorString);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    setViewMode(ViewMode.LAYOUT_LOADING_FAILED);
                                }
                            });
                        }
                    });
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });
        }

        // Set up the layout list
        mLayoutSelectorLayout = (LinearLayout) findViewById(R.id.widget_layout_selector);
        mLayoutSpinner = (Spinner) findViewById(R.id.widget_layout_spinner);

        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        mTextSample = (TextView) findViewById(R.id.text_size_sample);

        mTextSizeSpinner = (Spinner) findViewById(R.id.text_size_spinner);
        mTextSizeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String value = (String) mTextSizeSpinner.getItemAtPosition(position);
                mTextSample.setTextSize(TypedValue.COMPLEX_UNIT_DIP, Integer.valueOf(value));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        mColumnCountSpinner = (Spinner) findViewById(R.id.column_count_spinner);
        mRowCountSpinner = (Spinner) findViewById(R.id.row_count_spinner);
        mComponentHeightSpinner = (Spinner) findViewById(R.id.component_height_spinner);
        mLoadingSplash = (LinearLayout) findViewById(R.id.loading_splash);
        mLoadingFailedSplash = (LinearLayout) findViewById(R.id.loading_failed_splash);
        mConfigurationLayout = (LinearLayout) findViewById(R.id.widget_configuration_options);

        setViewMode(ViewMode.LAYOUT_LOADING);

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
    }

    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {

            final Context context = WidgetConfigurationActivity.this;
            AEZLayout layout = mLayoutAdapter.getItem(mLayoutSpinner.getSelectedItemPosition());

            AEZWidgetConfiguration configurationItem =
                    new AEZWidgetConfiguration(
                            mServerAdapter.getItem(mServerSpinner.getSelectedItemPosition()),
                            layout,
                            Integer.valueOf((String) mTextSizeSpinner.getSelectedItem()),
                            Integer.valueOf((String) mColumnCountSpinner.getSelectedItem()),
                            Integer.valueOf((String) mRowCountSpinner.getSelectedItem()),
                            (String)mComponentHeightSpinner.getSelectedItem());

            PreferencesAccessor.saveConfigurationItem(context, mAppWidgetId, configurationItem);

            // It is the responsibility of the configuration activity to update the app widget
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            WidgetProvider.configureWidget(context, appWidgetManager, mAppWidgetId);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    enum ViewMode {
        LAYOUT_LOADING,
        LAYOUT_LOADING_FAILED,
        LAYOUT_LOADING_SUCCESS
    }

    private void setViewMode(ViewMode viewMode) {
        switch(viewMode) {
            case LAYOUT_LOADING:
                mConfigurationLayout.setVisibility(View.GONE);
                mLoadingSplash.setVisibility(View.VISIBLE);
                mLoadingFailedSplash.setVisibility(View.GONE);
                break;

            case LAYOUT_LOADING_FAILED:
                mConfigurationLayout.setVisibility(View.GONE);
                mLoadingSplash.setVisibility(View.GONE);
                mLoadingFailedSplash.setVisibility(View.VISIBLE);
                break;

            case LAYOUT_LOADING_SUCCESS:
                mConfigurationLayout.setVisibility(View.VISIBLE);
                mLoadingSplash.setVisibility(View.GONE);
                mLoadingFailedSplash.setVisibility(View.GONE);
                break;
        }
    }
}



