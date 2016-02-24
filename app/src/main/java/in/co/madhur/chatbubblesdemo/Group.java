package in.co.madhur.chatbubblesdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;

/**
 * Created by rube on 5/3/15.
 */
public class Group extends ActionBarActivity {

    private final String TAG = in.co.madhur.chatbubblesdemo.Group.class.getSimpleName();
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(in.co.madhur.chatbubblesdemo.R.layout.group_new);

        toolbar = (Toolbar)findViewById(in.co.madhur.chatbubblesdemo.R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(in.co.madhur.chatbubblesdemo.R.string.group_header);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

    }




}
