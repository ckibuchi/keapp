package in.co.madhur.chatbubblesdemo.data;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import com.loopj.android.http.Base64;
import in.co.madhur.chatbubblesdemo.KEApp;
import in.co.madhur.chatbubblesdemo.db.DB;
import in.co.madhur.chatbubblesdemo.db.DBProvider;
import in.co.madhur.chatbubblesdemo.db.DbManager;
import in.co.madhur.chatbubblesdemo.fragments.ContactFragment;
import in.co.madhur.chatbubblesdemo.models.ContactModel;
import in.co.madhur.chatbubblesdemo.utils.Constants;
import in.co.madhur.chatbubblesdemo.utils.Utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONStringer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

/**
 * Created by rube on 18/08/15.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    private static String TAG = SyncAdapter.class.getSimpleName();
    ProgressDialog prgDialog;
    private final AccountManager mAccountManager;
    private static DbManager dbManager;
    private ContentResolver contentResolver;
    private final static String SERVER_ACCESS_URL = Constants.SERVER_URL;


    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        if(autoInitialize){
            this.dbManager = new DbManager(context);
        }
        mAccountManager = AccountManager.get(context);
    }

    // ...
    /**
     * Set up the sync adapter. This form of the constructor maintains
     * compatibility with Android 3.0 and later platform versions
     */
    @SuppressLint("NewApi")
    public SyncAdapter(Context context, boolean autoInitialize,
                       boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        /*
         * If your app uses a content resolver, get an instance of it from the
         * incoming Context
         */
        prgDialog = new ProgressDialog(getContext());
        // Set Progress Dialog Text
        prgDialog.setMessage("Please wait...");
        // Set Cancelable as False
        prgDialog.setCancelable(false);
        if(autoInitialize){
            this.dbManager = new DbManager(context);
        }
        mAccountManager = AccountManager.get(context);

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
                              ContentProviderClient provider, SyncResult syncResult) {
        try{
            Log.i(TAG, "Loading Local data to array");

            // Implementing bi-dectional synch
            doSyncProcess(provider);

        }
        catch (Exception e){
            e.printStackTrace();
        }
    }



    private void doSyncProcess(ContentProviderClient provider){
        //start sync process
        String[] tableNames = new String[]{
        DB.Chat.TABLE, DB.ProfileStatus.TABLE, DB.Contact.TABLE,
        DB.Profile.TABLE, DB.PageCategory.TABLE, DB.Media.TABLE,
        DB.Page.TABLE, DB.Story.TABLE, DB.Comment.TABLE,
        DB.Share.TABLE, DB.Like.TABLE, DB.KeyValue.TABLE};

        //For each table insert to server fetch from server
        for(String tableName: tableNames){
            try {
                insertDataFromServer(provider, tableName);
                insertDataToServer(provider, tableName);
            }catch (RemoteException rme){
                rme.printStackTrace();
            }catch (IOException ioe){
                ioe.printStackTrace();
            }

        }

    }

    /**
     * Fetch data from the remote server and populate the local database via the
     * content provider (item)
     */
    private void insertDataFromServer(ContentProviderClient contentProviderClient, String tableName)
            throws RemoteException, IOException {
        Log.d(TAG, "insertDataFromServer  ... " + tableName);
        Uri contentURL = Uri.parse("content://" + DB.AUTHORITY + "/" + tableName);

        String lastServerId = this.dbManager.getLastServerID(tableName);

        HashMap<String, Object>[] responseMap = SyncAdapter.readFromServer(lastServerId, tableName);

        try {

            for (HashMap<String, Object> itemMap : responseMap) {

                Log.d(TAG, "Inserting item  ... " + itemMap);

                String duplicateRecord = this.dbManager.getDuplicateItem(tableName,
                        String.valueOf(itemMap.get("server_id")));

                // only recreate if we are empty
                if (duplicateRecord == null) {
                    ContentValues contentValues = new ContentValues();
                    for (String key : itemMap.keySet()) {
                        String value = String.valueOf(itemMap.get(key));
                        System.out.println("Key = " + key + ", Value = " + value);
                        contentValues.put(key, value);
                    }

                    contentProviderClient.insert(contentURL, contentValues);
                } else {
                    Log.d(TAG, "Found duplicate item : " + itemMap.get("name"));
                }

            }

        } catch (Exception ex) {
            Log.e(TAG, "Something failed inserting item " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void insertDataToServer(ContentProviderClient contentProviderClient, String tableName)
            throws RemoteException, IOException {
        Log.d(TAG, "Calling insertDataToServer MBAAM");
        ArrayList<HashMap<String, String>> unsyncedItems = this.dbManager.getUnsyncedItems(tableName);

        if (!unsyncedItems.isEmpty()) {
            //Shoot them to the server
            for (HashMap<String, String> newItem : unsyncedItems) {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                for(String key: newItem.keySet()){
                    params.add(new BasicNameValuePair(key, String.valueOf(newItem.get(key))));
                }

                try {
                    HashMap<String, Object> response = SyncAdapter.postToServer(params, tableName);
                    Object serverId = response.get("server_id");
                    Object itemId = response.get("server_id");
                    this.dbManager.updateItemAsPosted(tableName, String.valueOf(serverId), String.valueOf(itemId));
                } catch (Exception ex) {
                    Log.d(TAG, "Error posting to server: " + ex.getMessage());
                    ex.printStackTrace();
                }
            }

        } else {
            Log.d(TAG, "Skipping insert to server no items on client");
        }

    }

        public static HashMap<String, Object>[] readFromServer(String lastServerId, String tableName) {
            return SyncAdapter.readFromServer(lastServerId, tableName,null);
        }



        public static HashMap<String, Object>[] readFromServer(String lastServerId, String tableName,
                                                               List<NameValuePair> params) {
            URL url;
            String path = "api/"+tableName;
            String query = "";
            try{

                for(NameValuePair para: params)
                {
                    Log.d("PARAM ",para.getValue());

                }
            }
            catch(Exception e)
            {
                Log.d("Error "," "+e.getMessage());

            }

            if(params != null){
                try {
                    query += SyncAdapter.getQuery(params);
                }catch (Exception ex){
                    ex.printStackTrace();
                    Log.d(TAG, "Error excepted .. Ignoring params");
                }
            }
            HttpURLConnection urlConnection = null;

            try {
                if(lastServerId != null){
                    if(!query.equals("")){
                        query += "&";
                    }
                    query += "server_id=" + URLEncoder.encode(lastServerId, "utf-8");
                }

                if(!query.equals("")) {
                    url = new URL(SERVER_ACCESS_URL + path + "?" + query);
                }else{
                    url = new URL(SERVER_ACCESS_URL + path);
                }



                try {

                    // make web service connection
                    HttpPost request = new HttpPost(SERVER_ACCESS_URL + "api/user");
                    request.setHeader("Accept", "application/json");
                    request.setHeader("Content-type", "application/json");
                    // Build JSON string
                    JSONStringer TestApp = new JSONStringer().object().key("id")
                            .value("1").key("username").value("chris").key("email")
                            .value("chriskibuchics@gmail.com").key("password")
                            .value("chris").key("country")
                            .value("Kenya").endObject();
                    StringEntity entity = new StringEntity(TestApp.toString());

                    Log.d("****Parameter Input****", "Testing:" + TestApp);
                    request.setEntity(entity);
                    // Send request to WCF service
                    DefaultHttpClient httpClient = new DefaultHttpClient();

                    HttpResponse response = httpClient.execute(request);

                    Log.d("WebInvoke", "Saving: " + response.getStatusLine().toString());
                    // Get the status of web service
                    BufferedReader rd = new BufferedReader(new InputStreamReader(
                            response.getEntity().getContent()));
                    // print status in log
                    String line = "";
                    while ((line = rd.readLine()) != null) {
                        Log.d("****Status Line***", "Webservice: " + line);

                    }
                    return null ;

                } catch (Exception e) {
                    e.printStackTrace();
                }



                Log.d(TAG, "Attempting connection open init.. readFromServer " + url.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestProperty("Accept-Charset", "utf-8");
                String authorizationString = "Basic";///* + Base64.encodeToString(("chris" + ":" + "chris").getBytes(), Base64.DEFAULT*/);
                urlConnection.setRequestProperty("Authorization", authorizationString);
                urlConnection.setRequestMethod("GET");
                Log.d("Params 0",params.get(0).getValue());
                urlConnection.addRequestProperty("username", params.get(0).getValue());
                urlConnection.addRequestProperty("password",params.get(1).getValue());
                //urlConnection.setDoOutput(false);

                Log.d(TAG, "Logging: about to read input steam: " + urlConnection.toString());
                InputStream inputStream  = urlConnection.getInputStream();

                Log.d(TAG, "FOUND INPUT STREAM :" + inputStream.toString());
                BufferedReader bufReader = new BufferedReader(
                        new InputStreamReader(inputStream, "UTF-8"));
                Log.d(TAG, "Calling : get response code ");
                int statusCode = urlConnection.getResponseCode();
                Gson gson = new Gson();

                Long id = null;
                String responseMessage = new String();
                String line = null;
                // looping through rows
                while ((line = bufReader.readLine()) != null) {
                    responseMessage += line;
                }
                //responseMessage = responseMessage.substring(1, responseMessage.length()-1);
                Log.d(TAG, "FOUND RESPONSE STRING " + responseMessage);

                JsonReader reader = new JsonReader(new StringReader(responseMessage));
                reader.setLenient(true);

                HashMap<String, Object>[] responseMap = gson.fromJson(reader, HashMap[].class);

                return responseMap;

            } catch (Exception ex) {
                Log.e(TAG, "Something failed inserting readFromServer " + ex.getMessage());
                ex.printStackTrace();
            }finally {
                if(urlConnection != null) {
                    urlConnection.disconnect();
                }
            }

        return null;
    }

    public static void test()
    {

        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        HttpResponse response;
        try{
            httpget = new HttpGet(SERVER_ACCESS_URL+"api/user");

            String auth =new String(Base64.encode(("chris:chris").getBytes(),Base64.URL_SAFE|Base64.NO_WRAP));
            httpget.addHeader("Authorization", "Basic " + auth);
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda",response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertInputStreamToString(instream);
                Log.d("RESULT ",result);
                // now you have the string representation of the HTML request
                instream.close();
            }
            else
                Log.d("ERR"," NOTHING READ ");

        }
        catch(Exception e)
        {Log.d("ERR",e.getMessage());

        }
    }
    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }
    public static JSONArray  GET(ArrayList<NameValuePair> params, String thepath,String accept, String content_type) throws Exception {
        // Create a new HttpClient and Post Header
        String path = "" + thepath;

        URL url = new URL(SERVER_ACCESS_URL + path);
        HttpClient httpClient = new DefaultHttpClient();
        HttpGet post = new HttpGet(SERVER_ACCESS_URL + path);
        Log.i(TAG,SERVER_ACCESS_URL + path);
        if(content_type.trim().length()>0) {
            post.setHeader("Content-Type",content_type);
        }
        if(accept.trim().length()>0)
        {
            post.setHeader("Accept", accept);
        }
        JSONObject dato = new JSONObject();



        if(params.size()>0) {
            for (int i = 0; i < params.size(); i++) {
                Log.i(params.get(i).getName(), params.get(i).getValue());
                dato.put(params.get(i).getName(), params.get(i).getValue());
            }
        }

        Log.i("URL ",url.toString());

        HttpResponse resp = httpClient.execute(post);
        String responseMessage = EntityUtils.toString(resp.getEntity());
      //  String authentication = params.get(0).getValue() + ":" + params.get(1).getValue();
        //  String encoding = Base64.encodeToString(authentication.getBytes(), 0);
       // String encoding = Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP);
/*
        HashMap<String, Object> responseMap;
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        conn.setRequestMethod("GET");

        //conn.setRequestProperty("Authorization", "Basic " + encoding);
        conn.setDoOutput(false);

        conn.connect();
        Log.d("CODE ", "" + conn.getResponseCode());

        Log.d("RESP ", conn.getResponseMessage());
        Log.d("data ", conn.getContent().toString());
        String line;
        String responseMessage = new String();
        BufferedReader reader = new BufferedReader(new InputStreamReader(
                conn.getInputStream()));
        while ((line = reader.readLine()) != null) {
           // Log.d(TAG, "Reading line by line: " + line);
            responseMessage += line;
        }*/
        Log.d("data 2 ", responseMessage);
        //{"count":1,"next":null,"previous":null,"results":[{"id":3,"username":"chriskibuchics@gmail.com","first_name":"chris","last_name":"","email":"chriskibuchics@gmail.com","password":"pbkdf2_sha256$20000$F5OamqbC5roW$+H5IgxLnRrhDQkmnLH/FqyF8ub479/V0TFU4CxC79Sg=","is_active":false,"pageId":"7830202669"}]}
       // JSONArray json = new JSONArray(responseMessage);
        JSONArray jsonObject = new JSONArray(responseMessage);
        return jsonObject;


    }
    public static JSONArray POST(ArrayList<NameValuePair> params, String thepath,String accept, String content_type) throws Exception {
        // Create a new HttpClient and Post Header
        String path = "" + thepath;

        URL url = new URL(SERVER_ACCESS_URL + path);
       HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVER_ACCESS_URL + path);
        Log.i(TAG,SERVER_ACCESS_URL + path);
        if(content_type.trim().length()>0) {
         post.setHeader("Content-Type",content_type);
        }
        if(accept.trim().length()>0)
        {
            post.setHeader("Accept", accept);
        }
        JSONObject dato = new JSONObject();



            if(params.size()>0) {
                for (int i = 0; i < params.size(); i++) {
                    Log.i(params.get(i).getName(), params.get(i).getValue());
                    dato.put(params.get(i).getName(), params.get(i).getValue());
                }
            }

                post.setEntity(new UrlEncodedFormEntity(params));

                HttpResponse resp = httpClient.execute(post);
                String responseMessage = EntityUtils.toString(resp.getEntity());
        Log.i(TAG,"responseMessage " +responseMessage);
        String code=""+resp.getStatusLine().getStatusCode();
        Log.d("CODE ",code);
        String respcode="[{\"CODE\":\""+code+"\"}]";
        if(!code.contains("20"))
        {
            responseMessage="[{\"status\":\"UNABLE TO PROCESS\"}]";
        }
        /*JSONArray jsonArray = new JSONArray();
        jsonArray.put(responseMessage);*/
        JSONArray jsonArray = new JSONArray(responseMessage);
        return jsonArray;

    }
    public static JSONArray POSTRAW(ArrayList<NameValuePair> params, String thepath,String accept, String content_type) throws Exception {
        // Create a new HttpClient and Post Header
        String path = "" + thepath;
        Log.i(TAG,path);
        URL url = new URL(SERVER_ACCESS_URL + path);
        Log.i("URL: ",url.toString());
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost(SERVER_ACCESS_URL + path);

        if(accept.trim().length()>0)
        {
            post.setHeader("Accept", accept);
        } if(content_type.trim().length()>0)
        {
            post.setHeader("Content-Type", content_type);
        }

        JSONObject dato = new JSONObject();
               if(params.size()>0) {
            for (int i = 0; i < params.size(); i++) {
               // Log.i("PARAMEMTER "+params.get(i).getName(), params.get(i).getValue());


                dato.put(params.get(i).getName(), params.get(i).getValue());
            }
        }
        Log.i(TAG, "Details for signup: " + dato.toString());
        StringEntity entity = new StringEntity(dato.toString());


        post.setEntity(entity);

        HttpResponse resp = httpClient.execute(post);
        String  responseMessage="[]";
        Log.i(TAG,"Status CODE: "+resp.getStatusLine().getStatusCode());
        String code=""+resp.getStatusLine().getStatusCode();
        if(code.contains("20"))
        {
            responseMessage="[{\"status\":\"OK\"}]";
        }
       /* try {
            responseMessage = EntityUtils.toString(resp.getEntity());

        }
        catch(Exception e)
        {}*/
        JSONArray jsonObject = new JSONArray(responseMessage);

        return jsonObject;

    }
    public static HashMap jsonToMap(String t) throws JSONException {
        HashMap<String, String> pairs = new HashMap<String, String>();
        JSONArray jsonArray = new JSONArray(t);
        Log.i(TAG,
                "Number of entries " + jsonArray.length());
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
          //  Log.i(TAG, jsonObject.getString("text"));
            Iterator<?> keys = jsonObject.keys();
            String key = (String)keys.next();
            String value = jsonObject.getString(key);
            pairs.put(key, value);
        }

        return pairs;
    }
        public static HashMap<String, Object> postToServer(ArrayList<NameValuePair> params, String tableName) throws Exception {
        // Create a new HttpClient and Post Header
        String path = "api/"+tableName;
        URL url = new URL(SERVER_ACCESS_URL + path);

            String authentication = params.get(0).getValue()+":"+params.get(1).getValue();
          //  String encoding = Base64.encodeToString(authentication.getBytes(), 0);
            String encoding = Base64.encodeToString(authentication.getBytes(), Base64.NO_WRAP);

            HashMap<String, Object> responseMap;
            HttpURLConnection  conn = (HttpURLConnection) url.openConnection();

            conn.setRequestMethod("GET");

            conn.setRequestProperty("Authorization", "Basic " + encoding);
            conn.setDoOutput(false);

            conn.connect();
            Log.d("CODE ", "" + conn.getResponseCode());

            Log.d("RESP ",conn.getResponseMessage());
            Log.d("data ",conn.getContent().toString());
            String line;
            String responseMessage = new String();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, "Reading line by line: " + line);
                responseMessage += line;
            }


        return null;

    }

    public static HashMap<String, Object> postToServer(List<NameValuePair> params, String tableName) throws Exception {
        // Create a new HttpClient and Post Header
        HashMap<String, Object> responseMap =null;
        String path = "api/"+tableName;
        URL url = new URL(SERVER_ACCESS_URL + path);
        HttpClient httpclient = new DefaultHttpClient();
        HttpGet httpget;
        HttpResponse response;
        try{
            httpget = new HttpGet(SERVER_ACCESS_URL+path);

            String auth =new String(Base64.encode((params.get(0)+":"+params.get(1)).getBytes(),Base64.URL_SAFE|Base64.NO_WRAP));
            httpget.addHeader("Authorization", "Basic " + auth);
            httpget.addHeader("username",params.get(0).toString());
            httpget.addHeader("password",params.get(1).toString());
            response = httpclient.execute(httpget);
            // Examine the response status
            Log.i("Praeda",response.getStatusLine().toString());

            // Get hold of the response entity
            HttpEntity entity = response.getEntity();
            // If the response does not enclose an entity, there is no need
            // to worry about connection release

            if (entity != null) {

                // A Simple JSON Response Read
                InputStream instream = entity.getContent();
                String result= convertInputStreamToString(instream);
                Log.d("RESULT ",result);
                // now you have the string representation of the HTML request
                instream.close();
                Gson gson = new Gson();
                JsonReader jsonReader = new JsonReader(new StringReader(result));
                jsonReader.setLenient(true);

                responseMap = gson.fromJson(jsonReader, HashMap.class);

            }
            else
                Log.d("ERR"," NOTHING READ ");

        }
        catch(Exception e)
        {Log.d("ERR",e.getMessage());

        }
        return responseMap;
      /*  Log.d(TAG, "Attempting connection open post conn.. " + url.getPath());
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestProperty("Accept-Charset", "utf-8");
        urlConnection.setRequestProperty("User-Agent", "Mozilla/5.0 ( compatible ) ");
        urlConnection.setRequestProperty("Accept", "*");
        String authorizationString = "Basic " + Base64.encodeToString(("admin" + ":" + "admin").getBytes(), Base64.DEFAULT);
        urlConnection.setRequestProperty("Authorization", authorizationString);

        urlConnection.setRequestMethod("POST");
        urlConnection.addRequestProperty("first_name", params.get(0).getValue());
        urlConnection.addRequestProperty("email", params.get(1).getValue());
        urlConnection.addRequestProperty("password", params.get(2).getValue());
        urlConnection.setDoOutput(true);
        HashMap<String, Object> responseMap;
        try{

            for(NameValuePair para: params)
            {
                Log.d("POST TO SERVER PARAM ",para.getValue());

            }
        }
        catch(Exception e)
        {
            Log.d("Error "," "+e.getMessage());

        }
        try {
            Log.d(TAG, "Trying to connect ...");
            OutputStreamWriter writer = new OutputStreamWriter(
                    urlConnection.getOutputStream());
            writer.write(getQuery(params));
            writer.flush();
            String line;
            String responseMessage = new String();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    urlConnection.getInputStream()));

            int statusCode = urlConnection.getResponseCode();
            while ((line = reader.readLine()) != null) {
                Log.d(TAG, "Reading line by line: " + line);
                responseMessage += line;
            }

            Gson gson = new Gson();
            JsonReader jsonReader = new JsonReader(new StringReader(responseMessage));
            jsonReader.setLenient(true);

            responseMap = gson.fromJson(jsonReader, HashMap.class);

            responseMap.put("responseStatus", statusCode);
            writer.close();
            reader.close();
            urlConnection.disconnect();
            Log.d(TAG, "RESPONSE MAP " + responseMap);

        } catch (Exception ex) {
            Log.e(TAG, "Something failed inserting postToServer " + ex.getMessage());
            ex.printStackTrace();
            responseMap = new HashMap<String, Object>();
            responseMap.put("responseStatus", 0);

        }finally {
            if(urlConnection != null){
                urlConnection.disconnect();
            }
        }

        return responseMap;*/

    }

    private static String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
        StringBuilder result = new StringBuilder();
        boolean first = true;

        for (NameValuePair pair : params) {
            if (first) {
                first = false;
            } else {
                result.append("&");
            }

            result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
            result.append("=");
            result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
        }

        return result.toString();
    }

    public static  void syncContactToLocal(ContactModel contactModel, Context context){
        //check if we have data skip keeping otherwise fetch all contacts to out contacts table
        //create contact->profile->profile status
        //              ->status
try {

    if(Utils.getNineDigits(contactModel.getPhoneNumber()).equalsIgnoreCase(Utils.getNineDigits(KEApp.accountName)))
    {
        return;
    }
    int ismember=0;
    DBProvider dbProvider = new DBProvider(context);
    String rawSql = "Select * from " + DB.Profile.TABLE + " where " + DB.Profile.MSISDN + "='" + contactModel.getPhoneNumber() + "'";
    Cursor localContacts = dbProvider.rawQuery(DB.Contact.CONTENT_URI, rawSql, null);
    try
    {
        ismember=1;
        Log.d(TAG, "Phone " + contactModel.getPhoneNumber());
        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        Log.i("start ",""+(contactModel.getPhoneNumber().trim().length()-9));
        Log.i("end ", "" + contactModel.getPhoneNumber().trim().length());

        nameValuePairs.add(new BasicNameValuePair("msisdn", contactModel.getPhoneNumber().trim().substring(contactModel.getPhoneNumber().trim().length() - 9, contactModel.getPhoneNumber().trim().length())));  //0727894083
        JSONArray userdetails=POST(nameValuePairs, "WebService/Users/FromPhone", ACCEPT.JSON,CONTENT_TYPE.URLENCODED);
        JSONObject userhere=userdetails.getJSONObject(0);
        if(!userhere.has("regId"))
        {
            Log.i("MISSING ","Contact not registered");
           return;
        }


    }
    catch(Exception e)
    {
        e.printStackTrace();
        return;
    }
    try {
        if (localContacts.getCount() > 0) {
            Log.d(TAG, "Skipping contact " + contactModel.getPhoneNumber() + " already synced in local db");
            try {
                ContentValues updatecontact = new ContentValues();


                updatecontact.put(DB.Contact.IS_MEMBER, ismember);
                Log.d(TAG, "Calling update into contact, id: " + contactModel.getId() + " ");
              //  DB.update(DB.Contact.TABLE, updatecontact, "_id = ?", new String[]{""+contactModel.getId()});
               // dbProvider.update(DB.Contact.CONTENT_URI,updatecontact,DB.Contact._ID+"=? ",new String[] {""+contactModel.getId()});
               Log.d(TAG,""+dbProvider.updateContactAsMember(contactModel));


            /*    int u=dbProvider.updateUserStatus(DB.Contact.TABLE
                       , ismember,
                       "" + contactModel.getId());
                Log.d(" Update status ","  "+u);*/
            }
            catch(Exception e)
            {e.printStackTrace();}
            return;
        }
    } catch (Exception ex) {
        ex.printStackTrace();
    } finally {
        if (localContacts != null) {
            localContacts.close();
        }
    }

    ContentValues profileParams = new ContentValues();
    String imagePath = "";
    //save images
    if (contactModel.getPic() != null) {
        Log.d(TAG, "Calling saveImage ... ");
        imagePath = Utils.saveImage(context, contactModel.getPic(), contactModel.getPhoneNumber(), "jpeg");
    }

    profileParams.put(DB.Profile.SERVER_ID, 0);
    profileParams.put(DB.Profile.NAME, contactModel.getName());
    profileParams.put(DB.Profile.MSISDN, contactModel.getPhoneNumber());
    profileParams.put(DB.Profile.EMAIL, "");
    profileParams.put(DB.Profile.PHOTO, imagePath);
    Log.d(TAG, "Calling insert into Profile");
    Uri uri = dbProvider.insert(DB.Profile.CONTENT_URI, profileParams);
    String profileId = uri.getLastPathSegment();

    ContentValues statusParams = new ContentValues();
    statusParams.put(DB.ProfileStatus.SERVER_ID, 0);
    statusParams.put(DB.ProfileStatus.MESSAGE, "keapp");
    statusParams.put(DB.ProfileStatus.PROFILE, profileId);
    statusParams.put(DB.ProfileStatus.ACTIVE, ismember);

    Log.d(TAG, "Calling insert into profile status");
    Uri pUri = dbProvider.insert(DB.ProfileStatus.CONTENT_URI, statusParams);
    String profileStatusId = pUri.getLastPathSegment();
    Log.d(TAG, "Inserted into profile status");
//Search from the service if a member with this phone number exists....
    //The logic goes here...
    ContentValues contactParams = new ContentValues();
    Log.i("ISMEMBER ",""+ismember+" ");


    contactParams.put(DB.Contact.IS_MEMBER, ismember);
    contactParams.put(DB.Contact.SERVER_ID, 0);
    contactParams.put(DB.Contact.PROFILE, profileId);
    contactParams.put(DB.Contact.STATUS, profileStatusId);
    Log.d(TAG, "Calling insert into contact");
    Uri cUri = dbProvider.insert(DB.Contact.CONTENT_URI, contactParams);
    Log.d(TAG, "INSERT CONTACT " + cUri.toString());
    ContactFragment.search(null);
}
catch(Exception e)
{
 Log.d("DB ERR ",e.getMessage());
}

    }

    public static void updateContactAsInvited(String phone){

    }

}
