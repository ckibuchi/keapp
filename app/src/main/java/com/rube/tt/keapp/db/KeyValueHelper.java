package com.rube.tt.keapp.db;

import android.content.Context;

/**
 * Created by rube on 6/3/15.

 * Middle-end Helper, if we ever want to integrate the Alias function also in
 * the App itself: This is the way to go.
 * At one place in the code, the constructor needs to be called, to make sure
 * that the Database is setup correctly.
 *
 * @author Florian Schmaus fschmaus@gmail.com - on behalf of the GTalkSMS Team
 *
 */
public class KeyValueHelper {


    public static final String KEY_XMPP_STATUS = "xmppStatus";

    private static KeyValueHelper keyValueHelper = null;
    private  Context context;

    /**
     * This constructor ensures that the database is setup correctly
     * @param ctx
     */
    private KeyValueHelper(Context ctx) {
        this.context = ctx;
        new KeyValueDatabase(ctx);
    }

    public static KeyValueHelper getKeyValueHelper(Context ctx) {
        if (keyValueHelper == null) {
            keyValueHelper = new KeyValueHelper(ctx);
        }
        return keyValueHelper;
    }

    public boolean addKey(String key, String value) {
        if (key.contains("'") || value.contains("'"))
            return false;

        addOrUpdate(key, value);
        return true;
    }

    public boolean deleteKey(String key) {
        if(!key.contains("'") && KeyValueDatabase.containsKey(key, this.context)) {
            return KeyValueDatabase.deleteKey(key, this.context);
        } else {
            return false;
        }
    }

    public boolean containsKey(String key) {
        if(!key.contains("'")) {
            return KeyValueDatabase.containsKey(key, this.context);
        } else {
            return false;
        }

    }

    public String getValue(String key) {
        if (!key.contains("'")) {
            return KeyValueDatabase.getValue(key, this.context);
        } else {
            return null;
        }
    }

    public Integer getIntegerValue(String key) {
        String value = getValue(key);
        Integer res;
        try {
            res = Integer.parseInt(value);
        } catch (Exception e) {
            res = null;
        }
        return res;
    }

    public String[][] getAllKeyValue() {
        String[][] res = KeyValueDatabase.getFullDatabase(this.context);
        if (res.length == 0)
            res = null;
        return res;
    }

    private void addOrUpdate(String key, String value) {
        if(KeyValueDatabase.containsKey(key, this.context)) {
            KeyValueDatabase.updateKey(key, value, this.context);
        } else {
            KeyValueDatabase.addKey(key, value, this.context);
        }
    }

}
