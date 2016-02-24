package in.co.madhur.chatbubblesdemo.adapters;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.AndroidUtilities;
import in.co.madhur.chatbubblesdemo.MainActivity;
import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.models.ChatMessage;
import in.co.madhur.chatbubblesdemo.utils.Constants;
import in.co.madhur.chatbubblesdemo.utils.RubeImageLoader;
import in.co.madhur.chatbubblesdemo.widgets.Emoji;

/**
 * Created by rube on 4/8/15.
 */
public class MessageListAdapter extends BaseAdapter implements View.OnClickListener {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = MessageListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<ChatMessage> messageListData;
        private static LayoutInflater inflater=null;


        public MessageListAdapter(Activity activity, ArrayList<ChatMessage> messageListData ){
            this.activity = activity;                //have seen earlier
            this.messageListData = messageListData;

            this.inflater = inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        @Override
        public int getCount() {
            try
            {if(messageListData==null)
            {return  0;}
            }
            catch(Exception e)
            {e.printStackTrace();}
            return messageListData.size();
        }

        @Override
        public Object getItem(int position) {
            return position;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        // Creating a ViewHolder which extends the RecyclerView View Holder
        // ViewHolder are used to to store the inflated views in order to recycle them

        public class ViewHolder {

            ImageView profileImage;
            TextView messageView;
            TextView dateView;
            TextView profileName;

        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){

            /****** Inflate message_list_item.xml file for each row ( Defined below ) *******/
            vi = inflater.inflate(R.layout.message_list_item, null);

            /****** View Holder Object to contain message_list_item.xml file elements ******/

            holder = new ViewHolder();
            holder.profileImage = (ImageView) vi.findViewById(R.id.message_profile_icon);
            holder.messageView=(TextView)vi.findViewById(R.id.message_text);
            holder.dateView=(TextView)vi.findViewById(R.id.message_date);
            holder.profileName=(TextView)vi.findViewById(R.id.message_profile_name);

            /************  Set holder with LayoutInflater ************/
            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }


        /***** Get each Model object from Arraylist ********/
        ChatMessage messageModel = (ChatMessage) messageListData.get( position );

        /************  Set Model values in Holder elements ***********/
        RubeImageLoader rubeImageLoader = new RubeImageLoader(activity);

        rubeImageLoader.loadImageFormDevice(Constants.CONTENT_TYPE_PROFILE,
                0, holder.profileImage,
                activity.getResources().getDrawable(R.drawable.default_profile_pic));
        //holder.profileImage.setImageBitmap(messageModel.getPic());

        holder.messageView.setText(Emoji.replaceEmoji(messageModel.getMessage(),holder.messageView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
        holder.dateView.setText( in.co.madhur.chatbubblesdemo.Constants.SIMPLE_DATE_FORMAT2.format(messageModel.getMessageTime()));
        holder.profileName.setText(messageModel.getFrom());


        /******** Set Item Click Listner for LayoutInflater for each row *******/

        vi.setOnClickListener(new OnItemClickListener( position ));

        return vi;
    }

    @Override
    public void onClick(View v) {
        Log.v("CustomAdapter", "=====Row button clicked=====");
    }

    /********* Called when Item click in ListView ************/
    private class OnItemClickListener  implements View.OnClickListener{
        private int mPosition;

        OnItemClickListener(int position){
            mPosition = position;
        }

        @Override
        public void onClick(View arg0) {
            ChatMessage messageModel = (ChatMessage) messageListData.get( mPosition );

            Intent i = new Intent(activity, MainActivity.class);
            i.putExtra("phone", messageModel.getTo());
            i.putExtra("name", messageModel.getFrom());
            i.putExtra("selectedPage", mPosition);
            activity.startActivity(i);
        }
    }


    private boolean isPositionHeader(int position) {
            return position == 0;
        }

    }


