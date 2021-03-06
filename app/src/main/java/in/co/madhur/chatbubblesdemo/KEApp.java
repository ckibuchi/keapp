package in.co.madhur.chatbubblesdemo;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ProgressBar;

import com.astuetz.PagerSlidingTabStrip;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.adapters.DrawerListAdapter;
import in.co.madhur.chatbubblesdemo.adapters.FragmentPageAdapter;
import in.co.madhur.chatbubblesdemo.data.SyncAdapter;
import in.co.madhur.chatbubblesdemo.db.DbManager;
import in.co.madhur.chatbubblesdemo.fragments.ContactFragment;
import in.co.madhur.chatbubblesdemo.fragments.MessageFragment;
import in.co.madhur.chatbubblesdemo.fragments.PageFragment;
import in.co.madhur.chatbubblesdemo.listeners.KEAppPageTabListner;
import in.co.madhur.chatbubblesdemo.models.ContactModel;
import in.co.madhur.chatbubblesdemo.observers.SmsSendObserver;
import in.co.madhur.chatbubblesdemo.utils.DividerItemDecoration;
import in.co.madhur.chatbubblesdemo.utils.FragmentNavigationDrawer;
import in.co.madhur.chatbubblesdemo.utils.Utils;


public class KEApp extends ActionBarActivity implements SmsSendObserver.SmsSendListener {

    private static  final  String TAG = in.co.madhur.chatbubblesdemo.KEApp.class.getSimpleName();

    private final Handler handler = new Handler();
    private SearchView search;

    private FragmentNavigationDrawer mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;
    private Toolbar toolbar;                              // Declaring the Toolbar Object
    MessageFragment mf=new MessageFragment();
    RecyclerView mRecyclerView;                           // Declaring RecyclerView
    RecyclerView.Adapter mAdapter;                        // Declaring Adapter For Recycler View
    RecyclerView.LayoutManager mLayoutManager;            // Declaring Layout Manager as a linear layout manager


    private CharSequence mTitle;
    private String[] titles;


    //<item>My Pages</item>
    //<item>Groups</item>
    //<item>Broadcasts</item>
    //<item>Profile</item>
    //<item>Invite Friends</item>
    //<item>Settings</item>
    //<item>Logout</item>


    int icons[] = {
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_paste,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_group,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_email,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_dial_pad,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_new,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_settings,
            in.co.madhur.chatbubblesdemo.R.drawable.ic_action_stop,

    };

    PagerSlidingTabStrip tabsStrip;
    ViewPager viewPager;
    public  static int notif_counter=0;
    public int selectedTab = 0;
    public int INSERT_CONTACT = 1;
    public static int CREATE_INVITE_MESSAGE = 2;
    public  static ProgressBar progressBar;
    public static String userName = "", accountName = "", profileText = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(in.co.madhur.chatbubblesdemo.R.layout.activity_keapp);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        AndroidUtilities.statusBarHeight = getStatusBarHeight();
        try
        {
            NativeLoader.initNativeLibs(App.getInstance());
        }
        catch(Exception e)
        {

        }
        titles = getResources().getStringArray(in.co.madhur.chatbubblesdemo.R.array.drawer_titles);
        Intent i = getIntent();
        Bundle extras = i.getExtras();
        int currentPage = 1;
        notif_counter=0;

        if(extras != null) {
            if (extras.containsKey("FULL_NAME")) {
                userName = extras.getString("FULL_NAME");
            }
            if (extras.containsKey(AccountManager.KEY_ACCOUNT_NAME)) {
                accountName = extras.getString(AccountManager.KEY_ACCOUNT_NAME);
            }
            if (extras.containsKey("FROFILE_TEXT")) {
                profileText = extras.getString("FROFILE_TEXT");
            }
            if (extras.containsKey("CURRENT_PAGE_INDEX")) {
                currentPage = Integer.valueOf(extras.getString("CURRENT_PAGE_INDEX"));
                //We need to load the correct menu as well
                selectedTab = currentPage-1;
            }
        }


        mAdapter = new DrawerListAdapter(this, titles,icons,null,null,0);

        /* Assinging the toolbar object ot the view
        and setting the the Action bar to our toolbar
        */

        toolbar = (Toolbar)findViewById(in.co.madhur.chatbubblesdemo.R.id.tool_bar);

