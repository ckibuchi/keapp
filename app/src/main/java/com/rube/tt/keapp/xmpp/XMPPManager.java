package com.rube.tt.keapp.xmpp;

/**
 * Created by rube on 5/27/15.
 */

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.StanzaListener;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.StanzaFilter;
import org.jivesoftware.smack.filter.StanzaTypeFilter;
import org.jivesoftware.smack.packet.DefaultExtensionElement;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.Stanza;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.sasl.SASLMechanism;
import org.jivesoftware.smack.sasl.provided.SASLPlainMechanism;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smack.chat.ChatManager;
import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.FileTransferNegotiator;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.ping.PingFailedListener;
import org.jivesoftware.smackx.ping.PingManager;
import org.jivesoftware.smackx.ping.android.ServerPingWithAlarmManager;
import org.jivesoftware.smackx.xhtmlim.XHTMLManager;
import org.jivesoftware.smackx.xhtmlim.XHTMLText;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

import com.rube.tt.keapp.services.XMPPService;
import com.rube.tt.keapp.utils.Constants;

public class XMPPManager {

    public static final String HOST = Constants.XMPP_HOST;
    public static final int PORT = Constants.XMPP_PORT;
    public static final String SERVICE = Constants.XMPP_SERVICE;
    public static final String USERNAME = Constants.XMPP_USERNAME;
    public static final String PASSWORD = Constants.XMPP_PASSWORD;
    public static final  String RESOURCE = Constants.XMPP_RESOURCE;

    public static final int DISCONNECTED = 1;
    // A "transient" state - will only be CONNECTING *during* a call to start()
    public static final int CONNECTING = 2;
    public static final int CONNECTED = 3;
    // A "transient" state - will only be DISCONNECTING *during* a call to stop()
    public static final int DISCONNECTING = 4;
    // This state means we are waiting for a retry attempt etc.
    // mostly because a connection went down
    public static final int WAITING_TO_CONNECT = 5;
    // We are waiting for a valid data connection
    public static final int WAITING_FOR_NETWORK = 6;

    // Indicates the current state of the service (disconnected/connecting/connected)
    private int status = DISCONNECTED;

    private XMPPConnectionLister xmppConnectionLister;
    private XMPPStatus xmppStatus;
    private String statusAction = "";
    private int currentRetryCount = 0;
    private Handler reconnectHandler;
    private long lastPing = new Date().getTime();
    private int pingIntervalInSec = 5;
    private int newConnectionCount = 0;
    private int reusedConnectionCount = 0;



    private static final String TAG = XMPPManager.class.getSimpleName();

    private AbstractXMPPConnection connection;
    private XMPPTCPConnectionConfiguration.Builder configBuilder;
    private ArrayList<String> messages = new ArrayList<String>();
    private Handler mHandler = new Handler();
    private Activity activity;
    private  Context context;
    private ProgressDialog progressDialog;
    private static XMPPManager xmppManager;

    private EditText recipient;
    private EditText textMessage;
    private ListView listview;

    private final List<XMPPConnectionChangeListener> connectionChangeListeners;


    public XMPPManager(Activity activity, ProgressDialog progressDialog){
        this.activity = activity;
        this.context = this.activity.getApplicationContext();
        xmppStatus = XMPPStatus.getInstance(context);
        this.progressDialog = progressDialog;
        reconnectHandler = new Handler(XMPPService.getServiceLooper());
        connectionChangeListeners = new ArrayList<XMPPConnectionChangeListener>();


    }

    //Skip the activity if started frm receiver context
    public XMPPManager(){
        connectionChangeListeners = new ArrayList<XMPPConnectionChangeListener>();

    }

    //Skip the activity if started frm receiver context
    public XMPPManager(Context context){
        this.context = context;
        xmppStatus = XMPPStatus.getInstance(context);
        reconnectHandler = new Handler(XMPPService.getServiceLooper());
        connectionChangeListeners = new ArrayList<XMPPConnectionChangeListener>();
    }

