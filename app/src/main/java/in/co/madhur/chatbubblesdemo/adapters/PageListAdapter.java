package in.co.madhur.chatbubblesdemo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import in.co.madhur.chatbubblesdemo.KEApp;
import in.co.madhur.chatbubblesdemo.Page;
import in.co.madhur.chatbubblesdemo.PageDetails;
import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.data.ACCEPT;
import in.co.madhur.chatbubblesdemo.data.SyncAdapter;
import in.co.madhur.chatbubblesdemo.fragments.PageFragment;
import in.co.madhur.chatbubblesdemo.models.PageModel;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

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
            private TextView by;
            private TextView description;
            private TextView likes;
            private LinearLayout likearea;
            private TextView liked;
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
            holder.by = (TextView)vi.findViewById(R.id.by);
            holder.description = (TextView)vi.findViewById(R.id.page_description);

            holder.likes = (TextView)vi.findViewById(R.id.page_likes);
            holder.likearea = (LinearLayout)vi.findViewById(R.id.likearea);
            holder.liked = (TextView)vi.findViewById(R.id.liked);
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
        holder.by.setText(" ("+pageModel.getBy()+")");
        holder.description.setText(pageModel.getDescription());
        if(!pageModel.getLiked().equalsIgnoreCase("0"))
        {
            holder.liked.setText("unlike");
            holder.likes.setOnClickListener(new LikeClickListener(position, pageModel, false));
            holder.liked.setOnClickListener(new LikeClickListener(position, pageModel, false));
            holder.likearea.setOnClickListener(new LikeClickListener(position, pageModel, false));
        }
        else
        {
            holder.liked.setText("like");
            holder.likes.setOnClickListener(new LikeClickListener(position, pageModel, true));
            holder.liked.setOnClickListener(new LikeClickListener(position, pageModel,true));
            holder.likearea.setOnClickListener(new LikeClickListener(position, pageModel, true));

        }
        holder.likes.setText("Likes(" + pageModel.getLikes() + ")");

        holder.comments.setText("Comments(" + pageModel.getComments() + ")");


        /******** Set Item Click Listner for LayoutInflater for each row *******/
        holder.pic.setOnClickListener(new PageProfileImageClickListener(position, pageModel.getId()));
        holder.name.setOnClickListener(new PageTitleClickListener(position, pageModel));

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
    private class LikeClickListener  implements View.OnClickListener{
        private int mPosition;
        private PageModel pageModel;
        boolean welike;

        LikeClickListener(int position,PageModel pageModel,boolean welike){
            this.mPosition = position;
            this.pageModel=pageModel;
            this.welike=welike;

        }

        @Override
        public void onClick(View clickedView) {

            int viewId = clickedView.getId();
            Log.d(TAG, "Calling on click view for page ::" + viewId);
            // do the liking thingy here and update the page
            like(pageModel,welike);
            //Utils.sleep(500);

           /* ArrayList<PageModel>   pageModels= PageFragment.getPageList(0, 20, null,pageModel.getId());
          //  Utils.sleep(500);
            if(pageModels!=null && pageModels.size()>0)
            {
                pageModel=pageModels.get(0);

            }*/
            PageFragment.refreshlist();


        }
    }
/*********************************************************************/
    //Perform the actual like action
    /********************************************************************************/

    public void like(final PageModel page,final boolean like)
    {
        new AsyncTask<String, Void, Intent>() {



            @Override
            public Intent doInBackground(String... params) {

                Intent result;
                Bundle data = new Bundle();
                    String welike=like?"like":"dislike";


                try {
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                  // nameValuePairs.add(new BasicNameValuePair("datecreated", Constants.SIMPLE_DATEONLY_FORMAT.format(new Date())));
                    nameValuePairs.add(new BasicNameValuePair("msisdn", KEApp.accountName));
                    nameValuePairs.add(new BasicNameValuePair("pageid",page.getId()));

                  /*  nameValuePairs.add(new BasicNameValuePair("user","{\"pageId\": \""+ KEApp.accountName+"\"}"));
                    nameValuePairs.add(new BasicNameValuePair("page","{\"id\": \""+ page.getId()+"\"}"));
                    SyncAdapter.POST(nameValuePairs, "WebService/Users/login", ACCEPT.JSON,"");
                    */
                    JSONArray likeresponse=  SyncAdapter.POST(nameValuePairs, "WebService/likes/"+welike, ACCEPT.JSON, "");
                    JSONObject likedobj=likeresponse.getJSONObject(0);

                   try{
                       if(likedobj.has("status"))
                       {

                       }
                       else
                       {
                           data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error creating  getting likes, Server unreachable");

                       }


                                }
                                catch(Exception e)
                                {

                                    e.printStackTrace();
                                }





                        result = new Intent();




                } catch (Exception e) {
                    //Log.d(TAG, "Error getting pages 22:22222"+e.getMessage());
                    e.printStackTrace();
                    result = new Intent();
                    data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error creating  getting likes, please try again later");
                }

                result.putExtras(data);
                return result;


            }

            @Override
            protected void onPreExecute() {


                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Intent intent) {

                try {
                    if (intent.hasExtra(PageFragment.KEY_ERROR_MESSAGE)) {
                        Log.d(TAG, "Error getting pages 23:" + intent.getStringExtra(PageFragment.KEY_ERROR_MESSAGE));
                    } else {
                        //   Log.d(TAG, "Page Modelsize on PostExecute: " + mypageModels.size());
                           // PageFragment.refreshlist();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.execute();


    }



    /********* Called when Item click in ListView ************/
    private class PageTitleClickListener  implements View.OnClickListener{
        private int mPosition;
        private PageModel page;

        PageTitleClickListener(int position, PageModel page){
            this.mPosition = position;
            this.page = page;

        }

        @Override
        public void onClick(View clickedView) {

            int viewId = clickedView.getId();
            Log.d(TAG, "Calling on click view for page ::"+viewId);
            startPageDetailActivity(mPosition, page);

        }
    }

    public void startPageDetailActivity(int mPosition, PageModel page){
        Log.d(TAG, "Page list view clicked ~ load page detail");
        Intent intent= new Intent(activity, PageDetails.class);
        intent.putExtra("selectedPage", mPosition);
        intent.putExtra("pageId", page.getId());
        intent.putExtra("name", page.getName());
        intent.putExtra("by", page.getBy());
        activity.startActivity(intent);
        //activity.finish();
    }

    public void startPageProfileActivity(int mPosition, String pageId){
        Log.d(TAG, "Page list view clicked ~ load page detail");
        Intent intent= new Intent(activity, Page.class);
        intent.putExtra("selectedPage", mPosition);
        intent.putExtra("pageId", pageId);
        activity.startActivity(intent);
       // activity.finish();
    }

    private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }


