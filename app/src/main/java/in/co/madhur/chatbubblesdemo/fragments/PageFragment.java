package in.co.madhur.chatbubblesdemo.fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import org.apache.http.NameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.KEApp;
import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.adapters.PageListAdapter;
import in.co.madhur.chatbubblesdemo.data.ACCEPT;
import in.co.madhur.chatbubblesdemo.data.SyncAdapter;
import in.co.madhur.chatbubblesdemo.models.PageModel;
import in.co.madhur.chatbubblesdemo.utils.Utils;

/**
 * Created by rube on 4/1/15.
 */
public class PageFragment extends Fragment {

    private static ListView listView;
    private final static  String TAG = PageFragment.class.getSimpleName();
    private static  PageListAdapter adapter;
    private static Activity activity;
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    private static ArrayList<PageModel>  mypageModels;
    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page, container, false);
        activity = this.getActivity();
        mypageModels=new ArrayList<PageModel>();
        mypageModels=getPageList(0, 20, null,"0");

        Log.d(TAG, "Page Model Size before adapter : " + mypageModels.size());
        listView= ( ListView )view.findViewById( R.id.page_list_view );
        adapter=new PageListAdapter(getActivity(),mypageModels);
        Log.d(TAG, "Setting Data into listView text");
        listView.setAdapter(adapter);
        if(adapter.getCount() == 0) {
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView =  inflater.inflate(R.layout.empty_list_item, parentGroup, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyText.setText("No pages found Refresh or create a page ");
            parentGroup.addView(emptyView);
            Log.d(TAG, "Setting empty text");
            listView.setEmptyView(emptyView);
        } else {

            //listView.setOnScrollListener(listViewOnScrollListener);

        }
        if(adapter!=null)
            adapter.notifyDataSetChanged();

        return view;
    }

    public static MenuItemCompat.OnActionExpandListener searchMenuActionExpandListener =
            new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                 //   ArrayList<PageModel> pageModels =
                            getPageList(0, 20, null,"0");
                    adapter.refresh(mypageModels);
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                    return false;
                }
            };

    public static SearchView.OnQueryTextListener searchViewQueryTextListener =
            new SearchView.OnQueryTextListener()
            {

                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(TAG, "Reading OnQueryTextListener for search box");
                    search(query);
                    return true;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return true;
                }


            };

    private void PostPage() {
       /* new AsyncTask<String, Void, Intent>() {


            String name = _name.getText().toString().trim();
            String _email = _mail.getText().toString().trim();
            String accountPassword = _passworda.getText().toString().trim();
            String pageId=_phone.getText().toString().trim();
            final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

            @Override
            protected Intent doInBackground(String... params) {

                String authtoken = null;
                Intent result;
                Bundle data = new Bundle();

                nameValuePairs.add(new BasicNameValuePair("username", _email));
                nameValuePairs.add(new BasicNameValuePair("email", _email));
                nameValuePairs.add(new BasicNameValuePair("password", accountPassword));
                nameValuePairs.add(new BasicNameValuePair("first_name", name));
                nameValuePairs.add(new BasicNameValuePair("last_name", name));
                nameValuePairs.add(new BasicNameValuePair("pageId", pageId));
                nameValuePairs.add(new BasicNameValuePair("regId", regId));
                Log.d(TAG, "> Started Reg");




                try {
                    authtoken = sServerAuthenticate.userSignUp(nameValuePairs, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,accountPassword);

                    if (authtoken != null){
                        createProfile(name, _email);
                        data.putString(KEY_FULL_NAME, name);
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, _email);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                        data.putString(PARAM_USER_PASS, accountPassword);
                        result = new Intent(RegisterActivity.this, FinishSignup.class);
                    }else{
                        result = new Intent();
                        data.putString(KEY_ERROR_MESSAGE, "Error creating user account, please try again later");
                    }

                } catch (Exception e) {
                    Log.d(TAG, "Error in get auth token:"+e.getMessage());
                    e.printStackTrace();
                    result = new Intent();
                    data.putString(KEY_ERROR_MESSAGE, "Error creating user account, please try again later");
                }

                result.putExtras(data);
                return result;
            }

            @Override
            protected void onPreExecute() {

                progressDialog = new ProgressDialog(RegisterActivity.this);
                //progressDialog.setTitle("Processing...");
                //progressDialog
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (progressDialog!=null) {
                    progressDialog.dismiss();
                }
                try {
                    if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                    } else {
                        RegisterActivity.this.startActivity(intent);
                        finish();
                    }
                }
                catch(Exception e)
                {}
            }
        }.execute();*/
    }


    public static void search(String query){
      //  ArrayList<PageModel> simplePageModels =
      getPageList(0, 20, query,"0");
        adapter.refresh(mypageModels);
        listView.invalidateViews();
        listView.refreshDrawableState();
        if (adapter != null)
            adapter.notifyDataSetChanged();

    }
        public static void refreshlist()
        {
           /* adapter.refresh(mypageModels);
            listView.invalidateViews();
            listView.refreshDrawableState();*/
            try{
               mypageModels= getPageList(0, 20, "","0");
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();

                    }
                });

                if (adapter != null)
                    adapter.notifyDataSetChanged();

            }
            catch(Exception e)
            {e.printStackTrace();}
        }
    public static ArrayList<PageModel> getPageList(int offset, int limit, String query, final String pageid) {

      final  ArrayList<PageModel>   mypageModels=new ArrayList<PageModel>();
        new AsyncTask<String, Void, Intent>() {


            PageModel newpage;

            @Override
            public Intent doInBackground(String... params) {

                String authtoken = null;
                Intent result;
                Bundle data = new Bundle();



                try {
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    JSONArray pages= SyncAdapter.GET(nameValuePairs, "WebService/pages/custom/" + KEApp.accountName+"/"+pageid, ACCEPT.JSON, ACCEPT.JSON);
                    if(pages.length()>0) {
                        for(int i=0;i<pages.length();i++)
                        {
                            JSONObject pageobj=pages.getJSONObject(i);
                            //Get page details here....
                            if(pageobj.has("pagename"))
                            {//page exists
                                try {
                                    if(pageobj.getString("pagename").length()<1) continue;
                                    newpage = new PageModel();
                                    newpage.setId("" + pageobj.getLong("id"));
                                    Log.d("DATE ", pageobj.getString("date_created"));
                                    newpage.setDate(pageobj.getString("date_created"));
                                    newpage.setName(pageobj.getString("pagename"));
                                    //JSONObject userobj = new JSONObject( pageobj.getString("user"));
                                   // JSONObject userobj = userarray.getJSONObject(0);
                                    newpage.setCategory("Category: "+pageobj.getString("category"));//" );
                                    newpage.setBy(""+pageobj.getString("user_names"));


                                    newpage.setDescription(pageobj.getString("story"));
                                    try{
                                        String photoData=pageobj.getString("picture");

                                        newpage.setPic(Utils.decodeBase64old(photoData));

                                    }
                                    catch(Exception e)
                                    {
                                        e.printStackTrace();
                                    }

                                   newpage.setComments(pageobj.getString("comments"));
                                    newpage.setLikes(pageobj.getString("likes"));
                                    newpage.setLiked(pageobj.getString("liked"));
                                    Log.d("USER ", newpage.getCategory());
                                    mypageModels.add(newpage);
                                   // Log.d(TAG, "Page Model Size now  : " + mypageModels.size());


                                }
                                catch(Exception e)
                                {

                                    e.printStackTrace();
                                }
                            }

                        }


                        result = new Intent();


                    }
                    else{
                        result = new Intent();
                        data.putString(KEY_ERROR_MESSAGE, "Error getting pages, please try again later");
                    }

                } catch (Exception e) {
                    //Log.d(TAG, "Error getting pages 22:22222"+e.getMessage());
                    e.printStackTrace();
                    result = new Intent();
                    data.putString(KEY_ERROR_MESSAGE, "Error creating  getting pages, please try again later");
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
                    if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                        Log.d(TAG, "Error getting pages 23:" + intent.getStringExtra(KEY_ERROR_MESSAGE));
                    } else {
                     //   Log.d(TAG, "Page Modelsize on PostExecute: " + mypageModels.size());
                        if (adapter != null)
                            adapter.notifyDataSetChanged();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.execute();
        if(adapter!=null)
            adapter.notifyDataSetChanged();

        return mypageModels;
    }

    public static void addpage(PageModel newpage)
    {
        mypageModels.add(newpage);
       refreshlist();
    }


}
