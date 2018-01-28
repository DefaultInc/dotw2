package com.example.muslimbeibytuly.dotw2.Services;

import android.net.wifi.p2p.WifiP2pDevice;
import android.util.Log;

import com.example.muslimbeibytuly.dotw2.MessagingActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessagesStorage {
    public MessagingActivity activity;
    private static MessagesStorage instance = null;
    private Map<String, List<String>> messages;
    private Map<String, String> users;

    private MessagesStorage() {
        messages = new HashMap<>();
        users = new HashMap<>();
        for (WifiP2pDevice device : DevicesStorage.getInstance().getP2pDevices()) {
            users.put(device.deviceAddress, device.deviceName);
        }
    }

    public void addMessage(String user, boolean who, String message) {
        List<String> l;
        l = messages.get(activity.config.deviceAddress);
        if (who) {
            if (l == null) l = new ArrayList<>();
            l.add(users.get(activity.config.deviceAddress) + ": " + message);
        }
        else {
            if (l == null) l = new ArrayList<>();
            l.add("Me: " + message);
        }
        messages.put(activity.config.deviceAddress, l);
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                activity.refreshMessageList();
            }
        });

    }

    public void updateUsers() {
        users = new HashMap<>();
        for (WifiP2pDevice device : DevicesStorage.getInstance().getP2pDevices()) {
            users.put(device.deviceAddress, device.deviceName);
        }
    }

    public static MessagesStorage getInstance() {
        if (instance == null) {
            instance = new MessagesStorage();
        }
        return instance;
    }

    public List<String> getMessages() {
        Log.i("GET MESSAGES", activity.config.deviceAddress);
        if (messages != null && messages.containsKey(activity.config.deviceAddress)) {
            return messages.get(activity.config.deviceAddress);
        } else {
            return new ArrayList<>();
        }
    }
}
