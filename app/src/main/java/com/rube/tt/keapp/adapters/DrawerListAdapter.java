package com.rube.tt.keapp.adapters;


import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.rube.tt.keapp.KEApp;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.listeners.DrawerClickListener;

/**
 * Created by rube on 3/31/15.
 */
public class DrawerListAdapter extends RecyclerView.Adapter<DrawerListAdapter.ViewHolder> {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = DrawerListAdapter.class.getSimpleName();

        private String mNavTitles[]; // String Array to store the passed titles Value from MainActivity.java
        private int mIcons[];       // Int Array to store the passed icons resource value from MainActivity.java

        private String name;        //String Resource for header View Name
        private int profile;        //int Resource for header view profile picture
        private String status;       //String Resource for header view email

        private Activity activity;

        public DrawerListAdapter(Activity activity, String titles[],int icons[],String name,String status, int profile){
            // MyAdapter Constructor with titles and icons parameter
            // titles, icons, name, email, profile pic are passed from the main activity as we
            this.mNavTitles = titles;                //have seen earlier
            this.mIcons = icons;
            this.name = name;
            this.status = status;
            this.profile = profile;                     //here we assign those passed values to the values we declared here
            //in adapter
            this.activity = activity;
        }

        // Creating a ViewHolder which extends the RecyclerView View Holder
        // ViewHolder are used to to store the inflated views in order to recycle them

        public static class ViewHolder extends RecyclerView.ViewHolder {
            int holderId;

            TextView textView;
            ImageView imageView;
            ImageView profile;
            TextView name;
            TextView status;


            public ViewHolder(View itemView,int ViewType) {                 // Creating ViewHolder Constructor with View and viewType As a parameter
                super(itemView);
                // Here we set the appropriate view in accordance with the the view type as passed when the holder object is created

                if(ViewType == TYPE_ITEM) {
                    textView = (TextView) itemView.findViewById(R.id.drawer_row_text); // Creating TextView object with the id of textView from item_row.xml
                    imageView = (ImageView) itemView.findViewById(R.id.drawer_row_icon);// Creating ImageView object with the id of ImageView from item_row.xml
                    holderId = 1;                                               // setting holder id as 1 as the object being populated are of type item row
                }
                else{
                    try{


                    }
                    catch(Exception e)
                    {
                        e.printStackTrace();
                    }
                    name = (TextView) itemView.findViewById(R.id.theusername);         // Creating Text View object from header.xml for name
                    name.setText(KEApp.accountName);
                    status = (TextView) itemView.findViewById(R.id.theuserstatus);       // Creating Text View object from header.xml for email
                    status.setText(KEApp.profileText);
                    profile = (ImageView) itemView.findViewById(R.id.circleView);// Creating Image view object from header.xml for profile pic
                    holderId = 0;                                                // Setting holder id = 0 as the object being populated are of type header view
                }

            }

        }

        //Below first we ovverride the method onCreateViewHolder which is called when the ViewHolder is
        //Created, In this method we inflate the item_row.xml layout if the viewType is Type_ITEM or else we inflate header.xml
        // if the viewType is TYPE_HEADER
        // and pass it to the view holder

        @Override
        public DrawerListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType ) {

            if (viewType == TYPE_ITEM) {
                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_item_row,parent,false); //Inflating the layout
                v.setOnClickListener(new DrawerClickListener(activity));
                ViewHolder vhItem = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

                return vhItem; // Returning the created object

                //inflate your layout and pass it to view holder

            } else if (viewType == TYPE_HEADER) {

                View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.drawer_header,parent,false); //Inflating the layout

                ViewHolder vhHeader = new ViewHolder(v,viewType); //Creating ViewHolder and passing the object of type view

                return vhHeader; //returning the object created


            }
            return null;

        }

        //Next we override a method which is called when the item in a row is needed to be displayed, here the int position
        // Tells us item at which position is being constructed to be displayed and the holder id of the holder object tell us
        // which view type is being created 1 for item row
        @Override
        public void onBindViewHolder(DrawerListAdapter.ViewHolder holder, int position) {
            if(holder.holderId ==1) {                              // as the list view is going to be called after the header view so we decrement the
                // position by 1 and pass it to the holder while setting the text and image
                holder.textView.setText(mNavTitles[position - 1]); // Setting the Text with the array of our Titles
                holder.imageView.setImageResource(mIcons[position -1]);// Settimg the image with array of our icons
            }
            else{
                //Only change the header if data is supplied
                if(name != null && status != null && profile > 0) {
                    holder.profile.setImageResource(profile);           // Similarly we set the resources for header view
                    holder.name.setText(name);
                    holder.status.setText(status);
                }
            }
        }

        // This method returns the number of items present in the list
        @Override
        public int getItemCount() {
            if(mNavTitles == null){
                return 0;
            }
            return mNavTitles.length+1; // the number of items in the list will be +1 the titles including the header view.
        }


        // With the following method we check what type of view is being passed
        @Override
        public int getItemViewType(int position) {
            if (isPositionHeader(position))
                return TYPE_HEADER;

            return TYPE_ITEM;
        }

        private boolean isPositionHeader(int position) {
            return position == 0;
        }


    }

