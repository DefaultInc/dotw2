package com.example.muslimbeibytuly.dotw2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.example.muslimbeibytuly.dotw2.Services.DevicesStorage;
import com.example.muslimbeibytuly.dotw2.Services.ServerAsyncTask;
import com.example.muslimbeibytuly.dotw2.Services.WiFiDirectBroadcastReceiver;

public class MainActivity extends AppCompatActivity {
    WifiP2pManager manager;
    WifiP2pManager.Channel channel;
    BroadcastReceiver receiver;
    IntentFilter intentFilter;
    public ListView devicesListView;
    ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        devicesListView = findViewById(R.id.devicesListView);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);

        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        new ServerAsyncTask().execute();
        devicesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                WifiP2pDevice device;
                WifiP2pConfig config = new WifiP2pConfig();
                config.deviceAddress = DevicesStorage.getInstance().getP2pDevices().get(i).deviceAddress;
                manager.connect(channel, config, new WifiP2pManager.ActionListener() {

                    @Override
                    public void onSuccess() {
                        Log.i("DEBUG", "SUCCESSFULL CONNECTED?");
                    }

                    @Override
                    public void onFailure(int reason) {
                        Log.i("DEBUG", "ERROR CODE!");
                    }
                });
                Intent intent = new Intent(getApplicationContext(), MessagingActivity.class);
                intent.putExtra("deviceAddress", DevicesStorage.getInstance().getP2pDevices().get(i).deviceAddress);
                startActivity(intent);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(manager, channel, this);
        registerReceiver(receiver, intentFilter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    public void refreshDevicesListView() {
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, DevicesStorage.getInstance().getP2pDevicesNames());
        devicesListView.setAdapter(adapter);
    }
}

