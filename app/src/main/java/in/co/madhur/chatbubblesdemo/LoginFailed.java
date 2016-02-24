package in.co.madhur.chatbubblesdemo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

/**
 * Created by rube on 19/09/15.
 */
public class LoginFailed   extends ActionBarActivity {

    private final String TAG = this.getClass().getSimpleName();
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(in.co.madhur.chatbubblesdemo.R.layout.login_failed);
        toolbar = (Toolbar)findViewById(in.co.madhur.chatbubblesdemo.R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(in.co.madhur.chatbubblesdemo.R.string.title_login_failed);
        toolbar.setNavigationIcon(null);
        setSupportActionBar(toolbar);

        findViewById(in.co.madhur.chatbubblesdemo.R.id.re_login_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                Intent i = new Intent(in.co.madhur.chatbubblesdemo.LoginFailed.this, Login.class);
                startActivity(i);
                finish();
            }
        });

        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
