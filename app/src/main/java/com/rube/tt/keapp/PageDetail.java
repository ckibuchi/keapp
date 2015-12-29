package com.rube.tt.keapp;

import android.app.ActionBar;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.rube.tt.keapp.adapters.PageDetailListAdapter;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.models.PageDetailModel;
import com.rube.tt.keapp.utils.Utils;

import java.util.ArrayList;


/**
 * Created by rube on 4/10/15.
 */
public class PageDetail extends ActionBarActivity {

    private static  final  String TAG = PageDetail.class.getSimpleName();

    private final Handler handler = new Handler();

    private Toolbar toolbar;
    private String pageId;
    private String postion;
    private DBProvider dbProvider;

    private TextView pageCategoryTextView;
    private ImageView pageProfileImageView;
    private RelativeLayout pageBannerLayout;
    private  ListView listView;
    private PageDetailListAdapter adapter;
    private LinearLayout newStroryLayout;
    private String thisPageName;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_detail);

        Bundle extras = getIntent().getExtras();
        pageId = extras.getString("pageId");

        LinearLayout pageDetailHeader  = (LinearLayout)findViewById(R.id.page_detail_header_id);

        pageCategoryTextView = (TextView)pageDetailHeader.findViewById(R.id.page_profile_category);
        pageProfileImageView = (ImageView)pageDetailHeader.findViewById(R.id.page_profile_icon);
        pageBannerLayout = (RelativeLayout)pageDetailHeader.findViewById(R.id.layout_header_banner);
        newStroryLayout = (LinearLayout)pageDetailHeader.findViewById(R.id.new_story_layout);
        newStroryLayout.setOnClickListener(newStroryLayoutClickListener);

        dbProvider = new DBProvider(this);
        loadPageData(pageId);

        toolbar = (Toolbar)findViewById(R.id.page_tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(thisPageName);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

        listView = ( ListView )findViewById(R.id.page_detail_list_view);
        //listView.addHeaderView(pageDetailHeader);
        adapter=new PageDetailListAdapter(this, getPageDetailList(0, 10, pageId, null) );

        Log.d(TAG, "Checking adapter size and setting adapter: "+adapter.getCount());
        listView.setAdapter(adapter);

        if(adapter.getCount() == 0) {
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView = getLayoutInflater().inflate(R.layout.empty_list_item, null, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT
            ));
            emptyText.setText("No stories found ");
            parentGroup.addView(emptyView);
            Log.d(TAG, "Setting empty text");
            listView.setEmptyView(emptyText);
        } else {
            Log.d(TAG, "Notify action chnaged ..");
            adapter.notifyDataSetChanged();
            listView.invalidateViews();
            listView.setVisibility(View.VISIBLE);
        }

    }


    //When we have page data - load the data
    public void loadPageData(String pageId){
        //has contact ... check id contacts already exist
        String rawSql = "Select p."+DB.Page._ID +", " + " p."+DB.Page.NAME+ ", p."+DB.Page.DATE_CREATED + ", " +
                " p."+DB.Page.TEXT+", pc."+DB.PageCategory.NAME+ " as page_category, " +
                " p." + DB.Page.MEDIA_PHOTO_ID+ ", "+ "p." + DB.Page.MEDIA_BG_ID +
                " from " + DB.Page.TABLE + " p inner join " + DB.PageCategory.TABLE +
                " pc on  pc." + DB.PageCategory._ID + " = p." + DB.Page.CATEGORY_ID
                + " where p."+ DB.Page._ID +"= '"+pageId+"'";

        Log.d(TAG, rawSql);

        Cursor pagesCursor = dbProvider.rawQuery(DB.Page.CONTENT_URI, rawSql, null);
        try {
            if (pagesCursor.moveToFirst()) {
                String pageName = pagesCursor.getString(pagesCursor.getColumnIndex( DB.Page.NAME));
                thisPageName = pageName;
                String dateCreated = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.DATE_CREATED));
                String categoryName = pagesCursor.getString(pagesCursor.getColumnIndex("page_category"));
                String text = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.TEXT));
                String profilePhotoID = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.MEDIA_PHOTO_ID));
                String bannerPhotoID = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.MEDIA_BG_ID));

                Log.d(TAG, "PAGE DATA :pageName =>" + pageName + ", Date created =>" + dateCreated
                        + ", cateoryName =>" + categoryName + ", text =>" + text);


                pageCategoryTextView.setText(categoryName);
                //toolbar.setTitle(pageName);

                //Add background to page Layout
                Cursor bannerCursor = dbProvider.query(DB.Media.CONTENT_URI, new String[]{DB.Media.URL},
                        DB.Media._ID+"=?", new String[]{bannerPhotoID}, null);

                try {
                    if (bannerCursor.moveToFirst()) {
                        String bannerFileName = bannerCursor.getString(bannerCursor.getColumnIndex(DB.Media.URL));
                        Uri bannerUri = Utils.getImageUri(bannerFileName);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bannerUri);
                        //BitmapDrawable scaledImage = Utils.scaleImage(this, bitmap, pageBannerLayout.getHeight(), pageBA)
                        Drawable bitMapDrawable = new BitmapDrawable(getResources(), bitmap);
                        pageBannerLayout.setBackground(bitMapDrawable);
                    }
                    bannerCursor.close();
                }catch (Exception ex)
                {

                    Log.d(TAG,  "Exception loading page banner image: "+ex.getMessage());
                }
                Cursor profileCursor = dbProvider.query(DB.Media.CONTENT_URI, new String[]{DB.Media.URL},
                        DB.Media._ID+"=?", new String[]{profilePhotoID}, null);

                Log.d(TAG, "Loading Page profile URL media ID "+profilePhotoID);
                try {
                    if (profileCursor.moveToFirst()) {
                        String profileImageName = profileCursor.getString(profileCursor.getColumnIndex(DB.Media.URL));
                        Uri profileUri = Utils.getImageUri(profileImageName);
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileUri);
                        pageProfileImageView.setImageBitmap(bitmap);
                        Utils.scaleImage(this, pageProfileImageView);

                    }
                    profileCursor.close();
                }catch (Exception ex){
                    Log.d(TAG, "Error loading profile image :"+ex.getMessage());
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(pagesCursor != null) {
                pagesCursor.close();
            }
        }

    }

    public ArrayList<PageDetailModel> getPageDetailList(int offset, int limit, String pageId, String query){

        ArrayList<PageDetailModel> pageDetailModels = new ArrayList<PageDetailModel>();

        PageDetailModel pageDetailModel = new PageDetailModel();
        String searchString = "";
        String selectionArgs[] = new String[]{pageId};
        if(query != null){
            searchString = " and "+DB.Story.NAME +" like '%?%' ";
            selectionArgs = new String[]{pageId, query};
        }

        String rawSql = "Select st.*, p." +DB.Profile.NAME + " as profile_name "+
                " from " + DB.Story.TABLE + " st inner join " + DB.Profile.TABLE +
                " p on  st." + DB.Story.PROFILE_ID + " = p." + DB.Profile._ID +
                " where st."+ DB.Story.PAGE_ID +"= ? " +searchString +
                " order by st."+DB.Story._ID + " desc limit "+offset+", "+limit;

        Log.d(TAG, rawSql);

        Cursor storyDetailCursor =  dbProvider.rawQuery(DB.Story.CONTENT_URI, rawSql, selectionArgs);
        Log.d(TAG, "Found records on stroy Query:" + storyDetailCursor.getCount());
        try {
            if (storyDetailCursor.moveToFirst()) {

                do {
                    Log.d(TAG, "Found Record Strory Profile ID:" +
                            storyDetailCursor.getString(storyDetailCursor.getColumnIndex(DB.Story.PROFILE_ID)));
                    String storyName = storyDetailCursor.getString(storyDetailCursor.getColumnIndex(DB.Story.NAME));
                    String creatorProfileName = storyDetailCursor.getString(storyDetailCursor.getColumnIndex("profile_name"));
                    String mediaPhotoID = storyDetailCursor.getString(storyDetailCursor.getColumnIndex(DB.Story.MEDIA_PHOTO_ID));
                    String text = storyDetailCursor.getString(storyDetailCursor.getColumnIndex(DB.Story.TEXT));
                    String dateCreated = storyDetailCursor.getString(storyDetailCursor.getColumnIndex(DB.Story.DATE_CREATED));

                    Cursor storyCursor = dbProvider.query(DB.Media.CONTENT_URI, new String[]{DB.Media.URL},
                            DB.Media._ID + "=?", new String[]{mediaPhotoID}, null);

                    Bitmap storyBitmapImage = null;

                    try {
                        if (storyCursor.moveToFirst()) {
                            String stroyFileName = storyCursor.getString(storyCursor.getColumnIndex(DB.Media.URL));
                            Uri storyProfileUri = Utils.getImageUri(stroyFileName);
                            storyBitmapImage = MediaStore.Images.Media.getBitmap(this.getContentResolver(), storyProfileUri);

                        }
                        storyCursor.close();
                    }catch (Exception ex)
                    {

                        Log.d(TAG,  "Exception loading page banner image: "+ex.getMessage());
                    }


                    pageDetailModel = new PageDetailModel();
                    pageDetailModel.setProfileName(creatorProfileName);
                    pageDetailModel.setTitle(storyName);
                    if(storyBitmapImage != null){
                        pageDetailModel.setProfilePic(storyBitmapImage);
                    }else {
                        pageDetailModel.setProfilePic(BitmapFactory.decodeResource(getResources(),
                                R.drawable.default_profile_pic));
                    }
                    pageDetailModel.setText(text);
                    pageDetailModel.setDate(dateCreated);
                    pageDetailModels.add(pageDetailModel);

                    Log.d(TAG, "StoryName => "+storyName + " text =>"+text + " dateCreated =>"+dateCreated +
                        " createor,  "+creatorProfileName);

                } while (storyDetailCursor.moveToNext());
            }
        }catch (Exception ex){
            Log.d(TAG, "Got error trying to get stories");
            ex.printStackTrace();
        }finally {
            try {
                if(storyDetailCursor != null) {
                    storyDetailCursor.close();
                }

            }catch (Exception ex){
                ex.printStackTrace();
            }
        }

        return pageDetailModels;
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // app icon in action bar clicked; go home
                Intent intent = new Intent(this, KEApp.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.putExtra("CURRENT_PAGE_INDEX", "3");

                startActivity(intent);
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public boolean onPrepareOptionsMenu (Menu menu) {
        MenuItem searchMenuItem;
        menu.clear();
        Log.d(TAG, "I am changing page detail right menu with selected seach_icon_only ");
        getMenuInflater().inflate(R.menu.search_icon_only_menu, menu);
        searchMenuItem = menu.findItem(R.id.toolbar_icon_search_only);
        MenuItemCompat.setOnActionExpandListener(searchMenuItem, searchMenuActionExpandListener);
        search = (SearchView)searchMenuItem.getActionView();
        search.setOnQueryTextListener(searchViewQueryTextListener);
        return super.onPrepareOptionsMenu(menu);
    }


    public  MenuItemCompat.OnActionExpandListener searchMenuActionExpandListener =
            new MenuItemCompat.OnActionExpandListener() {
                @Override
                public boolean onMenuItemActionExpand(MenuItem item) {
                    return false;
                }

                @Override
                public boolean onMenuItemActionCollapse(MenuItem item) {
                    ArrayList<PageDetailModel> pageModels = getPageDetailList(0, 20, pageId, null);
                    adapter.refresh(pageModels);
                    listView.invalidateViews();
                    listView.refreshDrawableState();
                    return false;
                }
            };

    public  SearchView.OnQueryTextListener searchViewQueryTextListener =
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


    public  void search(String query){
        ArrayList<PageDetailModel> simplePageModels = getPageDetailList(0, 20, pageId, query);
        adapter.refresh(simplePageModels);
        listView.invalidateViews();
        listView.refreshDrawableState();

    }

    public View.OnClickListener newStroryLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(PageDetail.this, Story.class);
            Bundle args = new Bundle();
            args.putString("pageId", pageId);
            args.putString("pageName", thisPageName);
            intent.putExtras(args);
            PageDetail.this.startActivity(intent);

        }
    };


    private SearchView search;

}

