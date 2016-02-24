package in.co.madhur.chatbubblesdemo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
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

import com.google.android.gms.gcm.GoogleCloudMessaging;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.atomic.AtomicInteger;

import in.co.madhur.chatbubblesdemo.data.ACCEPT;
import in.co.madhur.chatbubblesdemo.data.SyncAdapter;
import in.co.madhur.chatbubblesdemo.db.DbManager;
import in.co.madhur.chatbubblesdemo.fragments.PageFragment;
import in.co.madhur.chatbubblesdemo.model.Status;
import in.co.madhur.chatbubblesdemo.model.UserType;
import in.co.madhur.chatbubblesdemo.models.Comment;
import in.co.madhur.chatbubblesdemo.utils.Utils;
import in.co.madhur.chatbubblesdemo.widgets.Emoji;
import in.co.madhur.chatbubblesdemo.widgets.EmojiView;
import in.co.madhur.chatbubblesdemo.widgets.SizeNotifierRelativeLayout;


public class PageDetails extends ActionBarActivity implements SizeNotifierRelativeLayout.SizeNotifierRelativeLayoutDelegate, NotificationCenter.NotificationCenterDelegate {

    //public static ListView chatListView;
    private EditText chatEditText2;
    public static ArrayList<Comment> commentsList;
    private ImageView enterChatView1, emojiButton;
    public static Comments_ListAdapter listAdapter;
    private EmojiView emojiView;
    private SizeNotifierRelativeLayout sizeNotifierRelativeLayout;
    private boolean showingEmoji;
    private int keyboardHeight;
    private boolean keyboardVisible;
    GoogleCloudMessaging gcm;
    AtomicInteger ccsMsgId = new AtomicInteger();
    AsyncTask<Void, Void, String> sendTask;
    private WindowManager.LayoutParams windowLayoutParams;

