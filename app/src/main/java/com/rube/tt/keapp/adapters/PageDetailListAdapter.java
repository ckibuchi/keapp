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

import com.rube.tt.keapp.PageDetail;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.models.PageDetailModel;
import com.rube.tt.keapp.models.PageModel;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class PageDetailListAdapter extends BaseAdapter implements View.OnClickListener {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = PageDetailListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<PageDetailModel> pageDetailListData;
        private LayoutInflater inflater=null;

        public PageDetailListAdapter(Activity activity, ArrayList<PageDetailModel> pageDetailListData){
            this.activity = activity;                //have seen earlier
            this.pageDetailListData = pageDetailListData;

            this.inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void refresh(ArrayList<PageDetailModel> items)
        {
            this.pageDetailListData.clear();
            this.pageDetailListData.addAll(items);
            notifyDataSetChanged();
        }

        public void update(ArrayList<PageDetailModel> items)
        {
            this.pageDetailListData.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return pageDetailListData.size();
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

            private ImageView profilePic;
            private TextView profileName;
            private TextView title;
            private TextView date;
            private TextView text;
            private TextView likes;
            private TextView comments;

        }

    public View getView(int position, View convertView, ViewGroup parent) {
        Log.d(TAG, "Calling get view pf pageDetailListAdapter");
        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.page_detail_list_item, null);

            holder = new ViewHolder();
            holder.profilePic  = (ImageView) vi.findViewById(R.id.page_detail_profile_icon);
            holder.profileName =(TextView)vi.findViewById(R.id.page_detail_profile_name);
            holder.title =(TextView)vi.findViewById(R.id.page_detail_story_name);

            holder.date=(TextView)vi.findViewById(R.id.page_detail_text_date);

            holder.text = (TextView)vi.findViewById(R.id.page_detail_text);

            holder.likes = (TextView)vi.findViewById(R.id.page_detail_likes);
            holder.comments = (TextView)vi.findViewById(R.id.page_detail_comments);
            vi.setTag( holder );
        }else {
            holder =  (ViewHolder) vi.getTag();
        }


        /***** Get each Model object from Arraylist ********/
        PageDetailModel pageDetailModel = (PageDetailModel) pageDetailListData.get( position );

        /************  Set Model values in Holder elements ***********/
        holder.profilePic.setImageBitmap(pageDetailModel.getProfilePic());
        holder.profileName.setText(pageDetailModel.getProfileName());
        holder.date.setText(pageDetailModel.getDate());
        holder.text.setText(pageDetailModel.getText());
        holder.title.setText(pageDetailModel.getTitle());

        holder.likes.setText("Likes(" + pageDetailModel.getLikes() + ")");
        holder.comments.setText("Comments(" + pageDetailModel.getComments() + ")");


        /******** Set Item Click Listner for LayoutInflater for each row *******/
        //vi.setOnClickListener(new OnItemClickListener( position ));

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

        }
    }

    }


