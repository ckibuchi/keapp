package in.co.madhur.chatbubblesdemo.listeners;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import in.co.madhur.chatbubblesdemo.BroadCast;
import in.co.madhur.chatbubblesdemo.Group;
import in.co.madhur.chatbubblesdemo.Login;
import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.UserStatus;


/**
 * Created by rube on 5/3/15.
 */
public class DrawerClickListener implements  View.OnClickListener {
    private final String TAG  = DrawerClickListener.class.getSimpleName();
    int position;
    Activity activity;
    RecyclerView recyclerView;
    private SharedPreferences sharedPreferences;

    public DrawerClickListener(Activity activity){

        this.activity = activity;
        recyclerView = (RecyclerView)activity.findViewById(R.id.RecyclerView);

    }

    @Override
    public void onClick(View v) {
        this.position =  recyclerView.getChildLayoutPosition(v);
        Log.d(TAG, "Clicked and Position is " + position);
        navigateTo(position);

    }

    public void navigateTo(int position) {
        Intent i;
        switch(position) {
            case 0:
                //Refuse navigation this is the header
                break;
            case 1:
                i = new Intent(activity, BroadCast.class);
                i.putExtra("selectedPage", position);
                activity.startActivity(i);
                break;

            case 2:
                i = new Intent(activity, Group.class);
                i.putExtra("selectedPage", position);
                activity.startActivity(i);
                break;
            case 3:
                i = new Intent(activity, UserStatus.class);
                i.putExtra("selectedPage", position);
                activity.startActivity(i);
                break;
            case 4:
                sharedPreferences = activity.getSharedPreferences(Login.preference, Context.MODE_PRIVATE);
                Log.d(TAG, "Preferences contains values :"+sharedPreferences.getAll());
                sharedPreferences.edit().clear().commit();
                Log.d(TAG, "Cleared shared preferences .." +sharedPreferences.getAll());
                i = new Intent(activity, Login.class);
                i.putExtra("selectedPage", position);
                activity.startActivity(i);
                break;
            case 5:
                break;
            case 6:
                break;
            case 7:
                removeprefs();
                break;
        }
    }
    public  void removeprefs()
    {
        new AlertDialog.Builder(activity)
                .setTitle("Confirm")
                .setMessage("Do you really want to Logout?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int whichButton) {
                        SharedPreferences prefs = activity.getSharedPreferences("MyAccountPreference", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = prefs.edit();
                        editor.clear();
                        editor.commit();
                        final Intent Login = new Intent(activity, Login.class);
                        activity.startActivity(Login);
                        activity.finish();
                    }})
                .setNegativeButton("NO", null).show();


    }
}
