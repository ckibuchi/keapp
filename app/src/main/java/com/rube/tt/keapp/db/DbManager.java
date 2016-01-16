package com.rube.tt.keapp.db;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.ContactsContract;
import android.util.Log;

import com.rube.tt.keapp.model.Status;
import com.rube.tt.keapp.model.UserType;
import com.rube.tt.keapp.models.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * Created by rube on 5/9/15.
 */
public class DbManager extends SQLiteOpenHelper {

    private static String TAG = DbManager.class.getSimpleName();
    private ContentResolver contentResolver;
    private Context context;

    public DbManager(Context context){
        super(context, DB.DATABASE_NAME, null, DB.DATABASE_VERSION);
        this.context = context;
        this.contentResolver = this.context.getContentResolver();

    }

    public ContentResolver getContentResolver(){

        return this.contentResolver;
    }

    public Cursor getPhoneContactsCursor(String filter, int offset, int limit, Uri uri){

        String[] PROJECTION = new String[] { ContactsContract.RawContacts._ID,
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts.PHOTO_ID,
                ContactsContract.CommonDataKinds.Phone.DATA,
                ContactsContract.CommonDataKinds.Email.DATA,
                ContactsContract.CommonDataKinds.Photo.CONTACT_ID };

        String order = ContactsContract.Contacts.DISPLAY_NAME
                + ", "
                + ContactsContract.CommonDataKinds.Email.DATA
                + " COLLATE NOCASE";

        if (filter==null){filter = ContactsContract.CommonDataKinds.Phone.DATA + " NOT LIKE ''";}

        String limitString = " limit "+offset+", "+limit+" ";

        if(uri != null){
                return this.getContentResolver().query(uri, PROJECTION, filter, null, order + limitString);
        }

        return this.getContentResolver().query(
               ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, filter, null, order + limitString);
    }


