package com.rube.tt.keapp;

import android.content.ContentValues;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rube.tt.keapp.adapters.ImageAdapter;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.utils.TouchImageView;
import com.rube.tt.keapp.utils.Utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by rube on 16/10/15.
 */
public class BigImageViewActivity extends ActionBarActivity{
    private final String TAG = Group.class.getSimpleName();
    private Toolbar toolbar;
    TouchImageView bigDisplayImage;
    static String position = "0";
    Bitmap bitMapImage = null;
    Bitmap newBitMapImage = null;
    private String fileName = null;
    private static int PICK_IMAGE_REQUEST = 1;
    private TouchImageView largeImageDisplay;
    private boolean imageChanged  = false;
    private String parentId = "0";
    private TextView saveImageButton;
    private TextView cancelSaveImageButton;
    private String parentType = "";
    private String parentDescription="";
    private String parentClass = "";
    private boolean showEdit = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.large_image_display);

        largeImageDisplay = (TouchImageView)findViewById(R.id.large_image_view);
        Log.d(TAG, "Loading image big image activity");
        Bundle extras = getIntent().getExtras();
        Log.d(TAG, "Found intent extaras :"+extras.toString());

        byte[] byteImage = getIntent().getByteArrayExtra("image");

        if(byteImage != null) {
            bitMapImage = BitmapFactory.decodeByteArray(
                    byteImage, 0, byteImage.length);
        }
        String title = extras.getString("title");

        if(extras.containsKey("position")){
            position = extras.getString("position");
        }
        if(extras.containsKey("fileName")){
            fileName = extras.getString("fileName");
        }
        if(extras.containsKey("parentId")){
            parentId = extras.getString("parentId");
        }
        if(extras.containsKey("parentType")){
            parentType = extras.getString("parentType");
        }

        if(extras.containsKey("parentDescription")){
            parentDescription = extras.getString("parentDescription");
        }

        if(extras.containsKey("parentClass")){
            parentClass = extras.getString("parentClass");
        }

        if(extras.containsKey("showEdit")){
            showEdit = extras.getBoolean("showEdit");
        }

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(title);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

        bigDisplayImage = (TouchImageView)findViewById(R.id.large_image_view);
        saveImageButton = (TextView)findViewById(R.id.page_upload_save_button);
        cancelSaveImageButton = (TextView)findViewById(R.id.page_upload_cancel_button);
        saveImageButton.setOnClickListener( saveImageButtonOnclickLister);
        cancelSaveImageButton.setOnClickListener( cancelSaveImageButtonOnclickLister);
        Log.d(TAG, "Loading bitmap image next ...");
        bigDisplayImage.setImageBitmap(bitMapImage );

    }

    public View.OnClickListener saveImageButtonOnclickLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            saveImage(parentId, 0);
        }
    };

    public View.OnClickListener cancelSaveImageButtonOnclickLister = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            cancelSaveImage();
        }
    };

    public void cancelSaveImage(){
        bigDisplayImage.setImageBitmap(bitMapImage );
        saveImageButton.setVisibility(View.GONE);
        cancelSaveImageButton.setVisibility(View.GONE);
    }



    public  void saveImage(String parentId, int count){
        Log.d(TAG, "Loading onBack pressed event");

        ByteArrayOutputStream bs = new ByteArrayOutputStream();
        newBitMapImage.compress(Bitmap.CompressFormat.PNG, 100, bs);

        DBProvider dbProvider = new DBProvider(this);
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String date = df.format(new Date());
        df = new SimpleDateFormat("yyyyMMddhhss");
        String date_ext = df.format(new Date());

        String fileName = parentType+"_"+date_ext+ "_"+count;

        Log.d(TAG, "Saving file page image : " + fileName);
        Utils.saveImage(this, newBitMapImage, fileName, "jpg");

        ContentValues args = new ContentValues();
        args.put(DB.Media.SERVER_ID, 0);
        args.put(DB.Media.MEDIA_TYPE, "PHOTO");
        args.put(DB.Media.CONTENT_ID, parentId);
        args.put(DB.MediaColumns.CONTENT_TYPE, parentType);
        args.put(DB.Media.URL, fileName);
        args.put(DB.Media.DESCRIPTION, parentDescription);
        args.put(DB.Media.DATE_CREATED, date);

        Uri uri = dbProvider.insert(DB.Media.CONTENT_URI, args);
        String mediaId = uri.getLastPathSegment();
        Log.d(TAG, "Image  inserted success ID " + mediaId);

        ContentValues updateValues = new ContentValues();
        if(parentType.equals("PAGE_PROFILE")){
            Log.d(TAG, "Found parent type page profile calling update page mediaID");
            updateValues.put(DB.Page.MEDIA_PHOTO_ID, mediaId);

            int update = dbProvider.update(DB.Page.CONTENT_URI, updateValues, DB.Page._ID +"=?",
                    new String[]{parentId});
            Log.d(TAG, "Page update results "+update);
        }else if(parentType.equals("PAGE_BANNER")){
            Log.d(TAG, "Faound parent type PAGE BG");
            updateValues.put(DB.Page.MEDIA_BG_ID, mediaId);

            int update = dbProvider.update(DB.Page.CONTENT_URI, updateValues, DB.Page._ID +"=?",
                    new String[]{parentId});
            Log.d(TAG, "Page banner update results "+update);

        }
        Log.d(TAG, "Image save success proceeding to launch intent");

        Utils.createToast(this, "Image save success", Toast.LENGTH_SHORT);

        Intent data;
        try{
            data = new Intent(this, Class.forName(parentClass));
        }catch (ClassNotFoundException cnfe){
            Log.d(TAG, "Failed to find class :"+parentClass);
            cnfe.printStackTrace();
            data = new Intent();

        }
        data.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        data.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if(imageChanged) {
            data.putExtra("position", position);
            data.putExtra("fileName", fileName);
            data.putExtra("parentId", parentId);
            data.putExtra("imageChanged", imageChanged);
        }
        Log.d(TAG, "Logging start change image activity");
        this.startActivity(data);
        this.finish();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(!showEdit){
            getMenuInflater().inflate(R.menu.image_menu_share_only, menu);
        }else {
            getMenuInflater().inflate(R.menu.image_menu, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            case R.id.image_view_change_menu:
                uploadImage();
                break;
            case R.id.image_view_share_menu:
                initShareIntent(fileName);
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    private void initShareIntent(String fileName)
    {
        Intent shareIntent = new Intent();
        shareIntent.setAction(Intent.ACTION_SEND);
        shareIntent.putExtra(Intent.EXTRA_STREAM, Utils.getImageUri(fileName));
        shareIntent.setType("image/*");
        shareIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        startActivity(Intent.createChooser(shareIntent, "share"));
    }

    private void uploadImage(){
        Intent intent = new Intent();
        // Show only images, no videos or anything else
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        // Always show the chooser (if there are multiple options available)
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * Handle image video load response
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
         if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri uri = data.getData();
            Log.d(TAG, "Loading PICK_IMAGE_REQUEST after result:" + uri.toString());

            try {
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 4;
                options.inScaled = false;

                AssetFileDescriptor fileDescriptor =null;
                fileDescriptor =
                        getContentResolver().openAssetFileDescriptor(uri, "r");

                newBitMapImage
                        = BitmapFactory.decodeFileDescriptor(
                        fileDescriptor.getFileDescriptor(), null, options);

                largeImageDisplay.setImageBitmap(newBitMapImage);
                largeImageDisplay.buildDrawingCache();
                //Attempt to save page on imageUpload
                //update image changed
                imageChanged = true;
                saveImageButton.setVisibility(View.VISIBLE);
                cancelSaveImageButton.setVisibility(View.VISIBLE);
                Log.d(TAG, "Image loaded successfully");
                //onBackPressed();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
