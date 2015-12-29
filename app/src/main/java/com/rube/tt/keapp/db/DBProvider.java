package com.rube.tt.keapp.db;

import android.content.ContentProvider;
import android.content.ContentProviderClient;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;

/**
 * Created by rube on 5/16/15.
 */
public class DBProvider  extends ContentProvider{

    private static String TAG = DBProvider.class.getSimpleName();
    private static UriMatcher sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH );
    private Context context;

    /* Action types as numbers for using the UriMatcher */
    private static final int CHAT            = 1;
    private static final int STATUS          = 2;
    private static final int PROFILE_STATUS  = 3;
    private static final int PROFILE         = 4;
    private static final int CONTACT         = 5;
    private static final int PAGE_CATEGORY   = 6;
    private static final int MEDIA_TYPE      = 7;
    private static final int MEDIA           = 8;
    private static final int PAGE            = 9;
    private static final int STORY           = 10;
    private static final int COMMENT         = 11;
    private static final int CONTENT_TYPE    = 12;
    private static final int SHARE           = 13;
    private static final int LIKE            = 14;

    private static final int CHAT_ID            = 15;
    private static final int STATUS_ID          = 16;
    private static final int PROFILE_STATUS_ID  = 17;
    private static final int PROFILE_ID         = 18;
    private static final int CONTACT_ID         = 19;
    private static final int PAGE_CATEGORY_ID   = 20;
    private static final int MEDIA_TYPE_ID      = 21;
    private static final int MEDIA_ID           = 22;
    private static final int PAGE_ID            = 23;
    private static final int STORY_ID           = 24;
    private static final int COMMENT_ID         = 25;
    private static final int CONTENT_TYPE_ID    = 26;
    private static final int SHARE_ID           = 27;
    private static final int LIKE_ID            = 28;

    private static final int KEY_VALUE           = 29;
    private static final int KEY_VALUE_ID            = 30;


    static {
        DBProvider.sURIMatcher = new UriMatcher( UriMatcher.NO_MATCH );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "chat", DBProvider.CHAT );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "chat/#", DBProvider.CHAT_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "status", DBProvider.STATUS );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "status/#", DBProvider.STATUS_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "profile_status", DBProvider.PROFILE_STATUS );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "profile_status/#", DBProvider.PROFILE_STATUS_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "profile", DBProvider.PROFILE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "profile/#", DBProvider.PROFILE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "contact", DBProvider.CONTACT );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "contact/#", DBProvider.CONTACT_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "page/category", DBProvider.PAGE_CATEGORY );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "page/category/#", DBProvider.PAGE_CATEGORY_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "media/type", DBProvider.MEDIA_TYPE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "media/type/#", DBProvider.MEDIA_TYPE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "media", DBProvider.MEDIA );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "media/#", DBProvider.MEDIA_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "page", DBProvider.PAGE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "page/#", DBProvider.PAGE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "story", DBProvider.STORY );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "story/#", DBProvider.STORY_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "comment", DBProvider.COMMENT );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "comment/#", DBProvider.COMMENT_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "content/type", DBProvider.CONTENT_TYPE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "content/type/#", DBProvider.CONTENT_TYPE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "share", DBProvider.SHARE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "share/#", DBProvider.SHARE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "like", DBProvider.LIKE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "like/#", DBProvider.LIKE_ID );

        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "key_value", DBProvider.KEY_VALUE );
        DBProvider.sURIMatcher.addURI( DB.AUTHORITY, "key_value/#", DBProvider.KEY_VALUE_ID );


    };

    private DbManager dbManager;

    //Cooking force on create on instantiation
    public DBProvider(Context context){
        if(this.dbManager == null){
            this.dbManager = new DbManager(context);
        }
        this.context = context;
    }
    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#onCreate()
     */
    @Override
    public boolean onCreate()
    {

        if (this.dbManager == null)
        {
            this.dbManager = new DbManager( getContext() );
        }
        return true;
    }

    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#getType(android.net.Uri)
     */
    @Override
    public String getType( Uri uri ) {
        int match = DBProvider.sURIMatcher.match(uri);
        String mime = null;
        switch (match) {
            case CHAT:
                mime = DB.Chat.CONTENT_TYPE;
                break;
            case CHAT_ID:
                mime = DB.Chat.CONTENT_ITEM_TYPE;
                break;
            case STATUS:
                mime = DB.Status.CONTENT_TYPE;
                break;
            case STATUS_ID:
                mime = DB.Status.CONTENT_ITEM_TYPE;
                break;
            case PROFILE_STATUS:
                mime = DB.ProfileStatus.CONTENT_TYPE;
                break;
            case PROFILE_STATUS_ID:
                mime = DB.ProfileStatus.CONTENT_ITEM_TYPE;
                break;

            case PROFILE:
                mime = DB.Profile.CONTENT_TYPE;
                break;
            case PROFILE_ID:
                mime = DB.Profile.CONTENT_ITEM_TYPE;
                break;

            case CONTACT:
                mime = DB.Contact.CONTENT_TYPE;
                break;
            case CONTACT_ID:
                mime = DB.Contact.CONTENT_ITEM_TYPE;
                break;

            case PAGE_CATEGORY:
                mime = DB.PageCategory.CONTENT_TYPE;
                break;
            case PAGE_CATEGORY_ID:
                mime = DB.PageCategory.CONTENT_ITEM_TYPE;
                break;

            case MEDIA_TYPE:
                mime = DB.MediaType.CONTENT_TYPE;
                break;
            case MEDIA_TYPE_ID:
                mime = DB.MediaType.CONTENT_ITEM_TYPE;
                break;

            case MEDIA:
                mime = DB.Media.CONTENT_TYPE;
                break;
            case MEDIA_ID:
                mime = DB.Media.CONTENT_ITEM_TYPE;
                break;

            case PAGE:
                mime = DB.Page.CONTENT_TYPE;
                break;
            case PAGE_ID:
                mime = DB.Page.CONTENT_ITEM_TYPE;
                break;

            case STORY:
                mime = DB.Story.CONTENT_TYPE;
                break;
            case STORY_ID:
                mime = DB.Story.CONTENT_ITEM_TYPE;
                break;

            case COMMENT:
                mime = DB.Comment.CONTENT_TYPE;
                break;
            case COMMENT_ID:
                mime = DB.Comment.CONTENT_ITEM_TYPE;
                break;

            case CONTENT_TYPE:
                mime = DB.ContentType.CONTENT_TYPE;
                break;
            case CONTENT_TYPE_ID:
                mime = DB.ContentType.CONTENT_ITEM_TYPE;
                break;

            case SHARE:
                mime = DB.Share.CONTENT_TYPE;
                break;
            case SHARE_ID:
                mime = DB.Share.CONTENT_ITEM_TYPE;
                break;

            case LIKE:
                mime = DB.Like.CONTENT_TYPE;
                break;
            case LIKE_ID:
                mime = DB.Like.CONTENT_ITEM_TYPE;
                break;
            case UriMatcher.NO_MATCH:
            default:
                Log.w(TAG, "There is not MIME type defined for URI " + uri);
                break;
        }
        return  mime;


    }

    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
     */
    @Override
    public Uri insert( Uri uri, ContentValues values )
    {
        Log.d( TAG, "insert on "+uri );
        Uri insertedUri = null;
        int match = DBProvider.sURIMatcher.match( uri );
        switch (match){
            case CHAT:
                insertedUri = this.insertChat(uri, values);
                break;

            case STATUS:
                insertedUri = this.insertStatus(uri, values);
                break;

            case PROFILE_STATUS:
                Log.d(TAG, "Calling insert profile status");
                insertedUri = this.insertProfileStatus(uri, values);
                break;

            case PROFILE:
                insertedUri = this.insertProfile(uri, values);
                break;

            case CONTACT:
                insertedUri = this.insertContact(uri, values);
                break;

            case PAGE_CATEGORY:
                insertedUri = this.insertPageCategory(uri, values);
                break;

            case MEDIA_TYPE:
                insertedUri = this.insertMediaType(uri, values);
                break;

            case MEDIA:
                insertedUri = this.insertMedia(uri, values);
                break;

            case PAGE:
                insertedUri = this.insertPage(uri, values);
                break;

            case STORY:
                insertedUri = this.insertStory(uri, values);
                break;

            case COMMENT:
                insertedUri = this.insertComment(uri, values);
                break;

            case CONTENT_TYPE:
                insertedUri = this.insertContentType(uri, values);
                break;


            case SHARE:
                insertedUri = this.insertShare(uri, values);
                break;

            case LIKE:
                insertedUri = this.insertLike(uri, values);
                break;
            case KEY_VALUE:
                insertedUri = this.insertKeyValue(uri, values);
                break;

            default:
                Log.e(TAG, "Unable to match the insert URI: " + uri.toString() );
                insertedUri =  null;
                break;
        }
        return insertedUri;

    }

    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
     */
    @Override
    public int update( Uri uri, ContentValues contentValues, String selection, String[] selectionArgs )
    {
        //Handle update later here

        Log.d( TAG, "insert on "+uri );
        int update = -1;
        SQLiteDatabase sqlDB = this.dbManager.getWritableDatabase();
        int match = DBProvider.sURIMatcher.match( uri );
        switch (match){
            case CHAT:
                update = sqlDB.update(DB.Chat.TABLE, contentValues,selection, selectionArgs );
                break;

            case STATUS:
                update = sqlDB.update(DB.Chat.STATUS, contentValues, selection, selectionArgs);
                break;

            case PROFILE_STATUS:

                update = sqlDB.update(DB.ProfileStatus.TABLE, contentValues, selection, selectionArgs);
                break;

            case PROFILE:
                update = sqlDB.update(DB.Profile.TABLE, contentValues, selection, selectionArgs);
                break;

            case CONTACT:
                update = sqlDB.update(DB.Contact.TABLE, contentValues, selection, selectionArgs);
                break;

            case PAGE_CATEGORY:
                update = sqlDB.update(DB.PageCategory.TABLE, contentValues, selection, selectionArgs);
                break;

            case MEDIA_TYPE:
                update = sqlDB.update(DB.MediaType.TABLE, contentValues, selection, selectionArgs);
                break;

            case MEDIA:
                update = sqlDB.update(DB.Media.TABLE, contentValues, selection, selectionArgs);
                break;

            case PAGE:
                update = sqlDB.update(DB.Page.TABLE, contentValues, selection, selectionArgs);
                break;

            case STORY:
                update = sqlDB.update(DB.Story.TABLE, contentValues, selection, selectionArgs);
                break;

            case COMMENT:
                update = sqlDB.update(DB.Comment.TABLE, contentValues, selection, selectionArgs);
                break;

            case CONTENT_TYPE:
                update = sqlDB.update(DB.ContentType.TABLE, contentValues, selection, selectionArgs);
                break;


            case SHARE:
                update = sqlDB.update(DB.Share.TABLE, contentValues, selection, selectionArgs);
                break;

            case LIKE:
                update = sqlDB.update(DB.Like.TABLE, contentValues, selection, selectionArgs);
                break;
            case KEY_VALUE:
                update = sqlDB.update(DB.KeyValue.TABLE, contentValues, selection, selectionArgs);
                break;

            default:
                Log.e(TAG, "Unable to match the insert URI: " + uri.toString() );
                break;
        }
        return update;

    }

    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
     */
    @Override
    public Cursor query( Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder ) {
        //      Log.d( TAG, "Query on Uri:"+uri );
        int match = DBProvider.sURIMatcher.match( uri );
        //FIX ME FOR URL MATCHES
        SQLiteQueryBuilder queryBuilder = new SQLiteQueryBuilder();
        String tableName = null;
        String innerSelection = "1";
        String[] innerSelectionArgs = new String[]{};
        String sortorder = sortOrder;

        switch (match) {
            case CHAT:
                tableName = DB.Chat.TABLE+ " inner join " +DB.Profile.TABLE + " on " +
                        DB.Profile.TABLE + "." + DB.Profile._ID + "="+ DB.Chat.TABLE + "." + DB.Chat.FROM;
                break;

            case STATUS:
                tableName = DB.Status.TABLE;
                break;

            case PROFILE_STATUS:
                tableName = DB.ProfileStatus.TABLE;
                break;

            case PROFILE:
                tableName = DB.Profile.TABLE;
                break;

            case CONTACT:
                tableName = DB.Contact.TABLE;
                break;

            case PAGE_CATEGORY:
                tableName = DB.PageCategory.TABLE;
                break;

            case MEDIA_TYPE:
                tableName = DB.MediaType.TABLE;
                break;

            case MEDIA:
                tableName = DB.Media.TABLE;
                break;

            case PAGE:
                tableName = DB.Page.TABLE;
                break;

            case STORY:
                tableName = DB.Story.TABLE;
                break;

            case COMMENT:
                tableName = DB.Comment.TABLE;
                break;

            case CONTENT_TYPE:
                tableName = DB.ContentType.TABLE;
                break;

            case SHARE:
                tableName = DB.Share.TABLE;
                break;

            case LIKE:
                tableName = DB.Like.TABLE;
                break;

            case KEY_VALUE:
                tableName = DB.KeyValue.TABLE;
                break;

            default:
                Log.e(DBProvider.TAG, "Unable to come to an action in the query uri: " + uri.toString());
                return null;
        }

        // Set the table we're querying.
        queryBuilder.setTables( tableName );

        if( selection == null )
        {
            selection = innerSelection;
        }
        else
        {
            selection = "( "+ innerSelection + " ) and " + selection;
        }
        LinkedList<String> allArgs = new LinkedList<String>();
        if( selectionArgs == null )
        {
            allArgs.addAll(Arrays.asList(innerSelectionArgs));
        }
        else
        {
            allArgs.addAll(Arrays.asList(innerSelectionArgs));
            allArgs.addAll(Arrays.asList(selectionArgs));
        }
        selectionArgs = allArgs.toArray(innerSelectionArgs);

        // Make the query.
        SQLiteDatabase mDb = this.dbManager.getWritableDatabase();

        Cursor c = queryBuilder.query( mDb, projection, selection, selectionArgs, null, null, sortorder  );
        c.setNotificationUri( context.getContentResolver(), uri );
        return c;

    }

    /**
     * (non-Javadoc)
     * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
     */
    @Override
    public int delete( Uri uri, String selection, String[] selectionArgs ) {
        int match = DBProvider.sURIMatcher.match(uri);

        int affected = 0;
        switch( match )
        {
            case CHAT_ID:
                affected = dbManager.delete(DB.Chat.TABLE, DB.Chat._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;
            case STATUS_ID:
                affected = dbManager.delete(DB.Status.TABLE, DB.Status._ID,
                            new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case PROFILE_STATUS_ID:
                affected = dbManager.delete(DB.ProfileStatus.TABLE, DB.ProfileStatus._ID,
                            new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case PROFILE_ID:
                affected = dbManager.delete(DB.Profile.TABLE, DB.Profile._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case CONTACT_ID:
                affected = dbManager.delete(DB.Contact.TABLE, DB.Contact._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case PAGE_CATEGORY_ID:
                affected = dbManager.delete(DB.PageCategory.TABLE, DB.PageCategory._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case MEDIA_TYPE_ID:
                affected = dbManager.delete(DB.MediaType.TABLE, DB.MediaType._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case MEDIA_ID:
                affected = dbManager.delete(DB.Media.TABLE, DB.Media._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case PAGE_ID:
                affected = dbManager.delete(DB.Page.TABLE, DB.Page._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case STORY_ID:
                affected = dbManager.delete(DB.Story.TABLE, DB.Story._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case COMMENT_ID:
                affected = dbManager.delete(DB.Comment.TABLE, DB.Comment._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case CONTENT_TYPE_ID:
                affected = dbManager.delete(DB.ContentType.TABLE, DB.ContentType._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;


            case SHARE_ID:
                affected = dbManager.delete(DB.Share.TABLE, DB.Share._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;

            case LIKE_ID:
                affected = dbManager.delete(DB.Like.TABLE, DB.Like._ID,
                        new Long( uri.getLastPathSegment() ).longValue() );
                break;
            default:
                affected = 0;
                break;
        }
        return affected;

    }

    private Uri insertChat(Uri uri, ContentValues values){
        int  fromId = values.getAsInteger(DB.Chat.FROM);
        int  toId = values.getAsInteger(DB.Chat.TO);
        String text = values.getAsString(DB.Chat.MESSAGE);
        int status = values.getAsInteger(DB.Chat.STATUS);
        String date = values.getAsString(DB.Chat.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long chatId = this.dbManager.insertChat(fromId, toId, text, status, date);
        Log.d( TAG, "Have inserted to chat with id "+chatId );
        return ContentUris.withAppendedId(uri, chatId);

    }

    private Uri insertStatus(Uri uri, ContentValues values){
        String   name = values.getAsString(DB.Status.NAME);
        String  description = values.getAsString(DB.Status.DESCRIPTION);
        String date = values.getAsString(DB.Chat.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long statusId = this.dbManager.insertStatus(name, description, date);
        Log.d( TAG, "Have inserted to status with id "+statusId );
        return ContentUris.withAppendedId(uri, statusId);

    }

    private Uri insertProfileStatus(Uri uri, ContentValues values){
        Log.d( TAG, "insert into profile status ()" );
        int   status = values.getAsInteger(DB.ProfileStatus.ACTIVE);
        String  text = values.getAsString(DB.ProfileStatus.MESSAGE);
        int  profileId = values.getAsInteger(DB.ProfileStatus.PROFILE);
        String date = null; //values.getAsString(DB.Chat.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }

        Log.d(TAG, "Params: profileId:" +profileId+ " status "+status +" text :"+text +" date:"+date);
        long profileStatusId = this.dbManager.insertProfileStatus(profileId, status, text, date);
        Log.d( TAG, "Have inserted to profile_status with id "+profileStatusId );
        return ContentUris.withAppendedId(uri, profileStatusId);


    }

    private Uri insertProfile(Uri uri, ContentValues values){
        String   name = values.getAsString(DB.Profile.NAME);
        String  photo = values.getAsString(DB.Profile.PHOTO);
        String  msisdn = values.getAsString(DB.Profile.MSISDN);
        String  username = values.getAsString(DB.Profile.USERNAME);
        String  email = values.getAsString(DB.Profile.EMAIL);
        String date = values.getAsString(DB.Chat.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());
        }
        long profileId = this.dbManager.insertProfile(name, msisdn, email, username, photo, date);
        Log.d( TAG, "Have inserted to profile with id "+profileId );
        return ContentUris.withAppendedId(uri, profileId);
    }


    private Uri insertContact(Uri uri, ContentValues values){
        int  profileId = values.getAsInteger(DB.Contact.PROFILE);
        int  statusId = values.getAsInteger(DB.Contact.STATUS);
        String date = values.getAsString(DB.Contact.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());
        }
        long contactId = this.dbManager.insertContact(profileId, statusId, date);
        Log.d( TAG, "Have inserted to contact with id "+contactId );
        return ContentUris.withAppendedId(uri, contactId);


    }

    private Uri insertPageCategory(Uri uri, ContentValues values){
        String  name = values.getAsString(DB.PageCategory.NAME);
        String  description = values.getAsString(DB.PageCategory.DESCRIPTION);
        String date = values.getAsString(DB.PageCategory.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long pageCategoryId = this.dbManager.insertPageCategory(null, name, description, date);
        Log.d( TAG, "Have inserted to page category with id "+pageCategoryId );
        return ContentUris.withAppendedId(uri, pageCategoryId);


    }

    private Uri insertMediaType(Uri uri, ContentValues values){
        String  name = values.getAsString(DB.MediaType.NAME);
        String  description = values.getAsString(DB.MediaType.DESCRIPTION);
        String date = values.getAsString(DB.MediaType.DATE_CREATED);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long mediaTypeId = this.dbManager.insertMediaType(null, name, description, date);
        Log.d( TAG, "Have inserted to media  type with id "+mediaTypeId );
        return ContentUris.withAppendedId(uri, mediaTypeId);

    }

    private Uri insertMedia(Uri uri, ContentValues values){
        int  contentId = values.getAsInteger(DB.Media.CONTENT_ID);
        String contentType = values.getAsString(DB.MediaColumns.CONTENT_TYPE);
        String  url = values.getAsString(DB.Media.URL);
        String date = values.getAsString(DB.Media.DATE_CREATED);
        String mediaTypeId = values.getAsString(DB.Media.MEDIA_TYPE);
        String description = values.getAsString(DB.Media.DESCRIPTION);

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());
        }
        long mediaId = this.dbManager.insertMedia(url, contentId, contentType, mediaTypeId, description, date);
        Log.d( TAG, "Have inserted to media with id "+mediaId );
        return ContentUris.withAppendedId(uri, mediaId);

    }

    private Uri insertPage(Uri uri, ContentValues values){
        int  categoryId = values.getAsInteger(DB.Page.CATEGORY_ID);
        int  bgPhotoId = values.getAsInteger(DB.Page.MEDIA_BG_ID);
        int  profilePhotoId = values.getAsInteger(DB.Page.MEDIA_PHOTO_ID);
        String name = values.getAsString(DB.Page.NAME);
        int profileId = values.getAsInteger(DB.Page.PROFILE_ID);
        String text = values.getAsString(DB.Page.TEXT);
        String date = values.getAsString(DB.Page.DATE_CREATED);


        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long pageId = this.dbManager.insertPage(categoryId, bgPhotoId, profilePhotoId, name, profileId, text, date);
        Log.d( TAG, "Have inserted to page with id "+pageId );
        return ContentUris.withAppendedId(uri, pageId);

    }

    private Uri insertStory(Uri uri, ContentValues values){

        int  photoId = 0;
        if(values.containsKey(DB.Story.MEDIA_PHOTO_ID)){
            photoId = values.getAsInteger(DB.Story.MEDIA_PHOTO_ID);
        }
        int  profileId = 0;
        if(values.containsKey(DB.Story.PROFILE_ID)){
            profileId =  values.getAsInteger(DB.Story.PROFILE_ID);
        }

        String  text = values.getAsString(DB.Story.TEXT);
        String name = values.getAsString(DB.Story.NAME);
        String pageId = values.getAsString(DB.Story.PAGE_ID);
        String date = null;

        if(values.containsKey(DB.Story.DATE_CREATED)){
            date =   values.getAsString(DB.Page.DATE_CREATED);
        }

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }

        long storyId = this.dbManager.insertStory(name, profileId, photoId, text, pageId, date);
        Log.d( TAG, "Have inserted to story with id "+storyId );
        return ContentUris.withAppendedId(uri, storyId);

    }

    private Uri insertComment(Uri uri, ContentValues values){
        int  contentId = values.getAsInteger(DB.Comment.CONTENT_ID);
        int  contentType = values.getAsInteger(DB.CommentColumns.CONTENT_TYPE);
        int  profileId = values.getAsInteger(DB.Comment.PROFILE_ID);
        int mediaId = values.getAsInteger(DB.Comment.MEDIA_ID);
        String text = values.getAsString(DB.Comment.TEXT);
        String date = values.getAsString(DB.Comment.DATE_CREATED);


        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long commentId = this.dbManager.insertComment(contentId, contentType, profileId,
                mediaId, text, date);
        Log.d( TAG, "Have inserted to story with id "+commentId );
        return ContentUris.withAppendedId(uri, commentId);

    }

    private Uri insertContentType(Uri uri, ContentValues values){
        String  name = values.getAsString(DB.ContentType.NAME);
        String  description = values.getAsString(DB.ContentType.DESCRIPTION);
        String date = values.getAsString(DB.Comment.DATE_CREATED);


        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long contentTypeId = this.dbManager.insertContentType(name, description, date);
        Log.d( TAG, "Have inserted to story with id "+contentTypeId );
        return ContentUris.withAppendedId(uri, contentTypeId);


    }

    private Uri insertShare(Uri uri, ContentValues values){
        int  contentId = values.getAsInteger(DB.Share.CONTENT_ID);
        int  contentType = values.getAsInteger(DB.ShareColumns.CONTENT_TYPE);
        int  profileId = values.getAsInteger(DB.Share.PROFILE_ID);
        String date = values.getAsString(DB.Share.DATE_CREATED);


        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long shareId = this.dbManager.insertShare(contentId, contentType, profileId, date);
        Log.d( TAG, "Have inserted to share with id "+shareId );
        return ContentUris.withAppendedId(uri, shareId);


    }

    private Uri insertLike(Uri uri, ContentValues values){
        int  contentId = values.getAsInteger(DB.Like.CONTENT_ID);
        int  contentType = values.getAsInteger(DB.LikeColumns.CONTENT_TYPE);
        int  profileId = values.getAsInteger(DB.Like.PROFILE_ID);
        String date = values.getAsString(DB.Like.DATE_CREATED);


        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long likeId = this.dbManager.insertLike(contentId, contentType, profileId, date);
        Log.d( TAG, "Have inserted to like with id "+likeId );
        return ContentUris.withAppendedId(uri, likeId);


    }

    private Uri insertKeyValue(Uri uri, ContentValues values){
        String  key = values.getAsString(DB.KeyValue.KEY);
        String  value = values.getAsString(DB.KeyValue.VALUE);

        String date = values.getAsString(DB.Like.DATE_CREATED);
        Log.d(TAG, "InsertKey Value wint data: "+value.toString());

        if(date == null){
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
            date = df.format(new Date());

        }
        long keyValueId = this.dbManager.insertKeyValue(key, value, date);
        Log.d( TAG, "Have inserted to like with id "+keyValueId );
        return ContentUris.withAppendedId(uri, keyValueId);
    }

    public Cursor rawQuery(Uri url, String sql, String[] args){
        SQLiteDatabase dbHandle= this.dbManager.getWritableDatabase();
        Cursor cursor = dbHandle.rawQuery(sql , args);

        return cursor;

    }
}
