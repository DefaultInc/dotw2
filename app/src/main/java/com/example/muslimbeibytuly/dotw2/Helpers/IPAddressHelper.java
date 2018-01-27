package com.example.muslimbeibytuly.dotw2.Helpers;
import android.util.Log;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPAddressHelper {
    private final static String p2pInt = "p2p-p2p0";

    public static String getIPFromMACAddress(String deviceMACAddress) {
        BufferedReader bufferedReader = null;
        try {
            bufferedReader = new BufferedReader(new FileReader("/proc/net/arp"));
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                String[] splitted = line.split(" +");
                if (splitted.length >= 4) {
                    String deviceAddress = splitted[5];
                    if (deviceAddress.matches(".*" + p2pInt + ".*")) {
                        String countedMACAddress = splitted[3];
                        if (countedMACAddress.matches(deviceMACAddress)) {
                            return splitted[0];
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    public static String getLocalIPAddress() {
        try {
            for (Enumeration<NetworkInterface> enumeration = NetworkInterface.getNetworkInterfaces();
                 enumeration.hasMoreElements(); ) {
                NetworkInterface networkInterface = enumeration.nextElement();
                for (Enumeration<InetAddress> enumerationOfIPAdressess = networkInterface.getInetAddresses();
                     enumerationOfIPAdressess.hasMoreElements(); ) {
                    InetAddress inetAddress = enumerationOfIPAdressess.nextElement();
                    String networkInterfaceName = networkInterface.getName();
                    if (networkInterfaceName.matches(".*" + p2pInt + ".*")) {
                        if (inetAddress instanceof Inet4Address) {
                            return getDottedDecimalIP(inetAddress.getAddress());
                        }
                    }
                }
            }
        } catch (SocketException | NullPointerException ex) {
            Log.e("AndroidNetworkAddress", "getLocalIPAddress()", ex);
        }
        return null;
    }

    private static String getDottedDecimalIP(byte[] ipAddressBytes) {
        StringBuilder ipAddressString = new StringBuilder();
        for (int i = 0; i < ipAddressBytes.length; i++) {
            if (i > 0) ipAddressString.append(".");
            ipAddressString.append(ipAddressBytes[i] & 0xFF);
        }
        return ipAddressString.toString();
    }
}