    public String getLastServerID(String table){

        SQLiteDatabase sqldb = getWritableDatabase();
        String query = " SELECT server_id  FROM "+table+"  where server_id "
                + " > 0 order by id desc limit 1";

        Cursor cursor = sqldb.rawQuery(query, null);

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String serverId = cursor.getString(cursor.getColumnIndex("server_id"));
                Log.d(TAG, "Returning date created " + serverId);
                return serverId;
            }
            cursor.close();
        }


        Log.d(TAG, "Returning date created null .." + query + " ..." + table);
        return null;

    }

    public String getDuplicateItem(String table, String serverId){

        SQLiteDatabase sqldb = getWritableDatabase();
        String query = " SELECT server_id  FROM "+table+"  where server_id "
                + " = ? order by id desc limit 1";

        Cursor cursor = sqldb.rawQuery(query, new String[]{serverId});

        if (cursor != null && cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                String duplicateId = cursor.getString(cursor.getColumnIndex("server_id"));
                Log.d(TAG, "Returning duplacate server ID  " + duplicateId);
                return duplicateId;
            }
            cursor.close();
        }


        Log.d(TAG, "Returning date created null .." + query + " ..." + table);
        return null;

    }
    public  ArrayList<ChatMessage> getChats(String msisdn){
        ArrayList<ChatMessage> listMessages=new ArrayList<ChatMessage>();
        try {

            SQLiteDatabase sqldb = getWritableDatabase();
            String query = " SELECT *  FROM " + DB.Chat.TABLE + "  where " + DB.Chat.TO + " "
                    + " = ? OR "+DB.Chat.FROM+" = ? order by " + DB.Chat._ID + " ASC";
            Log.d("GETTING CHATS: ",query);
            Cursor cursor = sqldb.rawQuery(query, new String[]{msisdn,msisdn});


            if (cursor != null && cursor.moveToFirst()) {
                Log.i("CHAT COUNT",""+cursor.getCount());
                Log.i("CHAT DATA",""+cursor.toString());
                do {
                    Log.i("USER TYPE ","FRom DB "+cursor.getString(cursor.getColumnIndex(DB.Chat.USER_TYPE)));
                    Log.i("USER TYPE ","Then "+UserType.valueOf(cursor.getString(cursor.getColumnIndex(DB.Chat.USER_TYPE))));
                    Log.i("DATE CREATED ",cursor.getString(cursor.getColumnIndex(DB.Chat.DATE_CREATED)));
                    ChatMessage chatMessage = new ChatMessage();
                    chatMessage.setMessage(cursor.getString(cursor.getColumnIndex(DB.Chat.MESSAGE)));
                    chatMessage.setFrom(cursor.getString(cursor.getColumnIndex(DB.Chat.FROM)));
                    chatMessage.setMessageStatus(Status.valueOf(cursor.getString(cursor.getColumnIndex(DB.Chat.STATUS))));
                    chatMessage.setUserType(UserType.valueOf(cursor.getString(cursor.getColumnIndex(DB.Chat.USER_TYPE))));
                    chatMessage.setMessageTime(new Date(cursor.getString(cursor.getColumnIndex(DB.Chat.DATE_CREATED))));
                    listMessages.add(chatMessage);

                } while (cursor.moveToNext());

                cursor.close();
            }

        }//End of try
        catch(Exception e)
        {
            Log.d("SQL ERR ",e.getMessage());

        }

        return listMessages;

    }


    public ArrayList<HashMap<String, String>> getUnsyncedItems(String table){
        SQLiteDatabase sqldb = getWritableDatabase();
        String query = " SELECT *  FROM "+table+"  where server_id "
                + " = 0 order by id desc limit 20 ";

        Cursor cursor = sqldb.rawQuery(query, null);
        ArrayList<HashMap<String, String>> records = new ArrayList<HashMap<String, String>> ();

        if (cursor != null ) {
            cursor.moveToFirst();
            int columns = cursor.getColumnCount();

            HashMap<String, String>  record = new HashMap<String, String>();
            do  {
                for(int i=0; i< columns; i++) {
                    record.put(cursor.getColumnName(i), cursor.getString(i));
                }
                records.add(record);
            }while(cursor.moveToNext());
            cursor.close();
        }


        Log.d(TAG, "Returning date created null .." + query + " ..." + table);
        return records;

    }

    public void updateItemAsPosted(String tableName, String serverId, String itemId){
        Log.d(TAG, "Updating item as posted to the server");
        SQLiteDatabase sqldb = getWritableDatabase();
        ContentValues args = new ContentValues();
        args.put("server_id", serverId);

        sqldb.update(tableName, args, "id=?", new String[]{itemId});

    }




    public void updateContactAsInvited(String phone){
        Log.d(TAG, "Updating conatct as invited to the server:" + phone);
        SQLiteDatabase sqldb = getWritableDatabase();

        String query = " SELECT "+DB.Profile._ID+"  FROM "+DB.Profile.TABLE+"  where  "+DB.Profile.MSISDN
                + " = '"+phone+"'";

        Cursor cursor = sqldb.rawQuery(query, null);
        String profileId = "";
        if (cursor != null ) {
            cursor.moveToFirst();
            profileId = cursor.getString(0);
            cursor.close();
        }
        ContentValues args = new ContentValues();
        args.put(DB.Contact.IS_MEMBER, 2);

        sqldb.update(DB.Contact.TABLE, args, DB.Contact.PROFILE + "=?", new String[]{profileId});
        Log.d(TAG, "Profile update success :" + phone + " profile:" + profileId);

    }
    public void updateContactAsMember(String _id){
        Log.d(TAG, "Updating conatct as member to the server:" + _id);
        SQLiteDatabase sqldb = getWritableDatabase();

        String query = " SELECT "+DB.Contact._ID+"  FROM "+DB.Contact.TABLE+"  where  "+DB.Contact._ID
                + " = "+_id+"";
        Log.i(TAG,query);
        Cursor cursor = sqldb.rawQuery(query, null);
        String profileId = "";
        if (cursor != null ) {
            cursor.moveToFirst();
            profileId = cursor.getString(0);
            cursor.close();
        }
        ContentValues args = new ContentValues();
        args.put(DB.Contact.IS_MEMBER, 1);

        sqldb.update(DB.Contact.TABLE, args, DB.Contact._ID + "=?", new String[]{_id});
        Log.d(TAG, "Contact update success :" + _id + " profile:" + profileId);

    }
    /*
    * (non-Javadoc)
    * @see
    * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
    * .SQLiteDatabase)
    */
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        db.execSQL(DB.Chat.CREATE_STATEMENT);
        //db.execSQL(DB.Status.CREATE_STATEMENT);
        db.execSQL(DB.ProfileStatus.CREATE_STATEMENT);
        db.execSQL(DB.Contact.CREATE_STATEMENT);
        db.execSQL(DB.Profile.CREATE_STATEMENT);

        db.execSQL(DB.PageCategory.CREATE_STATEMENT);

        //manually create page categories
        createPageCategories(db);
        //db.execSQL(DB.MediaType.CREATE_STATEMENT);
        //create media Types
        //createMediaTypes(db);

        //create media Types
        //createContentTypes(db);


        db.execSQL(DB.Media.CREATE_STATEMENT);
        db.execSQL(DB.Page.CREATE_STATEMENT);
        db.execSQL(DB.Story.CREATE_STATEMENT);

        db.execSQL(DB.Comment.CREATE_STATEMENT);
        //db.execSQL(DB.ContentType.CREATE_STATEMENT);
        db.execSQL(DB.Share.CREATE_STATEMENT);
        db.execSQL(DB.Like.CREATE_STATEMENT);

        db.execSQL(DB.KeyValue.CREATE_STATEMENT);

    }

    public void createPageCategories(  SQLiteDatabase sqldb){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String  date = df.format(new Date());
        Log.d(TAG, "Creating categories FOOTBAL");
        insertPageCategory(sqldb, "FOOTBAL", "FOOTBALL", date);
        Log.d(TAG, "Creating categories GAMES");
        insertPageCategory(sqldb, "GAMES", "GAMES", date);
        Log.d(TAG, "Creating categories POLITICS");
        insertPageCategory(sqldb, "POLITICS", "POLITICS", date);


    }

    public void createMediaTypes(  SQLiteDatabase sqldb){

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd hh:ss");
        String  date = df.format(new Date());
        Log.d(TAG, "Creating media type PAGE");
        insertMediaType(sqldb, "PAGE", "PAGE MEDIA", date);
        Log.d(TAG, "Creating media STORY");
        insertMediaType(sqldb, "STORY", "STORY MEDIA", date);
        Log.d(TAG, "Creating media PROFILE");
        insertMediaType(sqldb, "PROFILE", "PROFILE MEDIA", date);

        Log.d(TAG, "Creating media CHAT");
        insertMediaType(sqldb, "CHAT", "PROFILE CHAT", date);
        Log.d(TAG, "Creating media PAGE BG");
        insertMediaType(sqldb, "PAGE BG", "PAGE BG", date);

        Log.d(TAG, "Creating media PAGE PROFILE");
        insertMediaType(sqldb, "PAGE PROFILE", "PAGE PROFILE", date);



    }


    /**
     * Will update version 1 through 5 to version 8
     *
     * @see android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite.SQLiteDatabase,
     *      int, int)
     *
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int current, int targetVersion)
    {
        Log.i(TAG, "Just relax will think about upgrade later :)");
    }

    public void vacuum()
    {
        new Thread()
        {
            @Override
            public void run()
            {
                SQLiteDatabase sqldb = getWritableDatabase();
                sqldb.execSQL("VACUUM");
            }
        }.start();

    }

    public long insertChat(int fromId,  int toId, String text, String status, String date,String  user_tpe){

        if (fromId < 0 || toId < 0)
        {
            throw new IllegalArgumentException("Chat message must have to and from profile IDS.");
        }

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Chat.DATE_CREATED, date);
        args.put(DB.Chat.MESSAGE, text);
        args.put(DB.Chat.FROM, fromId);
        args.put(DB.Chat.TO, toId);
        args.put(DB.Chat.STATUS, status);
        args.put(DB.Chat.USER_TYPE, user_tpe);

        long chatInsertId = sqldb.insert(DB.Chat.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Chat.CONTENT_URI, null);

        return chatInsertId;


    }

    public long insertStatus(String name,  String description, String date){

        if (name == null || name.equals(""))
        {
            throw new IllegalArgumentException("Status must have name.");
        }

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Status.DATE_CREATED, date);
        args.put(DB.Status.NAME, name);
        args.put(DB.Status.DESCRIPTION, description);

        long statusInsertId = sqldb.insert(DB.Status.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Status.CONTENT_URI, null);

        return statusInsertId;


    }

    public long insertProfileStatus(int profileId, int status, String text, String date){

        if (profileId < 1 || text == null)
        {
            throw new IllegalArgumentException("Profile status must have to and from profile ID and text.");
        }

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.ProfileStatus.SERVER_ID, "0");
        args.put(DB.ProfileStatus.DATE_CREATED, date);
        args.put(DB.ProfileStatus.PROFILE, profileId);
        args.put(DB.ProfileStatus.MESSAGE, text);
        args.put(DB.ProfileStatus.ACTIVE, status);

        Log.d(TAG, "DBManger.insert into profile: " + args.toString());

        long profileStatusInsertId = sqldb.insert(DB.ProfileStatus.TABLE, null, args);
        Log.d(TAG, "DB indert returned : "+profileStatusInsertId);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.ProfileStatus.CONTENT_URI, null);
        Log.d(TAG, "Insert into profile status success, returning ID " + profileStatusInsertId);
        return profileStatusInsertId;
    }
    public long insertProfile(String name, String msisdn, String email, String username,
                              String photo, String date) {

        if ( name == null) {
            throw new IllegalArgumentException("Profile must have name.");
        }
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Profile.DATE_CREATED, date);
        args.put(DB.Profile.NAME, name);
        args.put(DB.Profile.MSISDN, msisdn);
        args.put(DB.Profile.USERNAME, username);
        args.put(DB.Profile.EMAIL, email);
        args.put(DB.Profile.PHOTO, photo);

        long profileInsertId = sqldb.insert(DB.Profile.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Profile.CONTENT_URI, null);
        Log.d(TAG, "Insert into profile success, returning ID " + profileInsertId);
        return profileInsertId;
    }


    public long insertContact(int profileId, int statusId, String date,int member) {

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Contact.DATE_CREATED, date);
        args.put(DB.Contact.PROFILE, profileId);
        args.put(DB.Contact.STATUS, statusId);
        args.put(DB.Contact.IS_MEMBER,member);

        long contactInsertId = sqldb.insert(DB.Contact.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Contact.CONTENT_URI, null);

        return contactInsertId;
    }

    public long insertPageCategory(  SQLiteDatabase sqldb, String name, String description, String date){

        if(sqldb == null){
            sqldb = getWritableDatabase();
        }

        ContentValues args = new ContentValues();
        args.put(DB.PageCategory.DATE_CREATED, date);
        args.put(DB.PageCategory.NAME, name);
        args.put(DB.PageCategory.DESCRIPTION, description);

        long pageCategoryInsertId = sqldb.insert(DB.PageCategory.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.PageCategory.CONTENT_URI, null);

        return pageCategoryInsertId;
    }

    public long insertMediaType(SQLiteDatabase sqldb, String name, String description, String date){
        if(sqldb == null) {
            sqldb = getWritableDatabase();
        }

        ContentValues args = new ContentValues();
        args.put(DB.MediaType.DATE_CREATED, date);
        args.put(DB.MediaType.NAME, name);
        args.put(DB.MediaType.DESCRIPTION, description);

        long mediaTypeId = sqldb.insert(DB.MediaType.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.MediaType.CONTENT_URI, null);

        return mediaTypeId;
    }


    public long  insertMedia(String url,int contentId, String contentType, String mediaTypeId,
                             String description, String date){

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Media.DATE_CREATED, date);
        args.put(DB.Media.URL, url);
        args.put(DB.Media.CONTENT_ID, contentId);
        args.put(DB.MediaColumns.CONTENT_TYPE, contentType);
        args.put(DB.Media.MEDIA_TYPE, mediaTypeId);
        args.put(DB.Media.DESCRIPTION, description);

        long mediaId = sqldb.insert(DB.Media.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Media.CONTENT_URI, null);

        return mediaId;
    }

    public long insertPage(int categoryId, int bgPhotoId, int profilePhotoId,
                           String name, int profileId, String text, String date){
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Page.DATE_CREATED, date);
        args.put(DB.Page.CATEGORY_ID, categoryId);
        args.put(DB.Page.MEDIA_BG_ID, bgPhotoId);
        args.put(DB.Page.MEDIA_PHOTO_ID, profilePhotoId);
        args.put(DB.Page.PROFILE_ID, profileId);
        args.put(DB.Page.NAME, name);
        args.put(DB.Page.TEXT, text);

        long pageId = sqldb.insert(DB.Page.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Page.CONTENT_URI, null);

        return pageId;

    }

    public long insertStory(String name, int profileId, int photoId, String text, String pageId, String date){

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Story.DATE_CREATED, date);
        args.put(DB.Story.NAME, name);
        args.put(DB.Story.PROFILE_ID, profileId);
        args.put(DB.Story.MEDIA_PHOTO_ID, photoId);
        args.put(DB.Story.TEXT, text);
        args.put(DB.Story.PAGE_ID, pageId);


        long storyId = sqldb.insert(DB.Story.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Story.CONTENT_URI, null);

        return storyId;
    }

    public  long insertComment(int contentId,int contentType, int profileId,
                                int mediaId, String text, String date){

        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Comment.DATE_CREATED, date);
        args.put(DB.Comment.CONTENT_ID, contentId);
        args.put(DB.CommentColumns.CONTENT_TYPE, contentType);
        args.put(DB.Comment.PROFILE_ID, profileId);
        args.put(DB.Comment.MEDIA_ID, mediaId);
        args.put(DB.Comment.TEXT, text);


        long commentId = sqldb.insert(DB.Story.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Comment.CONTENT_URI, null);

        return commentId;


    }

    public long insertContentType(String name, String description, String date){
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.ContentType.DATE_CREATED, date);
        args.put(DB.ContentType.NAME, name);
        args.put(DB.ContentType.DESCRIPTION, description);

        long contentTypeId = sqldb.insert(DB.ContentType.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.ContentType.CONTENT_URI, null);

        return contentTypeId;

    }

    public long insertShare(int contentId, int contentType, int profileId, String date){
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Share.DATE_CREATED, date);
        args.put(DB.Share.CONTENT_ID, contentId);
        args.put(DB.ShareColumns.CONTENT_TYPE, contentType);
        args.put(DB.Share.PROFILE_ID, profileId);

        long shareId = sqldb.insert(DB.Share.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Share.CONTENT_URI, null);

        return shareId;

    }

    public long insertLike(int contentId, int contentType, int profileId, String date){
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.Like.DATE_CREATED, date);
        args.put(DB.Like.CONTENT_ID, contentId);
        args.put(DB.LikeColumns.CONTENT_TYPE, contentType);
        args.put(DB.Like.PROFILE_ID, profileId);

        long likeId = sqldb.insert(DB.Like.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Like.CONTENT_URI, null);

        return likeId;

    }

    public long insertKeyValue(String key, String value, String date){
        SQLiteDatabase sqldb = getWritableDatabase();

        ContentValues args = new ContentValues();
        args.put(DB.KeyValue.DATE_CREATED, date);
        args.put(DB.KeyValue.KEY, key);
        args.put(DB.KeyValue.VALUE, value);

        long keyValueId = sqldb.insert(DB.KeyValue.TABLE, null, args);

        ContentResolver resolver = this.context.getContentResolver();
        resolver.notifyChange(DB.Like.CONTENT_URI, null);

        return keyValueId;

    }

    public int delete(String table, String column, long value){
        SQLiteDatabase sqldb = getWritableDatabase();
        int affected = sqldb.delete(table, column + "= ?", new String[] { String.valueOf(value) });
        return affected;

    }
}

