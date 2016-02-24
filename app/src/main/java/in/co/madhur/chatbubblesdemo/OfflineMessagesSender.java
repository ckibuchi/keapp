package in.co.madhur.chatbubblesdemo;

/**
 * Created by ckibuchi on 1/23/16.
 */

import android.app.Service;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.db.DbManager;
import in.co.madhur.chatbubblesdemo.models.ChatMessage;

public class OfflineMessagesSender extends Service {
    private ArrayList<ChatMessage> listMessages;
    MainActivity ma=new MainActivity();
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        while(true)
        {
            try{
                DbManager dbManager =  new DbManager(getApplicationContext());
                listMessages=dbManager.getOfflineChats();
                for(ChatMessage msg : listMessages)
                {
                    SendInBackground("ECHO", msg.getMessageID(), msg);

                }
            Thread.sleep(200);
            }
            catch(Exception e)
            {
            e.printStackTrace();
            }
        }


        //return START_STICKY;
    }

    private void SendInBackground(final String action, final String id, final ChatMessage message) {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String msg = "";
                try {
                    ma.sendMessageToXmpp(action,id,message);
                } catch (Exception ex) {
                    msg = "Error :" + ex.getMessage();
                    Log.d("OfflineMessageSender", "Error: " + msg);
                }
                Log.d("OfflineMessageSender", "AsyncTask completed: " + msg);
                return msg;
            }

            @Override
            protected void onPostExecute(String msg) {
                /*Toast.makeText(getApplicationContext(),
                        "Registered with GCM Server." + msg, Toast.LENGTH_LONG)
                        .show();*/
            }
        }.execute(null, null, null);
    }



   /* @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }*/
}
