package com.rube.tt.keapp.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Binder;
import android.os.Build;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.rube.tt.keapp.ChatView;
import com.rube.tt.keapp.KEApp;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.receivers.NetworkChangeReceiver;
import com.rube.tt.keapp.xmpp.XMPPManager;
import com.rube.tt.keapp.xmpp.XMPPMessage;
import com.rube.tt.keapp.xmpp.XMPPStatus;

/**
 * Created by rube on 6/2/15.
 */
public class XMPPService extends Service{

    // The following actions are documented and registered in our manifest
    public final static String ACTION_CONNECT = "com.rube.tt.keapp.action.CONNECT";
    public final static String ACTION_DISCONNECT = "com.rube.tt.keapp.action.DISCONNECT;";
    public final static String ACTION_TOGGLE = "com.rube.tt.keapp.action.TOGGLE";
    public final static String ACTION_SEND = "com.rube.tt.keapp.action.SEND";



    // The following actions are undocumented and internal to our implementation.
    public final static int ACTION_BROADCAST_STATUS = 5;
    public final static String ACTION_NETWORK_STATUS_CHANGED = "com.rube.tt.keapp.action.NETWORK_STATUS_CHANGED";

    // A list of intent actions that the XmppManager broadcasts.
    public static final String ACTION_XMPP_MESSAGE_RECEIVED = "com.rube.tt.keapp.action.XMPP.MESSAGE_RECEIVED";
    public static final String ACTION_XMPP_PRESENCE_CHANGED = "com.rube.tt.keapp.action.XMPP.PRESENCE_CHANGED";
    public static final String ACTION_XMPP_CONNECTION_CHANGED = "com.rube.tt.keapp.action.XMPP.CONNECTION_CHANGED";
    public static final String ACTION_NEW_XMPP_ACCOUNT = "com.rube.tt.keapp.action.XMPP.NEW_ACCOUNT";

    public  XMPPService xmppService;

    public static boolean IsRunning = false;
    public static boolean showStatusIcon = true;
    public XMPPManager xmppManager;

    private static BroadcastReceiver xmppConChangedReceiver;
    private static BroadcastReceiver storageLowReceiver;
    private PendingIntent pendingIntentLaunchApplication;
    private final static int NOTIFICATION_CONNECTION = 1;

    private long handlerThreadId;
    public static String SERVICE_THREAD_NAME = "Keapp";
    public Handler delayedDisconnectHandler;
    private  static String TAG = XMPPService.class.getSimpleName();
    private static PowerManager.WakeLock wakeLock;
    private static PowerManager powerManager;
    private NotificationManager notificationManager;






    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // ensure XMPP manager is setup (but not yet connected)
            if (xmppManager == null) {
                setupXmppManager();
            }

            Intent intent = (Intent)msg.obj;
            String action = intent.getAction();
            int id = msg.arg1;

            if (action.equals(ACTION_CONNECT)
                    || action.equals(ACTION_DISCONNECT)
                    || action.equals(ACTION_TOGGLE)
                    || action.equals(ACTION_NETWORK_STATUS_CHANGED)) {
                onHandleIntentTransportConnection(intent);
            } else if (!action.equals(ACTION_XMPP_CONNECTION_CHANGED)){
                onHandleIntentMessage(intent);
            }

