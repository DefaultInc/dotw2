package com.example.muslimbeibytuly.dotw2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver {
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
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
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.i("p2p connection status", "p2p is enabled");
            } else {
                Log.i("p2p connection status", "p2p is not enabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            Log.i("p2p wifi status", "peers changed");
            if (mManager != null) {
                mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener(){
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peers) {
                        Log.i("Access to peers",String.format("PeerListListener: %d peers available, updating device list", peers.getDeviceList().size()));
                        for (WifiP2pDevice x: peers.getDeviceList()
                             ) {
                            Log.i("peer device", x.deviceName + " " + x.deviceAddress);

                        }

                    }
                });
            }
        }
    }
}
