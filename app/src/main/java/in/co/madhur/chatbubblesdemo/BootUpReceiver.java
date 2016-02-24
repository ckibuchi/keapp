package in.co.madhur.chatbubblesdemo;

/**
 * Created by ckibuchi on 1/23/16.
 */
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;


public class BootUpReceiver extends BroadcastReceiver{
    public static int getRunning() {
        return running;
    }

    public static void setRunning(int running) {
        BootUpReceiver.running = running;
    }

    static int running=0;
    @Override
    public void onReceive(Context context, Intent intent) {


        /***** For start Service  ****/
     /*   Intent myIntent = new Intent(context, OfflineMessagesSender.class);
        context.startService(myIntent);*/
        new Intent(context, OfflineMessagesSender.class);
        running=1;
    }

}
