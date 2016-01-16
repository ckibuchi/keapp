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
import android.widget.TextView;

import com.rube.tt.keapp.MainActivity;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.models.MessageModel;
import com.rube.tt.keapp.utils.Constants;
import com.rube.tt.keapp.utils.RubeImageLoader;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class MessageListAdapter extends BaseAdapter implements View.OnClickListener {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = MessageListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<MessageModel> messageListData;
        private static LayoutInflater inflater=null;


        public MessageListAdapter(Activity activity, ArrayList<MessageModel> messageListData ){
            this.activity = activity;                //have seen earlier
            this.messageListData = messageListData;

            this.inflater = inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {
            return messageListData.size();
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
            TextView messageView;
            TextView dateView;
            TextView profileName;

        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate message_list_item.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.message_list_item, null);

            /****** View Holder Object to contain message_list_item.xml file elements ******/

            holder = new ViewHolder();
            holder.profileImage = (ImageView) vi.findViewById(R.id.message_profile_icon);
            holder.messageView=(TextView)vi.findViewById(R.id.message_text);
            holder.dateView=(TextView)vi.findViewById(R.id.message_date);
            holder.profileName=(TextView)vi.findViewById(R.id.message_profile_name);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }


        /***** Get each Model object from Arraylist ********/
        MessageModel messageModel = (MessageModel) messageListData.get( position );

        /************  Set Model values in Holder elements ***********/
        RubeImageLoader rubeImageLoader = new RubeImageLoader(activity);

        rubeImageLoader.loadImageFormDevice(Constants.CONTENT_TYPE_PROFILE,
                messageModel.getProfilePhotoId(), holder.profileImage,
                activity.getResources().getDrawable(R.drawable.default_profile_pic));
        //holder.profileImage.setImageBitmap(messageModel.getPic());

        holder.messageView.setText(messageModel.getMessage());
        holder.dateView.setText(messageModel.getDate());
        holder.profileName.setText(messageModel.getName());


        /******** Set Item Click Listner for LayoutInflater for each row *******/

        vi.setOnClickListener(new OnItemClickListener( position ));

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            Intent i = new Intent(activity, MainActivity.class);
            i.putExtra("selectedPage", mPosition);
            activity.startActivity(i);
        }
    }


    private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }


