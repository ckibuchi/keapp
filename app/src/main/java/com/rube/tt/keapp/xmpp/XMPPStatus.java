package com.rube.tt.keapp.xmpp;

import android.content.Context;
import android.util.Log;

import com.rube.tt.keapp.db.KeyValueHelper;

import java.io.File;

/**
 * Created by rube on 6/3/15.
 *
 * This class provides an interface to the keyValue database
 * the last known state of the XMPP connection is saved. This helps
 * MainService to detect an unintentional restart of GTalkSMS and restore
 * the last known state.
 *
 */
public class XMPPStatus {


    private static final String STATEFILE_NAME = "xmppStatus";

    private static XMPPStatus sXmppStatus;

    private final File mStatefile;
    private final KeyValueHelper mKeyValueHelper;
    private final String TAG = getClass().getSimpleName();


    private XMPPStatus(Context ctx) {
        File filesDir = ctx.getFilesDir();
        mStatefile = new File(filesDir, STATEFILE_NAME);
        mKeyValueHelper = KeyValueHelper.getKeyValueHelper(ctx.getApplicationContext());
        // Delete the old statefile
        // TODO remove this check with a future release
        if (mStatefile.isFile()) {
            mStatefile.delete();
        }
    }

    public static XMPPStatus getInstance(Context ctx) {
        if (sXmppStatus == null) {
            sXmppStatus = new XMPPStatus(ctx);
        }
        return sXmppStatus;
    }

    /**
     * Gets the last known XMPP status from the statefile
     * if there is no statefile the status for DISCONNECTED is returned
     *
     * @return integer representing the XMPP status as defined in XmppManager
     */
    public int getLastKnowState() {
        int res = XMPPManager.DISCONNECTED;
        String value = mKeyValueHelper.getValue(KeyValueHelper.KEY_XMPP_STATUS);
        if (value != null) {
            try {
                res = Integer.parseInt(value);
            } catch(NumberFormatException e) {
                Log.d(TAG, "XmppStatus unable to parse integer " + e);
            }
        }
        return res;
    }

    /**
     * Writes the current status int into the statefile
     *
     * @param status
     */
    public void setState(int status) {
        String value = Integer.toString(status);
        mKeyValueHelper.addKey(KeyValueHelper.KEY_XMPP_STATUS, value);
    }


}
