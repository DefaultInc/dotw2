package com.example.muslimbeibytuly.dotw2.Services;

import android.app.Activity;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerAsyncTask extends AsyncTask<Void, Void, Void> {

    public ServerAsyncTask() {
    }


    @Override
    protected Void doInBackground(Void... params) {
        Log.i("socket status", "started");
        ServerSocket serverSocket = null;
        Socket client;
        try {
            serverSocket = new ServerSocket(8888);
            while (true) {
                client = serverSocket.accept();
                Log.i("socket status", "accepted");
                InputStream inputStream = client.getInputStream();
                Log.i("socket status", "got stream");
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder result = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    result.append(line);
                }
                Log.i("received from socket", result.toString());

                        MessagesStorage.getInstance().addMessage(result.toString());
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (serverSocket != null) {
                    serverSocket.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}
