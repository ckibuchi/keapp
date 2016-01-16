package com.rube.tt.keapp.auth;

/**
 * Created by rube on 5/24/15.
 */

import android.util.Log;

import com.rube.tt.keapp.data.ACCEPT;
import com.rube.tt.keapp.data.SyncAdapter;
import com.rube.tt.keapp.utils.Constants;
import com.rube.tt.keapp.utils.HashGenerator;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Handles the communication with server to create an xmpp account for u
 *
 */
public class KeappServerAuthenticate implements ServerAuthenticate{

    private  final String TAG = getClass().getSimpleName();
    private final String SERVER_ACCESS_URL =  Constants.SERVER_URL;

    @Override
    public String userSignUp(ArrayList<NameValuePair> nameValuePairs, String authType,String pass) throws Exception {

        JSONArray response = SyncAdapter.POSTRAW(nameValuePairs, "WebService/Users", ACCEPT.JSON, ACCEPT.JSON);
        Log.i("RESPONSE",response.toString());
       /* int responseStatus = Integer.valueOf(response.get("responseStatus").toString());

        if (responseStatus != 201){
            //We never actually created this user
            Log.d(TAG, "Error connecting to server nkt!, found response "+responseStatus);
            return  null;
        }*/

        JSONObject signupdetails=response.getJSONObject(0);

        if(signupdetails.has("status"))
        {
          //  Log.d(TAG, "Authentication Passsword "+signupdetails.getString("password"));
            Log.d(TAG, "Received response " + signupdetails.toString());
            return HashGenerator.generateMD5(HashGenerator.generateSHA1(pass));
        }
        else
            return null;

    }

    @Override
    public String userSignIn(String email, String pass, String authType) throws Exception {

        Log.d(TAG, "userSignIn");

        ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("email", email));
        nameValuePairs.add(new BasicNameValuePair("password", pass));

     //   HashMap<String, Object>[] response = SyncAdapter.readFromServer(null, "user", nameValuePairs);
        JSONArray response =SyncAdapter.POST(nameValuePairs, "WebService/Users/login", ACCEPT.JSON,"");

       JSONObject logindetails=response.getJSONObject(0);

        if (logindetails.has("id")){

            Log.d(TAG, "Authentication Passsword "+logindetails.getString("password"));
            Log.d(TAG, "Received response "+logindetails.toString());
            return logindetails.getString("msisdn");/*HashGenerator.generateMD5(logindetails.getString("password"));*/
        }
        else
            return null;

    }

}

