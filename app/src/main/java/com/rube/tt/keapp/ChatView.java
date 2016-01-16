package com.rube.tt.keapp;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.rube.tt.keapp.adapters.ChatMessageListAdapter;
import com.rube.tt.keapp.models.ChatMessage;
//import com.rube.tt.keapp.xmpp.XMPPManager;

import java.util.ArrayList;

/*import com.rube.tt.keapp.utils.emojicons.EmojiconEditText;
import com.rube.tt.keapp.utils.emojicons.EmojiconGridView;
import com.rube.tt.keapp.utils.emojicons.EmojiconTextView;
import com.rube.tt.keapp.utils.emojicons.EmojiconsPopup;
import com.rube.tt.keapp.utils.emojicons.emoji.Emojicon;*/

/**
 * Created by rube on 4/15/15.
 */
public class ChatView extends ActionBarActivity {

    // LogCat tag
    private static final String TAG = ChatView.class.getSimpleName();

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
        setContentView(R.layout.main_chat_layout);
       // setContentView(R.layout.activity_main);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        toolbar = (Toolbar)findViewById(R.id.new_chat_toolbar_layout);
        toolbar.getMenu().clear();
        //toolbar.setLogo(null);
        //toolbar.setTitle(R.string.chat_page_detail);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(false) ;
        getSupportActionBar().setHomeButtonEnabled(false);

        listViewMessages = (ListView) findViewById(R.id.list_view_messages);

        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");
            try
                {
                    TextView username=(TextView)findViewById(R.id.user_names);
                    username.setText(name);
                }
                catch(Exception e)
                {}

        listMessages = new ArrayList<ChatMessage>();

        adapter = new ChatMessageListAdapter(getApplicationContext(), getChatMessages());
        listViewMessages.setAdapter(adapter);

        //adding emoji
       // mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);


        emojiconsIcon = (ImageView)findViewById(R.id.emojicons_pop_icon);
        //emojiIconsCover = (LinearLayout) findViewById(R.id.footer_for_emojiicons);
        parentLayout = (LinearLayout) findViewById(R.id.main_chat_layout_holder);
        Log.d(TAG, "Found parent layout "+parentLayout.toString());
        //emojiFrameLayout = (FrameLayout)findViewById(R.id.emojicons);

    /*    final EmojiconsPopup popup = new EmojiconsPopup(parentLayout, this);


        //submit button image
        emojiconsSubmit = (ImageView)findViewById(R.id.chat_send_now_button);
        emojiconsNonSubmit = (LinearLayout)findViewById(R.id.chat_buttons_not_sumit);

        //Will automatically set size according to the soft keyboard size
        popup.setSizeForSoftKeyboard();

        //Set on emojicon click listener
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {
            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                mEditEmojicon.append(emojicon.getEmoji());
            }
        });

        //Set on backspace click listener
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mEditEmojicon.dispatchKeyEvent(event);
            }
        });

        //If the emoji popup is dismissed, change emojiButton to smiley icon
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                changeEmojiKeyboardIcon(emojiconsIcon, R.drawable.smiley);
            }
        });

        //If the text keyboard closes, also dismiss the emoji popup
        popup.setOnSoftKeyboardOpenCloseListener(new EmojiconsPopup.OnSoftKeyboardOpenCloseListener() {

            @Override
            public void onKeyboardOpen(int keyBoardHeight) {

            }

            @Override
            public void onKeyboardClose() {
                if(popup.isShowing())
                    popup.dismiss();
            }
        });

        //On emoji clicked, add it to edittext
        popup.setOnEmojiconClickedListener(new EmojiconGridView.OnEmojiconClickedListener() {

            @Override
            public void onEmojiconClicked(Emojicon emojicon) {
                mEditEmojicon.append(emojicon.getEmoji());
            }
        });

        //On backspace clicked, emulate the KEYCODE_DEL key event
        popup.setOnEmojiconBackspaceClickedListener(new EmojiconsPopup.OnEmojiconBackspaceClickedListener() {

            @Override
            public void onEmojiconBackspaceClicked(View v) {
                KeyEvent event = new KeyEvent(
                        0, 0, 0, KeyEvent.KEYCODE_DEL, 0, 0, 0, 0, KeyEvent.KEYCODE_ENDCALL);
                mEditEmojicon.dispatchKeyEvent(event);
            }
        });

        // To toggle between text keyboard and emoji keyboard keyboard(Popup)
        emojiconsIcon.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                //If popup is not showing => emoji keyboard is not visible, we need to show it
                if(!popup.isShowing()){

                    //If keyboard is visible, simply show the emoji popup
                    if(popup.isKeyBoardOpen()){
                        popup.showAtBottom();
                        changeEmojiKeyboardIcon(emojiconsIcon, R.drawable.ic_action_keyboard);
                    }

                    //else, open the text keyboard first and immediately after that show the emoji popup
                    else{
                        mEditEmojicon.setFocusableInTouchMode(true);
                        mEditEmojicon.requestFocus();
                        popup.showAtBottomPending();
                        final InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        inputMethodManager.showSoftInput(mEditEmojicon, InputMethodManager.SHOW_IMPLICIT);
                        changeEmojiKeyboardIcon(emojiconsIcon, R.drawable.ic_action_keyboard);
                    }
                }

                //If popup is showing, simply dismiss it to show the undelying text keyboard
                else{
                    popup.dismiss();
                }
            }
        });

        mEditEmojicon.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() < 1 ){
                    if(emojiconsSubmit.isShown()) {
                        emojiconsSubmit.setVisibility(ImageView.GONE);
                    }
                    if(!emojiconsNonSubmit.isShown()) {
                        emojiconsNonSubmit.setVisibility(LinearLayout.VISIBLE);
                    }
                }else{
                    if(emojiconsNonSubmit.isShown()) {
                        emojiconsNonSubmit.setVisibility(LinearLayout.GONE);
                    }
                    if(!emojiconsSubmit.isShown()) {
                        emojiconsSubmit.setVisibility(ImageView.VISIBLE);
                    }
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        });


        //On submit, add the edittext text to listview and clear the edittext
        emojiconsSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                String newText = mEditEmojicon.getText().toString();
                mEditEmojicon.getText().clear();
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                ChatMessage2 chatMessage = new ChatMessage2();
                chatMessage.setFrom("admin@"+ Constants.XMPP_SERVICE);
                chatMessage.setTo("rube@"+Constants.XMPP_SERVICE);
                chatMessage.setSent(false);
                chatMessage.setDelivered(false);
                chatMessage.setMessage(newText);
                chatMessage.setMessageTime(format.format(new Date()));
                chatMessage.setSelf(true);

                ArrayList<ChatMessage2> chatMessageList = new ArrayList<ChatMessage2>();
                chatMessageList.add(chatMessage);

                adapter.update(chatMessageList);

                //send message to chat server
                Intent intent = new Intent(XMPPService.ACTION_SEND);
                intent.putExtra("message", chatMessage.getMessage());
                intent.putExtra("from", chatMessage.getFrom());
                intent.putExtra("to", chatMessage.getTo());
                intent.putExtra("chat_date", chatMessage.getMessageTime());
                intent.putExtra("position", adapter.getCount()-1);
                startService(intent);

            }
        });*/