    public void setContext(Context context){
        this.context = context;
    }

    private final Runnable reconnectRunnable = new Runnable() {
        public void run() {
            Log.i(TAG, "attempting reconnection by issuing intent ");
            Intent chatServiceIntent = new Intent(XMPPService.ACTION_CONNECT);
            if(activity != null){
                activity.startService(chatServiceIntent);
            }else if(context != null){
                context.startService(chatServiceIntent);
            }else{
                Log.i(TAG, "Unable to start service context undefined!!! ");

            }
        }
    };

    /**
     * This getter creates the XmppManager and init the XmppManager
     * with a new connection with the current preferences.
     *
     * @param ctx
     * @return
     */
    public static XMPPManager getInstance(Context ctx) {
        if (xmppManager == null) {
            xmppManager = new XMPPManager(ctx);
        }
        return xmppManager;
    }


    private void start(int initialState) {
        switch (initialState) {
            case CONNECTED:
                initConnection();
                break;
            case WAITING_TO_CONNECT:
                updateStatus(initialState, "Waiting to connect");
                break;
            case WAITING_FOR_NETWORK:
                updateStatus(initialState, "Waiting for network");
                break;
            default:
                throw new IllegalStateException("xmppMgr start() Invalid State: " + initialState);
        }
    }

    /**
     * Initializes the XMPP connection
     *
     * 1. Creates a new XMPPConnection object if necessary
     * 2. Connects the XMPPConnection
     * 3. Authenticates the user with the server
     *
     * Calls maybeStartReconnect() if something went wrong
     *
     */
    private void initConnection() {

        // assert we are only ever called from one thread
        assert (!Thread.currentThread().getName().equals(XMPPService.SERVICE_THREAD_NAME));

        // everything is ready for a connection attempt
        updateStatus(CONNECTING, "");

        // create a new connection if the connection is obsolete or if the old connection is still active
        if (connection == null || !connection.isConnected() ) {
            try {
                Log.d(TAG, "Creating new connectin ...getConnection()");
                connection = getConnection();

            } catch (Exception e) {
                // connection failure
                Log.d(TAG, "Exception creating new XMPP Connection"+e);
                maybeStartReconnect("");
                return;
            }

            connectAndAuth(connection);
            onConnectionEstablished(connection);
            newConnectionCount++;
        } else {

            // we reuse the xmpp connection so only connect() is needed
            //connectAndAuth(connection);
            reusedConnectionCount++;
        }
        // this code is only executed if we have an connection established

    }

    private void onConnectionEstablished(AbstractXMPPConnection conn) {
        connection = conn;
        updateAction("Connection established ..");
        xmppConnectionLister = new XMPPConnectionLister();
        connection.addConnectionListener(xmppConnectionLister);

        Log.d(TAG, "connection established with parameters: con=" + connection.isConnected() +
                " auth=" + connection.isAuthenticated() +
                " enc=" + connection.isSecureConnection() +
                " comp=" + connection.isUsingCompression());

        currentRetryCount = 0;
        Date now = new Date();
        updateStatus(CONNECTED, String.format("%tF  %tT", now, now));

        Log.d(TAG, "Logged in as " + connection.getUser());

        //Adding stanza listener
        addStanzaListener();

        getRoster();
        Log.d(TAG, "Called getRoster, calling receiver Message");
    }



