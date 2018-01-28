package com.example.muslimbeibytuly.dotw2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muslimbeibytuly.dotw2.Helpers.IPAddressHelper;
import com.example.muslimbeibytuly.dotw2.Services.DevicesStorage;
import com.example.muslimbeibytuly.dotw2.Services.MessagesStorage;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.util.Collections;
import java.util.List;

public class MessagingActivity extends AppCompatActivity {
    WifiP2pManager manager;
    Channel channel;
    EditText editText, editTextIP;
    Button button;
    public WifiP2pConfig config;
    ListView messagesListView;
    public static String IP_SERVER = "192.168.49.1";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        editText = findViewById(R.id.editText);
//        editTextIP = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        config = new WifiP2pConfig();
        config.deviceAddress = getIntent().getStringExtra("deviceAddress");
        config.wps.setup = WpsInfo.PBC;
        messagesListView = findViewById(R.id.messagesListView);
        MessagesStorage.getInstance().activity = this;
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MessagesStorage.getInstance().getMessages());
        messagesListView.setAdapter(adapter);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!editText.getText().toString().equals("")) {
                    new SenderAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                }
            }
        });
    }

    @SuppressLint("StaticFieldLeak")
    public class SenderAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Socket socket = new Socket();
            editText.getText();
            try {
                socket.bind(null);
                String _ip = IPAddressHelper.getIPFromMACAddress(config.deviceAddress);
                if (_ip == null) _ip = IP_SERVER;
                socket.connect((new InetSocketAddress(_ip, 8888)), 500);
                PrintWriter outputStream = new PrintWriter(socket.getOutputStream(), true);
                outputStream.write(getOwnMacAddress() + "$$" + editText.getText().toString());
                outputStream.close();
                MessagesStorage.getInstance().addMessage(config.deviceAddress, false, editText.getText().toString());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }
    }

    public static String loadFileAsString(String filePath) throws java.io.IOException {
        StringBuffer data = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(filePath));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            data.append(readData);
        }
        reader.close();
        return data.toString();
    }


    private String getOwnMacAddress() {
        @SuppressLint("WifiManagerLeak") WifiManager wifiMgr = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        String name = wifiInfo.getSSID();
        Log.i("DEBUG NAME", name);
        return name;
    }

    public void refreshMessageList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MessagesStorage.getInstance().getMessages());
        messagesListView.setAdapter(adapter);
        editText.setText("");
    }

    public void BrowseFile() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, 42);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode,
                                 Intent resultData) {

        // The ACTION_OPEN_DOCUMENT intent was sent with the request code
        // READ_REQUEST_CODE. If the request code seen here doesn't match, it's the
        // response to some other intent, and the code below shouldn't run at all.

        if (requestCode == 42 && resultCode == Activity.RESULT_OK) {
            // The document selected by the user won't be returned in the intent.
            // Instead, a URI to that document will be contained in the return intent
            // provided to this method as a parameter.
            // Pull that URI using resultData.getData().
            Uri uri = null;
            if (resultData != null) {
                uri = resultData.getData();
                //Log.i(TAG, "Uri: " + uri.toString());
                //showImage(uri);
            }
        }
    }
}
