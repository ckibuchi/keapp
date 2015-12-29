package com.rube.tt.keapp.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
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

import com.rube.tt.keapp.R;
import com.rube.tt.keapp.adapters.ContactListAdapter;
import com.rube.tt.keapp.adapters.PageListAdapter;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.models.ContactModel;
import com.rube.tt.keapp.models.PageModel;
import com.rube.tt.keapp.models.SimplePageModel;
import com.rube.tt.keapp.utils.Utils;

import java.util.ArrayList;

/**
 * Created by rube on 4/1/15.
 */
public class PageFragment extends Fragment {

    private static ListView listView;
    private final static  String TAG = PageFragment.class.getSimpleName();
    private static  PageListAdapter adapter;
    private static Activity activity;

    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.page, container, false);
        activity = this.getActivity();

        listView= ( ListView )view.findViewById( R.id.page_list_view );
        adapter=new PageListAdapter(getActivity(), getPageList(0, 20, null) );

        if(adapter.getCount() == 0) {
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView =  inflater.inflate(R.layout.empty_list_item, parentGroup, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyText.setText("No pages found ");
            parentGroup.addView(emptyView);
            Log.d(TAG, "Setting empty text");
            listView.setEmptyView(emptyView);
        } else {
            Log.d(TAG, "Setting Data into listView text");
            listView.setAdapter(adapter);
            //listView.setOnScrollListener(listViewOnScrollListener);

        }

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
                    ArrayList<PageModel> pageModels = getPageList(0, 20, null);
                    adapter.refresh(pageModels);
                    listView.invalidateViews();
                    listView.refreshDrawableState();
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


    public static void search(String query){
        ArrayList<PageModel> simplePageModels = getPageList(0, 20, query);
        adapter.refresh(simplePageModels);
        listView.invalidateViews();
        listView.refreshDrawableState();

    }

    private static ArrayList<PageModel> getPageList(int offset, int limit, String query) {

        ArrayList<PageModel> pageModels = new ArrayList<PageModel>();

        PageModel pageModel;

        DBProvider dbProvider = new DBProvider(activity);

        String filter = "";
        if(query != null && !query.equals("")){
            filter = " where ( p."+ DB.Page.NAME + " LIKE '%"+query+"%'  or pc."+DB.PageCategory.NAME+ " LIKE '%"+query+"%' )";

        }
        //has contact ... check id contacts already exist
        String rawSql = "Select p."+DB.Page._ID +", " + " p."+DB.Page.NAME+ ", p."+DB.Page.DATE_CREATED + ", " +
                " p."+DB.Page.TEXT+"," +" p."+DB.Page.MEDIA_PHOTO_ID+ ","+ " pc."+DB.PageCategory.NAME+ " as page_category " +
                " from " + DB.Page.TABLE + " p inner join " + DB.PageCategory.TABLE +
                " pc on  pc." + DB.PageCategory._ID + " = p." + DB.Page.CATEGORY_ID
                + filter + " order by p." + DB.Page._ID
                + " desc limit "+offset+ ", "+limit;

        Log.d(TAG, rawSql);
        Cursor pagesCursor = dbProvider.rawQuery(DB.Page.CONTENT_URI, rawSql, null);
        try {
            if (pagesCursor.moveToFirst()) {
                do {
                    String pageId = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page._ID));
                    String pageName = pagesCursor.getString(pagesCursor.getColumnIndex( DB.Page.NAME));
                    String dateCreated = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.DATE_CREATED));
                    String categoryName = pagesCursor.getString(pagesCursor.getColumnIndex("page_category"));
                    String profilePhotoID = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.MEDIA_PHOTO_ID));
                    String text = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.TEXT));

                    Log.d(TAG, "PAGE DATA :pageName =>"+ pageName + ", Date created =>"+dateCreated
                        + ", cateoryName =>"+categoryName + ", text =>"+text);

                    pageModel = new PageModel();
                    pageModel.setId(pageId);
                    pageModel.setDate(dateCreated);
                    pageModel.setName(pageName);
                    pageModel.setCategory(categoryName);
                    pageModel.setSubCategory(null);
                    pageModel.setDescription(text);
                    pageModel.setLikes("0");
                    pageModel.setLikes("0");

                    Log.d(TAG, "Looking form inage on ...");

                    //simpleContactModel.setPic(Utils.getImageBitmap(activity, photoPath, "profile", 55, 55));
                    pageModels.add(pageModel);

                    Cursor profileCursor = dbProvider.query(DB.Media.CONTENT_URI, new String[]{DB.Media.URL},
                            DB.Media._ID+"=?", new String[]{profilePhotoID}, null);

                    Log.d(TAG, "Loading Page profile URL media ID " + profilePhotoID);


                    try {
                        if (profileCursor.moveToFirst()) {
                            String profileImageName = profileCursor.getString(profileCursor.getColumnIndex(DB.Media.URL));
                            Uri profileUri = Utils.getImageUri(profileImageName);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(activity.getContentResolver(), profileUri);
                            pageModel.setPic(bitmap);

                        }else{
                            pageModel.setPic(BitmapFactory.decodeResource(activity.getResources(), R.drawable.pages_default_pic));
                        }
                        profileCursor.close();
                    }catch (Exception ex){
                        Log.d(TAG, "Error loading profile image :"+ex.getMessage());
                    }

                } while (pagesCursor.moveToNext());
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(pagesCursor != null) {
                pagesCursor.close();
            }
        }

        Log.d(TAG, "Returning simpleContactModels: "+pageModels.size());
        return  pageModels;

    }


}
