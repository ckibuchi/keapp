package com.rube.tt.keapp.services;

/**
 * Created by rube on 18/08/15.
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

import com.rube.tt.keapp.data.SyncAdapter;


public class SyncAdapterService extends Service {

    private String TAG =SyncAdapterService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        SyncAdapter syncAdapter = new SyncAdapter(getApplicationContext(),true);
        return syncAdapter.getSyncAdapterBinder();
    }

}
