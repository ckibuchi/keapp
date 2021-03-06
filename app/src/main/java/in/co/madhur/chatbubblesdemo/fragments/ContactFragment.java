package in.co.madhur.chatbubblesdemo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.KEApp;
import in.co.madhur.chatbubblesdemo.MainActivity;
import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.adapters.ContactListAdapter;
import in.co.madhur.chatbubblesdemo.db.DB;
import in.co.madhur.chatbubblesdemo.db.DBProvider;
import in.co.madhur.chatbubblesdemo.models.ContactModel;
import in.co.madhur.chatbubblesdemo.observers.SmsSendObserver;
import in.co.madhur.chatbubblesdemo.utils.Utils;

/**
 * Created by rube on 4/1/15.
 */
public class ContactFragment extends Fragment {


    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title

    private static String TAG = ContactFragment.class.getSimpleName();
    private int offset = 0;
    private int limit = 10;
    private static ContactListAdapter adapter;
    private static ListView listView;
    private static String contactFilterQuery = null;

    private static Activity activity;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "Calling contact listView fragment ...");
        activity = this.getActivity();
        View view = inflater.inflate(R.layout.contact_page, container, false);

        listView= ( ListView )view.findViewById( R.id.contact_list_view );

        adapter=new ContactListAdapter(getActivity(),  getContacts(0, 20, contactFilterQuery));
        Log.d(TAG, "Post contact listView adapter ");

        if(adapter.getCount() == 0) {
            listView.setAdapter(adapter);
            listView.setOnScrollListener(listViewOnScrollListener);
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView =  inflater.inflate(R.layout.empty_list_item, parentGroup, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyText.setText("No contacts found, select refresh from menu to update");
            parentGroup.addView(emptyView);
            Log.d(TAG, "Setting empty text");
            listView.setEmptyView(emptyView);
        } else {
            Log.d(TAG, "Setting Data into listView text");
            listView.setAdapter(adapter);
            listView.setOnScrollListener(listViewOnScrollListener);

        }

        return view;
    }

    public AbsListView.OnScrollListener listViewOnScrollListener = new AbsListView.OnScrollListener(){

        @Override
        public void onScroll(AbsListView view,int firstVisibleItem, int visibleItemCount,int totalItemCount) {
            //Algorithm to check if the last item is visible or not
            final int lastItem = firstVisibleItem + visibleItemCount;

            if (lastItem == totalItemCount) {
                // you have reached end of list, load more data
                offset = lastItem;
                Log.d(TAG, "Loding more data from "+offset + "to:"+ offset+ 20);
                ArrayList<ContactModel> contactModels = getContacts(offset, offset+20, contactFilterQuery);
                adapter.update(contactModels);
            }
        }

        @Override
        public void onScrollStateChanged(AbsListView view, int scrollState) {
            //blank, not using this
        }

    };
        public static void refreshcontactlist()
        {
            try{
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                    }
                });



            }
            catch(Exception e)
            {e.printStackTrace();}
        }
    public static ListView.OnItemClickListener listViewOnItemClick = new ListView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Log.d(TAG, "My list clicked at :"+position);
            ContactModel contactModel =(ContactModel)listView.getItemAtPosition(position);

            if(contactModel.isMember()){
                //load chat window
                final Intent chatactivity = new Intent(activity, MainActivity.class);
                Log.d("Names ",contactModel.getName());
                chatactivity.putExtra("name", contactModel.getName());
                activity.startActivity(chatactivity);
            }else{
                Log.d(TAG, "Starting sendSMS activities");
                sendSMS(activity, contactModel.getPhoneNumber());
            }

        }

    };

    public static  ArrayList<ContactModel> getContacts(int offset, int limit, String query) {

        //check if we have data skip keeping otherwise fetch all contacts to out contacts table
        //create contact->profile->profile status
        //              ->status

        DBProvider dbProvider = new DBProvider(activity);
        ContactModel simpleContactModel;
        ArrayList<ContactModel> simpleContactModels = new ArrayList<ContactModel>();

        String filter = "";
        if(query != null && !query.equals("")){
            filter = " where ( p."+DB.Profile.NAME + " LIKE '%"+query+"%' or p."+ DB.Profile.MSISDN + " LIKE '%"+query+"%')";

        }
        //has contact ... check id contacts already exist
        String rawSql = "Select * from " + DB.Contact.TABLE + " c inner join " + DB.Profile.TABLE +
                " p on  c." + DB.Contact.PROFILE + " = p." + DB.Profile._ID + " inner join " + DB.ProfileStatus.TABLE
                + " ps on  p." + DB.Profile._ID + "= ps." + DB.ProfileStatus.PROFILE + filter + " order by p." + DB.Profile.NAME
                + " asc limit "+offset+ ", "+limit;

        Log.d(TAG, rawSql);
        Cursor localContacts = dbProvider.rawQuery(DB.Contact.CONTENT_URI, rawSql, null);
        try {
            if (localContacts.moveToFirst()) {
                do {
                    int isMember = localContacts.getInt(localContacts.getColumnIndex(DB.Contact.IS_MEMBER));

                    //Long photoId = localContacts.getLong(localContacts.getColumnIndex(DB.Profile.PHOTO));
                    String name = localContacts.getString(localContacts.getColumnIndex(DB.Profile.NAME));
                    String message = localContacts.getString(localContacts.getColumnIndex(DB.ProfileStatus.MESSAGE));
                    String msisdn = localContacts.getString(localContacts.getColumnIndex(DB.Profile.MSISDN));
                    String photoPath = localContacts.getString(localContacts.getColumnIndex(DB.Profile.PHOTO));




                    int id = localContacts.getInt(localContacts.getColumnIndex(DB.Contact._ID));
                    Log.i("Actual status ",msisdn+", is member "+isMember+" ID "+id+" alt "+localContacts.getInt(localContacts.getColumnIndex(DB.Contact._ID)));

                    msisdn=msisdn.replace("-", "");
                    msisdn=Utils.getNineDigits(msisdn);
                    if(msisdn==null)
                    {
                        Log.e("NUMBER ERR ","INVALID PHONE ");
                    }
                    else
                    {
                    simpleContactModel = new ContactModel();
                    simpleContactModel.setName(name);
                    simpleContactModel.setId(id);
                    Log.d("Immediate id ", "" + simpleContactModel.getId());
                    simpleContactModel.setPhoneNumber(msisdn);
                    simpleContactModel.setStatusMessage(message);
                    Log.i("Immediate member? ", "" + isMember);
                    if(isMember == 1){
                        simpleContactModel.setIsMember(true);
                    }else if(isMember ==2){
                        simpleContactModel.setIsInvited(true);
                    }

                        Log.d(TAG, "Looking form image on " + photoPath);

                        simpleContactModel.setPic(Utils.getImageBitmap(activity, photoPath, "profile", 55, 55));

                    simpleContactModels.add(simpleContactModel);}

                } while (localContacts.moveToNext());
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(localContacts != null) {
                localContacts.close();
            }
        }

        Log.d(TAG, "Returning simpleContactModels: "+simpleContactModels.size());
        return  simpleContactModels;
    }

    public static void sendSMS(Activity context, String phone) {
        Log.d("PHONE NUMBER ",phone);
        // This example has a timeout set to 15 seconds
        new SmsSendObserver(activity, phone, 15000).start();


        String text = "Checkout keapp for your phone. Download it today from http://keapp.com/ ";
try
{
    Log.d(TAG, "Selecting option two to launch sms pie");
    Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
    smsIntent.setType("vnd.android-dir/mms-sms");
    smsIntent.putExtra("address", phone);
    smsIntent.putExtra("sms_body", text);
    smsIntent.putExtra("exit_on_sent", true);
    context.startActivityForResult(smsIntent, KEApp.CREATE_INVITE_MESSAGE);
    /*Intent sendIntent = new Intent(Intent.ACTION_VIEW);
    sendIntent.putExtra("sms_body",text);
    sendIntent.putExtra("address", new String(phone));
    sendIntent.setType("vnd.android-dir/mms-sms");
    activity.startActivity(sendIntent);*/
} catch (Exception e)
{
    Toast.makeText(context,"ERROR: "+e.getMessage(),Toast.LENGTH_LONG).show();

}
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) // At least KitKat
        {
            Log.d(TAG, "Selecting option one to launch sms pie > KITKAT");
            String defaultSmsPackageName = Telephony.Sms.getDefaultSmsPackage(context); // Need to change the build to API 19

            Intent sendIntent = new Intent(Intent.ACTION_SEND);

            sendIntent.putExtra("exit_on_sent", true);


            if (defaultSmsPackageName != null)// Can be null in case that there is no default, then the user would be able to choose
            // any app that support this intent.
            {
                sendIntent.setPackage(defaultSmsPackageName);
            }
            context.startActivityForResult(sendIntent, KEApp.CREATE_INVITE_MESSAGE);

        } else // For early versions, do what worked for you before.
        {
            Log.d(TAG, "Selecting option two to launch sms pie");
            Intent smsIntent = new Intent(android.content.Intent.ACTION_VIEW);
            smsIntent.setType("vnd.android-dir/mms-sms");
            smsIntent.putExtra("address", phone);
            smsIntent.putExtra("sms_body", text);
            smsIntent.putExtra("exit_on_sent", true);
            context.startActivityForResult(smsIntent, KEApp.CREATE_INVITE_MESSAGE);
        }*/

    }

    public static SearchView.OnQueryTextListener searchViewQueryTextListener = new SearchView.OnQueryTextListener()
    {

            @Override
            public boolean onQueryTextSubmit(String query) {
                Log.d(TAG, "Reading OnQueryTextListener for search box");
                contactFilterQuery = query;
                search(contactFilterQuery);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                search(newText);
                return true;
            }


    };


    public static SearchView.OnCloseListener conatactSearchCloseListener = new SearchView.OnCloseListener() {
        @Override
        public boolean onClose() {
            Log.d(TAG, "Calling contacts on search view close");
            ArrayList<ContactModel> contactModels = getContacts(0, 20, null);
            adapter.refresh(contactModels);
            listView.invalidateViews();
            listView.refreshDrawableState();

            return false;
        }
    };

    public static MenuItemCompat.OnActionExpandListener searchMenuActionExpandListener =
            new MenuItemCompat.OnActionExpandListener() {
        @Override
        public boolean onMenuItemActionExpand(MenuItem item) {
            return false;
        }

        @Override
        public boolean onMenuItemActionCollapse(MenuItem item) {
            Log.d(TAG, "Calling model refrecsh o search collapse");
            ArrayList<ContactModel> contactModels = getContacts(0, 20, null);
            adapter.refresh(contactModels);
            listView.invalidateViews();
            listView.refreshDrawableState();
            return false;
        }
    };

    public static void  search(String query){

        ArrayList<ContactModel> contactModels = getContacts(0, 20, query);
        adapter.refresh(contactModels);
        listView.invalidateViews();
        listView.refreshDrawableState();

    }



}
