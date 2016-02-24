package in.co.madhur.chatbubblesdemo.db;

/**
 * Created by rube on 6/3/15.

 * Backend Class for the MUC Database(s)
 * allows manipulation of the database
 *
 * @author Florian Schmaus fschmaus@gmail.com - on behalf of the GTalkSMS Team
 *
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

public class KeyValueDatabase {

        private Context context;

        public KeyValueDatabase(Context ctx) {
                this.context = ctx;

        }

        public static Uri addKey(String key, String value, Context context) {
            ContentValues values = composeValues(key, value);

            DBProvider dbProvider = new DBProvider(context);
            Uri ret = dbProvider.insert(DB.KeyValue.CONTENT_URI, values);
            return ret;
        }

        public static boolean updateKey(String key, String value, Context context) {
           return false;
        }

        public static boolean deleteKey(String key, Context context) {
            return false;
        }

        public static String getValue(String key,Context context ) {
            DBProvider dbProvider = new DBProvider(context);
            Cursor c = dbProvider.query(DB.KeyValue.CONTENT_URI, new String[] {"key", "value"},
                    "key = ?", new String[] {key}, null ) ;
            if(c.getCount() == 1) {
                c.moveToFirst();
                String res = c.getString(0);
                c.close();
                return res;
            } else {
                c.close();
                return null;
            }
        }

        public static boolean containsKey(String key, Context context) {
            DBProvider dbProvider = new DBProvider(context);
            Cursor c = dbProvider.query(DB.KeyValue.CONTENT_URI, new String[] {"key", "value"},
                    "key = ?", new String[] {key}, null );

            if(c == null){
                return false;
            }

            boolean ret = c.getCount() == 1;
            c.close();
            return ret;
        }

        public static String[][] getFullDatabase(Context context) {
            DBProvider dbProvider = new DBProvider(context);
            Cursor c = dbProvider.query(DB.KeyValue.CONTENT_URI, new String[] {"key", "value"},
                    null, null, null );

            int rowCount = c.getCount();
            c.moveToFirst();
            String[][] res = new String[rowCount][2];
            for (int i = 0; i < rowCount; i++) {
                res[i][0] = c.getString(0);  // key field
                res[i][1] = c.getString(1);  // value field
                c.moveToNext();
            }
            c.close();
            return res;
        }

        private static ContentValues composeValues(String key, String value) {
            ContentValues values = new ContentValues();
            values.put("key", key);
            values.put("value", value);
            return values;
        }


}