        //load the chat server

    }

    public static void runOnUI(Runnable runnable) {
        UIHandler.post(runnable);
    }

    private void changeEmojiKeyboardIcon(ImageView iconToBeChanged, int drawableResourceId){
        iconToBeChanged.setImageResource(drawableResourceId);
    }





    private ArrayList<ChatMessage> getChatMessages(){

            listMessages = new ArrayList<ChatMessage>();

            ChatMessage chatMessage = new ChatMessage();
            chatMessage.setFrom("Reuben Paul Wafula");
            chatMessage.setMessage("Hi there?");
            chatMessage.setTo("admin");
            chatMessage.setSent(true);
            chatMessage.setDelivered(true);
            //chatMessage.setMessageTime("15:35pm");
            chatMessage.setSelf(true);

            listMessages.add(chatMessage);

            chatMessage = new ChatMessage();
            chatMessage.setFrom("Madam O");
            chatMessage.setTo("admin");
            chatMessage.setMessage("Hi, Did you get my message?");
          //  chatMessage.setMessageTime("15:36pm");
            chatMessage.setSelf(false);
            chatMessage.setSent(true);
            chatMessage.setDelivered(true);
            listMessages.add(chatMessage);

            chatMessage = new ChatMessage();
            chatMessage.setFrom("Reuben Paul Wafula");
            chatMessage.setMessage("Ya, n am not buying the bananas!");
            //chatMessage.setMessageTime("15:37pm");
            chatMessage.setSelf(true);
            chatMessage.setSent(true);
            chatMessage.setDelivered(true);
            listMessages.add(chatMessage);

            chatMessage = new ChatMessage();
            chatMessage.setFrom("Reuben Paul Wafula");
            chatMessage.setMessage("What did u expect?");
           // chatMessage.setMessageTime("15:38pm");
            chatMessage.setTo("admin");
            chatMessage.setSelf(true);
            chatMessage.setSent(true);
            chatMessage.setDelivered(true);
            listMessages.add(chatMessage);

            chatMessage = new ChatMessage();
            chatMessage.setFrom("Madam O");
            chatMessage.setMessage("bastard!");
            chatMessage.setTo("admin");
            //chatMessage.setMessageTime("15:39pm");
            chatMessage.setSelf(false);
            chatMessage.setSent(true);
            chatMessage.setDelivered(false);
            listMessages.add(chatMessage);

            return listMessages;
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
        }

   }