    public PageDetails()
    {}
    private EditText.OnKeyListener keyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // If the event is a key-down event on the "enter" button
            if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                    (keyCode == KeyEvent.KEYCODE_ENTER)) {
                // Perform action on key press

                EditText editText = (EditText) v;

                if(v== chatEditText2)
                {
                    sendComment(editText.getText().toString(), UserType.ME);
                }

                chatEditText2.setText("");

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
                sendComment(chatEditText2.getText().toString(), UserType.ME);
            }

            chatEditText2.setText("");

        }
    };

    private final TextWatcher watcher1 = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {
            if (chatEditText2.getText().toString().equals("")) {

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

    private static final String TAG = PageDetails.class.getSimpleName();

    private Button btnSend;
    private EditText inputMsg;
    /*
        EmojiconEditText mEditEmojicon;
        EmojiconTextView mTxtEmojicon;*/
    CheckBox mCheckBox;

    // Chat comments list adapter
    public static CommentListAdapter adapter;
    private ArrayList<Comment> listComments;
    private ListView listViewComments;
    private Toolbar toolbar;
    private ImageView emojiconsIcon;
    //private LinearLayout parentLayout;
    private ImageView emojiconsSubmit;
    private LinearLayout emojiconsNonSubmit;


    //private XMPPManager xmppManager;

    // Client name
    private String name = null;
    public static String pageId;
    public static String by;


    // JSON flags to identify the kind of JSON response
    private static final String TAG_SELF = "self", TAG_NEW = "new",
            TAG_MESSAGE = "comment", TAG_EXIT = "exit";

    public static Handler UIHandler;

    static {
        UIHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.newpagedetails);



        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        AndroidUtilities.statusBarHeight = getStatusBarHeight();
        try {
            toolbar = (Toolbar) findViewById(R.id.page_tool_bar);
            toolbar.getMenu().clear();

            //toolbar.setLogo(null);
            //toolbar.setTitle(R.string.chat_page_detail);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true) ;
            getSupportActionBar().setHomeButtonEnabled(true);
        }
        catch (Exception e)
        {
            e.printStackTrace();}

        gcm = GoogleCloudMessaging.getInstance(this);
        // Getting the person name from previous screen
        Intent i = getIntent();
        name = i.getStringExtra("name");
        pageId = i.getStringExtra("pageId");
        by = i.getStringExtra("by");
        try
        {
            TextView pageName=(TextView)findViewById(R.id.page_profile_category);
            pageName.setText(name);

        }
        catch(Exception e)
        {}
        listViewComments = (ListView) findViewById(R.id.comments_list_view);



        commentsList = new ArrayList<>();
        listComments = new ArrayList<Comment>();



        //adding emoji
        // mEditEmojicon = (EmojiconEditText) findViewById(R.id.editEmojicon);


        emojiconsIcon = (ImageView)findViewById(R.id.emojicons_pop_icon);
        //emojiIconsCover = (LinearLayout) findViewById(R.id.footer_for_emojiicons);
        //      parentLayout = (LinearLayout) findViewById(R.id.main_chat_layout_holder);
        //    Log.d(TAG, "Found parent layout "+parentLayout.toString());



        //
        listViewComments = (ListView) findViewById(R.id.comments_list_view);

        chatEditText2 = (EditText) findViewById(R.id.chat_edit_text2);
        enterChatView1 = (ImageView) findViewById(R.id.enter_chat2);

        // Hide the emoji on click of edit text
        chatEditText2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (showingEmoji)
                    hideEmojiPopup();
            }
        });


        emojiButton = (ImageView)findViewById(R.id.emojiButton);


        emojiButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEmojiPopup(!showingEmoji);
            }
        });

        listAdapter = new Comments_ListAdapter(commentsList, this);
        //Imported


        listViewComments.setAdapter(listAdapter);

        chatEditText2.setOnKeyListener(keyListener);

        enterChatView1.setOnClickListener(clickListener);

        chatEditText2.addTextChangedListener(watcher1);

        sizeNotifierRelativeLayout = (SizeNotifierRelativeLayout) findViewById(R.id.pages_layout);
        sizeNotifierRelativeLayout.delegate = this;

        NotificationCenter.getInstance().addObserver(this, NotificationCenter.emojiDidLoaded);

        getComments();
    }

    public void sendComment(final String commentText, final UserType userType)
    {
        if(commentText.trim().length()==0)
            return;

        String id = Integer.toString(ccsMsgId.incrementAndGet());
        final Comment comment = new Comment();
        //comment.setCommentStatus(Status.NOT_SENT);
        comment.setComment(commentText);
        comment.setUserType(userType);
        comment.setPageTitle(name);
        comment.setCommentTime(Constants.SIMPLE_DATEONLY_FORMAT.format(new Date()));
        comment.setCommentID(id);
        comment.setFrom("Me");
        commentsList.add(comment);

        if(listAdapter!=null)
            listAdapter.notifyDataSetChanged();

        saveToLocal(comment, getApplicationContext());
        sendCommentToXmpp("ECHO", id, comment,true);

    }
    public void sendCommentToXmpp(final String action,final String id,final Comment comment,final boolean dowecomment) {
        new AsyncTask<String, Void, Intent>() {



            @Override
            public Intent doInBackground(String... params) {

                Intent result;
                Bundle data = new Bundle();
                String wecomment=dowecomment?"comment":"uncomment";


                try {
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    // nameValuePairs.add(new BasicNameValuePair("datecreated", Constants.SIMPLE_DATEONLY_FORMAT.format(new Date())));
                    nameValuePairs.add(new BasicNameValuePair("msisdn", KEApp.accountName));
                    nameValuePairs.add(new BasicNameValuePair("pageid",pageId));
                    nameValuePairs.add(new BasicNameValuePair("comment", Utils.encodestring(comment.getComment()) == null ? comment.getComment() : Utils.encodestring(comment.getComment())));

                  /*  nameValuePairs.add(new BasicNameValuePair("user","{\"pageId\": \""+ KEApp.accountName+"\"}"));
                    nameValuePairs.add(new BasicNameValuePair("page","{\"id\": \""+ page.getId()+"\"}"));
                    SyncAdapter.POST(nameValuePairs, "WebService/Users/login", ACCEPT.JSON,"");
                    */
                    JSONArray commentresponse=  SyncAdapter.POST(nameValuePairs, "WebService/comments/"+wecomment, ACCEPT.JSON, "");
                    JSONObject commentobj=commentresponse.getJSONObject(0);

                    try{
                        if(commentobj.has("status"))
                        {

                        }
                        else
                        {
                            data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error creating  getting comments, Server unreachable");

                        }


                    }
                    catch(Exception e)
                    {

                        e.printStackTrace();
                    }





                    result = new Intent();




                } catch (Exception e) {
                    //Log.d(TAG, "Error getting pages 22:22222"+e.getMessage());
                    e.printStackTrace();
                    result = new Intent();
                    data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error creating  getting comments, please try again later");
                }

                result.putExtras(data);
                return result;


            }

            @Override
            protected void onPreExecute() {


                super.onPreExecute();

            }

            @Override
            protected void onPostExecute(Intent intent) {

                try {
                    if (intent.hasExtra(PageFragment.KEY_ERROR_MESSAGE)) {
                        Log.d(TAG, "Error getting pages 23:" + intent.getStringExtra(PageFragment.KEY_ERROR_MESSAGE));
                    } else {
                        //   Log.d(TAG, "Page Modelsize on PostExecute: " + mypageModels.size());
                        // PageFragment.refreshlist();

                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

        }.execute();

    }
    public  void ReceiveComment(final String commentText,String from,Context context)
    {
        if(commentText.trim().length()==0)
            return;

        final Comment comment = new Comment();
        comment.setCommentStatus(Status.DELIVERED);
        comment.setComment(Utils.decodestring(commentText)==null?commentText:Utils.decodestring(commentText));
        comment.setFrom(from);
        comment.setPageTitle(name);
        comment.setUserType(UserType.OTHER);
        comment.setCommentTime(Constants.SIMPLE_DATEONLY_FORMAT.format(new Date()));
        Log.i(TAG, pageId +" :"+from);
        if(pageId ==null)
        {
            KEApp.updatechats(commentText,from);
        }
        if(!Utils.getNineDigits(pageId).equals(Utils.getNineDigits(from)))
        {
            KEApp.updatechats(commentText,from);
        }
        {

            commentsList.add(comment);

        }
        try{
            listViewComments.post(new Runnable() {
                @Override
                public void run() {
                    listAdapter.notifyDataSetChanged();

                }
            });



        }
        catch(Exception e)
        {e.printStackTrace();}
        //Save The comment to Local Database
        saveToLocal(comment,context);
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
                        chatEditText2.dispatchKeyEvent(new KeyEvent(0, 67));
                    }

                    public void onEmojiSelected(String symbol) {
                        int i = chatEditText2.getSelectionEnd();
                        if (i < 0) {
                            i = 0;
                        }
                        try {
                            CharSequence localCharSequence = Emoji.replaceEmoji(symbol, chatEditText2.getPaint().getFontMetricsInt(), AndroidUtilities.dp(20));
                            chatEditText2.setText(chatEditText2.getText().insert(i, localCharSequence));
                            int j = i + localCharSequence.length();
                            chatEditText2.setSelection(j, j);
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

            if (listViewComments != null) {
                listViewComments.invalidateViews();
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

    private void getComments(){

        listComments = new ArrayList<Comment>();


        DbManager dbManager =  new DbManager(getApplicationContext());
        //listComments=//dbManager.getChats(Utils.getNineDigits(pageId));  //use sync adaptor....
        sendTask = new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... params) {
                String ret="ERR";
                Intent result;
                Bundle data = new Bundle();
                try {
                    final ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                    nameValuePairs.add(new BasicNameValuePair("pageid", pageId));
                    JSONArray comments= SyncAdapter.POST(nameValuePairs, "WebService/comments/getCommentsFromPageId", ACCEPT.JSON,"");
                    if(comments.length()>0) {
                        for(int i=0;i<comments.length();i++)
                        {
                            JSONObject commentobj=comments.getJSONObject(i);
                            //Get page details here....
                            if(commentobj.has("comment"))
                            {//page exists
                                try {
                                    if(commentobj.getString("comment").length()<1) continue;
                                   Comment newcomment = new Comment();
                                    newcomment.setCommentID("" + commentobj.getLong("id"));
                                    Log.d("DATE ", commentobj.getString("datecreated"));
                                    newcomment.setCommentTime(commentobj.getString("datecreated"));
                                    newcomment.setComment(Utils.decodestring(commentobj.getString("comment"))==null?commentobj.getString("comment"):Utils.decodestring(commentobj.getString("comment")));
                                    newcomment.setPageTitle(name);
                                    JSONObject userobj = new JSONObject( commentobj.getString("user"));
                                    newcomment.setFrom(userobj.getString("first_name") + " " + userobj.getString("last_name"));


                                   // listComments.add(newpage);
                                    commentsList.add(newcomment);

                                    // Log.d(TAG, "Page Model Size now  : " + mypageModels.size());


                                }
                                catch(Exception e)
                                {

                                    e.printStackTrace();
                                }
                            }

                        }


                        result = new Intent();


                    }
                    else{
                        result = new Intent();
                        data.putString(PageFragment.KEY_ERROR_MESSAGE, "Error getting pages, please try again later");
                    }

                  refresh();

                } catch (Exception e) {
                    e.printStackTrace();

                }

                return ret;

            }
    @Override
    protected void onPostExecute(String result) {
        sendTask = null;
        if(result.equalsIgnoreCase("OK")) {
            //message.setMessageStatus(in.co.madhur.chatbubblesdemo.model.Status.SENT);
            //Save The message to Local Database

        }
        else
        {
          //  message.setMessageStatus(in.co.madhur.chatbubblesdemo.model.Status.NOT_SENT);
                /*Toast.makeText(getApplicationContext(), result,
                        Toast.LENGTH_LONG).show();*/

        }


        if (listAdapter != null)
            listAdapter.notifyDataSetChanged();
    }

};
sendTask.execute(null, null, null);


        }


    public void saveToLocal(Comment comment,Context context)
    {
       /* try{
            DBProvider dbProvider = new DBProvider(context);
            ContentValues chatparams = new ContentValues();
            chatparams.put(DB.Chat.FROM,KEApp.accountName);
            chatparams.put(DB.Chat.TO, Utils.getNineDigits(pageId));
            chatparams.put(DB.Chat.DATE_CREATED,new Date().toString());
            chatparams.put(DB.Chat.MESSAGE,comment.getComment());
            chatparams.put(DB.Chat.commentID,comment.getCommentID());
            chatparams.put(DB.Chat.STATUS,comment.getCommentStatus().toString());
            chatparams.put(DB.Chat.USER_TYPE,comment.getUserType().toString());

            Log.d(TAG, "Calling insert into chats");
            Uri pUri = dbProvider.insert(DB.Chat.CONTENT_URI, chatparams);
            Log.d(TAG, "Chat SAVED!!");
        }
        catch(Exception e)
        {
            e.printStackTrace();

        }
*/
    }
public void refresh()
{
    try{
        listViewComments.post(new Runnable() {
            @Override
            public void run() {
                listAdapter.notifyDataSetChanged();

            }
        });



    }
    catch(Exception e){}

}

}
