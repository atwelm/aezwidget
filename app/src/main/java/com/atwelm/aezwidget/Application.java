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

package com.atwelm.aezwidget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.atwelm.aezwidget.data.ConfigurationServer;
import com.atwelm.aezwidget.data.PreferencesAccessor;

import java.util.ArrayList;
import java.util.List;


public class Application extends Activity {

    private final List<ConfigurationServer> mServerList = new ArrayList<ConfigurationServer>();

    private ArrayAdapter<ServerTypes> mServerTypeAdapter;

    private ArrayAdapter<ConfigurationServer> mServerAdapter;

    private Spinner mServerTypeSpinner;
    private EditText mNewServerUrlEditText;
    private EditText mNewServerNameEditText;

    private Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure);

        mContext = this;

//        PreferencesAccessor.loadTestServers(mContext);

        List<ConfigurationServer> configurationServers = PreferencesAccessor.getServerList(mContext);

        mServerList.addAll(configurationServers);

        mServerAdapter = new ArrayAdapter<ConfigurationServer>(this, android.R.layout.simple_list_item_1, mServerList);

        final ListView serverListView = (ListView) findViewById(R.id.server_list);
        serverListView.setAdapter(mServerAdapter);

        mServerTypeSpinner = (Spinner) findViewById(R.id.server_format);
        mNewServerUrlEditText = (EditText) findViewById(R.id.add_server_url);
        mNewServerNameEditText = (EditText) findViewById(R.id.add_server_name);

        Button addButton = (Button) findViewById(R.id.add_server_button);

        final Activity self = this;

        ServerTypes[] types = ServerTypes.values();

        mServerTypeAdapter = new ArrayAdapter<ServerTypes>(mContext, android.R.layout.simple_spinner_item, ServerTypes.values());

        mServerTypeSpinner.setAdapter(mServerTypeAdapter);

        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String newServerUrl = mNewServerUrlEditText.getText().toString();
                String newServerName = mNewServerNameEditText.getText().toString();
                ServerTypes selectedType = mServerTypeAdapter.getItem(mServerTypeSpinner.getSelectedItemPosition());

                ConfigurationServer server = PreferencesAccessor.addServer(mContext, selectedType, newServerUrl, newServerName);

                mServerAdapter.add(server);
                mNewServerNameEditText.setText("");
                mNewServerUrlEditText.setText("");
            }
        });

        serverListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final ConfigurationServer server = (ConfigurationServer) serverListView.getItemAtPosition(position);

                String serverName = server.getServerName();
                String serverAddress = server.getServerAddress();

                String message;
                if(serverName == null) {
                    message = "Delete server \"" + serverAddress + "\"?";
                } else {
                    message = "Delete server \"" + serverName + "\" with address of \"" + serverAddress + "\"?";
                }

                AlertDialog.Builder builder = new AlertDialog.Builder(self);

                builder.setTitle("Delete Server");
                builder.setMessage(message);

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        PreferencesAccessor.removeServer(mContext, server);

                        mServerList.remove(server);
                        mServerAdapter.notifyDataSetChanged();
                    }
                });

                builder.setNegativeButton("No", null);
                builder.show();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.configure, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
