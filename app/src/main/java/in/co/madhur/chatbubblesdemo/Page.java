package in.co.madhur.chatbubblesdemo;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import in.co.madhur.chatbubblesdemo.adapters.ImageAdapter;
import in.co.madhur.chatbubblesdemo.adapters.SpinnerItemAdapter;
import in.co.madhur.chatbubblesdemo.data.ACCEPT;
import in.co.madhur.chatbubblesdemo.data.SyncAdapter;
import in.co.madhur.chatbubblesdemo.db.DB;
import in.co.madhur.chatbubblesdemo.db.DBProvider;
import in.co.madhur.chatbubblesdemo.fragments.PageFragment;
import in.co.madhur.chatbubblesdemo.models.PageModel;
import in.co.madhur.chatbubblesdemo.utils.Utils;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;
//import org.lucasr.twowayview.TwoWayView;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by rube on 27/09/15.
 */
public class Page extends ActionBarActivity {

    private static  final  String TAG = Page.class.getSimpleName();

    private final Handler handler = new Handler();

    private Toolbar toolbar;


    private Spinner pageCategorySpinner;
    private EditText pageNameEditText;
    private TextView pageDescription;
    private ImageView newPageMenu;

    private TextView saveTextView;
    private DBProvider dbProvider;
    HashMap<String, String> categoriesMap = new HashMap<String, String>();
    private String thisProfile = "0";
    private LinearLayout createPageLayout;
    AsyncTask<Void, Void, String> sendTask;
    private TextView pageOverlayProfileName;
    //private ImageView uploadImageView ;
    private static ImageAdapter imageAdapter;
    private ImageView pageProfileImage;
    private RelativeLayout pageBannerLayout;

    public static final String preference = "MyAccountPreference" ;
    private SharedPreferences sharedPreferences;


    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;
    public static final int PICK_IMAGE_REQUEST=3;
    private Uri fileUri;

    public static final int VIEW_PAGE_PROFILE_PHOTO = 4;
    public static final int VIEW_PAGE_BANNER_PHOTO  = 5;
    public static final int VIEW_PAGE_FILES_PHOTO   = 6;

    public static Bitmap selectedMediaImage = null;

    //TwoWayView imageHorizontalList;
    private ArrayList<Bitmap> profileImages = new ArrayList<Bitmap>();

