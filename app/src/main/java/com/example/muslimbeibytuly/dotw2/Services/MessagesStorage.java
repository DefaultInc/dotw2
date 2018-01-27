package com.example.muslimbeibytuly.dotw2.Services;

import android.widget.ListAdapter;

import com.example.muslimbeibytuly.dotw2.MessagingActivity;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by muslimbeibytuly on 1/28/18.
 */

public class MessagesStorage {
    public MessagingActivity activity;
    public static MessagesStorage instance = null;
    public Map<String, List<String>> messages;

    MessagesStorage() {
        messages = new HashMap<String, List<String>>();
    }

    public void addMessage(String message) {
        messages.get((String) activity.config.deviceAddress).add(message);
    }

    public static MessagesStorage getInstance() {
        if (instance == null) {
            instance = new MessagesStorage();
        }
        return instance;
    }

    public List<String> getMessages() {
        return messages.get((String) activity.config.deviceAddress);
    }
}

