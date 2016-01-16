package com.rube.tt.keapp;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.rube.tt.keapp.auth.AccountGeneral;
import com.rube.tt.keapp.data.SyncAdapter;
import com.rube.tt.keapp.utils.CustomAccountAunthenticationActivity;
import com.rube.tt.keapp.utils.HashGenerator;
import com.rube.tt.keapp.utils.Validation;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.rube.tt.keapp.auth.AccountGeneral.ACCOUNT_TYPE;
import static com.rube.tt.keapp.auth.AccountGeneral.sServerAuthenticate;

//import com.rube.tt.keapp.utils.CustomAccountAunthenticationActivity;

//import com.rube.tt.keapp.utils.CustomAccountAunthenticationActivity;

/**
 * Created by rube on 5/23/15.
 */
public class Login extends CustomAccountAunthenticationActivity {

    public final static String ARG_ACCOUNT_TYPE = "keapp.com";
    public final static String ARG_AUTH_TYPE = "AUTH_TYPE";
    public final static String ARG_ACCOUNT_NAME = "ACCOUNT_NAME";
    public final static String ARG_IS_ADDING_NEW_ACCOUNT = "IS_ADDING_ACCOUNT";
    public static final String KEY_ERROR_MESSAGE = "ERR_MSG";
    public final static String PARAM_USER_PASS = "USER_PASS";
    public final static String KEY_FULL_NAME = "FULL_NAME";
    public final static String KEY_PROFILE_STATUS = "PROFILE_STATUS";

    private final int REQ_SIGNUP = 1;
    private final String TAG = this.getClass().getSimpleName();
    private AccountManager accountManager;
    private String authTokenType;
    private Toolbar toolbar;
    private SharedPreferences sharedPreferences;
    public static final String preference = "MyAccountPreference" ;
    EditText _mail,_password;

    private ProgressDialog progressDialog;

    @Override
    public void onCreate(Bundle savedInstanceState){

        super.onCreate(savedInstanceState);
        //We need to check if this guy ever logged in and push them straight to the next screen

        Intent i = getIntent();
        Bundle extras = i.getExtras();
        final String action = i.getAction();


        if (Intent.ACTION_VIEW.equals(action)){

           /* Uri date_url = getIntent().getData();
            //String scheme = date_url.getScheme(); // "http"
            //String host = date_url.getHost(); // "keapp.com"
            List<String> params = date_url.getPathSegments();
            Log.d(TAG, "PARAMETERS FROM URL " + params.toString());
            String email = params.get(1); // "status"
            String key = params.get(2);
            verifyRegistration(email, key);*/

        }

        sharedPreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
        Log.d(TAG, "Preferences contains values :"+ sharedPreferences.getAll());

        if (sharedPreferences.contains("accountName") ){

            Log.d(TAG, "Acccount preference contains mentioned items should start ke app now ..."+
                    sharedPreferences.getString("accountName", ""));
            Bundle data = new Bundle();
            data.putString(AccountManager.KEY_ACCOUNT_NAME, sharedPreferences.getString("accountName", ""));
            data.putString(AccountManager.KEY_ACCOUNT_TYPE, sharedPreferences.getString("accountType", ""));
            data.putString(AccountManager.KEY_AUTHTOKEN, sharedPreferences.getString("authToken", ""));
            data.putString(PARAM_USER_PASS, sharedPreferences.getString("accountPassword", ""));
            data.putString(KEY_FULL_NAME, sharedPreferences.getString("fullName", ""));

            final Intent mainActivity = new Intent(Login.this, KEApp.class);
            mainActivity.putExtras(data);
            setAccountAuthenticatorResult(mainActivity.getExtras());
            setResult(RESULT_OK, mainActivity);
            startActivity(mainActivity);
            finish();

        }
        Log.d(TAG, "Missing in account preferences loading login screen");

        setContentView(R.layout.login);
        registerviews();
        accountManager = AccountManager.get(getBaseContext());

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.title_login_page);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        String accountName = getIntent().getStringExtra(ARG_ACCOUNT_NAME);
        authTokenType = getIntent().getStringExtra(ARG_AUTH_TYPE);
        if (authTokenType == null)
            authTokenType = AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS;

        if (accountName != null) {
            ((TextView)findViewById(R.id.email_address)).setText(accountName);
        }
        try {
        if (getIntent().getExtras() != null) {

                Uri date_url = getIntent().getData();
                //String scheme = date_url.getScheme(); // "http"
                //String host = date_url.getHost(); // "keapp.com"
                List<String> params = date_url.getPathSegments();
                Log.d(TAG, "PARAMETERS FROM URL " + params.toString());
                String email = params.get(1); // "email"
                EditText mail = (EditText) findViewById(R.id.email_address);
                mail.setText(email);
            }


        }
        catch(Exception e)
        {
            e.printStackTrace();
        }

        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(checkValidation())
                submit();

            }
        });
        findViewById(R.id.sign_up).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent signup = new Intent(getBaseContext(), Signup.class);
                if(getIntent().getExtras() != null) {
                    signup.putExtras(getIntent().getExtras());
                }
                startActivityForResult(signup, REQ_SIGNUP);
            }
        });

        findViewById(R.id.forgot_password).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent forgotPassword = new Intent(getBaseContext(), ForgotPassword.class);
                startActivity(forgotPassword);
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);

    }
public void registerviews()
{

   _mail= ((EditText) findViewById(R.id.email_address));
    _mail.addTextChangedListener(new TextWatcher() {
        // after every change has been made to this editText, we would like to check validity
        public void afterTextChanged(Editable s) {
            Validation.isEmailAddress(_mail, true);
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
    });
    _password= ((EditText) findViewById(R.id.account_password));
    _password.addTextChangedListener(new TextWatcher() {
        public void afterTextChanged(Editable s) {
            Validation.hasText(_password);
        }
        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
        public void onTextChanged(CharSequence s, int start, int before, int count){}
    });
}
    private boolean checkValidation() {
        boolean ret = true;

        if (!Validation.isEmailAddress(_mail, true)) ret = false;
        if (!Validation.hasText(_password)) ret = false;


        return ret;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        // The sign up activity returned that the user has successfully created an account
        if (requestCode == REQ_SIGNUP && resultCode == RESULT_OK) {
            finishLogin(data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void verifyRegistration(String email, String key){
        if(email != null && key !=null) {
            final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("authtoken", key));
            nameValuePairs.add(new BasicNameValuePair("email", email));

            new AsyncTask<String, Void, Intent>() {

                @Override
                protected Intent doInBackground(String... params) {

                    Log.d(TAG, " Started verification");

                    String authtoken = null;

                    Bundle responseData = new Bundle();
                    Intent res = null;


                    try {
                        HashMap<String, Object>[] response = SyncAdapter.readFromServer(null, "user", nameValuePairs);

                        if (response != null) {
                            if (response.length > 0) {
                                Log.d(TAG, "User confirmation sucess redirecting to index");

                                try {
                                    authtoken = HashGenerator.generateMD5(response[0].get("password").toString());
                                } catch (Exception ex) {
                                    Log.d(TAG, "Error, generating auth token");
                                }

                                responseData.putString(AccountManager.KEY_ACCOUNT_NAME, response[0].get("first_name").toString());
                                responseData.putString(AccountManager.KEY_ACCOUNT_TYPE, ARG_ACCOUNT_TYPE);
                                responseData.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                                responseData.putString(PARAM_USER_PASS, response[0].get("password").toString());
                                res = new Intent(Login.this, KEApp.class);

                            }else{
                                res = new Intent(Login.this, LoginFailed.class);
                            }
                        }

                        Log.d(TAG, "Found params :" + responseData.toString());

                    } catch (Exception e) {
                        Log.d(TAG, "Hitting Error " + e.getMessage());
                        e.printStackTrace();
                        responseData.putString(KEY_ERROR_MESSAGE, "Error, verification failed. Please try again later");

                    }
                    if(res == null){
                        res = new Intent(Login.this, LoginFailed.class);
                    }

                    res.putExtras(responseData);
                    return res;
                }

                @Override
                protected void onPreExecute() {
                    progressDialog = new ProgressDialog(Login.this);
                    //progressDialog.setTitle("Processing...");
                    //progressDialog
                    progressDialog.setMessage("Please wait.");
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(true);
                    progressDialog.show();
                    super.onPreExecute();

                }

                @Override
                protected void onPostExecute(Intent intent) {
                    if(progressDialog != null){
                        progressDialog.dismiss();
                    }

                    finishLogin(intent);

                }
            }.execute();
        }
    }

    public void submit() {

        final String email =_mail.getText().toString();
        final String userPass = _password.getText().toString();
        try {
            Log.i(TAG, "SHA1 PASS " + HashGenerator.generateSHA1(userPass));
        }
        catch(Exception e)
        {

            Log.e("SHA1  ERR ",e.getMessage());
        }
        final String accountType = getIntent().getStringExtra(AccountManager.KEY_ACCOUNT_TYPE);

        new AsyncTask<String, Void, Intent>() {

            @Override
            protected Intent doInBackground(String... params) {

                Log.d(TAG , " Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                Intent res = null;
                try {
                    authtoken= sServerAuthenticate.userSignIn(email, userPass, authTokenType);

                    if(authtoken != null) {
                        data.putString(AccountManager.KEY_ACCOUNT_NAME, authtoken);
                        data.putString(AccountManager.KEY_ACCOUNT_TYPE, accountType);
                        data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                        data.putString(PARAM_USER_PASS, userPass);

                        Log.d(TAG, String.format("email:%s, accountType:%s,userPass:%s, auth:%s",
                                email, accountType, userPass, authtoken));

                        Log.d(TAG, "Found params :" + data.toString());
                        res = new Intent(Login.this, KEApp.class);
                    }else{
                        data.putString(KEY_ERROR_MESSAGE, "Error, Invalid email and or password");

                    }

                } catch (Exception e) {
                    Log.d(TAG, "Hitting Error "+e.getMessage());
                    e.printStackTrace();
                    data.putString(KEY_ERROR_MESSAGE, "Error, GET failed. Please try again later");
                }

                if(res == null){
                    res = Login.this.getIntent();
                }

                res.putExtras(data);
                return res;
            }
            @Override
            protected void onPreExecute() {

                progressDialog = new ProgressDialog(Login.this);
                //progressDialog.setTitle("Processing...");
                //progressDialog
                progressDialog.setMessage("Please wait.");
                progressDialog.setCancelable(false);
                progressDialog.setIndeterminate(true);
                progressDialog.show();
                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Intent intent) {
                if(progressDialog != null){
                    progressDialog.dismiss();
                }
                if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    finishLogin(intent);
                }
            }
        }.execute();
    }


    private void finishLogin(Intent intent) {
        Log.d(TAG,  "finishLogin");

        String accountName = intent.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
        String accountPassword = intent.getStringExtra(PARAM_USER_PASS);

        if(accountName == null || accountPassword == null){

            Log.d(TAG, "Found Finish params =>accountName: "+ accountName+ ", accountPassword: "+accountPassword );
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            startActivity(intent);
            finish();
        }else {

            Log.d(TAG, "Found Finsh params =>accountName: " + accountName + ", accountPassword: " + accountPassword);

            final Account account = new Account(accountName, ACCOUNT_TYPE);
            //We logging in properly save a copy
            SharedPreferences prefs = getSharedPreferences(preference, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.clear();
            editor.putString("accountName", accountName);
            editor.putString("accountPassword", accountPassword);
            editor.putString("accountType", intent.getStringExtra(AccountManager.KEY_ACCOUNT_TYPE));

            if (getIntent().getBooleanExtra(ARG_IS_ADDING_NEW_ACCOUNT, false)) {
                Log.d(TAG, "finishLogin > addAccountExplicitly");
                String authToken = intent.getStringExtra(AccountManager.KEY_AUTHTOKEN);
                editor.putString("authToken", authToken);

                // Creating the account on the device and setting the auth token we got
                // (Not setting the auth token will cause another call to the server to authenticate the user)
                accountManager.addAccountExplicitly(account, accountPassword, null);
                accountManager.setAuthToken(account, authTokenType, authToken);
            } else {
                Log.d(TAG, "finishLogin > setPassword");
                try {
                    accountManager.setPassword(account, accountPassword);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }

            editor.commit();

            Log.d(TAG, "Edited preferences, saving: " + prefs.getAll());
            setAccountAuthenticatorResult(intent.getExtras());
            setResult(RESULT_OK, intent);
            startActivity(intent);
            finish();
        }
    }

}
