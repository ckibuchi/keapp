package com.rube.tt.keapp.xmpp;

import org.jivesoftware.smack.XMPPConnection;

/**
 * Created by rube on 6/7/15.
 */
public abstract class XMPPConnectionChangeListener {

    public abstract void newConnection(XMPPConnection connection);

}

