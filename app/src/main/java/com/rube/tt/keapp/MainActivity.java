package com.rube.tt.keapp;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.rube.tt.keapp.adapters.ChatMessageListAdapter;
import com.rube.tt.keapp.data.SyncAdapter;
import com.rube.tt.keapp.db.DB;
import com.rube.tt.keapp.db.DBProvider;
import com.rube.tt.keapp.db.DbManager;
import com.rube.tt.keapp.model.Status;
import com.rube.tt.keapp.model.UserType;
import com.rube.tt.keapp.models.ChatMessage;
import com.rube.tt.keapp.utils.Utils;
import com.rube.tt.keapp.widgets.Emoji;
import com.rube.tt.keapp.widgets.EmojiView;
import com.rube.tt.keapp.widgets.SizeNotifierRelativeLayout;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;


public class MainActivity extends ActionBarActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    public static ListView chatListView;
    private EditText chatEditText1;
    public static ArrayList<ChatMessage> chatMessages;
    private ImageView enterChatView1, emojiButton;
    public static ChatListAdapter listAdapter;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    GoogleCloudMessaging gcm;
    AtomicInteger ccsMsgId = new AtomicInteger();
    AsyncTask<Void, Void, String> sendTask;
    private WindowManager.LayoutParams windowLayoutParams;

