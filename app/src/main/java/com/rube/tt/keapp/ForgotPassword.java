package com.rube.tt.keapp;

/**
 * Created by rube on 5/24/15.
 */


import android.accounts.AccountManager;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.rube.tt.keapp.auth.AccountGeneral;

import static com.rube.tt.keapp.Login.ARG_ACCOUNT_TYPE;
import static com.rube.tt.keapp.Login.KEY_ERROR_MESSAGE;
import static com.rube.tt.keapp.Login.PARAM_USER_PASS;
import static com.rube.tt.keapp.auth.AccountGeneral.sServerAuthenticate;

/**
 * In charge of the Sign up process. Since it's not an AuthenticatorActivity decendent,
 * it returns the result back to the calling activity, which is an AuthenticatorActivity,
 * and it return the result back to the Authenticator
 *
 * User: udinic
 */
public class ForgotPassword extends ActionBarActivity {

    private String TAG = getClass().getSimpleName();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.forgot_password);
        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.title_forgot_password);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        findViewById(R.id.back_to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(ForgotPassword.this, Login.class);
                startActivity(i);
                finish();
                }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);
    }


}


