package com.rube.tt.keapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.nostra13.universalimageloader.cache.memory.impl.WeakMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;
import com.nostra13.universalimageloader.core.display.FadeInBitmapDisplayer;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;

/**
 * Created by rube on 5/19/15.
 */
public class RubeImageLoader {

    private Context context;

    public RubeImageLoader(Context context){
        this.context = context;
        // UNIVERSAL IMAGE LOADER SETUP
        DisplayImageOptions defaultOptions = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .imageScaleType(ImageScaleType.EXACTLY)
                .displayer(new FadeInBitmapDisplayer(300)).build();

        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
                this.context)
                .defaultDisplayImageOptions(defaultOptions)
                .memoryCache(new WeakMemoryCache()).build();

        ImageLoader.getInstance().init(config);
    }

    public void loadImage(String url, ImageView imageView, Drawable fallback){

        ImageLoader imageLoader = ImageLoader.getInstance();
        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true)
                .resetViewBeforeLoading(true)
                .showImageForEmptyUri(fallback)
                .showImageOnFail(fallback)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .cacheOnDisk(true)
                .showImageOnLoading(fallback).build();

        //download and display image from url
        imageLoader.displayImage(url, imageView, options);
    }

    public void loadImageFormDevice(int contentType, int mediaId, ImageView imageView, Drawable fallback){
         String filePath = getImageLocation(contentType, mediaId);
         this.loadImage(filePath, imageView, fallback);
    }

    /**
     *
     * @param contentType -  from constants file defining type of content
     * @param mediaId - sqlite ID for contentType defines location of image
     */

    public String getImageLocation(int contentType, int mediaId){


        Cursor cursor = new DBProvider(this.context).query(DB.Media.CONTENT_URI,
                new String[]{DB.Media.URL, DB.Media.DATE_CREATED}, DB.Media._ID+ " = ? and "+
                        DB.MediaColumns.CONTENT_TYPE + "= ? and "+DB.Media.MEDIA_TYPE + "= ?",
                new String[]{String.valueOf(mediaId), String.valueOf(contentType),
                        String.valueOf(Constants.MEDIA_TYPE_PHOTO)}, null);

        //Media files are stored under /data/keapp/content_type/media_type/~url.png
        String url = null;

        if(cursor.moveToNext()){
            url= cursor.getString(cursor.getColumnIndex(DB.Media.URL));

        }else{

            return url;
        }

        return "file://"+ Constants.DEFAULT_DATA_PATH +"/"+ contentType+ "/"+Constants.MEDIA_TYPE_PHOTO+"/"+url;

    }
}
