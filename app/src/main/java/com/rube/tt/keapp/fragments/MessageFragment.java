package com.rube.tt.keapp.fragments;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.rube.tt.keapp.R;
import com.rube.tt.keapp.adapters.MessageListAdapter;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.models.MessageModel;

import java.util.ArrayList;

/**
 * Created by rube on 4/1/15.
 */
public class MessageFragment extends Fragment {

    private String TAG = MessageFragment.class.getSimpleName();
    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title\

    private ListView listView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_page, container, false);


        listView = ( ListView )view.findViewById( R.id.message_list_view );
        MessageListAdapter adapter=new MessageListAdapter(getActivity(), getMessageList() );

        if(adapter.getCount() == 0) {
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView =  inflater.inflate(R.layout.empty_list_item, parentGroup, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyText.setText("No chats found ");
            parentGroup.addView(emptyView);
            Log.d(TAG, "Setting empty text");
            listView.setEmptyView(emptyView);
        } else {
            Log.d(TAG, "Setting Data into listView text");
            listView.setAdapter(adapter);
            //listView.setOnScrollListener(listViewOnScrollListener);

        }

        return view;
    }

    private ArrayList<MessageModel> getMessageList(){

        Uri messageUrl = DB.Chat.CONTENT_URI;

        Cursor messageResults= new DBProvider(getActivity()).query(messageUrl, new String[]{
                DB.Chat.TABLE + "." + DB.Chat._ID,
                DB.Chat.TABLE + "." + DB.Chat.FROM,
                DB.Chat.TABLE + "." + DB.Chat.TO,
                DB.Chat.TABLE + "." + DB.Chat.MEDIA,
                DB.Chat.TABLE + "." + DB.Chat.MEDIA,
                DB.Profile.TABLE + "." + DB.Profile.PHOTO,
        }, null, null, null);

        ArrayList<MessageModel> messageModels = new ArrayList<MessageModel>();

        MessageModel messageModel;

        messageResults.moveToFirst();

        while (messageResults.isAfterLast() == false) {
            messageModel = new MessageModel();
            messageModel.setProfilePhotoId(messageResults.getInt(
                    messageResults.getColumnIndex(DB.Profile.PHOTO)));
            messageModel.setMediaId(messageResults.getInt(messageResults.getColumnIndex(DB.Chat.MEDIA)));
            messageModel.setDate(messageResults.getString(messageResults.getColumnIndex(DB.Chat.DATE_CREATED)));
            messageModel.setName(messageResults.getString(messageResults.getColumnIndex(DB.Chat.TO)));
            messageModel.setMessage(messageResults.getString(messageResults.getColumnIndex(DB.Chat.MESSAGE)));
            messageModels.add(messageModel);
            messageResults.moveToNext();
        }
        return messageModels;

    }


}