    private void maybeStartReconnect(String status) {
        endConnection();

        // a simple linear back off strategy with 5 min max
        // + 100ms to avoid post delayed issue
        int timeout = currentRetryCount < 20 ? 5000 * currentRetryCount + 100 : 1000 * 60 * 5;
        Log.d(TAG, status + "\n" + "Attempt #" + currentRetryCount + " in " + timeout / 1000 + "s");
        updateStatus(WAITING_TO_CONNECT, status);
        Log.d(TAG, "maybeStartReconnect scheduling retry in " + timeout + "ms. Retry #" + currentRetryCount);
        reconnectHandler = new Handler(XMPPService.getServiceLooper());
        if (!reconnectHandler.postDelayed(reconnectRunnable, timeout)) {
            Log.d(TAG, "maybeStartReconnect fails to post delayed job, reconnecting in 5s.");
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}

            Intent serviceIntent = new Intent(this.context, XMPPService.class);
            this.context.startService(serviceIntent);

        }
        currentRetryCount++;
    }


    /**
     * This method *requests* a state change - what state things actually
     * wind up in is impossible to know (eg, a request to connect may wind up
     * with a state of CONNECTED, DISCONNECTED or WAITING_TO_CONNECT...
     */
    public void xmppRequestStateChange(int newState) {
        int currentState = getConnectionStatus();
        Log.d(TAG, "xmppRequestStateChange " + statusAsString(currentState) + " => " + statusAsString(newState));
        switch (newState) {
            case XMPPManager.CONNECTED:
                if (!isXmppConnected()) {
                    endConnection();
                    start(XMPPManager.CONNECTED);
                }
                break;
            case XMPPManager.DISCONNECTED:
                stop();
                break;
            case XMPPManager.WAITING_TO_CONNECT:
                endConnection();
                start(XMPPManager.WAITING_TO_CONNECT);
                break;
            case XMPPManager.WAITING_FOR_NETWORK:
                endConnection();
                start(XMPPManager.WAITING_FOR_NETWORK);
                break;
            default:
                Log.d(TAG, "xmppRequestStateChange() invalid state to switch to: " + statusAsString(newState));
        }
        // Now we have requested a new state, our state receiver will see when
        // the state actually changes and update everything accordingly.
    }




    /**
     * calls cleanupConnection and
     * sets _status to DISCONNECTED
     */
    private void stop() {
        updateStatus(DISCONNECTING, "");
        endConnection();
        updateStatus(DISCONNECTED, "");
        connection = null;
    }



    public  AbstractXMPPConnection getConnection(){

        if(this.connection != null) {
            if(this.connection.isConnected()) {
                return this.connection;
            }else{
                endConnection();
            }
        }

        configBuilder = XMPPTCPConnectionConfiguration.builder();
        configBuilder.setUsernameAndPassword(USERNAME, PASSWORD);
        configBuilder.setHost(HOST);
        configBuilder.setPort(PORT);
        configBuilder.setServiceName(SERVICE);
        configBuilder.setResource(RESOURCE);
        configBuilder.setCompressionEnabled(false);
        configBuilder.setConnectTimeout(6000);
        configBuilder.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
        configBuilder.setDebuggerEnabled(true);


        configBuilder.setSendPresence(true);

        return  new XMPPTCPConnection(configBuilder.build());
    }


    /**
     * Called by Settings dialog when a connection is establised with the XMPP
     * server
     *
     */
    public void addStanzaListener () {
        Log.d(TAG, "Calling addstanza listener message ...");

        // Create a packet filter to listen for new messages from a particular
        // user. We use an AndFilter to combine two other filters._
        StanzaFilter filter = new StanzaTypeFilter(Message.class);
        // Assume we've created an XMPPConnection name "connection".

        // First, register a packet collector using the filter we created.
        PacketCollector myCollector = connection.createPacketCollector(filter);
        // Normally, you'd do something with the collector, like wait for new packets.

        // Next, create a packet listener. We use an anonymous inner class for brevity.
        StanzaListener myListener = new StanzaListener() {
            @Override
            public void processPacket(Stanza packet) throws SmackException.NotConnectedException {

                Message message = (Message)packet;
                if(message.getType() == Message.Type.chat) {
                    //single chat message
                    Log.d(TAG, "FOUND CHAT MESSAGE");
                } else if(message.getType() == Message.Type.groupchat) {
                    //group chat message
                    Log.d(TAG, "FOUND GROUP CHAT MESSAGE");
                } else if(message.getType() == Message.Type.error) {
                    //error message
                    Log.d(TAG, "FOUND ERROR MESSAGE");
                }

            }
        };
        // Register the listener._
        connection.addSyncStanzaListener(myListener, filter);
    }


    public synchronized void endConnection() {

        if (connection != null) {
            // Removing the PacketListener should not be necessary
            // as it's also done by XMPPConnection.disconnect()
            // but it couldn't harm anyway
            connection.removeConnectionListener(xmppConnectionLister);


            if (connection.isConnected()) {
                try {
                    connection.disconnect();
                } catch (Exception e) {
                    Log.d(TAG, "Error tyring to disconnect :" +e.getMessage());
                }
                connection = null;

            }
        }
        xmppConnectionLister = null;

    }

    /**
     * updates the connection status
     * and calls broadCastStatus()
     *
     * @param _status
     * @param action
     */
    private void updateStatus(int _status, String action) {
        if (_status != status) {
            // ensure _status is set before broadcast, just in-case a receiver happens to wind up querying the state on delivery.
            int old = status;
            status = _status;
            xmppStatus.setState(status);
            statusAction = action;
            Log.d(TAG, "broadcasting state transition from " + statusAsString(old) + " to " + statusAsString(status) +
                    " via Intent " + XMPPService.ACTION_XMPP_CONNECTION_CHANGED);

            broadcastStatus(this.context, old, status, action);
        }
    }

    private void updateAction(String action) {
        if (action != statusAction) {
            // ensure action is set before broadcast, just in-case a receiver happens to wind up querying the state on delivery.
            statusAction = action;
            Log.d(TAG, "broadcasting new action " + action + " for status " + statusAsString(status) +
                    " via Intent " + XMPPService.ACTION_XMPP_CONNECTION_CHANGED);
            broadcastStatus(this.context, status, status, action);
        }
    }


    /**
    * Updates the status about the service state (and the statusbar)
    * by sending an ACTION_XMPP_CONNECTION_CHANGED intent with the new
    * and old state.
    * needs to be static, because its called by MainService
    *
    * @param ctx
    * @param oldState
    * @param newState
    */
    public static void broadcastStatus(Context ctx, int oldState, int newState, String currentAction) {
        Intent intent = new Intent(XMPPService.ACTION_XMPP_CONNECTION_CHANGED);
        intent.putExtra("old_state", oldState);
        intent.putExtra("new_state", newState);
        intent.putExtra("current_action", currentAction);
        if (newState == CONNECTED && xmppManager != null && xmppManager.connection != null) {
            intent.putExtra("TLS", xmppManager.connection.isSecureConnection());
            intent.putExtra("Compression", xmppManager.connection.isUsingCompression());
        }
        ctx.sendBroadcast(intent);

    }


    /** returns the current connection state */
    public String getConnectionStatusAction() {
        return statusAction;
    }

    /** returns the current connection state */
    public int getConnectionStatus() {
        return status;
    }



    public void connectAndAuth(final AbstractXMPPConnection connection) {
        updateAction("Connecting to server ... " );

        Thread t = new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    SASLMechanism sm = new SASLPlainMechanism();
                    SASLAuthentication.registerSASLMechanism(sm.instanceForAuthentication(connection));
                    SASLAuthentication.blacklistSASLMechanism("DIGEST-MD5");
                    xmppConnectionLister = new XMPPConnectionLister();
                    connection.addConnectionListener(xmppConnectionLister);
                    connection.connect();
                    updateAction("Service discovery");

                    PingManager pingManager= PingManager.getInstanceFor(connection);
                    pingManager.registerPingFailedListener(new PingFailedListener() {
                        @Override
                        public void pingFailed() {
                            // Note: remember that maybeStartReconnect is called from a different thread (the PingTask) here, it may causes synchronization problems
                            long now = new Date().getTime();
                            if (now - lastPing > pingIntervalInSec * 500) {
                                Log.d(TAG, "PingManager reported failed ping, calling maybeStartReconnect()");
                                restartConnection();
                                lastPing = now;
                            } else {
                                Log.d(TAG, "Ping failure reported too early. Skipping this occurrence.");
                            }
                        }
                    });


                    Log.d(TAG, "Connected to " + connection.getHost());

                    updateAction("Login with " + USERNAME);

                    connection.login(USERNAME, PASSWORD, Constants.XMPP_RESOURCE);
                    onConnectionEstablished(connection);

                } catch (XMPPException ex) {
                    Log.e(TAG, "Failed to connect to "+ connection.getHost());
                    ex.printStackTrace();
                    Log.e(TAG, ex.toString());
                    //maybeStartReconnect("");

                } catch (SmackException sex) {
                    Log.e(TAG, "Failed to connect to " + sex.getMessage());
                    //maybeStartReconnect("");

                } catch (IOException ioe) {
                    Log.e(TAG, "Failed to connect to " + ioe.getMessage());
                    //maybeStartReconnect("");
                }

            }
        });
        t.start();

    }

    private void restartConnection() {
        endConnection();
        connection = null;
        start(XMPPManager.CONNECTED);
    }


    public boolean isConnected(){
        if(this.connection == null){
            return false;
        }
        return this.connection.isConnected();

    }

    public void getRoster(){
        if(connection == null){
            Log.d(TAG, "Error building connection ... not connected");
            return;
        }

        Log.d(TAG, "Running get roster now");
        Roster roster = Roster.getInstanceFor(connection);
        roster.addRosterListener(new XMPPRosterListener());

        Collection<RosterEntry> entries = roster.getEntries();

        Log.d(TAG, "Found roster entries: "+entries.size());

        for (RosterEntry entry : entries) {
            Log.d(TAG,"--------------------------------------");
            Log.d(TAG,String.format("RosterEntry: %s, User: %s,  Name: %s, Status: %s, Type:%s",
                    entry, entry.getUser(), entry.getName(), entry.getStatus(), entry.getType() ));

            Presence entryPresence = roster.getPresence(entry
                    .getUser());
            Log.d(TAG,String.format("RosterPresence: %s, Status: %s, Type: %s, Mode:%s",
                    entryPresence, entryPresence.getStatus(), entryPresence.getType(), entryPresence.getMode() ));

            Presence.Type type = entryPresence.getType();

            if (type == Presence.Type.available) {
                Log.d(TAG, "Presence AVIALABLE");
            }

        }
    }

    public void sendMessage(String recepient, String text){

        Log.d(TAG, "Sending text " + text + " to " + recepient);
        Message msg = new Message(recepient, Message.Type.chat);
        msg.setBody(text);
        ChatManager chatManager = ChatManager.getInstanceFor(connection);

        Message message = new Message(recepient, Message.Type.chat);

        message.setFrom(connection.getUser());

        message.setBody(text);

        message.addExtension(new DefaultExtensionElement("type_chat", "jabber:client"));

        message.addExtension(new DefaultExtensionElement("from", "jabber:client"));

        message.addExtension(new DefaultExtensionElement("to", "jabber:client"));
        try {
            chatManager.createChat(recepient).sendMessage(message);
        }catch (SmackException sex){

            Log.d(TAG, "Error sending chat :"+sex.getMessage());
        }

    }

    public void registerConnectionChangeListener(XMPPConnectionChangeListener listener) {
        connectionChangeListeners.add(listener);
    }


    /**
     * Sends a XMPP Message, but only if we are connected
     * This method is thread safe.
     *
     * @param message
     * @param to - the receiving JID - if null the default notification address will be used
     * @return true, if we were connected and the message was handled over to the connection - otherwise false
     */
    public boolean send(XMPPMessage message, String to) {
        if (to == null) {
            Log.d(TAG, "Sending message \"" + message.toShortString() + "\"");
        } else {
            Log.d(TAG, "Sending message \"" + message.toShortString() + "\" to " + to);
        }
        Message msg;
        MultiUserChat muc = null;

        // to is null, so send to the default, which is the notifiedAddress
        if (to == null) {
            msg = new Message();
        } else {
            msg = new Message(to);
            // check if "to" is an known MUC JID
           // muc = mXmppMuc.getRoomViaRoomName(to);
        }

        //if (mSettings.formatResponses) {
            msg.setBody(message.generateFmtTxt());
        //} else {
          //  msg.setBody(message.generateTxt());
        //}

        // add an XTHML Body either when
        // - we don't know the recipient
        // - we know that the recipient is able to read XHTML-IM
        // - we are disconnected and therefore send the message later
        /**try {
            if ((to == null) || (connection != null && (XHTMLManager.isServiceEnabled(connection, to)
                    || !connection.isConnected()))) {
                XHTMLText xhtmlBody = message.generateXHTMLText();
                XHTMLManager.addBody(msg, xhtmlBody);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "XHTMLManager error. Ex:" + e.getMessage());
        }**/

        // determine the type of the message, groupchat or chat
        msg.setType(muc == null ? Message.Type.chat : Message.Type.groupchat);

        if (isConnected()) {
            // Message has no destination information send to all known resources
            if (muc == null && to == null) {
                //return XmppMultipleRecipientManager.send(mConnection, msg);
                return false;

                // Message has a known destination information
                // And we have set the to-address before
            } else if (muc == null) {
                try {
                    //connection.sendPacket(msg);
                    Log.d(TAG, "Attempting to send stanza over our connections ");
                    connection.sendStanza(msg);

                } catch (SmackException.NotConnectedException e) {
                    Log.d(TAG, "Send message error. Ex:" + e.getMessage());
                    return false;
                }
                // Message is for a known MUC
            } else {
                try {
                    muc.sendMessage(msg);
                } catch (Exception e) {
                    Log.d(TAG, "Send message MUC error. Ex:" + e.getMessage());
                    return false;
                }
            }
            return true;
        } else {
           //boolean result = mClientOfflineMessages.addOfflineMessage(msg);
           Log.d(TAG, "Skipping send message: \"" + message.toShortString()
                 + "\" sever is  offline, because we are not connected. Status=" + statusString());
            //return result;
            return  false;
        }
    }


    public void sendFile(String fileName, String destination)
            throws XMPPException {

        // Create the file transfer manager
        // FileTransferManager manager = new FileTransferManager(connection);
        FileTransferManager manager = FileTransferManager.getInstanceFor(connection);
        // Create the outgoing file transfer
        OutgoingFileTransfer transfer = manager.createOutgoingFileTransfer(destination);

        try{
            // Send the file
            transfer.sendFile(new File(fileName), "You won't believe this!");

            Thread.sleep(10000);
        } catch (SmackException sex){
            Log.d(TAG, "Exception :" + sex.getMessage());
        }catch (Exception ex){
            Log.d(TAG, "Exception :" + ex.getMessage());

        }
        Log.d(TAG, "Status :: " + transfer.getStatus() + " Error :: "
                + transfer.getError() + " Exception :: "
                + transfer.getException());

        Log.d(TAG, "Is it done? " + transfer.isDone());
    }

    public static String statusAsString(int state) {
        String res = "??";
        switch(state) {
            case DISCONNECTED:
                res = "Disconnected";
                break;
            case CONNECTING:
                res = "Connecting";
                break;
            case CONNECTED:
                res = "Connected";
                break;
            case DISCONNECTING:
                res = "Disconnecting";
                break;
            case WAITING_TO_CONNECT:
                res = "Waiting to connect";
                break;
            case WAITING_FOR_NETWORK:
                res = "Waiting for network";
                break;
        }
        return res;
    }


    boolean isXmppConnected() {
        return connection != null && connection.isConnected();
    }


    public String statusString() {
        return statusAsString(status);
    }
}