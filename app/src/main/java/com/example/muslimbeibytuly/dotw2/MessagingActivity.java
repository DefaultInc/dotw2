package com.example.muslimbeibytuly.dotw2;
import android.annotation.SuppressLint;
import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.muslimbeibytuly.dotw2.Helpers.IPAddressHelper;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class MessagingActivity extends AppCompatActivity {
    WifiP2pManager manager;
    Channel channel;
    EditText editText;
    Button button;
    TextView textView;
    WifiP2pConfig config;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);
        editText = findViewById(R.id.editText);
        button = findViewById(R.id.button);
        textView = findViewById(R.id.textView);
        manager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        channel = manager.initialize(this, getMainLooper(), null);
        config = new WifiP2pConfig();
        config.deviceAddress = getIntent().getStringExtra("deviceAddress");
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
                socket.connect((new InetSocketAddress(IPAddressHelper.getIPFromMACAddress(config.deviceAddress), 8888)), 500);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(editText.getText().toString());
                outputStream.close();
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
}
