package in.co.madhur.chatbubblesdemo.fragments;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.adapters.MessageListAdapter;
import in.co.madhur.chatbubblesdemo.db.DB;
import in.co.madhur.chatbubblesdemo.db.DBProvider;
import in.co.madhur.chatbubblesdemo.db.DbManager;
import in.co.madhur.chatbubblesdemo.models.ChatMessage;
import in.co.madhur.chatbubblesdemo.models.ContactModel;
import in.co.madhur.chatbubblesdemo.utils.Utils;

/**
 * Created by rube on 4/1/15.
 */
public class MessageFragment extends Fragment {

    private static String TAG = MessageFragment.class.getSimpleName();
    // Inflate the fragment layout we defined above for this fragment
    // Set the associated text for the title\

    private ListView listView;
    private static Activity activity;
    public static ArrayList<ChatMessage> chatMessages;
    MessageListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.message_page, container, false);
        activity = this.getActivity();
        chatMessages = new ArrayList<>();
        listView = ( ListView )view.findViewById( R.id.message_list_view );
         adapter=new MessageListAdapter(getActivity(), getMessageList() );

        if(adapter.getCount() == 0) {
            ViewGroup parentGroup = (ViewGroup)listView.getParent();
            View emptyView =  inflater.inflate(R.layout.empty_list_item, parentGroup, false);
            TextView emptyText = (TextView)emptyView.findViewById(R.id.empty_list_text);
            emptyText.setText("No chats found.\n Choose a contact and start chatting ");
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

    public void setmessageList(ArrayList<ChatMessage> messages)
    {
        if(messages!=null) {
            chatMessages = messages;
            refresh();
        }
    }
    public  MessageFragment()
    {}

    public ArrayList<ChatMessage> getMessageList(){

        ArrayList<ChatMessage> messageModels = new ArrayList<ChatMessage>();
        try {
        DbManager dbManager =  new DbManager(getContext());
        messageModels=dbManager.getRecentChats();


            for (ChatMessage msg : messageModels) {
                if (msg.getMessage().trim().length() == 0) {
                    Log.i("EMPTY ","The message is empty");

                }
                else {
                    Log.i("MESSAGE READ ",msg.getMessage());
                    chatMessages.add(msg);

                    if (adapter != null)
                        adapter.notifyDataSetChanged();
                }
                // sendComment(msg.getMessage(), msg.getUserType());
            }
        }//End of try
        catch(Exception e)
        {
            e.printStackTrace();

        }

        return chatMessages;

    }

    public static  ArrayList<ContactModel> getContacts(int offset, int limit, String query) {

        //check if we have data skip keeping otherwise fetch all contacts to out contacts table
        //create contact->profile->profile status
        //              ->status

        DBProvider dbProvider = new DBProvider(activity);
        ContactModel simpleContactModel;
        ArrayList<ContactModel> simpleContactModels = new ArrayList<ContactModel>();

        String filter = "";
        if(query != null && !query.equals("")){
            filter = " where ( p."+ DB.Profile.NAME + " LIKE '%"+query+"%' or p."+ DB.Profile.MSISDN + " LIKE '%"+query+"%')";

        }
        //has contact ... check id contacts already exist
        String rawSql = "Select * from " + DB.Contact.TABLE + " c inner join " + DB.Profile.TABLE +
                " p on  c." + DB.Contact.PROFILE + " = p." + DB.Profile._ID + " inner join " + DB.ProfileStatus.TABLE
                + " ps on  p." + DB.Profile._ID + "= ps." + DB.ProfileStatus.PROFILE + filter + " order by p." + DB.Profile.NAME
                + " asc limit "+offset+ ", "+limit;

        Log.d(TAG, rawSql);
        Cursor localContacts = dbProvider.rawQuery(DB.Contact.CONTENT_URI, rawSql, null);
        try {
            if (localContacts.moveToFirst()) {
                do {
                    int isMember = localContacts.getInt(localContacts.getColumnIndex(DB.Contact.IS_MEMBER));

                    //Long photoId = localContacts.getLong(localContacts.getColumnIndex(DB.Profile.PHOTO));
                    String name = localContacts.getString(localContacts.getColumnIndex(DB.Profile.NAME));
                    String message = localContacts.getString(localContacts.getColumnIndex(DB.ProfileStatus.MESSAGE));
                    String msisdn = localContacts.getString(localContacts.getColumnIndex(DB.Profile.MSISDN));
                    String photoPath = localContacts.getString(localContacts.getColumnIndex(DB.Profile.PHOTO));




                    int id = localContacts.getInt(localContacts.getColumnIndex(DB.Contact._ID));
                    Log.i("Actual status ",msisdn+", is member "+isMember+" ID "+id+" alt "+localContacts.getInt(localContacts.getColumnIndex(DB.Contact._ID)));

                    msisdn=msisdn.replace("-", "");
                    msisdn= Utils.getNineDigits(msisdn);
                    if(msisdn==null)
                    {
                        Log.e("NUMBER ERR ","INVALID PHONE ");
                    }
                    else
                    {
                        simpleContactModel = new ContactModel();
                        simpleContactModel.setName(name);
                        simpleContactModel.setId(id);
                        Log.d("Immediate id ", "" + simpleContactModel.getId());
                        simpleContactModel.setPhoneNumber(msisdn);
                        simpleContactModel.setStatusMessage(message);
                        Log.i("Immediate member? ",""+isMember);
                        if(isMember == 1){
                            simpleContactModel.setIsMember(true);
                        }else if(isMember ==2){
                            simpleContactModel.setIsInvited(true);
                        }

                        Log.d(TAG, "Looking form image on "+photoPath);

                        simpleContactModel.setPic(Utils.getImageBitmap(activity, photoPath, "profile", 55, 55));
                        simpleContactModels.add(simpleContactModel);}

                } while (localContacts.moveToNext());
            }



        } catch (Exception ex) {
            ex.printStackTrace();
        }finally {
            if(localContacts != null) {
                localContacts.close();
            }
        }

        Log.d(TAG, "Returning simpleContactModels: "+simpleContactModels.size());
        return  simpleContactModels;
    }

    public void refresh()
    { try{
        listView.post(new Runnable() {
            @Override
            public void run() {
                adapter.notifyDataSetChanged();

            }
        });



    }
    catch(Exception e)
    {e.printStackTrace();}}


}
