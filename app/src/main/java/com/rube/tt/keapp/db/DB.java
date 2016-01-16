package com.rube.tt.keapp.db;

import android.net.Uri;

/**
 * Created by rube on 5/16/15.
 * Stores all static db information about keapp
 */
public final class DB {

    /** The authority of this provider: com.rube.tt.keapp */
    public static final String AUTHORITY = "com.rube.tt.keapp";
    /** The content:// style Uri for this provider, content://com.rube.tt.keapp */
    public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY );
    /** The name of the database file */
    static final String DATABASE_NAME = "KEAPP.db";
    /** The version of the database schema */
    static final int DATABASE_VERSION = 01;

        /**
     * This table contains chats.
     *
     * @author rube
     */
    public static final class Chat extends ChatColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single chat. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.chat";

        /** The MIME type of CONTENT_URI providing a directory of chats. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.chat";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/chats */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Chat.TABLE );

        /** The name of this table */
        public static final String TABLE = "chat";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Chat.TABLE + "(" + " " + Chat._ID + " " + Chat._ID_TYPE +
                        "," + " " + Chat.FROM         + " " + Chat.FROM_TYPE +
                        "," + " " + Chat.USER_TYPE         + " " + Chat.USER_TYPE_FORMAT +
                        "," + " " + Chat.SERVER_ID         + " " + Chat.SERVER_ID_TYPE +
                        "," + " " + Chat.messageID         + " " + Chat.messageID_TYPE +
                        "," + " " + Chat.TO           + " " + Chat.TO_TYPE +
                        "," + " " + Chat.MESSAGE      + " " + Chat.MESSAGE_TYPE +
                        "," + " " + Chat.MEDIA        + " " + Chat.MEDIA_TYPE +
                        "," + " " + Chat.STATUS       + " " + Chat.STATUS_TYPE +
                        "," + " " + Chat.DATE_CREATED + " " + Chat.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the chats table.
     *
     * @author rube
     */
    public static class ChatColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID     = "server_id";
        public static final String USER_TYPE          = "user_type";
        public static final String FROM          = "from_id";
         public static final String TO            = "to_id";
        public static final String MESSAGE       = "message";
        public static final String STATUS        = "status_id";
        public static final String MEDIA         = "media_id";
        public static final String messageID         = "messageid";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String FROM_TYPE           = "TEXT NOT NULL";
        static final String TO_TYPE             = "TEXT NOT NULL";
        static final String USER_TYPE_FORMAT            = "TEXT NOT NULL";
        static final String MESSAGE_TYPE        = "TEXT NULL";
        static final String STATUS_TYPE         = "TEXT NOT NULL";
        static final String MEDIA_TYPE          = "INTEGER  NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static  final  String SERVER_ID_TYPE     = "INTEGER NULL DEFAULT 0";
        static  final  String messageID_TYPE     = "TEXT NULL DEFAULT 0";

    }


    /**
     * This table contains chats.
     *
     * @author rube
     */
    public static final class Status extends StatusColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single status. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.status";

        /** The MIME type of CONTENT_URI providing a directory of statuses. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.status";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/status */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Status.TABLE );

        /** The name of this table */
        public static final String TABLE = "status";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Status.TABLE + "(" + " " + Status._ID           + " " + Status._ID_TYPE +
                        "," + " " + Status.SERVER_ID         + " " + Status.SERVER_ID_TYPE +
                        "," + " " + Status.NAME         + " " + Status.NAME_TYPE +
                        "," + " " + Status.DESCRIPTION  + " " + Status.DESCRIPTION_TYPE +
                        "," + " " + Status.DATE_CREATED + " " + Status.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the status table.
     *
     * @author rube
     */
    public static class StatusColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID     = "server_id";
        public static final String NAME          = "name";
        public static final String DESCRIPTION   = "description";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String NAME_TYPE           = "VARCHAR(10) NOT NULL";
        static final String DESCRIPTION_TYPE    = "VARCHAR(45) NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


    /**
     * This table contains profile_status.
     *
     * @author rube
     */
    public static final class ProfileStatus extends ProfileStatusColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single profile status. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.profile_status";

        /** The MIME type of CONTENT_URI providing a directory of profile statuses. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.profile_status";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/profile_status */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + ProfileStatus.TABLE );

        /** The name of this table */
        public static final String TABLE = "profile_status";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + ProfileStatus.TABLE + "(" + " " + ProfileStatus._ID           + " " + ProfileStatus._ID_TYPE +
                        "," + " " + ProfileStatus.SERVER_ID         + " " + ProfileStatus.SERVER_ID_TYPE +
                        "," + " " + ProfileStatus.PROFILE      + " " + ProfileStatus.PROFILE_TYPE +
                        "," + " " + ProfileStatus.MESSAGE      + " " + ProfileStatus.MESSAGE_TYPE +
                        "," + " " + ProfileStatus.ACTIVE       + " " + ProfileStatus.ACTIVE_TYPE +
                        "," + " " + ProfileStatus.DATE_CREATED + " " + ProfileStatus.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the profile_status table.
     *
     * @author rube
     */
    public static class ProfileStatusColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String PROFILE       = "profile_id";
        public static final String MESSAGE       = "message";
        public static final String ACTIVE        = "active";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_TYPE        = "INTEGER NOT NULL";
        static final String MESSAGE_TYPE        = "VARCHAR(45) NOT NULL";
        static final String ACTIVE_TYPE         = "INTEGER NOT NULL DEFAULT 1";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";

    }


    /**
     * This table contains profile.
     *
     * @author rube
     */
    public static final class Profile extends ProfileColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single profile. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.profile";

        /** The MIME type of CONTENT_URI providing a directory of profiles. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.profile";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/profiles */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Profile.TABLE );

        /** The name of this table */
        public static final String TABLE = "profile";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Profile.TABLE + "(" + " " + Profile._ID           + " " + Profile._ID_TYPE +
                        "," + " " + Profile.SERVER_ID         + " " + Profile.SERVER_ID_TYPE +
                        "," + " " + Profile.NAME         + " " + Profile.NAME_TYPE +
                        "," + " " + Profile.USERNAME         + " " + Profile.USERNAME_TYPE +
                        "," + " " + Profile.MSISDN       + " " + Profile.MSISDN_TYPE +
                        "," + " " + Profile.EMAIL        + " " + Profile.EMAIL_TYPE +
                        "," + " " + Profile.PHOTO        + " " + Profile.PHOTO_TYPE +
                        "," + " " + Profile.DATE_CREATED + " " + Profile.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the profile table.
     *
     * @author rube
     */
    public static class ProfileColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String NAME          = "name";
        public static final String MSISDN        = "msisdn";
        public static final String EMAIL         = "email";
        public static final String PHOTO         = "photo_id";
        public static final String USERNAME         = "username";


        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String NAME_TYPE           = "VARCHAR(45) NOT NULL";
        static final String MSISDN_TYPE         = "VARCHAR(15) NULL";
        static final String USERNAME_TYPE         = "VARCHAR(200) NULL";
        static final String EMAIL_TYPE          = "VARCHAR(200) NULL";
        static final String PHOTO_TYPE          = "INTEGER NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";


    }


    /**
     * This table contains profile.
     *
     * @author rube
     */
    public static final class Contact extends ContactColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single contact. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.contact";

        /** The MIME type of CONTENT_URI providing a directory of contacts. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.contact";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/contacts */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Contact.TABLE );

        /** The name of this table */
        public static final String TABLE = "contact";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Contact.TABLE + "(" + " " + Contact._ID           + " " + Contact._ID_TYPE +
                        "," + " " + Contact.SERVER_ID         + " " + Contact.SERVER_ID_TYPE +
                        "," + " " + Contact.PROFILE      + " " + Contact.PROFILE_TYPE +
                        "," + " " + Contact.STATUS       + " " + Contact.STATUS_TYPE +
                        "," + " " + Contact.IS_MEMBER       + " " + Contact.IS_MEMBER_TYPE +
                        "," + " " + Contact.DATE_CREATED + " " + Contact.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the contact table.
     *
     * @author rube
     */
    public static class ContactColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String PROFILE       = "profile_id";
        public static final String STATUS        = "status_id";
        public static final String IS_MEMBER        = "member";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_TYPE        = "INTEGER NOT NULL";
        static final String STATUS_TYPE         = "INTEGER NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER DEFAULT 0";
        static final String IS_MEMBER_TYPE      = "INTEGER  DEFAULT 0";


    }


    /**
     * This table contains categories.
     *
     * @author rube
     */
    public static final class PageCategory extends PageCategoryColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single category. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.page.category";

        /** The MIME type of CONTENT_URI providing a directory of category. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.page.category";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/page_category */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/page/category" );

        /** The name of this table */
        public static final String TABLE = "page_category";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + PageCategory.TABLE + "(" + " " + PageCategory._ID           + " " + PageCategory._ID_TYPE +
                        "," + " " + PageCategory.SERVER_ID         + " " + PageCategory.SERVER_ID_TYPE +
                        "," + " " + PageCategory.NAME         + " " + PageCategory.NAME_TYPE +
                        "," + " " + PageCategory.DESCRIPTION  + " " + PageCategory.DESCRIPTION_TYPE +
                        "," + " " + PageCategory.DATE_CREATED + " " + PageCategory.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the page_category table.
     *
     * @author rube
     */
    public static class PageCategoryColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String NAME          = "name";
        public static final String DESCRIPTION   = "description";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String NAME_TYPE           = "VARCHAR(45) NOT NULL";
        static final String DESCRIPTION_TYPE    = "VARCHAR(100) NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";

    }


    /**
     * This table contains media types.
     *
     * @author rube
     */
    public static final class MediaType extends MediaTypeColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single media type. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.media.type";

        /** The MIME type of CONTENT_URI providing a directory of media type. */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.media.type";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/media_typ */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/media/type");

        /** The name of this table */
        public static final String TABLE = "media_type";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + MediaType.TABLE + "(" + " " + MediaType._ID           + " " + MediaType._ID_TYPE +
                        "," + " " + MediaType.SERVER_ID         + " " + MediaType.SERVER_ID_TYPE +
                        "," + " " + MediaType.NAME         + " " + MediaType.NAME_TYPE +
                        "," + " " + MediaType.DESCRIPTION  + " " + MediaType.DESCRIPTION_TYPE +
                        "," + " " + MediaType.DATE_CREATED + " " + MediaType.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the media_type table.
     *
     * @author rube
     */
    public static class MediaTypeColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String NAME          = "name";
        public static final String DESCRIPTION   = "description";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String NAME_TYPE           = "VARCHAR(45) NOT NULL";
        static final String DESCRIPTION_TYPE    = "VARCHAR(100) NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }

    /**
     * This table contains media.
     *
     * @author rube
     */
    public static final class Media extends MediaColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single media. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.media";

        /** The MIME type of CONTENT_URI providing a directory of media */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.media";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/media_typ */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Media.TABLE );

        /** The name of this table */
        public static final String TABLE = "media";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Media.TABLE + "(" + " " + Media._ID           + " " + Media._ID_TYPE +
                        "," + " " + Media.SERVER_ID         + " " + Media.SERVER_ID_TYPE +
                        "," + " " + Media.MEDIA_TYPE   + " " + Media.MEDIA_TYPE_TYPE +
                        "," + " " + Media.CONTENT_ID   + " " + Media.CONTENT_ID_TYPE +
                        "," + " " + MediaColumns.CONTENT_TYPE + " " + Media.CONTENT_TYPE_TYPE +
                        "," + " " + Media.URL          + " " + Media.URL_TYPE +
                        "," + " " + Media.DESCRIPTION  + " " + Media.DESCRIPTION_TYPE +
                        "," + " " + Media.DATE_CREATED + " " + Media.DATE_CREATED_TYPE +
                        ");";
    }

    //modeling table columns

    /**
     * Columns from the media table.
     *
     * @author rube
     */
    public static class MediaColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String MEDIA_TYPE    = "media_type";
        public static final String CONTENT_TYPE  = "content_type";
        public static final String CONTENT_ID    = "content_id";
        public static final String URL           = "link";
        public static final String DESCRIPTION   = "description";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String MEDIA_TYPE_TYPE     = "VARCHAR(45) ";
        static final String CONTENT_TYPE_TYPE   = "VARCHAR(45) ";
        static final String CONTENT_ID_TYPE     = "INTEGER NOT NULL";
        static final String URL_TYPE            = "VARCHAR(200) NOT NULL";
        static final String DESCRIPTION_TYPE    = "VARCHAR(200) NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }



    /**
     * This table contains pages.
     *
     * @author rube
     */
    public static final class Page extends PageColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single page. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.page";

        /** The MIME type of CONTENT_URI providing a directory of page */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.page";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/page */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Page.TABLE );

        /** The name of this table */
        public static final String TABLE = "page";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Page.TABLE + "(" + " " + Page._ID           + " " + Page._ID_TYPE +
                        "," + " " + Page.SERVER_ID         + " " + Page.SERVER_ID_TYPE +
                        "," + " " + Page.PROFILE_ID     + " " + Page.PROFILE_ID_TYPE +
                        "," + " " + Page.MEDIA_BG_ID    + " " + Page.MEDIA_BG_ID_TYPE +
                        "," + " " + Page.MEDIA_PHOTO_ID + " " + Page.MEDIA_PHOTO_ID_TYPE +
                        "," + " " + Page.NAME           + " " + Page.NAME_TYPE +
                        "," + " " + Page.TEXT           + " " + Page.TEXT_TYPE +
                        "," + " " + Page.CATEGORY_ID    + " " + Page.CATEGORY_ID_TYPE +
                        "," + " " + Page.DATE_CREATED   + " " + Page.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the page table.
     *
     * @author rube
     */
    public static class PageColumns
    {
        public static final String DATE_CREATED    = "date_created";
        public static final String SERVER_ID       = "server_id";
        public static final String PROFILE_ID      = "profile_id";
        public static final String MEDIA_BG_ID     = "bg_photo_id";
        public static final String MEDIA_PHOTO_ID  = "profile_photo_id";
        public static final String NAME            = "name";
        public static final String TEXT            = "text";
        public static final String CATEGORY_ID     = "category_id";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_ID_TYPE     = "INTEGER NOT NULL";
        static final String MEDIA_BG_ID_TYPE    = "INTEGER NULL";
        static final String MEDIA_PHOTO_ID_TYPE = "INTEGER NULL";
        static final String NAME_TYPE           = "VARCHAR(100) NOT NULL";
        static final String TEXT_TYPE           = "TEXT NOT NULL";
        static final String CATEGORY_ID_TYPE    = "INTEGER NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


    /**
     * This table contains stories.
     *
     * @author rube
     */
    public static final class Story extends StoryColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single story. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.story";

        /** The MIME type of CONTENT_URI providing a directory of stories */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.story";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/story */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Story.TABLE );

        /** The name of this table */
        public static final String TABLE = "story";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Story.TABLE + "(" + " " + Story._ID           + " " + Story._ID_TYPE +
                        "," + " " + Story.SERVER_ID      + " " + Story.SERVER_ID_TYPE +
                        "," + " " + Story.PROFILE_ID     + " " + Story.PROFILE_ID_TYPE +
                        "," + " " + Story.PAGE_ID        + " " + Story.PAGE_ID_TYPE +
                        "," + " " + Story.MEDIA_PHOTO_ID + " " + Story.MEDIA_PHOTO_ID_TYPE +
                        "," + " " + Story.NAME           + " " + Story.NAME_TYPE +
                        "," + " " + Story.TEXT           + " " + Story.TEXT_TYPE +
                        "," + " " + Story.DATE_CREATED   + " " + Story.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the story table.
     *
     * @author rube
     */
    public static class StoryColumns
    {
        public static final String DATE_CREATED    = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String PROFILE_ID      = "profile_id";
        public static final String PAGE_ID         = "page_id";
        public static final String MEDIA_PHOTO_ID  = "profile_photo_id";
        public static final String NAME            = "name";
        public static final String TEXT            = "message";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_ID_TYPE     = "INTEGER NOT NULL";
        static final String MEDIA_PHOTO_ID_TYPE = "INTEGER NULL";
        static final String PAGE_ID_TYPE        = "INTEGER NOT NULL";
        static final String NAME_TYPE           = "VARCHAR(100) NOT NULL";
        static final String TEXT_TYPE           = "TEXT NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


    /**
     * This table contains comments.
     *
     * @author rube
     */
    public static final class Comment extends CommentColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single comment. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.comment";

        /** The MIME type of CONTENT_URI providing a directory of comment */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.comment";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/comment */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Comment.TABLE );

        /** The name of this table */
        public static final String TABLE = "comment";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Comment.TABLE + "(" + " " + Comment._ID           + " " + Comment._ID_TYPE +
                        "," + " " + Comment.SERVER_ID         + " " + Comment.SERVER_ID_TYPE +
                        "," + " " + Comment.PROFILE_ID           + " " + Comment.PROFILE_ID_TYPE +
                        "," + " " + CommentColumns.CONTENT_TYPE  + " " + Comment.CONTENT_TYPE_TYPE +
                        "," + " " + Comment.CONTENT_ID           + " " + Comment.CONTENT_ID_TYPE +
                        "," + " " + Comment.MEDIA_ID             + " " + Comment.MEDIA_ID_TYPE +
                        "," + " " + Comment.TEXT                 + " " + Comment.TEXT_TYPE +
                        "," + " " + Comment.DATE_CREATED         + " " + Comment.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the story table.
     *
     * @author rube
     */
    public static class CommentColumns
    {
        public static final String DATE_CREATED    = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String CONTENT_TYPE    = "content_type";
        public static final String CONTENT_ID      = "content_id";
        public static final String PROFILE_ID      = "profile_id";
        public static final String MEDIA_ID        = "media";
        public static final String TEXT            = "message";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String CONTENT_TYPE_TYPE   = "INTEGER NOT NULL";
        static final String CONTENT_ID_TYPE       = "INTEGER NULL";
        static final String MEDIA_ID_TYPE       = "INTEGER NULL";
        static final String PROFILE_ID_TYPE     = "INTEGER NOT NULL";
        static final String TEXT_TYPE           = "TEXT NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }

    /**
     * This table contains content types.
     *
     * @author rube
     */
    public static final class ContentType extends ContentTypeColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single content_type. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.content.type";

        /** The MIME type of CONTENT_URI providing a directory of content type */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.content.type";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/content_type */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + ContentType.TABLE );

        /** The name of this table */
        public static final String TABLE = "content_type";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + ContentType.TABLE + "(" + " " + ContentType._ID           + " " + ContentType._ID_TYPE +
                        "," + " " + ContentType.SERVER_ID         + " " + ContentType.SERVER_ID_TYPE +
                        "," + " " + ContentType.NAME         + " " + ContentType.NAME_TYPE +
                        "," + " " + ContentType.DESCRIPTION  + " " + ContentType.DESCRIPTION_TYPE +
                        "," + " " + ContentType.DATE_CREATED + " " + ContentType.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the content type table.
     *
     * @author rube
     */
    public static class ContentTypeColumns
    {
        public static final String DATE_CREATED    = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String NAME            = "name";
        public static final String DESCRIPTION     = "description";


        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String DESCRIPTION_TYPE   = "INTEGER NOT NULL";
        static final String NAME_TYPE           = "VARCHAR(45) NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


    /**
     * This table contains share types.
     *
     * @author rube
     */
    public static final class Share extends ShareColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single share. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.share";

        /** The MIME type of CONTENT_URI providing a directory of content type */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.share";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/share */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Share.TABLE );

        /** The name of this table */
        public static final String TABLE = "share";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Share.TABLE + "(" + " " + Share._ID           + " " + Share._ID_TYPE +
                        "," + " " + Share.SERVER_ID         + " " + Share.SERVER_ID_TYPE +
                        "," + " " + ShareColumns.CONTENT_TYPE         + " " + ShareColumns.CONTENT_TYPE_TYPE +
                        "," + " " + Share.CONTENT_ID   + " " + Share.CONTENT_ID_TYPE +
                        "," + " " + Share.PROFILE_ID   + " " + Share.PROFILE_ID_TYPE +
                        "," + " " + Share.DATE_CREATED + " " + Share.DATE_CREATED_TYPE +
                        ");";
    }


    //modeling table columns

    /**
     * Columns from the content type table.
     *
     * @author rube
     */
    public static class ShareColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID     = "server_id";
        public static final String PROFILE_ID    = "profile_id";
        public static final String CONTENT_TYPE  = "content_type";
        public static final String CONTENT_ID    = "content_id";


        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_ID_TYPE     = "INTEGER NOT NULL";
        static final String CONTENT_TYPE_TYPE   = "INTEGER NOT NULL";
        static final String CONTENT_ID_TYPE     = "INTEGER NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }



    /**
     * This table contains like types.
     *
     * @author rube
     */
    public static final class Like extends LikeColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single like. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.like";

        /** The MIME type of CONTENT_URI providing a directory of content likes */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.like";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/like */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + Like.TABLE );

        /** The name of this table */
        public static final String TABLE = "like";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + Like.TABLE + "(" + " " + Like._ID           + " " + Like._ID_TYPE +
                        "," + " " + Like.SERVER_ID         + " " + Like.SERVER_ID_TYPE +
                        "," + " " + LikeColumns.CONTENT_TYPE         + " " + LikeColumns.CONTENT_TYPE_TYPE +
                        "," + " " + Like.CONTENT_ID   + " " + Like.CONTENT_ID_TYPE +
                        "," + " " + Like.PROFILE_ID   + " " + Like.PROFILE_ID_TYPE +
                        "," + " " + Like.DATE_CREATED + " " + Like.DATE_CREATED_TYPE +
                        ");";
    }

    //modeling table columns
    /**
     * Columns from the content type table.
     *
     * @author rube
     */
    public static class LikeColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID     = "server_id";
        public static final String PROFILE_ID    = "profile_id";
        public static final String CONTENT_TYPE  = "content_type";
        public static final String CONTENT_ID    = "content_id";


        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String PROFILE_ID_TYPE     = "INTEGER NOT NULL";
        static final String CONTENT_TYPE_TYPE   = "INTEGER NOT NULL";
        static final String CONTENT_ID_TYPE     = "INTEGER NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


    /**
     * This table contains keyvalue types.
     *
     * @author rube
     */
    public static final class KeyValue extends KeyValueColumns implements android.provider.BaseColumns
    {
        /** The MIME type of a CONTENT_URI subdirectory of a single like. */
        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.com.rube.tt.keapp.keyvalue";

        /** The MIME type of CONTENT_URI providing a directory of content likes */
        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.com.rube.tt.keapp.keyvalue";

        /** The content:// style URL for this provider, content://com.rube.tt.keapp/like */
        public static final Uri CONTENT_URI = Uri.parse( "content://" + DB.AUTHORITY + "/" + KeyValue.TABLE );

        /** The name of this table */
        public static final String TABLE = "key_value";

        static final String CREATE_STATEMENT =
                "CREATE TABLE " + KeyValue.TABLE + "("     + " " + KeyValue._ID           + " " + KeyValue._ID_TYPE +
                        "," + " " + KeyValue.SERVER_ID         + " " + KeyValue.SERVER_ID_TYPE +
                        "," + " " + KeyValue.KEY           + " " + KeyValue.KEY_TYPE +
                        "," + " " + KeyValue.VALUE         + " " + KeyValue.VALUE_TYPE +
                        "," + " " + KeyValue.DATE_CREATED  + " " + Like.DATE_CREATED_TYPE +
                        ");";
    }

    //modeling table columns
    /**
     * Columns from the content type table.
     *
     * @author rube
     */
    public static class KeyValueColumns
    {
        public static final String DATE_CREATED  = "date_created";
        public static final String SERVER_ID  = "server_id";
        public static final String KEY    = "key";
        public static final String VALUE  = "value";

        static final String DATE_CREATED_TYPE   = "TIMESTAMP DEFAULT CURRENT_TIMESTAMP";
        static final String KEY_TYPE     = "VARCHAR(35) NOT NULL";
        static final String VALUE_TYPE   = "VARCHAR(45) NOT NULL";
        static final String _ID_TYPE            = "INTEGER PRIMARY KEY AUTOINCREMENT";
        static final String SERVER_ID_TYPE      = "INTEGER NOT NULL DEFAULT 0";
    }


}
