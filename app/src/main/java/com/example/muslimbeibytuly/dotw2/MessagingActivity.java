package com.example.muslimbeibytuly.dotw2;

import android.content.Context;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.WifiP2pManager.Channel;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.muslimbeibytuly.dotw2.Services.ServerAsyncTask;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.Socket;
import java.net.SocketException;
import java.util.Enumeration;

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
        manager.connect(channel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                //success logic
                Log.i("connected", "we are connected");
            }

            @Override
            public void onFailure(int reason) {
                //failure logic
                Log.i("connected", "we are not connected");
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });
    }

    private void sendMessage() {
        Log.i("Message socket", "button clicked");
        new SenderAsyncTask().executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    public class SenderAsyncTask extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... voids) {
            Log.i("socket", "started sending");
            Socket socket = new Socket();
            editText.getText();
            byte buffer[] = new byte[1024];
            try {
                socket.bind(null);
                Log.i("Get IP from MAC", getIPFromMac(config.deviceAddress));
                socket.connect((new InetSocketAddress(getIPFromMac(config.deviceAddress), 8888)), 500);
                ObjectOutputStream outputStream = new ObjectOutputStream(socket.getOutputStream());
                outputStream.writeObject(editText.getText().toString());
                outputStream.close();
                Log.i("socket", "still sending");
            } catch (IOException e) {
                Log.i("socket", "error");
                e.printStackTrace();
            } finally {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //TODO: testing
                    }
                }
            }
            return null;
        }
    }
}
