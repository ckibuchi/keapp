package com.rube.tt.keapp;

import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rube.tt.keapp.auth.AccountGeneral;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.utils.CustomAccountAunthenticationActivity;
import com.rube.tt.keapp.utils.Validation;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.util.ArrayList;

import static com.rube.tt.keapp.Login.ARG_ACCOUNT_TYPE;
import static com.rube.tt.keapp.Login.KEY_ERROR_MESSAGE;
import static com.rube.tt.keapp.Login.KEY_FULL_NAME;
import static com.rube.tt.keapp.Login.PARAM_USER_PASS;
import static com.rube.tt.keapp.auth.AccountGeneral.sServerAuthenticate;

public class RegisterActivity extends CustomAccountAunthenticationActivity {

	Button btnGCMRegister;
	Button btnAppShare;
	GoogleCloudMessaging gcm;
	Context context;
	String regId;

	public static final String REG_ID = "regId";
	private static final String APP_VERSION = "appVersion";

	static final String TAG = "Register Activity";


	private String mAccountType;
	private Toolbar toolbar;
	private ProgressDialog progressDialog;
	EditText _name,_mail,_passworda,_passwordb,_phone;

	private SharedPreferences sharedPreferences;
	public static final String preference = "MyAccountPreference" ;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		sharedPreferences = getSharedPreferences(preference, Context.MODE_PRIVATE);
		Log.d(TAG, "Preferences contains values :" + sharedPreferences.getAll());

		if (sharedPreferences.contains("accountName") ){

			Log.d(TAG, "Acccount preference contains mentioned items should start ke app now ..."+
					sharedPreferences.getString("accountName", ""));
			Bundle data = new Bundle();
			data.putString(AccountManager.KEY_ACCOUNT_NAME, sharedPreferences.getString("accountName", ""));
			data.putString(AccountManager.KEY_ACCOUNT_TYPE, sharedPreferences.getString("accountType", ""));
			data.putString(AccountManager.KEY_AUTHTOKEN, sharedPreferences.getString("authToken", ""));
			data.putString(PARAM_USER_PASS, sharedPreferences.getString("accountPassword", ""));
			data.putString(KEY_FULL_NAME, sharedPreferences.getString("fullName", ""));

			final Intent mainActivity = new Intent(RegisterActivity.this, KEApp.class);
			mainActivity.putExtras(data);
			setAccountAuthenticatorResult(mainActivity.getExtras());
			setResult(RESULT_OK, mainActivity);
			startActivity(mainActivity);
			finish();

		}

		setContentView(R.layout.register);
		context = getApplicationContext();
		registerviews();
		try {
			TelephonyManager telemamanger = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
			String getSimSerialNumber = telemamanger.getSimSerialNumber();
			String getSimNumber = telemamanger.getLine1Number();
			((TextView) findViewById(R.id.msisdn)).setText(getSimNumber);
		}
		catch(Exception e)
		{}
		toolbar = (Toolbar)findViewById(R.id.tool_bar);
		toolbar.getMenu().clear();
		toolbar.setLogo(null);
		toolbar.setTitle(R.string.title_signup_page);
		toolbar.setNavigationIcon(null);
		setSupportActionBar(toolbar);

		mAccountType = getIntent().getStringExtra(ARG_ACCOUNT_TYPE);

