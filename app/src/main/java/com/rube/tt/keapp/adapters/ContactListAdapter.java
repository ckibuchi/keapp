package com.rube.tt.keapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rube.tt.keapp.MainActivity;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.fragments.ContactFragment;
import com.rube.tt.keapp.models.ContactModel;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class ContactListAdapter extends BaseAdapter {

    private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
    // IF the view under inflation and population is header or Item
    private static final int TYPE_ITEM = 1;

    private static final String TAG = ContactListAdapter.class.getSimpleName();


    private Activity activity;
    private ArrayList<ContactModel> contactListData;
    private static LayoutInflater inflater=null;


    public ContactListAdapter(Activity activity, ArrayList<ContactModel> contactListData){
        this.activity = activity;                //have seen earlier
        this.contactListData = contactListData;
        Log.d(TAG, "ContactListAdapter starting ...");

        this.inflater = inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    public void refresh(ArrayList<ContactModel> items)
    {
        this.contactListData.clear();
        this.contactListData.addAll(items);
        notifyDataSetChanged();
    }

    public void update(ArrayList<ContactModel> items)
    {
        this.contactListData.addAll(items);
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return contactListData.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    // Creating a ViewHolder which extends the RecyclerView View Holder
    // ViewHolder are used to to store the inflated views in order to recycle them

    public class ViewHolder {

        ImageView profileImage;
        TextView statusMessageView;
        TextView profileName;
        TextView phoneNumber;
        LinearLayout inviteButtonView;
        TextView emptyMessage;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;
        Log.d(TAG, "Calling getView  to load contacts");

        if(convertView==null){
            Log.d(TAG, "Content view is null");
            holder = new ViewHolder();

            Log.d(TAG, "Inflating contact_list_item");
            vi = inflater.inflate(R.layout.contact_list_item, null);

            holder.profileImage = (ImageView) vi.findViewById(R.id.contact_profile_icon);
            holder.statusMessageView = (TextView) vi.findViewById(R.id.status_message_text);
            holder.profileName = (TextView) vi.findViewById(R.id.contact_profile_name);
            holder.phoneNumber = (TextView) vi.findViewById(R.id.contact_phone_number);
            holder.inviteButtonView = (LinearLayout) vi.findViewById(R.id.invite_account_layout);


            vi.setTag( holder );
        } else {
            holder = (ViewHolder) vi.getTag();
        }
        /***** Get each Model object from Arraylist ********/
        ContactModel contactModel = (ContactModel) contactListData.get( position );
        /************  Set Model values in Holder elements ***********/

        holder.profileImage.setImageBitmap(contactModel.getPic());
        holder.statusMessageView.setText(contactModel.getStatusMessage());
        holder.profileName.setText(contactModel.getName());
        holder.phoneNumber.setText(contactModel.getPhoneNumber());
        /******** Set Item Click Listner for LayoutInflater for each row *******/
        //Display invite button id not member
        Log.i("IS MEMBER NOW? ",""+contactModel.isMember());
        if (contactModel.isMember()) {
            holder.inviteButtonView.setVisibility(View.GONE);
        }else if(contactModel.isInvited()){
            Log.d(TAG, "Found isInvited true");
            TextView inviteTextView = (TextView) holder.inviteButtonView.findViewById(R.id.invite_account);
            inviteTextView.setText("Invited");
            holder.inviteButtonView.setVisibility(View.VISIBLE);

        }else {
            holder.inviteButtonView.setVisibility(View.VISIBLE);

        }
        vi.setOnClickListener(new OnItemClickListener(position));

        return vi;
    }


    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            Log.d(TAG, "Someone clicked list item ...");
            ContactModel contactModel =(ContactModel)contactListData.get( mPosition );

            if(contactModel.isMember()){
                //load chat window
                Intent i = new Intent(activity, MainActivity.class);
                i.putExtra("phone", contactModel.getPhoneNumber());
                i.putExtra("name", contactModel.getName());

                activity.startActivity(i);
               // activity.finish();

            }
           else if(contactModel.isInvited()){
                Toast.makeText(activity, "Already invited.\nWaiting for member to join", Toast.LENGTH_SHORT).show();
                //load chat window
             /*   Intent i = new Intent(activity, ChatView.class);
                i.putExtra("phone", contactModel.getPhoneNumber());
                activity.startActivity(i);
                activity.finish();*/

            }
            else{
                try {
                    Log.d(TAG, "Starting sendSMS activities");
                    ContactFragment.sendSMS(activity, contactModel.getPhoneNumber());
                }
                catch(Exception e)
                {
                    Toast.makeText(arg0.getContext(),"Error: "+e.getMessage(),Toast.LENGTH_LONG).show();}
            }


            //do your stuff here
        }
    }

    private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }


