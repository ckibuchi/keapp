package com.rube.tt.keapp.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.rube.tt.keapp.R;
import com.rube.tt.keapp.db.DbManager;
import com.rube.tt.keapp.models.ContactModel;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Created by rube on 10/09/15.
 */
public class Utils {

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;
    private static final int CAMERA_CAPTURE_VIDEO_REQUEST_CODE = 200;

    public static String TAG = Utils.class.getSimpleName();
    private Uri fileUri; // file url to store image/video

    public static void animateView(final View view, final int toVisibility, float toAlpha, int duration) {
        boolean show = toVisibility == android.view.View.VISIBLE;
        if (show) {
            view.setAlpha(0);
        }
        view.setVisibility(android.view.View.VISIBLE);
        view.animate()
                .setDuration(duration)
                .alpha(show ? toAlpha : 0)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        view.setVisibility(toVisibility);
                    }
                });
    }

    public static ArrayList<ContactModel> getPhoneContactDetails(Context context, String filter, int offset, int limit, Uri uri) {
        ArrayList<ContactModel> simpleContactModels = new ArrayList<ContactModel>();

        ContactModel simpleContactModel = null;

        DbManager dbManager =  new DbManager(context);
        ContentResolver cr = dbManager.getContentResolver();
        Cursor cursor = null;
        try {
            cursor = dbManager.getPhoneContactsCursor(filter, offset, limit, uri);
                int count=0;
            if (cursor.moveToFirst()) {
                do {
                 /*   if(count>10)
                    {
                        break;
                    }
                    count+=1;*/
                    // names comes in hand sometimes
                    String phone=cursor.getString(3);
                    phone=phone.replace("-", "");
                    phone=Utils.getNineDigits(phone);
                    if(phone!=null) {
                        Long photoId = cursor.getLong(4);
                        simpleContactModel = new ContactModel();
                        simpleContactModel.setName(cursor.getString(1));
                        simpleContactModel.setId(cursor.getInt(0));

                        simpleContactModel.setPhoneNumber(cursor.getString(3));
                        simpleContactModel.setPic(getContactPhoto(context, photoId));
                        simpleContactModels.add(simpleContactModel);
                    }

                } while (cursor.moveToNext());
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }finally {
            if(cursor != null){
                cursor.close();
            }
        }


        return simpleContactModels;
    }
        public static String getNineDigits(String phone)
        {
            try{
                return phone.trim().substring(phone.trim().length() - 9, phone.trim().length());

            }
            catch(Exception e)
            {
                return null;
            }

        }
    public static Bitmap getContactPhoto(Context context, long contactID) {

        Bitmap photo = null;
        InputStream inputStream = null;

        Log.d(TAG, "calling getContactPhoto ");

        try {
            inputStream = ContactsContract.Contacts.openContactPhotoInputStream(context.getContentResolver(),
                    ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, new Long(contactID)));


            if (inputStream != null) {
                photo = BitmapFactory.decodeStream(inputStream);
                Log.d(TAG, "Initial photo load " + photo.toString());
                if(photo == null){
                    Log.d(TAG, "Null photo returning default photo");
                    photo =  null;
                }
                return photo;
            }else{
                Log.d(TAG, "Null input stream returning default photo");
                return null;

            }
        } catch (Exception e) {
            Log.d(TAG, "Error fetching contact photo "+e.getMessage());
            e.printStackTrace();
        }finally {
            try{
                if(inputStream != null) {
                    inputStream.close();
                }
            }catch (IOException ex){
                Log.d(TAG, "Error fetching contact photo on inputstream close " + ex.getMessage());

            };
        }
        return null;

    }

    public static ArrayList<ContactModel>   search(Context context, String query){
        String filter = ContactsContract.Contacts.DISPLAY_NAME + " LIKE '%"+query+"%'";

        ArrayList<ContactModel> simpleContactModels = getPhoneContactDetails(context,
                filter, 0, 0, null);
        return simpleContactModels;

    }

    public static String saveImage(Context context, Bitmap b,String name,String extension){
        ContextWrapper cw = new ContextWrapper(context);
        name=name+"."+extension;
        FileOutputStream out;
        if(b==null){
            Log.d(TAG, "Saving defualt image for profile: "+name);
            b = BitmapFactory.decodeResource(context.getResources(),
                        R.drawable.default_profile_pic);
        }

        try {
            // External sdcard location
            File mediaStorageDir = new File(
                    Environment
                            .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                    Constants.DEFAULT_DATA_PATH);

            // Create the storage directory if it does not exist
            if (!mediaStorageDir.exists()) {
                if (!mediaStorageDir.mkdirs()) {
                    Log.d(TAG, "Oops! Failed create "
                            + Constants.DEFAULT_DATA_PATH + " directory");
                    return null;
                }
            }


            //File directory = cw.getDir(Constants.DEFAULT_DATA_PATH, Context.MODE_PRIVATE);

            File filePath=new File(mediaStorageDir.getPath() + File.separator+name);
            out =  new FileOutputStream(filePath);

            b.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return name;
    }

    public static Uri  getImageUri(String fileName){
        fileName = fileName+".jpg";
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.DEFAULT_DATA_PATH);

        File file=new File(mediaStorageDir.getPath() + File.separator+fileName);

        return  Uri.fromFile(file);

    }

    public static Bitmap getImageBitmap(Context context,String name,
                                        String type, int width, int height){

        Log.d(TAG, "GetImageBitMap ...name: "+ name+ " type :"+type);

        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.DEFAULT_DATA_PATH);



        if(name == null || name.equals("")){
            if(type != null){
                if(type.equals("profile")){
                    Log.d(TAG, "Called get default image");
                    return getDefaultProfileImage(context, width, height);
                }
            }
        }

        try{
            File filePath=new File(mediaStorageDir.getPath() + File.separator+name);
            return shrinkBitmapFromFile(filePath.getAbsolutePath(), width, height);
        }
        catch(Exception e){

        }
        return null;
    }

    static Bitmap getDefaultProfileImage(Context context, int width, int height){
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        try {
            return shrinkBitmapFromResource(context, width, height);
        }catch (Exception ex){
            ex.printStackTrace();
            return null;
        }
    }


    static Bitmap shrinkBitmapFromFile(String file, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeFile(file, bmpFactoryOptions);
        return bitmap;
    }

    static Bitmap shrinkBitmapFromResource(Context context, int width, int height){

        BitmapFactory.Options bmpFactoryOptions = new BitmapFactory.Options();
        bmpFactoryOptions.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile_pic, bmpFactoryOptions);

        int heightRatio = (int)Math.ceil(bmpFactoryOptions.outHeight/(float)height);
        int widthRatio = (int)Math.ceil(bmpFactoryOptions.outWidth/(float)width);

        if (heightRatio > 1 || widthRatio > 1)
        {
            if (heightRatio > widthRatio)
            {
                bmpFactoryOptions.inSampleSize = heightRatio;
            } else {
                bmpFactoryOptions.inSampleSize = widthRatio;
            }
        }

        bmpFactoryOptions.inJustDecodeBounds = false;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.default_profile_pic, bmpFactoryOptions);
        return bitmap;
    }

    public  static void createToast(Activity activity, String message, int length){
        LayoutInflater inflater = activity.getLayoutInflater();
        View layout = inflater.inflate(R.layout.custom_toast,
                (ViewGroup) activity.findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.toast_view_text);
        text.setText(message);

        Toast toast = new Toast(activity.getApplicationContext());
        toast.setGravity(Gravity.CENTER_VERTICAL, 0, 0);
        toast.setDuration(length);
        toast.setView(layout);
        toast.show();
    }

    // Checking camera availability
    public static boolean  deviceSupportsCamera(Activity activity) {
        if (activity.getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    public static void captureImage(Activity activity, Uri fileUri) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);



        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);

        // start the image capture Intent
        activity.startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Launching camera app to record video
     */
    public static void recordVideo(Activity activity, Uri fileUri) {
        Intent intent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);

        //Uri fileUri = getOutputMediaFileUri(MEDIA_TYPE_VIDEO);

        // set video quality
        intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);

        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file
        // name

        // start the video capture Intent
        activity.startActivityForResult(intent, CAMERA_CAPTURE_VIDEO_REQUEST_CODE);
    }

    /**
     * Creating file uri to store image/video
     */
    public static  Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Constants.DEFAULT_DATA_PATH);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Constants.DEFAULT_DATA_PATH + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "PAGE_IMG_" + timeStamp + ".jpg");
        } else if (type == MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "PAGE_VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }


    public static void scaleImage(Context context, ImageView view)
    {
        Drawable drawing = view.getDrawable();
        if (drawing == null) {
            return;
        }
        Bitmap bitmap = ((BitmapDrawable)drawing).getBitmap();

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        int bounding_x = ((View)view.getParent()).getWidth();//EXPECTED WIDTH
        int bounding_y = ((View)view.getParent()).getHeight();//EXPECTED HEIGHT

        float xScale = ((float) bounding_x) / width;
        float yScale = ((float) bounding_y) / height;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        width = scaledBitmap.getWidth();
        height = scaledBitmap.getHeight();
        BitmapDrawable result = new BitmapDrawable(context.getResources(), scaledBitmap);

        view.setImageDrawable(result);

        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.width = width;
        params.height = height;
        view.setLayoutParams(params);
    }

    public static Drawable scaleImage(Context context, Bitmap bitmap, int height, int width)
    {

        int imageWidth = bitmap.getWidth();
        int imageHeight = bitmap.getHeight();
        int bounding_x = width; //EXPECTED WIDTH
        int bounding_y = height; //EXPECTED HEIGHT

        float xScale = ((float) bounding_x) / imageWidth;
        float yScale = ((float) bounding_y) / imageHeight;

        Matrix matrix = new Matrix();
        matrix.postScale(xScale, yScale);

        Bitmap scaledBitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        Drawable result = new BitmapDrawable(context.getResources(), scaledBitmap);

        return result;


    }
}