		findViewById(R.id.already_member).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				setResult(RESULT_CANCELED);
				Intent i = new Intent(RegisterActivity.this, Login.class);
				startActivity(i);
				finish();
			}
		});
		findViewById(R.id.create_account).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();
				String accountPassword2 = ((TextView) findViewById(R.id.accountPassword2)).getText().toString().trim();
				if (!accountPassword.equalsIgnoreCase(accountPassword2)) {
					try {
						Toast.makeText(getBaseContext(), "Password Mismatch ", Toast.LENGTH_LONG).show();
					} catch (Exception e) {

						e.printStackTrace();
					}
					return;
				}
				if (checkValidation())
					createAccount();



			}
		});

		getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
		getSupportActionBar().setHomeButtonEnabled(false);
	}

	public void registerviews()
	{
		_name=((EditText) findViewById(R.id.name));
		_mail = ((EditText) findViewById(R.id._email));
		_passworda = ((EditText) findViewById(R.id.accountPassword));
		_passwordb = ((EditText) findViewById(R.id.accountPassword2));
		_phone=((EditText) findViewById(R.id.msisdn));
		// TextWatcher would let us check validation error on the fly
		_name.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.hasText(_name);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});

		_mail.addTextChangedListener(new TextWatcher() {
			// after every change has been made to this editText, we would like to check validity
			public void afterTextChanged(Editable s) {
				Validation.isEmailAddress(_mail, true);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});

		_phone.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.isPhoneNumber(_phone, true);
				Validation.hasText(_phone);
			}

			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			public void onTextChanged(CharSequence s, int start, int before, int count) {
			}
		});
		_passworda.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.hasText(_passworda);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});
		_passwordb.addTextChangedListener(new TextWatcher() {
			public void afterTextChanged(Editable s) {
				Validation.hasText(_passwordb);
			}
			public void beforeTextChanged(CharSequence s, int start, int count, int after){}
			public void onTextChanged(CharSequence s, int start, int before, int count){}
		});


	}

	private boolean checkValidation() {
		boolean ret = true;

		if (!Validation.hasText(_name)) ret = false;
		if (!Validation.isEmailAddress(_mail, true)) ret = false;
		if (!Validation.isPhoneNumber(_phone, true)) ret = false;
		if (!Validation.hasText(_passworda)) ret = false;
		if (!Validation.hasText(_passwordb)) ret = false;


		return ret;
	}
	private void createAccount() {

		// TODO:  Validation!
		try
		{

			regId=registerGCM();

			if(regId==null)
			{
				Toast.makeText(getBaseContext(),"Could not Register your phone\n Please Try later", Toast.LENGTH_SHORT).show();
				return;
			}


		}
		catch(Exception e)
		{

			e.printStackTrace();
			return;
		}
		new AsyncTask<String, Void, Intent>() {


			String name = _name.getText().toString().trim();
			String _email = _mail.getText().toString().trim();
			String accountPassword = _passworda.getText().toString().trim();
			String msisdn=_phone.getText().toString().trim();
			final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();

			@Override
			protected Intent doInBackground(String... params) {

				String authtoken = null;
				Intent result;
				Bundle data = new Bundle();

				nameValuePairs.add(new BasicNameValuePair("username", _email));
				nameValuePairs.add(new BasicNameValuePair("email", _email));
				nameValuePairs.add(new BasicNameValuePair("password", accountPassword));
				nameValuePairs.add(new BasicNameValuePair("first_name", name));
				nameValuePairs.add(new BasicNameValuePair("last_name", name));
				nameValuePairs.add(new BasicNameValuePair("msisdn", msisdn));
				nameValuePairs.add(new BasicNameValuePair("regId", regId));
				Log.d(TAG, "> Started Reg");




				try {
					authtoken = sServerAuthenticate.userSignUp(nameValuePairs, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS,accountPassword);

					if (authtoken != null){
						createProfile(name, _email);
						data.putString(KEY_FULL_NAME, name);
						data.putString(AccountManager.KEY_ACCOUNT_NAME, _email);
						data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
						data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
						data.putString(PARAM_USER_PASS, accountPassword);
						result = new Intent(RegisterActivity.this, FinishSignup.class);
					}else{
						result = new Intent();
						data.putString(KEY_ERROR_MESSAGE, "Error creating user account, please try again later");
					}

				} catch (Exception e) {
					Log.d(TAG, "Error in get auth token:"+e.getMessage());
					e.printStackTrace();
					result = new Intent();
					data.putString(KEY_ERROR_MESSAGE, "Error creating user account, please try again later");
				}

				result.putExtras(data);
				return result;
			}

			@Override
			protected void onPreExecute() {

				progressDialog = new ProgressDialog(RegisterActivity.this);
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
				if (progressDialog!=null) {
					progressDialog.dismiss();
				}
				try {
					if (intent.hasExtra(KEY_ERROR_MESSAGE)) {
						Toast.makeText(getBaseContext(), intent.getStringExtra(KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
					} else {
						RegisterActivity.this.startActivity(intent);
						finish();
					}
				}
				catch(Exception e)
				{}
			}
		}.execute();
	}

	public String  createProfile(String fullName, String email){
		ContentValues args = new ContentValues();

		Log.d(TAG, "Saving profile name: "+ fullName + ", Email :"+email);

		args.put(DB.Profile.SERVER_ID, 0);
		args.put(DB.Profile.USERNAME, email);
		args.put(DB.Profile.EMAIL, email);
		args.put(DB.Profile.NAME, fullName);

		DBProvider dbProvider = new DBProvider(this);
		Uri uri = dbProvider.insert(DB.Profile.CONTENT_URI, args);
		return  uri.getLastPathSegment();
	}

	@Override
	public void onBackPressed() {
		setResult(RESULT_CANCELED);
		super.onBackPressed();
	}

	public String registerGCM() {

		gcm = GoogleCloudMessaging.getInstance(this);
		regId = getRegistrationId(context);

		if (TextUtils.isEmpty(regId)) {

			registerInBackground();

			Log.d("RegisterActivity",
					"registerGCM - successfully registered with GCM server - regId: "
							+ regId);
		} else {
			Log.i(TAG, "RegId already available. RegId: " + regId);
			/*Toast.makeText(getApplicationContext(),
					"RegId already available. RegId: " + regId,
					Toast.LENGTH_LONG).show();*/
		}
		return regId;
	}

	private String getRegistrationId(Context context) {
		final SharedPreferences prefs = getSharedPreferences(
				Login.class.getSimpleName(), Context.MODE_PRIVATE);
		String registrationId = prefs.getString(REG_ID, "");
		if (registrationId.isEmpty()) {
			Log.i(TAG, "Registration not found.");
			return "";
		}
		int registeredVersion = prefs.getInt(APP_VERSION, Integer.MIN_VALUE);
		int currentVersion = getAppVersion(context);
		if (registeredVersion != currentVersion) {
			Log.i(TAG, "App version changed.");
			return "";
		}
		return registrationId;
	}

	private static int getAppVersion(Context context) {
		try {
			PackageInfo packageInfo = context.getPackageManager()
					.getPackageInfo(context.getPackageName(), 0);
			return packageInfo.versionCode;
		} catch (NameNotFoundException e) {
			Log.d("RegisterActivity",
					"I never expected this! Going down, going down!" + e);
			throw new RuntimeException(e);
		}
	}

	private void registerInBackground() {
		new AsyncTask<Void, Void, String>() {
			@Override
			protected String doInBackground(Void... params) {
				String msg = "";
				try {
					if (gcm == null) {
						gcm = GoogleCloudMessaging.getInstance(context);
					}
					regId = gcm.register(Config.GOOGLE_PROJECT_ID);
					Log.d("RegisterActivity", "registerInBackground - regId: "
							+ regId);
					msg = "Device registered, registration ID=" + regId;

					storeRegistrationId(context, regId);
				} catch (IOException ex) {
					msg = "Error :" + ex.getMessage();
					Log.d("RegisterActivity", "Error: " + msg);
				}
				Log.d("RegisterActivity", "AsyncTask completed: " + msg);
				return msg;
			}

			@Override
			protected void onPostExecute(String msg) {
				Toast.makeText(getApplicationContext(),
						"Registered with GCM Server." + msg, Toast.LENGTH_LONG)
						.show();
			}
		}.execute(null, null, null);
	}

	private void storeRegistrationId(Context context, String regId) {
		final SharedPreferences prefs = getSharedPreferences(
				Login.class.getSimpleName(), Context.MODE_PRIVATE);
		int appVersion = getAppVersion(context);
		Log.i(TAG, "Saving regId on app version " + appVersion);
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString(REG_ID, regId);
		editor.putInt(APP_VERSION, appVersion);
		editor.commit();
	}
}
