package com.rube.tt.keapp.adapters;

import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rube.tt.keapp.Page;
import com.rube.tt.keapp.PageDetail;
import com.rube.tt.keapp.R;
import com.rube.tt.keapp.models.ContactModel;
import com.rube.tt.keapp.models.PageModel;
import com.rube.tt.keapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class PageListAdapter extends BaseAdapter implements View.OnClickListener {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = PageListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<PageModel> pageListData;
        private LayoutInflater inflater=null;


        public PageListAdapter(Activity activity, ArrayList<PageModel> pageListData){
            this.activity = activity;                //have seen earlier
            this.pageListData = pageListData;

            this.inflater  = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void refresh(ArrayList<PageModel> items)
        {
            this.pageListData.clear();
            this.pageListData.addAll(items);
            notifyDataSetChanged();
        }

        public void update(ArrayList<PageModel> items)
        {
            this.pageListData.addAll(items);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return pageListData.size();
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
            private TextView name;
            private TextView date;
            private TextView category;
            private TextView description;
            private TextView likes;
            private TextView comments;


        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate message_list_item.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.page_list_item, null);

            /****** View Holder Object to contain message_list_item.xml file elements ******/

            holder = new ViewHolder();
            holder.pic = (ImageView) vi.findViewById(R.id.page_profile_icon);
            holder.name=(TextView)vi.findViewById(R.id.page_name);

            holder.date=(TextView)vi.findViewById(R.id.page_date);
            holder.category = (TextView)vi.findViewById(R.id.page_category);
            holder.description = (TextView)vi.findViewById(R.id.page_description);

            holder.likes = (TextView)vi.findViewById(R.id.page_likes);
            holder.comments = (TextView)vi.findViewById(R.id.page_comments);


            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }

        /***** Get each Model object from Arraylist ********/
        PageModel pageModel = (PageModel) pageListData.get( position );

        /************  Set Model values in Holder elements ***********/

        holder.pic.setImageBitmap(pageModel.getPic());
        //Utils.scaleImage(activity, holder.pic);
        holder.name.setText(pageModel.getName());

        holder.date.setText(pageModel.getDate());
        holder.category.setText(pageModel.getCategory());
        holder.description.setText(pageModel.getDescription());

        holder.likes.setText("Likes(" + pageModel.getLikes() +")");
        holder.comments.setText("Comments("+pageModel.getComments()+")");


        /******** Set Item Click Listner for LayoutInflater for each row *******/
        holder.pic.setOnClickListener(new PageProfileImageClickListener(position, pageModel.getId()));
        holder.name.setOnClickListener(new PageTitleClickListener(position, pageModel.getId()));

        //vi.setOnClickListener(new OnItemClickListener(position, pageModel.getId() ));

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class PageProfileImageClickListener  implements View.OnClickListener{
        private int mPosition;
        private String pageId;

        PageProfileImageClickListener(int position, String pageId){
            this.mPosition = position;
            this.pageId = pageId;

        }

        @Override
        public void onClick(View clickedView) {

            int viewId = clickedView.getId();
            Log.d(TAG, "Calling on click view for page ::" + viewId);

            startPageProfileActivity(mPosition, pageId);

        }
    }

    /********* Called when Item click in ListView ************/
    private class PageTitleClickListener  implements View.OnClickListener{
        private int mPosition;
        private String pageId;

        PageTitleClickListener(int position, String pageId){
            this.mPosition = position;
            this.pageId = pageId;

        }

        @Override
        public void onClick(View clickedView) {

            int viewId = clickedView.getId();
            Log.d(TAG, "Calling on click view for page ::"+viewId);
            startPageDetailActivity(mPosition, pageId);

        }
    }

    public void startPageDetailActivity(int mPosition, String pageId){
        Log.d(TAG, "Page list view clicked ~ load page detail");
        Intent intent= new Intent(activity, PageDetail.class);
        intent.putExtra("selectedPage", mPosition);
        intent.putExtra("pageId", pageId);
        activity.startActivity(intent);
        activity.finish();
    }

    public void startPageProfileActivity(int mPosition, String pageId){
        Log.d(TAG, "Page list view clicked ~ load page detail");
        Intent intent= new Intent(activity, Page.class);
        intent.putExtra("selectedPage", mPosition);
        intent.putExtra("pageId", pageId);
        activity.startActivity(intent);
        activity.finish();
    }

    private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }


