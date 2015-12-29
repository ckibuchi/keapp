package com.rube.tt.keapp.xmpp;

import android.util.Log;

import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterListener;

import java.util.Collection;

/**
 * Created by rube on 5/28/15.
 */
public class XMPPRosterListener implements RosterListener{

    private final String TAG = XMPPRosterListener.class.getSimpleName();

    @Override
    public void entriesDeleted(Collection<String> addresses) {
        Log.d(TAG, "Detected Entries deleted");
    }
    @Override
    public void entriesUpdated(Collection<String> addresses) {
        Log.d(TAG, "Detected Entries updated");

    }

    @Override
    public void presenceChanged(Presence presence) {
        System.out.println("Presence changed: " + presence.getFrom() + " " + presence);
    }
    @Override
    public void entriesAdded(Collection<String> arg0) {
        // TODO Auto-generated method stub
        Log.d(TAG, "Detected Entries added");

    }

}
