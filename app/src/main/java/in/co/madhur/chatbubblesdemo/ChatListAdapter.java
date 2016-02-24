package in.co.madhur.chatbubblesdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.model.Status;
import in.co.madhur.chatbubblesdemo.model.UserType;
import in.co.madhur.chatbubblesdemo.models.ChatMessage;
import in.co.madhur.chatbubblesdemo.widgets.Emoji;

/**
 * Created by madhur on 17/01/15.
 */
public class ChatListAdapter extends BaseAdapter {

    private ArrayList<ChatMessage> chatMessages;
    private Context context;


    public ChatListAdapter(ArrayList<ChatMessage> chatMessages, Context context) {
        this.chatMessages = chatMessages;
        this.context = context;

    }


    @Override
    public int getCount() {
        return chatMessages.size();
    }

    @Override
    public Object getItem(int position) {
        return chatMessages.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        try {
            ChatMessage message = chatMessages.get(position);
            ViewHolder1 holder1;
            ViewHolder2 holder2;

            if (message.getUserType() == UserType.OTHER) {
                if (convertView == null) {
                   // v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                    v = LayoutInflater.from(context).inflate(R.layout.chat_list_item_message_left, null, false);
                    holder1 = new ViewHolder1();


                    holder1.messageTextView = (TextView) v.findViewById(R.id.txtMsg);
                    holder1.timeTextView = (TextView) v.findViewById(R.id.chat_date);

                    v.setTag(holder1);
                } else {
                    v = convertView;
                    holder1 = (ViewHolder1) v.getTag();

                }

                holder1.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder1.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
                holder1.timeTextView.setText(in.co.madhur.chatbubblesdemo.Constants.SIMPLE_DATE_FORMAT2.format(message.getMessageTime()));

            } else if (message.getUserType() == UserType.ME) {

                if (convertView == null) {
                    //v = LayoutInflater.from(context).inflate(R.layout.chat_user2_item, null, false);
                    v = LayoutInflater.from(context).inflate(R.layout.chat_list_item_message_right, null, false);

                    holder2 = new ViewHolder2();


                    holder2.messageTextView = (TextView) v.findViewById(R.id.txtMsg);
                    holder2.timeTextView = (TextView) v.findViewById(R.id.chat_date);
                    holder2.messageStatus = (ImageView) v.findViewById(R.id.sent_success_image_icon);
                    v.setTag(holder2);

                } else {
                    v = convertView;
                    holder2 = (ViewHolder2) v.getTag();

                }

                holder2.messageTextView.setText(Emoji.replaceEmoji(message.getMessage(), holder2.messageTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
                //holder2.messageTextView.setText(message.getMessageText());
                holder2.timeTextView.setText(Constants.SIMPLE_DATE_FORMAT2.format(message.getMessageTime()));

                if (message.getMessageStatus() == Status.DELIVERED) {
                    holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action));
                }
                else if (message.getMessageStatus() == Status.NOT_SENT) {
                    holder2.messageStatus.setImageDrawable(null);//

                }
                else if (message.getMessageStatus() == Status.SENT) {
                    holder2.messageStatus.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_action));

                }


            }
        }
        catch(Exception e)
        {e.printStackTrace();}


        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

   /* @Override
    public int getItemViewType(int position) {
        ChatMessage message = chatMessages.get(position);
        return message.getUserType().ordinal();
    }
*/
    private class ViewHolder1 {
        public TextView messageTextView;
        public TextView timeTextView;


    }

    private class ViewHolder2 {
        public ImageView messageStatus;
        public TextView messageTextView;
        public TextView timeTextView;

    }
}
