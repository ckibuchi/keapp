package com.rube.tt.keapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.rube.tt.keapp.adapters.StatusListAdapter;
import com.rube.tt.keapp.models.StatusModel;

import java.util.ArrayList;

/**
 * Created by rube on 5/3/15.
 */
public class UserStatus extends ActionBarActivity {

    private final String TAG = UserStatus.class.getSimpleName();
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.status);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.status_header);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

        ListView list= ( ListView )findViewById(R.id.status_list_view);
        StatusListAdapter adapter=new StatusListAdapter(this, getStatuses());
        list.setAdapter( adapter );

    }

    private ArrayList<StatusModel> getStatuses(){
        ArrayList<StatusModel> statusModels = new ArrayList<StatusModel>();

        StatusModel statusModel = new StatusModel();
        statusModel.setStatusMessasge("I am using KEapp");
        statusModels.add(statusModel);

        statusModel = new StatusModel();
        statusModel.setStatusMessasge("Nothing big just bags !!");
        statusModels.add(statusModel);

        statusModel = new StatusModel();
        statusModel.setStatusMessasge("No one called you here");
        statusModels.add(statusModel);

        return  statusModels;

    }



}