public MainActivity()
{}
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v==chatEditText1)
                {
                    sendMessage(editText.getText().toString(), UserType.ME);
                }

                chatEditText1.setText("");

                return true;
            }
            return false;

        }
    };

    private ImageView.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            if(v==enterChatView1)
            {
                sendMessage(chatEditText1.getText().toString(), UserType.ME);
            }

            chatEditText1.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText1.getText().toString().equals("")) {

            } else {
                enterChatView1.setImageResource(R.drawable.ic_chat_send);

            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            if(editable.length()==0){
                enterChatView1.setImageResource(R.drawable.ic_chat_send);
            }else{
                enterChatView1.setImageResource(R.drawable.ic_chat_send_active);
            }
        }
    };

    private static final String TAG = MainActivity.class.getSimpleName();

    private Button btnSend;
    private EditText inputMsg;
    /*
        EmojiconEditText mEditEmojicon;
        EmojiconTextView mTxtEmojicon;*/
    CheckBox mCheckBox;

    // Chat messages list adapter
    public static ChatMessageListAdapter adapter;
    private ArrayList<ChatMessage> listMessages;
    private ListView listViewMessages;
    private Toolbar toolbar;
    private ImageView emojiconsIcon;
    private LinearLayout parentLayout;
    private ImageView emojiconsSubmit;
    private LinearLayout emojiconsNonSubmit;


    //private XMPPManager xmppManager;

    // Client name
    private String name = null;
    public static String msisdn=null;

    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "message", TAG_EXIT = "exit";

    public static Handler UIHandler;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        toolbar = (Toolbar)findViewById(R.id.new_chat_toolbar_layout);
        toolbar.getMenu().clear();
        //toolbar.setLogo(null);
        //toolbar.setTitle(R.string.chat_page_detail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);
        gcm = GoogleCloudMessaging.getInstance(this);
        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");
        msisdn = i.getStringExtra("phone");
        try
        {
            TextView username=(TextView)findViewById(R.id.user_names);
            username.setText(name);
        }
        catch(Exception e)
        {}
        listViewMessages = (ListView) findViewById(R.id.chat_list_view);



        chatMessages = new ArrayList<>();
        listMessages = new ArrayList<ChatMessage>();



        //adding emoji
        // mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);


        emojiconsIcon = (ImageView)findViewById(R.id.emojicons_pop_icon);
        //emojiIconsCover = (LinearLayout) findViewById(R.id.footer_for_emojiicons);
        parentLayout = (LinearLayout) findViewById(R.id.main_chat_layout_holder);
        Log.d(TAG, "Found parent layout "+parentLayout.toString());



        //
        chatListView = (ListView) findViewById(R.id.chat_list_view);

        chatEditText1 = (EditText) findViewById(R.id.chat_edit_text1);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat1);

        // Hide the emoji on click of edit text
        chatEditText1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });


        emojiButton = (ImageView)findViewById(R.id.emojiButton);
      // emojiButton = (ImageView)findViewById(R.id.emojicons_pop_icon);


        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

         listAdapter = new ChatListAdapter(chatMessages, this);
        //Imported


        chatListView.setAdapter(listAdapter);

        chatEditText1.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText1.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.chat_layout);
        sizeNotifierRelativeLayout.delegate = this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);


        getChatMessages();
    }

    public void sendMessage(final String messageText, final UserType userType)
    {
       if(messageText.trim().length()==0)
            return;

        String id = Integer.toString(ccsMsgId.incrementAndGet());
        final ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.NOT_SENT);
        message.setMessage(messageText);
        message.setUserType(userType);
        message.setMessageTime(new Date());
        message.setMessageID(id);
        chatMessages.add(message);

        if(listAdapter!=null)
            listAdapter.notifyDataSetChanged();


        sendMessageToXmpp("ECHO", id, message);

    }
    private void sendMessageToXmpp(final String action,final String id,final ChatMessage message) {

        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {

                Bundle data = new Bundle();
                data.putString("ACTION", action);
                data.putString("CLIENT_MESSAGE", "[\"FROM\":\"719182382\",\"text\":\"Are you there?\"]");

                String ret="ERR";
                try {
                    Log.d("SEND ACTIVITY", "messageid: " + id+" Info: "+message.getMessage());
                    gcm.send(Config.GOOGLE_PROJECT_ID + "@gcm.googleapis.com", id,
                            data); //Try GCM XMPP SERVER

                    //GCMNotification?shareRegId=1
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("msisdn",msisdn/*msisdn KEApp.accountName*/)); // Remember to change this to msisdn up there
                    String meso=" {\n" +
                            "\"FROM\":\""+KEApp.accountName/*msisdn*/+"\",\n" + //Remember to change This to KEApp.accountName
                            "\"text\":\""+message.getMessage()+""+"\"\n" + //Content
                            "}";
                    nameValuePairs.add(new BasicNameValuePair("msisdn", KEApp.accountName/*msisdn*/)); // Remember to change this to msisdn up there
                    nameValuePairs.add(new BasicNameValuePair("message", meso));
                    try {
                        JSONArray sent = SyncAdapter.POST(nameValuePairs, "GCMNotification","","");
                    }
                    catch(Exception e)
                    {}

                    Log.d("SEND ACTIVITY", "After gcm.send successful.");
                    ret="OK";
                } catch (IOException e) {
                    ret="AN ERROR OCCURED "+e.getMessage();
                    Log.d("SEND ACTIVITY", "Exception: " + e);
                    e.printStackTrace();
                }
                return ret;
            }

            @Override
            protected void onPostExecute(String result) {
                sendTask = null;
                if(result.equalsIgnoreCase("OK")) {
                    message.setMessageStatus(com.rube.tt.keapp.model.Status.SENT);
                    //Save The message to Local Database
                    saveToLocal(message, getApplicationContext());
                    if (listAdapter != null)
                        listAdapter.notifyDataSetChanged();
                }
                else
                {

                Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();

                }
            }

        };
        sendTask.execute(null, null, null);

    }
    public  void ReceiveMessage(final String messageText,String from,Context context)
    {
        if(messageText.trim().length()==0)
            return;

        final ChatMessage message = new ChatMessage();
        message.setMessageStatus(Status.DELIVERED);
        message.setMessage(messageText);
        message.setFrom(from);
        message.setUserType(UserType.OTHER);
        message.setMessageTime(new Date());
        Log.i(TAG,msisdn+" :"+from);
        if(msisdn==null)
        {
            KEApp.updatechats(messageText,from);
        }
        if(!Utils.getNineDigits(msisdn).equals(Utils.getNineDigits(from)))
        {
          KEApp.updatechats(messageText,from);
        }
        {

            chatMessages.add(message);

        }
        try{
            chatListView.post(new Runnable() {
                @Override
                public void run() {
                    listAdapter.notifyDataSetChanged();

                }
            });



        }
        catch(Exception e)
        {e.printStackTrace();}
        //Save The message to Local Database
        saveToLocal(message,context);
    }


    private Activity getActivity()
    {
        return this;
    }


    /**
     * Show or hide the emoji popup
     *
     * @param show
     */
    private void showEmojiPopup(boolean show) {
        showingEmoji = show;

        if (show) {
            if (emojiView == null) {
                if (getActivity() == null) {
                    return;
                }
                emojiView = new EmojiView(getActivity());

                emojiView.setListener(new EmojiView.Listener() {
                    public void onBackspace() {
                        chatEditText1.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText1.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText1.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText1.setText(chatEditText1.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText1.setSelection(j, j);
                        } catch (Exception e) {
                            Log.e(Constants.TAG, "Error showing emoji");
                        }
                    }
                });


                windowLayoutParams = new WindowManager.LayoutParams();
                windowLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
                if (Build.VERSION.SDK_INT >= 21) {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_ERROR;
                } else {
                    windowLayoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_PANEL;
                    windowLayoutParams.token = getActivity().getWindow().getDecorView().getWindowToken();
                }
                windowLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            }

            final int currentHeight;

            if (keyboardHeight <= 0)
                keyboardHeight = App.getInstance().getSharedPreferences("emoji", 0).getInt("kbd_height", AndroidUtilities.dp(200));

            currentHeight = keyboardHeight;

            WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);

            windowLayoutParams.height = currentHeight;
            windowLayoutParams.width = AndroidUtilities.displaySize.x;

            try {
                if (emojiView.getParent() != null) {
                    wm.removeViewImmediate(emojiView);
                }
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
            }

            try {
                wm.addView(emojiView, windowLayoutParams);
            } catch (Exception e) {
                Log.e(Constants.TAG, e.getMessage());
                return;
            }

            if (!keyboardVisible) {
                if (sizeNotifierRelativeLayout != null) {
                    sizeNotifierRelativeLayout.setPadding(0, 0, 0, currentHeight);
                }

                return;
            }

        }
        else {
            removeEmojiWindow();
            if (sizeNotifierRelativeLayout != null) {
                sizeNotifierRelativeLayout.post(new Runnable() {
                    public void run() {
                        if (sizeNotifierRelativeLayout != null) {
                            sizeNotifierRelativeLayout.setPadding(0, 0, 0, 0);
                        }
                    }
                });
            }
        }


    }


    /**
     * Remove emoji window
     */
    private void removeEmojiWindow() {
        if (emojiView == null) {
            return;
        }
        try {
            if (emojiView.getParent() != null) {
                WindowManager wm = (WindowManager) App.getInstance().getSystemService(Context.WINDOW_SERVICE);
                wm.removeViewImmediate(emojiView);
            }
        } catch (Exception e) {
            Log.e(Constants.TAG, e.getMessage());
        }
    }



    /**
     * Hides the emoji popup
     */
    public void hideEmojiPopup() {
        if (showingEmoji) {
            showEmojiPopup(false);
        }
    }

    /**
     * Check if the emoji popup is showing
     *
     * @return
     */
    public boolean isEmojiPopupShowing() {
        return showingEmoji;
    }



    /**
     * Updates emoji views when they are complete loading
     *
     * @param id
     * @param args
     */
    @Override
    public void didReceivedNotification(int id, Object... args) {
        if (id == NotificationCenter.emojiDidLoaded) {
            if (emojiView != null) {
                emojiView.invalidateViews();
            }

            if (chatListView != null) {
                chatListView.invalidateViews();
            }
        }
    }

    @Override
    public void onSizeChanged(int height) {

        Rect localRect = new Rect();
        getActivity().getWindow().getDecorView().getWindowVisibleDisplayFrame(localRect);

        WindowManager wm = (WindowManager) App.getInstance().getSystemService(Activity.WINDOW_SERVICE);
        if (wm == null || wm.getDefaultDisplay() == null) {
            return;
        }


        if (height > AndroidUtilities.dp(50) && keyboardVisible) {
            keyboardHeight = height;
            App.getInstance().getSharedPreferences("emoji", 0).edit().putInt("kbd_height", keyboardHeight).commit();
        }


        if (showingEmoji) {
            int newHeight = 0;

            newHeight = keyboardHeight;

            if (windowLayoutParams.width != AndroidUtilities.displaySize.x || windowLayoutParams.height != newHeight) {
                windowLayoutParams.width = AndroidUtilities.displaySize.x;
                windowLayoutParams.height = newHeight;

                wm.updateViewLayout(emojiView, windowLayoutParams);
                if (!keyboardVisible) {
                    sizeNotifierRelativeLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            if (sizeNotifierRelativeLayout != null) {
                                sizeNotifierRelativeLayout.setPadding(0, 0, 0, windowLayoutParams.height);
                                sizeNotifierRelativeLayout.requestLayout();
                            }
                        }
                    });
                }
            }
        }


        boolean oldValue = keyboardVisible;
        keyboardVisible = height > 0;
        if (keyboardVisible && sizeNotifierRelativeLayout.getPaddingBottom() > 0) {
            showEmojiPopup(false);
        } else if (!keyboardVisible && keyboardVisible != oldValue && showingEmoji) {
            showEmojiPopup(false);
        }

    }

    private void getChatMessages(){

        listMessages = new ArrayList<ChatMessage>();


            DbManager dbManager =  new DbManager(getApplicationContext());
           listMessages=dbManager.getChats(Utils.getNineDigits(msisdn));

            try {
                for (ChatMessage msg : listMessages) {
                    if (msg.getMessage().trim().length() == 0) {
                       Log.i("EMPTY ","The message is empty");

                    }
                    else {
                        Log.i("MESSAGE READ ",msg.getMessage());
                        /*final ChatMessage message = new ChatMessage();
                        message.setMessageStatus(Status.NOT_SENT);
                        message.setMessage(msg.getMessage());
                        message.setUserType(msg.getUserType());
                        message.setMessageTime(new Date());*/
                        chatMessages.add(msg);

                        if (listAdapter != null)
                            listAdapter.notifyDataSetChanged();
                    }
                    // sendMessage(msg.getMessage(), msg.getUserType());
                }
            }//End of try
        catch(Exception e)
        {
            e.printStackTrace();

        }

    }


    public void saveToLocal(ChatMessage message,Context context)
    {
        try{
               DBProvider dbProvider = new DBProvider(context);
            ContentValues chatparams = new ContentValues();
            chatparams.put(DB.Chat.FROM,KEApp.accountName);
            chatparams.put(DB.Chat.TO, Utils.getNineDigits(msisdn));
            chatparams.put(DB.Chat.DATE_CREATED,new Date().toString());
            chatparams.put(DB.Chat.MESSAGE,message.getMessage());
            chatparams.put(DB.Chat.messageID,message.getMessageID());
            chatparams.put(DB.Chat.STATUS,message.getMessageStatus().toString());
            chatparams.put(DB.Chat.USER_TYPE,message.getUserType().toString());

            Log.d(TAG, "Calling insert into chats");
            Uri pUri = dbProvider.insert(DB.Chat.CONTENT_URI, chatparams);
            Log.d(TAG, "Chat SAVED!!");
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        NotificationCenter.getInstance().removeObserver(this, NotificationCenter.emojiDidLoaded);
    }

    /**
     * Get the system status bar height
     * @return
     */
    public int getStatusBarHeight() {
        int result = 0;
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    @Override
    protected void onPause() {
        super.onPause();

        hideEmojiPopup();
    }
}