        progressBar = (ProgressBar) toolbar.findViewById(in.co.madhur.chatbubblesdemo.R.id.progress_spinner);
        // Give the PagerSlidingTabStrip the ViewPager
        // Get the ViewPager and set it's PagerAdapter so that it can display items
        viewPager = (ViewPager) findViewById(in.co.madhur.chatbubblesdemo.R.id.viewpager);
        viewPager.setAdapter(new FragmentPageAdapter(getSupportFragmentManager()));
        viewPager.setCurrentItem(currentPage-1);

        tabsStrip = (PagerSlidingTabStrip) findViewById(in.co.madhur.chatbubblesdemo.R.id.sliding_tabs);
        tabsStrip.setShouldExpand(true);        // Attach the view pager to the tab strip
        tabsStrip.setViewPager(viewPager);


        tabsStrip.setOnPageChangeListener(new KEAppPageTabListner(this));
        //toolbar.setNavigationIcon(R.drawable.keapp_logo);
        toolbar.getMenu().clear();
        toolbar.setNavigationIcon(in.co.madhur.chatbubblesdemo.R.drawable.ic_drawer);
        toolbar.setTitle(in.co.madhur.chatbubblesdemo.R.string.app_name);


        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView) findViewById(in.co.madhur.chatbubblesdemo.R.id.RecyclerView); // Assigning the RecyclerView Object to the xml View
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);                              // Setting the adapter to RecyclerView
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);              // Setting the layout Manager

        mDrawerLayout = (FragmentNavigationDrawer) findViewById(in.co.madhur.chatbubblesdemo.R.id.drawer_layout);
        mDrawerLayout.setDrawerShadow(in.co.madhur.chatbubblesdemo.R.drawable.drawer_shadow,  GravityCompat.START);

        mDrawerToggle = new ActionBarDrawerToggle(
                this,
                mDrawerLayout,
                toolbar,
                in.co.madhur.chatbubblesdemo.R.string.drawer_open,
                in.co.madhur.chatbubblesdemo.R.string.drawer_close
        ) {
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }
            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
        mDrawerToggle.syncState();
        // Enable ActionBar app icon to behave as action to toggle the NavigationDrawer
        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

    }



    /*
     * If you do not have any menus, you still need this function
     * in order to open or close the NavigationDrawer when the user
     * clicking the ActionBar app icon.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(mDrawerToggle.onOptionsItemSelected(item)) {
            return true;
        }

        Intent intent;

        switch (item.getItemId()) {

            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search:
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search_chat:
                Log.d(TAG, "Toolbar Icon search");
                break;

            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_chat:
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_chat_chat:
                Log.d(TAG, "Toolbar new search");
                break;

            //chat menu
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search_contact:
                Log.d(TAG, "Toolbar Icon search contact");
                break;
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_contact:
                Log.d(TAG, "Toolbar new search contacts");
                Intent createContactIntent = new Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI);

                if (Integer.valueOf(Build.VERSION.SDK) > 14)
                    createContactIntent.putExtra("finishActivityOnSaveCompleted", true); // Fix for 4.0.3 +
                startActivityForResult(createContactIntent, INSERT_CONTACT);
                break;
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_contact_refresh:
                //Start contacts update
                updateContacts();
                break;

            //Page menu
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search_page:
                Log.d(TAG, "Toolbar Icon search page");
                break;
            case in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_page:
                Log.d(TAG, "Toolbar Icon new page");
                intent = new Intent(this, Page.class);
                this.startActivity(intent);
              //  this.finish();
                break;

            default:
                break;
        }
        //return  true;
        return super.onOptionsItemSelected(item);
    }

    private static ProgressDialog progressDialog;

    @Override
    public void onPause(){
        super.onPause();
        if(progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (progressDialog != null) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    public void updateContacts(){

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                Log.d(TAG, " Starting contacts sync");

                String authtoken = null;
                Bundle responseData = new Bundle();
                ArrayList<ContactModel> contactModels = null;

                try {
                    int offset = 0;
                    int limit = 100;

                    while(true) {
                        contactModels = Utils.getPhoneContactDetails(
                                in.co.madhur.chatbubblesdemo.KEApp.this, null, offset, limit, null,userName
                        );

                        if(contactModels.isEmpty()){
                            break;
                        }
                        for(ContactModel contactModel: contactModels){

                            SyncAdapter.syncContactToLocal(contactModel, in.co.madhur.chatbubblesdemo.KEApp.this);
                        }

                        offset = limit;
                        limit = offset+ 100;

                        //refresh contacts
                        ContactFragment.refreshcontactlist();
                    }

                    //


                } catch (Exception e) {

                }

                return null;
            }

            @Override
            protected void onPreExecute() {
                progressBar.setVisibility(View.VISIBLE);
                //progressDialog = new ProgressDialog(KEApp.this);
                //progressDialog.setMessage("Please wait.");
                //progressDialog.setCancelable(false);
                //progressDialog.setIndeterminate(true);
                //progressDialog.show();
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Intent intent) {
                progressBar.setVisibility(View.GONE);

            }
        }.execute();


    }

    @Override
    public void setTitle(CharSequence title) {
        mTitle = title;
        getActionBar().setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
       //getMenuInflater().inflate(R.menu.menu_keapp, menu);
       return true;
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        menu.clear();
        Log.d(TAG, "I am changing menu right now with selected tab : " + selectedTab);
        MenuItem searchMenuItem;
        switch (selectedTab){
            case 0:
                getMenuInflater().inflate(in.co.madhur.chatbubblesdemo.R.menu.menu_chat, menu);
              //  mf.setmessageList(mf.getMessageList());


                break;
            case 1:
                getMenuInflater().inflate(in.co.madhur.chatbubblesdemo.R.menu.menu_contact, menu);
                searchMenuItem = menu.findItem(in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search_contact);
                MenuItemCompat.setOnActionExpandListener(searchMenuItem, ContactFragment.searchMenuActionExpandListener);
                search = (SearchView)searchMenuItem.getActionView();
                search.setOnQueryTextListener(ContactFragment.searchViewQueryTextListener);
                search.setOnCloseListener(ContactFragment.conatactSearchCloseListener);
                search.requestFocus();

                break;
            case 2:
                getMenuInflater().inflate(in.co.madhur.chatbubblesdemo.R.menu.menu_page, menu);
                searchMenuItem = menu.findItem(in.co.madhur.chatbubblesdemo.R.id.toolbar_icon_search_page);
                MenuItemCompat.setOnActionExpandListener(searchMenuItem, PageFragment.searchMenuActionExpandListener);
                search = (SearchView)searchMenuItem.getActionView();
                search.setOnQueryTextListener(PageFragment.searchViewQueryTextListener);
                search.requestFocus();
                break;
            default:
                getMenuInflater().inflate(in.co.madhur.chatbubblesdemo.R.menu.menu_keapp,  menu);
                break;

        }
        return super.onPrepareOptionsMenu(menu);
    }

    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {

        if (requestCode == INSERT_CONTACT   && resultCode == RESULT_OK)
        {
            //returns a lookup URI to the contact just inserted
            Log.d(TAG, "Calling sync contact after adding new contact");
            Uri newContact = intent.getData();

            String[] segments = newContact.toString().split("/");
            String id = segments[segments.length - 1];

            String filter =  ContactsContract.CommonDataKinds.Phone.CONTACT_ID +" = '"+id +"'";

            ArrayList<ContactModel> contactModels = Utils.getPhoneContactDetails(
                    in.co.madhur.chatbubblesdemo.KEApp.this, filter, 0, 1, null,userName
            );

            Log.d(TAG, "Calling sync contact to local :" +newContact.toString());
            for(ContactModel contactModel: contactModels){
                Log.d(TAG, "Calling sync contact to local :" +contactModel.getPhoneNumber());
                String phone=contactModel.getPhoneNumber();
                phone=phone.replace("-", "");
                phone=Utils.getNineDigits(phone);
                if(phone==null)
                {}
                else {
                    contactModel.setPhoneNumber(phone);
                    SyncAdapter.syncContactToLocal(contactModel, in.co.madhur.chatbubblesdemo.KEApp.this);
                }
            }

        }else if(requestCode == CREATE_INVITE_MESSAGE && resultCode == RESULT_OK){
            Log.d(TAG, "Contact invite message sent success");
        }
    }//

    @Override
    public void onSmsSendEvent(boolean sent, String phoneNumber)
    {
        Log.d(TAG, "Message sent update locat as sent");
        DbManager dbManager = new DbManager(this);
        dbManager.updateContactAsInvited(phoneNumber);
        //Refresh contacts panel
        ContactFragment.search(null);

    }

    public static  void updatechats(String message,String from)
    {}

    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

}
