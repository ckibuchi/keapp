package com.rube.tt.keapp.xmpp;

import android.util.Log;

import org.jivesoftware.smack.ConnectionListener;
import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by rube on 5/28/15.
 */
public class XMPPConnectionLister implements ConnectionListener {

    private final String TAG = XMPPConnectionLister.class.getSimpleName();

    @Override
    public void connected(XMPPConnection connection){
        Log.v(TAG, "Successfully connected to the XMPP server.");
    }

    @Override
    public void authenticated(XMPPConnection connection, boolean resumed){
        Log.v(TAG, "Successfully authenticated to the XMPP server.");
    }

    @Override
    public void reconnectionSuccessful() {
        Log.v(TAG, "Successfully reconnected to the XMPP server.");

    }

    @Override
    public void reconnectionFailed(Exception arg0) {
        Log.v(TAG,"Failed to reconnect to the XMPP server.");
    }

    @Override
    public void reconnectingIn(int seconds) {
        Log.v(TAG,"Reconnecting in " + seconds + " seconds.");
    }

    @Override
    public void connectionClosedOnError(Exception arg0) {
        Log.v(TAG,"Connection to XMPP server was lost.");
    }

    @Override
    public void connectionClosed() {
        Log.v(TAG,"XMPP connection was closed.");

    }
}
