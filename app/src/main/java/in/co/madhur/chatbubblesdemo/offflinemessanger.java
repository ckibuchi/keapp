package in.co.madhur.chatbubblesdemo;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

public class offflinemessanger extends Service {
    public offflinemessanger() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        Log.d("SERVICE ", "Started....");
        throw new UnsupportedOperationException("Not yet implemented");

    }
}
