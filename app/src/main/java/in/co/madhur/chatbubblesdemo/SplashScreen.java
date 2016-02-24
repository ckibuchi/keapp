package in.co.madhur.chatbubblesdemo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import in.co.madhur.chatbubblesdemo.auth.AccountGeneral;

/**
 * Created by rube on 3/30/15.
 */
public class SplashScreen extends Activity {


    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 500;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.co.madhur.chatbubblesdemo.R.layout.activity_splash);

        /* New Handler to start the Menu-Activity
         * and close this Splash-Screen after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the Menu-Activity. */
                Intent mainIntent = new Intent(in.co.madhur.chatbubblesdemo.SplashScreen.this, Login.class);
                mainIntent.putExtra(Login.ARG_ACCOUNT_TYPE, AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS);
                in.co.madhur.chatbubblesdemo.SplashScreen.this.startActivity(mainIntent);
                in.co.madhur.chatbubblesdemo.SplashScreen.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

}