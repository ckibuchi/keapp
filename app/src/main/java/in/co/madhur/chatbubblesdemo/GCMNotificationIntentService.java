package in.co.madhur.chatbubblesdemo;

/**
 * Created by ckibuchi on 1/8/16.
 */

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.json.JSONObject;

import in.co.madhur.chatbubblesdemo.utils.Utils;

public class GCMNotificationIntentService extends IntentService {


    public static final int NOTIFICATION_ID = 1;
    private NotificationManager mNotificationManager;
    NotificationCompat.Builder builder;

    public GCMNotificationIntentService() {
        super("GcmIntentService");
    }

    public static final String TAG = "GCMNotificationIntentService";

    @Override
    protected void onHandleIntent(Intent intent) {
        Bundle extras = intent.getExtras();
        GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(this);
        Log.i("GCM ",gcm.toString());
        String messageType = gcm.getMessageType(intent);

        if (!extras.isEmpty()) {
            if (GoogleCloudMessaging.MESSAGE_TYPE_SEND_ERROR
                    .equals(messageType)) {
                sendNotification("Send error: " + extras.toString(),null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_DELETED
                    .equals(messageType)) {
                sendNotification("Deleted messages on server: "
                        + extras.toString(),null);
            } else if (GoogleCloudMessaging.MESSAGE_TYPE_MESSAGE
                    .equals(messageType)) {
                try {
                    String text="";
                    String from="";
                    String jsonString=extras.get(Config.MESSAGE_KEY).toString();
                    //JSONArray jsonArray = new JSONArray(jsonString);
                    Log.d("What is received :",jsonString);
                    JSONObject jsonObject=new JSONObject(jsonString);//jsonArray.getJSONObject(0);
                    if(jsonObject.has("FROM"))
                    {
                    from=jsonObject.getString("FROM");
                        text=jsonObject.getString("text");
                        /*{
                        ["FROM":"719182382","text":"Are you there?"]
                    }*/
                    }
                    Log.i(TAG, "Completed work @ " + SystemClock.elapsedRealtime());

                    sendNotification(text,from);
                }
                catch(Exception e)
                {

                    e.printStackTrace();
                }
                Log.i(TAG, "Received: " + extras.toString());
            }
        }
        GcmBroadcastReceiver.completeWakefulIntent(intent);
    }

    private void sendNotification(String msg,String from) {
        if(msg.length()<1)
        {return;}

        KEApp.notif_counter+=1;
        MainActivity m=new MainActivity();
        m.ReceiveMessage(Utils.decodestring(msg)==null?msg:Utils.decodestring(msg),from,getApplicationContext());


        Log.d(TAG, "Preparing to send notification...: " + msg);

        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        mNotificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, KEApp.class), 0);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(
                this).setSmallIcon(R.drawable.gcm_cloud)
                .setContentTitle("KE-APP Message")
                .setStyle(new NotificationCompat.BigTextStyle().bigText(Utils.decodestring(msg)==null?msg:Utils.decodestring(msg)))
                .setContentText(from+":"+from);

        mBuilder.setContentIntent(contentIntent);
        mBuilder.setAutoCancel(true);
        mBuilder.setSound(soundUri);
        mBuilder.setVibrate(new long[]{200, 500, 200, 500, 200});
        mBuilder.setNumber(KEApp.notif_counter);

        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
        Log.d(TAG, "Notification sent successfully.");
    }
}
