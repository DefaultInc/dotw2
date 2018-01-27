package com.example.muslimbeibytuly.dotw2.Services;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;

import com.example.muslimbeibytuly.dotw2.MainActivity;
import com.example.muslimbeibytuly.dotw2.Services.DevicesStorage;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager manager;
    private Channel channel;
    private MainActivity activity;

    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.manager = manager;
        this.channel = channel;
        this.activity = activity;
        this.manager.discoverPeers(this.channel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
            }

            @Override
            public void onFailure(int reasonCode) {
            }
        });
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        final String action = intent.getAction();
//        TODO: this logic must be written in separate service
        if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            if (manager != null) {
                manager.requestPeers(channel, new WifiP2pManager.PeerListListener() {
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        DevicesStorage.getInstance().setP2pDevices(peers.getDeviceList());
                        activity.refreshDevicesListView();
                    }
                });
            }
        }
    }
}
