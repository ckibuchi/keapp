package in.co.madhur.chatbubblesdemo;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import in.co.madhur.chatbubblesdemo.adapters.BroadcastListAdapter;
import in.co.madhur.chatbubblesdemo.models.SimpleContactModel;

import java.util.ArrayList;

/**
 * Created by rube on 5/3/15.
 */
public class BroadCast extends ActionBarActivity {

    private final String TAG = BroadCast.class.getSimpleName();
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.broadcast_new);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.broadcast_header);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

        ListView list= ( ListView )findViewById(R.id.broadcast_contact_list_view);
        BroadcastListAdapter adapter=new BroadcastListAdapter(this, getSelectedContacts() );
        list.setAdapter( adapter );

    }


    private ArrayList<SimpleContactModel> getSelectedContacts(){
        ArrayList<SimpleContactModel> simpleContactModels = new ArrayList<SimpleContactModel>();
        return simpleContactModels;
    }


}
