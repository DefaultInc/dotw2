package com.example.muslimbeibytuly.dotw2.Services;

import android.net.wifi.p2p.WifiP2pDevice;

import java.util.ArrayList;
import java.util.Collection;

public class DevicesStorage {
    private static DevicesStorage instance = null;

    public static DevicesStorage getInstance() {
        if (instance == null) {
            instance = new DevicesStorage();
        }
        return instance;
    }

    private ArrayList<WifiP2pDevice> p2pDevices;

    public ArrayList<WifiP2pDevice> getP2pDevices() {
        return p2pDevices;
    }

    void setP2pDevices(Collection<WifiP2pDevice> p2pDevices) {

        this.p2pDevices = new ArrayList<>(p2pDevices);
    }

    public ArrayList<String> getP2pDevicesNames() {
        ArrayList<String> devices = new ArrayList<>();
        if (p2pDevices.size() > 0) {
            for (WifiP2pDevice device : p2pDevices) {
                devices.add(device.deviceName);
            }
        }
        return devices;
    }
}
