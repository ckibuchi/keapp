package in.co.madhur.chatbubblesdemo;

/**
 * Created by rube on 5/24/15.
 */


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import static in.co.madhur.chatbubblesdemo.Login.ARG_ACCOUNT_TYPE;
import static in.co.madhur.chatbubblesdemo.Login.KEY_ERROR_MESSAGE;
import static in.co.madhur.chatbubblesdemo.Login.PARAM_USER_PASS;

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

        setContentView(in.co.madhur.chatbubblesdemo.R.layout.forgot_password);
        toolbar = (Toolbar)findViewById(in.co.madhur.chatbubblesdemo.R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(in.co.madhur.chatbubblesdemo.R.string.title_forgot_password);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        findViewById(in.co.madhur.chatbubblesdemo.R.id.back_to_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(in.co.madhur.chatbubblesdemo.ForgotPassword.this, Login.class);
                startActivity(i);
                finish();
                }
        });


        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);
    }


}


