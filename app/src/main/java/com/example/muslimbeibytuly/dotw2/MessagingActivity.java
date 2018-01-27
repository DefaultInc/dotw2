package com.example.muslimbeibytuly.dotw2;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.example.muslimbeibytuly.dotw2.Helpers.IPAddressHelper;
import com.example.muslimbeibytuly.dotw2.Services.DevicesStorage;
import com.example.muslimbeibytuly.dotw2.Services.MessagesStorage;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

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
                new SenderAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
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
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(editText.getText().toString());
                outputStream.close();
                MessagesStorage.getInstance().addMessage(false, editText.getText().toString());
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

    public void refreshMessageList() {
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, MessagesStorage.getInstance().getMessages());
        messagesListView.setAdapter(adapter);
    }
}