    private static String profileImageName;
    private static String bannerImageName;
    private static HashMap<Integer, String> pageFiles = new HashMap<Integer, String>();
    private static String selectedCategory = "";
    private static boolean pageAlreadySaved = false;
    private static String pageId = "0";
    private static List<String> categoryList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_page);
        toolbar = (Toolbar)findViewById(R.id.page_tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.title_create_page);
        setSupportActionBar(toolbar);

        Bundle args = getIntent().getExtras();
        if(args != null) {
            Log.d(TAG, "PAGE Data ..."+args.toString());
            if (args.containsKey("PROFILE_ID")) {
                thisProfile = args.getString("PROFILE_ID");
            }
            if (args.containsKey("pageId")) {
                pageId = args.getString("pageId");
                pageAlreadySaved = true;
            }
        }
        pageOverlayProfileName = (TextView)findViewById(R.id.page_profile_name);
        saveTextView = (TextView)toolbar.findViewById(R.id.save_page);

        createPageLayout = (LinearLayout)toolbar.findViewById(R.id.create_new_page_layout);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

        pageCategorySpinner =  (Spinner)findViewById(R.id.page_category_text_spiner);



        dbProvider = new DBProvider(this);
        categoryList = new ArrayList<String>();

        Cursor pageCategoriesCursor = dbProvider.query(
                DB.PageCategory.CONTENT_URI, new String[]{DB.PageCategory._ID, DB.PageCategory.NAME},
                null, null, null);

        Log.d(TAG, "Found categories cursor :" +pageCategoriesCursor.getCount());

        try {
            if (pageCategoriesCursor.moveToFirst()) {
                do {
                    //Long photoId = localContacts.getLong(localContacts.getColumnIndex(DB.Profile.PHOTO));
                    String id = pageCategoriesCursor.getString(pageCategoriesCursor.getColumnIndex(DB.PageCategory._ID));
                    String name = pageCategoriesCursor.getString(pageCategoriesCursor.getColumnIndex(DB.PageCategory.NAME));

                    categoriesMap.put(name, id);

                } while (pageCategoriesCursor.moveToNext());
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(pageCategoriesCursor != null) {
                pageCategoriesCursor.close();
            }
        }
        for(String name: categoriesMap.keySet()){
            categoryList.add(name);
        }

        SpinnerItemAdapter spinnerItemAdapter = new SpinnerItemAdapter(this, categoryList);

        pageCategorySpinner.setAdapter(spinnerItemAdapter);
        pageCategorySpinner.setOnItemSelectedListener(spinnerItemSelectedListener);

        pageNameEditText = (EditText)findViewById(R.id.page_name_text);

        pageDescription = (TextView)findViewById(R.id.new_page_description);
        newPageMenu = (ImageView)findViewById(R.id.new_page_menu);
        //uploadImageView = (ImageView)findViewById(R.id.page_upload_image);

        pageProfileImage = (ImageView)findViewById(R.id.page_profile_icon);
        pageBannerLayout = (RelativeLayout)findViewById(R.id.layout_header_banner);

        saveTextView.setOnClickListener(saveTextViewClickListerner);

//        uploadImageView.setOnClickListener(uploadPageImageClickListener);
        pageProfileImage.setOnClickListener(changePageProfileImageClickListener);
        pageBannerLayout.setOnClickListener(changeProfileBannerImageClickListener);

        newPageMenu.setOnClickListener(newPageViewClickListerner);

     //   imageHorizontalList = (TwoWayView)findViewById(R.id.horizontalList);
/*
        if(pageAlreadySaved && Integer.valueOf(pageId) > 0){
            Log.d(TAG, "Loading page Data ..."+pageId);
            loadPageData(pageId);
        }*/

    }

    //When we have page data - load the data
    public void loadPageData(String pageId){
        DBProvider dbProvider = new DBProvider(this);
        //Cursor pageCursor = dbProvider.query(DB.Page.CONTENT_URI, null, DB.Page._ID +"=?",
          //      new String[]{pageId}, null);

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
                String dateCreated = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.DATE_CREATED));
                String categoryName = pagesCursor.getString(pagesCursor.getColumnIndex("page_category"));
                String text = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.TEXT));
                String profilePhotoID = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.MEDIA_PHOTO_ID));
                String bannerPhotoID = pagesCursor.getString(pagesCursor.getColumnIndex(DB.Page.MEDIA_BG_ID));

                Log.d(TAG, "PAGE DATA :pageName =>" + pageName + ", Date created =>" + dateCreated
                        + ", cateoryName =>" + categoryName + ", text =>" + text);
                pageCategorySpinner.setSelection(categoryList.indexOf(categoryName));
                pageCategorySpinner.setEnabled(false);

                pageNameEditText.setText(pageName);
                pageNameEditText.setEnabled(false);
                toolbar.setTitle(pageName);


                pageOverlayProfileName.setText(pageName);
                saveTextView.setVisibility(View.GONE);
                newPageMenu.setVisibility(View.VISIBLE);

                pageDescription.setText(text);
                pageDescription.setEnabled(false);

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
                        pageProfileImage.setImageBitmap(bitmap);
                        Utils.scaleImage(this, pageProfileImage);

                    }
                    profileCursor.close();
                }catch (Exception ex){
                    Log.d(TAG, "Error loading profile image :"+ex.getMessage());
                }

                Cursor pageImageCursor = dbProvider.query(DB.Media.CONTENT_URI, new String[]{DB.Media.URL},
                        DB.Media.CONTENT_ID+"=? and "+DB.Media.MEDIA_TYPE +"=? " +
                        DB.MediaColumns.CONTENT_TYPE+ "=? ", new String[]{bannerPhotoID, "PHOTO", "PAGE"}, null);

                //Add page images
               try{
                    if(pageImageCursor.moveToFirst()){
                        do{
                            Uri profileUri = Utils.getImageUri(profileImageName);
                            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), profileUri);
                            profileImages.add(bitmap);
                            //Do image save before share here

                            imageAdapter= new ImageAdapter(this, profileImages);
                       //     imageHorizontalList.setAdapter(imageAdapter);
                      //      imageHorizontalList.setItemMargin(2);
                            //imageHorizontalList.setOnItemClickListener(pageFilesClickLister);

                        }while (pageImageCursor.moveToNext());
                    }
               }catch (Exception ex){
                    Log.d(TAG, "Error loadng page images :"+ex.getMessage()) ;
               }


            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(pagesCursor != null) {
                pagesCursor.close();
            }
        }
        loadPageImages(pageId);

    }

    public void loadPageImages(String pageId){
        String rawSql = "Select *  from " + DB.Media.TABLE + "  where "+ DB.Media.CONTENT_ID +"= '"+pageId+"'" +
                " and  " +DB.MediaColumns.CONTENT_TYPE +" = 'PAGE' "+
                " order by "+DB.Media._ID +" desc limit 10";

        Log.d(TAG, rawSql);
        Cursor mediaCursor = dbProvider.rawQuery(DB.Media.CONTENT_URI, rawSql, null);
        try {
            if (mediaCursor.moveToFirst()) {
                do {
                    String imageURL = mediaCursor.getString(mediaCursor.getColumnIndex(DB.Media.URL));

                    Uri bannerUri = Utils.getImageUri(imageURL);
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), bannerUri);

                    profileImages.add(bitmap);
                    //Do image save before share here
                }while (mediaCursor.moveToNext());

                //load the images to the adapter
                imageAdapter= new ImageAdapter(this, profileImages);
              //  imageHorizontalList.setAdapter(imageAdapter);
               // imageHorizontalList.setItemMargin(2);
                //imageHorizontalList.setOnItemClickListener(pageFilesClickLister);
            }
        }catch (Exception ex){
            Log.d(TAG, "Exception loading page images: "+ex.getMessage());
            ex.printStackTrace();
        }finally {
            try {
                if(mediaCursor != null) {
                    mediaCursor.close();
                }
            }catch (Exception ex){
                Log.d(TAG, "Error closing cursor "+ex.getMessage());
                ex.printStackTrace();
            }
        }

    }

    /********* Called when Item click in ListView ************/
    public View.OnClickListener saveTextViewClickListerner = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            saveNewPage();
            finish();
        }
    };

    /********* Called when Item click in ListView ************/
    public View.OnClickListener newPageViewClickListerner = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            Intent intent = new Intent(Page.this, Page.class);
            Page.this.startActivity(intent);
            Page.this.finish();

        }
    };

    private AdapterView.OnItemSelectedListener  spinnerItemSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> adapterView, View view, int pos, long id) {
            //selectedCategory = pageCategorySpinner.getSelectedItem().toString();

            TextView textView = (TextView)view.findViewById(R.id.spinner_item_name);
            selectedCategory = textView.getText().toString();
            Log.d(TAG, "Spinner selected item "+selectedCategory);
        }

        @Override
        public void onNothingSelected(AdapterView<?> adapterView) {
            //selectedCategory = "POLITICS";
        }
    };

    public View.OnClickListener changeProfileBannerImageClickListener = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            //Do image save before share here
            if(!pageAlreadySaved) {
                pageAlreadySaved = saveNewPage();
                if (!pageAlreadySaved) {
                    return;
                }
            }
            Bitmap image = ((BitmapDrawable)pageBannerLayout.getBackground()).getBitmap();

            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.PNG, 100, bs);

            Intent intent = new Intent(Page.this, BigImageViewActivity.class);
            Bundle extras = new Bundle();

            extras.putByteArray("image", bs.toByteArray());
            extras.putString("title", "Page Banner");
            extras.putString("fileName", bannerImageName);
            extras.putString("parentId", pageId);
            extras.putString("parentClass", Page.class.getCanonicalName());
            extras.putString("parentType", "PAGE_BANNER");
            extras.putString("parentDescription", "Profile banner photo");
            intent.putExtras(extras);
            Page.this.startActivityForResult(intent, VIEW_PAGE_BANNER_PHOTO);

        }
    };

    public View.OnClickListener changePageProfileImageClickListener = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
            //Do image save before share here
            if(!pageAlreadySaved) {
                pageAlreadySaved = saveNewPage();
                if (!pageAlreadySaved) {
                    return;
                }
            }
            Bitmap image = ((BitmapDrawable)pageProfileImage.getDrawable()).getBitmap();
            Intent intent = new Intent(Page.this, BigImageViewActivity.class);
            Bundle extras = new Bundle();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();

            image.compress(Bitmap.CompressFormat.PNG, 100, bs);
            extras.putByteArray("image", bs.toByteArray());
            extras.putString("title", "Page Profile");
            extras.putString("fileName", profileImageName);
            extras.putString("parentId", pageId);
            extras.putString("parentClass", Page.class.getCanonicalName());
            extras.putString("parentType", "PAGE_PROFILE");
            extras.putString("parentDescription", "Media profile photo");

            intent.putExtras(extras);
            Log.d(TAG, "Calling start big image load for profile photo");
            Page.this.startActivityForResult(intent, VIEW_PAGE_PROFILE_PHOTO);

        }
    };

    public AdapterView.OnItemClickListener pageFilesClickLister = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            //We could have missed saving page save it now
            Log.d(TAG, "Found on item click position " + position);
            if(!pageAlreadySaved) {
                pageAlreadySaved = saveNewPage();
                if (!pageAlreadySaved) {
                    return;
                }
            }

            Bitmap image = (Bitmap)imageAdapter.getItem(position);
            Intent intent = new Intent(Page.this, BigImageViewActivity.class);
            Bundle extras = new Bundle();
            ByteArrayOutputStream bs = new ByteArrayOutputStream();
            String pageName = pageNameEditText.getText().toString();

            image.compress(Bitmap.CompressFormat.PNG, 100, bs);
            extras.putByteArray("image", bs.toByteArray());
            extras.putString("title", pageName + " Media " + position + 1 + " of " + profileImages.size());
            extras.putString("position", ""+position);
            extras.putString("fileName", pageFiles.get(""+position+1));
            intent.putExtras(extras);
            Log.d(TAG, "Calling start big image load");
            Page.this.startActivityForResult(intent, VIEW_PAGE_FILES_PHOTO);

        }
    };

    /********* Called when Item click in ListView ************/
    public View.OnClickListener uploadPageImageClickListener = new   View.OnClickListener(){

        @Override
        public void onClick(View arg0) {
           //Do image upload here
            Intent intent = new Intent();
            // Show only images, no videos or anything else
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            // Always show the chooser (if there are multiple options available)
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
            //fileUri =  Utils.getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
            //Utils.captureImage(Page.this, fileUri);
        }
    };

    public boolean saveNewPage(){
//Utils.createToast(this,"Creating page" ,Toast.LENGTH_SHORT);
        String pageName = pageNameEditText.getText().toString();
        Cursor pageCheckCursor = dbProvider.query(
                DB.Page.CONTENT_URI, new String[]{DB.Page._ID},
                DB.Page.NAME + "=?", new String[]{pageName}, null);

        if(pageCheckCursor.getCount() > 0){
            Utils.createToast(this, "Duplicate page name", Toast.LENGTH_SHORT);
            //pageNameEditText.setText("");
           // return pageAlreadySaved;
        }


        if(pageName == null || pageName.equals("")){
            Utils.createToast(this, "Enter page name", Toast.LENGTH_SHORT);
         //   return pageAlreadySaved;

        }

        //From spinner onItemSelectedListener
        if(selectedCategory == null || selectedCategory.equals("")){
            Utils.createToast(this, "Choose page category", Toast.LENGTH_SHORT);
            //return pageAlreadySaved;

        }

        String description = pageDescription.getText().toString();

        if(description == null || description.equals("")){
            Utils.createToast(this, "Enter Story", Toast.LENGTH_SHORT);
          //  return pageAlreadySaved;

        }

        sharedPreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
        String userName = sharedPreferences.getString("accountName", "");
        if(userName.equals("")){
            Utils.createToast(this, "Invalid account login, please re-login to proceed", Toast.LENGTH_SHORT);
         //   return pageAlreadySaved;
        }

        Cursor profileIdCursor = dbProvider.query(DB.Profile.CONTENT_URI, new String[]{DB.Profile.USERNAME, DB.Profile._ID},
                DB.Profile.USERNAME+" =?", new String[]{userName}, null);

        String profileId = "0";
        try {
            if (profileIdCursor.moveToFirst()) {
                profileId = profileIdCursor.getString(profileIdCursor.getColumnIndex(DB.Profile._ID));
            }
            profileIdCursor.close();
        }catch (Exception ex){
            Log.d(TAG, "Error attempting to get loggedInUserID: "+ex.getMessage());
            Utils.createToast(this, "Invalid account login, please re-login to proceed", Toast.LENGTH_SHORT);
         //   return pageAlreadySaved;
        }

        //category = "" + Integer.valueOf(category) + 1;
        Log.d(TAG,"Found category"+selectedCategory);
        String categoryId = categoriesMap.get(selectedCategory);


        PageModel newpage=new PageModel();
        newpage.setName(pageName);
        newpage.setCategory(selectedCategory);
        newpage.setLikes("0");
        newpage.setComments("0");
        newpage.setBy("Me");
        newpage.setDescription(description);




        try {
            //here we send to server
        Log.d(TAG, "calling savePageToServer()");
            sendPageToXmpp(newpage);
            if(1==1)
            {
                ContentValues args = new ContentValues();

                Log.d(TAG, "Reading save page "+ pageName + ", category "+categoryId);
                args.put(DB.Page.SERVER_ID, 0);
                args.put(DB.Page.PROFILE_ID, profileId);
                args.put(DB.Page.MEDIA_BG_ID, 0);
                args.put(DB.Page.MEDIA_PHOTO_ID, 0);
                args.put(DB.Page.NAME, pageName);
                args.put(DB.Page.CATEGORY_ID, categoryId);
                args.put(DB.Page.PROFILE_ID, thisProfile);

                args.put(DB.Page.TEXT, description);

                DBProvider dbProvider = new DBProvider(this);
                Uri uri = dbProvider.insert(DB.Page.CONTENT_URI, args);
                pageId = uri.getLastPathSegment();
                Log.d(TAG, "Page created with pageID " + pageId);

                //savePage Images
              //  savePageMediaFiles(pageId);

            }
           // if(Integer.valueOf(pageId) > 0){
                toolbar.getMenu().clear();
                toolbar.setLogo(null);
                toolbar.setTitle(pageName);
                saveTextView.setVisibility(View.GONE);
                newPageMenu.setVisibility(View.VISIBLE);
                //createPageLayout.setVisibility(View.GONE);
                pageOverlayProfileName.setText(pageName);

                pageNameEditText.setFocusable(false);
                pageCategorySpinner.setFocusable(false);
                pageCategorySpinner.setEnabled(false);
                pageDescription.setFocusable(false);
                pageAlreadySaved = true;
               // Utils.createToast(this, saved?"Page created successfully":"Page will be saved later", Toast.LENGTH_SHORT);

           /* }else {
                pageAlreadySaved = false;
                Utils.createToast(this, "Error, could not create page", Toast.LENGTH_SHORT);
            }*/

            PageFragment.addpage(newpage);

          }catch (Exception ex){
            ex.printStackTrace();
        }

        return  pageAlreadySaved;

    }

    public void savePageMediaFiles(String pageId){
        Log.d(TAG, "Calling save page ID");
        int count = 1;
        saveProfileImage(pageId, null);
        saveBannerImage(pageId, null);

        for(Bitmap bmp: profileImages){
            savePageFilesImage(bmp, pageId, count);
            count++;

        }
        Log.d(TAG, "Create image pass returning");
    }

    public  void savePageFilesImage(Bitmap bmp, String pageId, int count){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String date = df.format(new Date());
        df = new SimpleDateFormat("yyyyMMddhhss");
        String date_ext = df.format(new Date());

        Log.d(TAG, "Found profile images " + profileImages.size());
        String fileName = "PAGE_"+date_ext+ "_"+count;
        pageFiles.put(count, fileName);
        Log.d(TAG, "Saving file page image : " + fileName);
        Utils.saveImage(this, bmp, fileName, "jpg");

        ContentValues args = new ContentValues();
        args.put(DB.Media.SERVER_ID, 0);
        args.put(DB.Media.MEDIA_TYPE, "PHOTO");
        args.put(DB.Media.CONTENT_ID, pageId);
        args.put(DB.MediaColumns.CONTENT_TYPE, "PAGE");
        args.put(DB.Media.URL, fileName);
        args.put(DB.Media.DESCRIPTION, "page media");
        args.put(DB.Media.DATE_CREATED, date);

        Uri uri = dbProvider.insert(DB.Media.CONTENT_URI, args);
        String mediaId = uri.getLastPathSegment();
        Log.d(TAG, "Image  inserted success ID " + mediaId);
    }


    public void saveProfileImage(String pageId, Bitmap profileImage){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String date = df.format(new Date());
        df = new SimpleDateFormat("yyyyMMddhhss");
        String date_ext = df.format(new Date());

        //Save banner image
        //Save banner image
        Log.d(TAG, "Create profile Image");
        if (profileImage == null) {
            profileImage = ((BitmapDrawable) pageProfileImage.getDrawable()).getBitmap();
        }
        profileImageName = "PAGE_PROFILE_"+date_ext+ "_0";
        Utils.saveImage(this, profileImage, profileImageName, "jpg");

        ContentValues args = new ContentValues();
        args.put(DB.Media.SERVER_ID, 0);
        args.put(DB.Media.MEDIA_TYPE, "PHOTO");
        args.put(DB.Media.CONTENT_ID, pageId);
        args.put(DB.MediaColumns.CONTENT_TYPE, "PAGE_PROFILE");
        args.put(DB.Media.URL, profileImageName);
        args.put(DB.Media.DESCRIPTION, "page media");
        args.put(DB.Media.DATE_CREATED, date);

        Uri uri = dbProvider.insert(DB.Media.CONTENT_URI, args);
        String mediaId = uri.getLastPathSegment();
        ContentValues updateValues = new ContentValues();
        updateValues.put(DB.Page.MEDIA_PHOTO_ID, mediaId);

        int update = dbProvider.update(DB.Page.CONTENT_URI, updateValues, DB.Page._ID +"=?",
                new String[]{pageId});

    }

    public void updateProfileImage(String pageId, Bitmap bitmap){
        pageProfileImage.setImageBitmap(bitmap);
        Utils.scaleImage(this, pageProfileImage);
        saveProfileImage(pageId, bitmap);
    }

    public void updateBannerImage(String pageId, Bitmap bitmap){
        Drawable bgDrawable = new BitmapDrawable(getResources(), bitmap);
        pageBannerLayout.setBackground(bgDrawable);
        saveBannerImage(pageId, bitmap);
    }

    public void updatePageImages(String pageId, Bitmap bitmap){
        profileImages.add(bitmap);
        ArrayList<Bitmap> addImageList = new ArrayList<Bitmap>();
        addImageList.add(bitmap);
        imageAdapter.update(addImageList);

        savePageFilesImage(bitmap, pageId, profileImages.size());
    }

    public void saveBannerImage(String pageId, Bitmap bannerImage){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String date = df.format(new Date());
        df = new SimpleDateFormat("yyyyMMddhhss");
        String date_ext = df.format(new Date());

        //Save banner image
        Log.d(TAG, "Create banner images");
        if(bannerImage == null) {
            bannerImage = ((BitmapDrawable) pageBannerLayout.getBackground()).getBitmap();
        }
        bannerImageName = "PAGE_BANNER"+date_ext+ "_0";
        Utils.saveImage(this, bannerImage, bannerImageName, "jpg");

        ContentValues args = new ContentValues();
        args.put(DB.Media.SERVER_ID, 0);
        args.put(DB.Media.MEDIA_TYPE, "PHOTO");
        args.put(DB.Media.CONTENT_ID, pageId);
        args.put(DB.MediaColumns.CONTENT_TYPE, "PAGE_BANNER");
        args.put(DB.Media.URL, bannerImageName);
        args.put(DB.Media.DESCRIPTION, "page media");
        args.put(DB.Media.DATE_CREATED, date);

        Uri uri = dbProvider.insert(DB.Media.CONTENT_URI, args);
        String mediaId = uri.getLastPathSegment();
        ContentValues updateValues = new ContentValues();
        updateValues.put(DB.Page.MEDIA_BG_ID, mediaId);

        int update = dbProvider.update(DB.Page.CONTENT_URI, updateValues, DB.Page._ID + "=?",
                new String[]{pageId});

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
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * Handle image video load response
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        Log.d(TAG, "Calling activity for result => " + resultCode);

        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                // successfully captured the image
                // launching upload activity
                //launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Utils.createToast(this,
                        "User cancelled image capture", Toast.LENGTH_SHORT);


            } else {
                // failed to capture image
                Utils.createToast(this,
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT);

            }

        } else if (requestCode == CAMERA_CAPTURE_VIDEO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // video successfully recorded
                // launching upload activity
                //launchUploadActivity(false);

            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled recording
                Utils.createToast(this,
                       "User cancelled video recording", Toast.LENGTH_SHORT);


            } else {
                // failed to record video
                Utils.createToast(this,
                        "Sorry! Failed to record video", Toast.LENGTH_SHORT);

            }
        }else if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.d(TAG, "Loading PICK_IMAGE_REQUEST after result:"+uri.toString());

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inScaled = false;

                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                        getContentResolver().openAssetFileDescriptor( uri, "r");

                Bitmap actuallyUsableBitmap
                        = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

                //Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                // Log.d(TAG, String.valueOf(bitmap));
                profileImages.add(actuallyUsableBitmap);
                //Do image save before share here

                imageAdapter= new ImageAdapter(this, profileImages);
               // imageHorizontalList.setAdapter(imageAdapter);
                //imageHorizontalList.setItemMargin(2);
                //imageHorizontalList.setOnItemClickListener(pageFilesClickLister);
                //Attempt to save page on imageUpload
                if(!pageAlreadySaved) {
                    pageAlreadySaved = saveNewPage();
                }else{
                    savePageFilesImage(actuallyUsableBitmap, pageId, profileImages.size());
                }
                Log.d(TAG, "Image loaded successfully");

            } catch (IOException e) {
                e.printStackTrace();
            }
        }else if( requestCode == VIEW_PAGE_PROFILE_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            // upload profile photo
            Log.d(TAG, "Found page image return call "+getIntent().getBooleanExtra("imageChanged", false));
            if(getIntent().getBooleanExtra("imageChanged", false)) {
                Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);

                updateProfileImage(pageId, bitMapImage);
            }

        }else if( requestCode == VIEW_PAGE_BANNER_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            // upload background photo
            Log.d(TAG, "Found page banner image return call "+getIntent().getBooleanExtra("imageChanged", false));
            if(getIntent().getBooleanExtra("imageChanged", false)) {
                Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);

                updateBannerImage(pageId, bitMapImage);
            }

        }else if( requestCode == VIEW_PAGE_FILES_PHOTO && resultCode == RESULT_OK && data != null && data.getData() != null){
            // upload page phot files
            Log.d(TAG, "Found page files image return call "+getIntent().getBooleanExtra("imageChanged", false));
            if(getIntent().getBooleanExtra("imageChanged", false)) {
                Bitmap bitMapImage = BitmapFactory.decodeByteArray(
                        getIntent().getByteArrayExtra("image"), 0, getIntent().getByteArrayExtra("image").length);

                updatePageImages(pageId, bitMapImage);
            }

        }

    }
    public void sendPageToXmpp(final PageModel newpage) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                Bundle data = new Bundle();

                data.putString("CLIENT_MESSAGE", "[\"FROM\":\"719182382\",\"text\":\"Are you there?\"]");

                String ret="ERR";
                try {
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    // nameValuePairs.add(new BasicNameValuePair("datecreated", Constants.SIMPLE_DATEONLY_FORMAT.format(new Date())));
                    nameValuePairs.add(new BasicNameValuePair("msisdn", KEApp.accountName));
                    nameValuePairs.add(new BasicNameValuePair("pagename", newpage.getName()));
                    nameValuePairs.add(new BasicNameValuePair("picture", ""));
                    nameValuePairs.add(new BasicNameValuePair("story", newpage.getDescription()));
                    nameValuePairs.add(new BasicNameValuePair("category", newpage.getCategory()));
                    Log.d("SERVER: ","calling the server");
                    try {
                        JSONArray newpageresp = SyncAdapter.POST(nameValuePairs, "WebService/pages/newpage", ACCEPT.JSON, "");
                        JSONObject pageresp = newpageresp.getJSONObject(0);

                        try {
                            if (pageresp.has("status")) {

                            } else {
                                data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error creating  getting likes, Server unreachable");

                            }


                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                    catch(Exception e)
                    {
                        ret="AN ERROR OCCURRED "+e.getMessage();
                        Log.d("SEND ACTIVITY", "Exception: " + e);
                        e.printStackTrace();

                    }


                } catch (Exception e) {
                    ret="AN ERROR OCCURRED "+e.getMessage();
                    Log.d("SEND ACTIVITY", "Exception: " + e);
                    e.printStackTrace();
                }
                return ret;
            }

            @Override
            protected void onPostExecute(String result) {
                sendTask = null;
                if(result.equalsIgnoreCase("OK")) {

                    //Save The message to Local Database

                }
                else
                {

                /*Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();*/

                }


            }

        };
        sendTask.execute(null, null, null);

    }




}