            // stop the service if we are disconnected (but stopping the service
            // doesn't mean the process is terminated - onStart can still happen.)
            if (getConnectionStatus() == XMPPManager.DISCONNECTED) {
                if (stopSelfResult(id)) {
                    Log.d(TAG, "service is stopping because we are disconnected and no pending intents exist");
                } else {
                    Log.d(TAG, "we are disconnected, but more pending intents to be delivered - service will not stop");
                }
            }
        }
    }

    /**
     * Does an initial one-time setup on the MainService by - Creating a
     * XmppManager Instance - Registering the commands - Registering a Listener
     * for ACTION_XMPP_CONNECTION_CHANGED which is issued by XmppManager for
     * every state change of the XMPP connection
     */
    private void setupXmppManager() {
        xmppConChangedReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Log.d(TAG, "Received xmpp connection changed intent ... ");
                intent.setClass(XMPPService.this, XMPPService.class);
                onConnectionStatusChanged(intent.getIntExtra("old_state", 0), intent.getIntExtra("new_state", 0));
                startService(intent);
            }
        };
        IntentFilter intentFilter = new IntentFilter(ACTION_XMPP_CONNECTION_CHANGED);
        registerReceiver(xmppConChangedReceiver, intentFilter);

        xmppManager = XMPPManager.getInstance(this);
    }

    /** Updates the status about the service state (and the status bar) */
    private void onConnectionStatusChanged(int oldStatus, int status) {
        Log.d(TAG, "Calling on connection changed status");
        if (showStatusIcon) {
            Log.d(TAG, "Show status enabled building notification : "+status);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setWhen(System.currentTimeMillis());

            switch (status) {
                case XMPPManager.CONNECTED:
                    builder.setContentText(getConnectionStatusAction());
                    builder.setSmallIcon(R.drawable.ic_action_flash_on);
                    break;
                case XMPPManager.CONNECTING:
                    builder.setContentText(getConnectionStatusAction());
                    builder.setSmallIcon(R.drawable.ic_action_flash_on);
                    break;
                case XMPPManager.DISCONNECTED:
                    builder.setContentText(getConnectionStatusAction());
                    builder.setSmallIcon(R.drawable.ic_action_flash_off);
                    break;
                case XMPPManager.DISCONNECTING:
                    builder.setContentText(getConnectionStatusAction());
                    builder.setSmallIcon(R.drawable.ic_action_flash_off);
                    break;
                case XMPPManager.WAITING_TO_CONNECT:
                case XMPPManager.WAITING_FOR_NETWORK:
                    builder.setContentText(getConnectionStatusAction());
                    builder.setSmallIcon(R.drawable.ic_action_flash_off);
                    break;
                default:
                    return;
            }

            builder.setContentIntent(pendingIntentLaunchApplication);
            builder.setContentTitle(getResources().getString(R.string.app_name));
            Log.d(TAG, "Calling notify from notification manager");
            notificationManager.notify(NOTIFICATION_CONNECTION, builder.build());
        }
    }

    void onHandleIntentTransportConnection(final Intent intent) {
        // Set Disconnected state by force to manage pending tasks
        // This is not actively used any more
        if (intent.getBooleanExtra("force", false) && intent.getBooleanExtra("disconnect", false)) {
            disconnectTransport();
        }

        if (Thread.currentThread().getId() != handlerThreadId) {
            throw new IllegalThreadStateException();
        }

        String action = intent.getAction();
        int initialState = getConnectionStatus();
        Log.d(TAG, "handling action '" + action + "' while in state " + XMPPManager.statusAsString(initialState));

        // Start with handling the actions the could result in a change
        // of the connection status
        if (action.equals(ACTION_CONNECT)) {
            if (intent.getBooleanExtra("disconnect", false)) {
                disconnectTransport();
            } else {
                connectTransport();
            }
        } else if (action.equals(ACTION_DISCONNECT)) {
            disconnectTransport();
        } else if (action.equals(ACTION_TOGGLE)) {
            switch (initialState) {
                case XMPPManager.CONNECTED:
                case XMPPManager.CONNECTING:
                case XMPPManager.WAITING_TO_CONNECT:
                case XMPPManager.WAITING_FOR_NETWORK:
                    disconnectTransport();
                    break;
                case XMPPManager.DISCONNECTED:
                case XMPPManager.DISCONNECTING:
                    connectTransport();
                    break;
                default:
                    throw new IllegalStateException("Unknown initialState while handling" + XMPPService.ACTION_TOGGLE);
            }
        } else if (action.equals(ACTION_NETWORK_STATUS_CHANGED)) {
            boolean networkChanged = intent.getBooleanExtra("networkChanged", false);
            boolean connectedOrConnecting = intent.getBooleanExtra("connectedOrConnecting", true);
            boolean connected = intent.getBooleanExtra("connected", true);
            Log.d(TAG, "NETWORK_CHANGED networkChanged=" + networkChanged + " connected=" + connected
                    + " connectedOrConnecting=" + connectedOrConnecting + " state="
                    + XMPPManager.statusAsString(initialState));

            if (!connectedOrConnecting && (initialState == XMPPManager.CONNECTED || initialState == XMPPManager.CONNECTING)) {
                // We are connected but the network has gone down - disconnect
                // and go into WAITING state so we auto-connect when we get a future
                // notification that a network is available.
                xmppManager.xmppRequestStateChange(XMPPManager.WAITING_FOR_NETWORK);
            } else if (connected && (initialState == XMPPManager.WAITING_TO_CONNECT || initialState == XMPPManager.WAITING_FOR_NETWORK)) {
                xmppManager.xmppRequestStateChange(XMPPManager.CONNECTED);
            } else if (networkChanged && initialState == XMPPManager.CONNECTED) {
                // The network has changed (WiFi <-> GSM switch) and we are connected, reconnect now
                disconnectTransport();
                connectTransport();
            }
        } else {
            Log.d(TAG, "Unexpected intent: " + action);
        }

        // Now that the connection state may has changed either because of a
        // Intent Action or because of connection changes that happened "externally"
        // (eg, due to a connection error, or running out of retries, or a retry
        // handler actually succeeding etc.) we may need to update the listener
        // TODO issue with asynch connection
        updateListenersToCurrentState(getConnectionStatus());
    }

    private int updateListenersToCurrentState(int currentState) {
        boolean wantListeners;
        switch (currentState) {
            case XMPPManager.CONNECTED:
            case XMPPManager.CONNECTING:
            case XMPPManager.DISCONNECTING:
            case XMPPManager.WAITING_TO_CONNECT:
            case XMPPManager.WAITING_FOR_NETWORK:
                wantListeners = true;
                break;
            case XMPPManager.DISCONNECTED:
                wantListeners = false;
                break;
            default:
                throw new IllegalStateException("updateListeners found invalid  int: " + currentState);
        }

        return currentState;
    }


    public static Looper getServiceLooper() {
        return sServiceLooper;
    }


    void onHandleIntentMessage(final Intent intent) {
        String action = intent.getAction();
        int initialState = getConnectionStatus();
        Log.d(TAG, "handling action '" + action + "' while in state " + xmppManager.statusAsString(initialState));

        if (action.equals(ACTION_SEND)) {
            Log.d(TAG, "Found ACTION_SEND send in on HandleIntentMessage");
            XMPPMessage xmppMsg =new XMPPMessage(intent.getStringExtra("message"));
            Log.d(TAG, "Calling send message to XMPP server");
            final boolean messageSentToServer = xmppManager.send(xmppMsg, intent.getStringExtra("to"));

            ChatView.runOnUI(new Runnable() {
                public void run() {
                    try {
                        //update chat view as out itme was send
                        ChatView.adapter.updateSend(intent.getIntExtra("position", 0), messageSentToServer);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });

        } else if (action.equals(ACTION_XMPP_MESSAGE_RECEIVED)) {
            Log.d(TAG, "Found ACTION_XMPP_MESSAGE_RECEIVED send in on HandleIntentMessage");
            maybeAcquireWakeLock();
            String message = intent.getStringExtra("message");
            if (message != null) {
                handleMessageFromXMPP(message, intent.getStringExtra("from"));
            }
            wakeLock.release();
        } else if(action.equals(ACTION_XMPP_PRESENCE_CHANGED)){
            Log.d(TAG, "Found ACTION_XMPP_PRESENCE_CHANGED send in on HandleIntentMessage");
            String from = intent.getStringExtra("from");
            String presence = intent.getStringExtra("presence");
            handlePresenceChangedFromXMPP(from, presence);
        } else if(action.equals(ACTION_NEW_XMPP_ACCOUNT)){
            Log.d(TAG, "Found ACTION_NEW_XMPP_ACCOUNT send in on HandleIntentMessage");
            String username = intent.getStringExtra("username");
            String password = intent.getStringExtra("password");
            String phone = intent.getStringExtra("password");
            handleCreateNewXMPPAccount(username, password, phone);

        }  else {
            Log.w(TAG, "Unexpected intent: " + action);
        }
        Log.d(TAG, "handled action '" + action + "' - state now: " + xmppManager.statusString());
    }


    private void handleMessageFromXMPP(String message, String from){


    }

    private void handlePresenceChangedFromXMPP(String from, String presence){

    }

    private void handleCreateNewXMPPAccount(String username, String password, String phone){

    }




    @Override
    public void onCreate(){
        super.onCreate();
        xmppService = this;

        NetworkChangeReceiver.setLastActiveNetworkName(this);

        powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getResources().getString(R.string.app_name)+ " WakeLock");


        // Start a new thread for the service
        HandlerThread thread = new HandlerThread(SERVICE_THREAD_NAME);
        thread.start();
        handlerThreadId = thread.getId();
        sServiceLooper = thread.getLooper();
        sServiceHandler = new ServiceHandler(sServiceLooper);
        delayedDisconnectHandler = new Handler(sServiceLooper);

        pendingIntentLaunchApplication = PendingIntent.getActivity(this, 0, new Intent(this, KEApp.class), 0);
        notificationManager = (NotificationManager)getSystemService(Context.NOTIFICATION_SERVICE);

        Log.d(TAG, "onCreate(): service thread created - IsRunning is set to true");
        IsRunning = true;

        // it seems that with gingerbread android doesn't issue null intents any
        // more when restarting a service but only calls the service's onCreate()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
            int lastStatus = XMPPStatus.getInstance(this).getLastKnowState();
            int currentStatus = (xmppManager == null) ? XMPPManager.DISCONNECTED : xmppManager.getConnectionStatus();
            if (lastStatus != currentStatus && lastStatus != XMPPManager.DISCONNECTING) {
                Log.d(TAG, "onCreate(): issuing connect intent because we are on gingerbread (or higher). " +
                        "lastStatus is " + lastStatus + " and currentStatus is " + currentStatus);
                startService(new Intent(XMPPService.ACTION_CONNECT));

            }
        }

    }

    public static boolean sendToServiceHandler(int i, Intent intent) {
        if (sServiceHandler != null) {
            Message msg = sServiceHandler.obtainMessage();
            msg.arg1 = i;
            msg.obj = intent;
            sServiceHandler.sendMessage(msg);
            return true;
        } else {
            Log.d(TAG, "sendToServiceHandler() called with " + intent.getAction() + " when service handler is null");
            return false;
        }
    }

    public static boolean sendToServiceHandler(Intent intent) {
        return sendToServiceHandler(0, intent);
    }

    public static void maybeAcquireWakeLock() {
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            // The application has been killed by Android and
            // we try to restart the connection
            // this null intent behavior is only for SDK < 9
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.GINGERBREAD) {
                startService(new Intent(XMPPService.ACTION_CONNECT));
            } else {
                Log.e(TAG, "onStartCommand() null intent with Gingerbread or higher");
            }
            return START_STICKY;
        }
        Log.d(TAG, "onStartCommand(): Intent " + intent.getAction());
        // A special case for the 'broadcast status' intent - we avoid setting
        // up the _xmppMgr etc
        if (intent.getAction().equals(ACTION_BROADCAST_STATUS)) {
            // A request to broadcast our current status even if _xmpp is null.
            int state = getConnectionStatus();
            xmppManager.broadcastStatus(this, state, state, getConnectionStatusAction());
            // A real action request
        } else {
            // redirect the intent to the service handler thread
            sendToServiceHandler(startId, intent);
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        unregisterReceiver(xmppConChangedReceiver);
        Log.d(TAG, "Killed xmppConChangedReceiver in onDestroy ... happily!" );
    }

    // some stuff for the async service implementation - borrowed heavily from
    // the standard IntentService, but that class doesn't offer fine enough
    // control for "foreground" services.
    private static volatile Looper sServiceLooper;
    private static volatile ServiceHandler sServiceHandler;

    /**
     * Class for clients to access. Because we know this service always runs in
     * the same process as its clients, we don't need to deal with IPC.
     */
    public class LocalBinder extends Binder {
        public XMPPService getService() {
            return XMPPService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return localBinder;
    }

    public int getConnectionStatus() {
        return xmppManager == null ? XMPPManager.DISCONNECTED : xmppManager.getConnectionStatus();
    }



    public String getConnectionStatusAction() {
        return xmppManager == null ? "" : xmppManager.getConnectionStatusAction();
    }

    void connectTransport() {
        xmppManager.xmppRequestStateChange(XMPPManager.CONNECTED);
    }

    void disconnectTransport() {
        xmppManager.xmppRequestStateChange(XMPPManager.DISCONNECTED);
    }

    // This is the object that receives interactions from clients. See
    // RemoteService for a more complete example.
    private final IBinder localBinder = new LocalBinder();

}
