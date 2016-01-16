package com.rube.tt.keapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rube.tt.keapp.R;
import com.rube.tt.keapp.models.SimpleContactModel;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class SimpleContactListAdapter extends BaseAdapter {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = SimpleContactListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<SimpleContactModel> simpleContactListData;
        private static LayoutInflater inflater=null;


        public SimpleContactListAdapter(Activity activity, ArrayList<SimpleContactModel> broadstListData){
            this.activity = activity;                //have seen earlier
            this.simpleContactListData = broadstListData;

            this.inflater = inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void refresh(ArrayList<SimpleContactModel> items)
        {
            this.simpleContactListData.clear();
            this.simpleContactListData.addAll(items);
            notifyDataSetChanged();
        }

        public void update(ArrayList<SimpleContactModel> items)
        {
            this.simpleContactListData.addAll(items);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return simpleContactListData.size();
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

            private ImageView pic;
            private TextView phoneNumber;
            private TextView name;

        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.simple_contact_list_item, null);

            holder = new ViewHolder();
            holder.pic = (ImageView) vi.findViewById(R.id.broadcast_profile_icon);
            holder.phoneNumber=(TextView)vi.findViewById(R.id.broadcast_phone_number);
            holder.name=(TextView)vi.findViewById(R.id.contact_name);
            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }


        if(!simpleContactListData.isEmpty()){

            /***** Get each Model object from Arraylist ********/
            SimpleContactModel simpleContactModel = (SimpleContactModel) simpleContactListData.get( position );

            /************  Set Model values in Holder elements ***********/

            holder.pic.setImageBitmap(simpleContactModel.getPic());
            holder.phoneNumber.setText(simpleContactModel.getPhoneNumber());
            holder.name.setText(simpleContactModel.getName());

        }
        return vi;
    }

}


