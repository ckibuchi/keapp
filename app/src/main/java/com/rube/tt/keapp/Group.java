package com.rube.tt.keapp;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ListView;

import com.rube.tt.keapp.adapters.BroadcastListAdapter;
import com.rube.tt.keapp.models.SimpleContactModel;

import java.util.ArrayList;

/**
 * Created by rube on 5/3/15.
 */
public class Group extends ActionBarActivity {

    private final String TAG = Group.class.getSimpleName();
    private Toolbar toolbar;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.group_new);

        toolbar = (Toolbar)findViewById(R.id.tool_bar);
        toolbar.getMenu().clear();
        toolbar.setLogo(null);
        toolbar.setTitle(R.string.group_header);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
        getSupportActionBar().setHomeButtonEnabled(true);

    }




}
