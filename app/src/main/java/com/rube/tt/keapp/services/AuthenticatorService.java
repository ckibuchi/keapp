package com.rube.tt.keapp.services;

/**
 * Created by rube on 5/23/15.
 */

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.text.TextUtils;
import android.util.Log;

import com.rube.tt.keapp.Login;
import com.rube.tt.keapp.auth.AccountGeneral;
import static android.accounts.AccountManager.KEY_BOOLEAN_RESULT;

public class AuthenticatorService extends Service {

    private final String TAG = AuthenticatorService.class.getSimpleName();
    @Override
    public IBinder onBind(Intent intent) {
        if (intent.getAction().equals(
                android.accounts.AccountManager.ACTION_AUTHENTICATOR_INTENT))
            return null;

        AbstractAccountAuthenticator authenticator =
                new KeappAuthenticator(this);
        return authenticator.getIBinder();
    }

    public class KeappAuthenticator extends AbstractAccountAuthenticator {
        protected Context context;
        private final String TAG = KeappAuthenticator.class.getSimpleName();

        public KeappAuthenticator(Context context) {
            super(context);
            this.context = context;
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
                                 String authTokenType, String[] requiredFeatures, Bundle options)
                throws NetworkErrorException {
            // We absolutely cannot add an account without some information
            // from the user; so we're definitely going to return an Intent
            // via KEY_INTENT
            final Bundle bundle = new Bundle();

            // We're going to use a LoginActivity to talk to the user (mContext
            // we'll have noted on construction).
            final Intent intent = new Intent(context, Login.class);

            // We can configure that activity however we wish via the
            // Intent.  We'll set ARG_IS_ADDING_NEW_ACCOUNT so the Activity
            // knows to ask for the account name as well
            intent.putExtra(Login.ARG_ACCOUNT_TYPE, accountType);
            intent.putExtra(Login.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(Login.ARG_IS_ADDING_NEW_ACCOUNT, true);

            // It will also need to know how to send its response to the
            // account manager; LoginActivity must derive from
            // AccountAuthenticatorActivity, which will want this key set
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE,
                    response);

            // Wrap up this intent, and return it, which will cause the
            // intent to be run
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return  bundle;

        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account,
                                         Bundle options) throws NetworkErrorException {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            // TODO Auto-generated method stub
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
                                   String authTokenType, Bundle options) throws NetworkErrorException {
            Log.d(TAG,  "> getAuthToken");

            // If the caller requested an authToken type we don't support, then
            // return an error
            if (!authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY) &&
                    !authTokenType.equals(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
                return result;
            }

            // Extract the username and password from the Account Manager, and ask
            // the server for an appropriate AuthToken.
            final AccountManager am = AccountManager.get(context);

            String authToken = am.peekAuthToken(account, authTokenType);

            Log.d(TAG, "PeekAuthToken returned - " + authToken);

            // Lets give another try to authenticate the user
            if (TextUtils.isEmpty(authToken)) {
                final String password = am.getPassword(account);
                if (password != null) {
                    try {
                        Log.d(TAG, "re-authenticating with the existing password");
                        authToken = AccountGeneral.sServerAuthenticate.userSignIn(account.name, password, authTokenType);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            // If we get an authToken - we return it
            if (!TextUtils.isEmpty(authToken)) {
                final Bundle result = new Bundle();
                result.putString(AccountManager.KEY_ACCOUNT_NAME, account.name);
                result.putString(AccountManager.KEY_ACCOUNT_TYPE, account.type);
                result.putString(AccountManager.KEY_AUTHTOKEN, authToken);
                return result;
            }

            // If we get here, then we couldn't access the user's password - so we
            // need to re-prompt them for their credentials. We do that by creating
            // an intent to display our AuthenticatorActivity.
            final Intent intent = new Intent(context, Login.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            intent.putExtra(Login.ARG_ACCOUNT_TYPE, account.type);
            intent.putExtra(Login.ARG_AUTH_TYPE, authTokenType);
            intent.putExtra(Login.ARG_ACCOUNT_NAME, account.name);
            final Bundle bundle = new Bundle();
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
            return bundle;

        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {

            if (AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS.equals(authTokenType))
                return AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS_LABEL;
            else if (AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY.equals(authTokenType))
                return AccountGeneral.AUTHTOKEN_TYPE_READ_ONLY_LABEL;
            else
                return authTokenType + " (Label)";
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account,
                                  String[] features) throws NetworkErrorException {
            final Bundle result = new Bundle();
            result.putBoolean(KEY_BOOLEAN_RESULT, false);
            return result;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
                                        String authTokenType, Bundle options) throws NetworkErrorException {
            // TODO Auto-generated method stub
            return null;
        }

    };
}

