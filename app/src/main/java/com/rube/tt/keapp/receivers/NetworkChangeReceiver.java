package com.rube.tt.keapp.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.rube.tt.keapp.Login;
import com.rube.tt.keapp.services.XMPPService;

import java.util.ArrayList;


/**
 * Created by rube on 6/2/15.
 */
public class NetworkChangeReceiver extends BroadcastReceiver {
    private static  final String TAG = NetworkChangeReceiver.class.getSimpleName();
    private Intent xMPPConnectionIntent;

    private static String lastActiveNetworkName = null;

    @SuppressWarnings("deprecation")
    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (cm == null) {
            Log.d(TAG, "Connectivity Manager is null returning!");
            return;
        }else{
            Log.d(TAG, "Found connectibity manager proceeding with to load change ");
        }

        for (NetworkInfo network : cm.getAllNetworkInfo()) {
            Log.d(TAG, "available=" + (network.isAvailable()?1:0)
                    + ", connected=" + (network.isConnected()?1:0)
                    + ", connectedOrConnecting=" + (network.isConnectedOrConnecting()?1:0)
                    + ", failover=" + (network.isFailover()?1:0)
                    + ", roaming=" + (network.isRoaming()?1:0)
                    + ", networkName=" + network.getTypeName());
        }

        Intent svcIntent;

        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null && XMPPService.IsRunning) {
            String networkName = network.getTypeName();
            boolean networkChanged = false;
            boolean connectedOrConnecting = network.isConnectedOrConnecting();
            boolean connected = network.isConnected();
            if (!networkName.equals(lastActiveNetworkName)) {
                lastActiveNetworkName = networkName;
                networkChanged = true;
            }
            Log.d(TAG, XMPPService.ACTION_NETWORK_STATUS_CHANGED + " name="
                    + network.getTypeName() + " changed=" + networkChanged + " connected=" + connected
                    + " connectedOrConnecting="
                    + connectedOrConnecting);



            svcIntent = new Intent(XMPPService.ACTION_NETWORK_STATUS_CHANGED);

            svcIntent.putExtra("networkChanged", networkChanged);
            svcIntent.putExtra("connectedOrConnecting", connectedOrConnecting);
            svcIntent.putExtra("connected", connected);
            context.startService(svcIntent);
        }else{
            Log.d(TAG, "Network is null or XMPPService.IsRunning is not running :++" +XMPPService.IsRunning);
        }

        SharedPreferences prefs = context.getSharedPreferences(Login.ARG_ACCOUNT_NAME, 0);
        network = intent.getParcelableExtra(ConnectivityManager.EXTRA_NETWORK_INFO);
        Log.d(TAG, "Found action from network type "+network.getTypeName());

        //Only allow connection on this tye of internet types
        ArrayList<String> internetNetworks = new ArrayList<String>();
        internetNetworks.add("WIFI");
        internetNetworks.add("MOBILE");
        internetNetworks.add("ETHERNET");

        if (internetNetworks.contains(network.getTypeName().toUpperCase()) && network.isConnected() &&
                prefs.getBoolean("startOnWifiConnected", true)) {
            // Start XMPP connection
            Log.d(TAG, "startOnWifiConnected enabled, wifi connected, sending intent");
            context.startService(new Intent(XMPPService.ACTION_CONNECT));

        } else if (internetNetworks.contains(network.getTypeName().toUpperCase()) && !network.isConnected() &&
                prefs.getBoolean("stopOnWifiDisconnected", true)) {
            // Stop XMPPService
            Log.d(TAG, "stopOnWifiDisconnected enabled, wifi disconnected, sending intent");
            context.startService(new Intent(XMPPService.ACTION_DISCONNECT));

        }
    }

    public static void setLastActiveNetworkName(Context ctx) {
        ConnectivityManager cm = (ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();
        if (network != null) {
            lastActiveNetworkName = network.getTypeName();
        }
    }


}
