package in.co.madhur.chatbubblesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by rube on 10/09/15.
 */

public class FinishSignup  extends ActionBarActivity {

    private String TAG = getClass().getSimpleName();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(in.co.madhur.chatbubblesdemo.R.layout.signup_success);
        toolbar = (Toolbar)findViewById(in.co.madhur.chatbubblesdemo.R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(in.co.madhur.chatbubblesdemo.R.string.title_finish_signup);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);
        findViewById(in.co.madhur.chatbubblesdemo.R.id.loginbutton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Since there can only be one AuthenticatorActivity, we call the sign up activity, get his results,
                // and return them in setAccountAuthenticatorResult(). See finishLogin().
                Intent  login= new Intent(getBaseContext(), Login.class);
                if (getIntent().getExtras() != null) {
                    login.putExtras(getIntent().getExtras());
                }
                startActivity(login);
                finish();

            }
        });
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
