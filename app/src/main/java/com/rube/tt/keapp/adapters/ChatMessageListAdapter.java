package com.rube.tt.keapp.adapters;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rube.tt.keapp.R;
import com.rube.tt.keapp.models.ChatMessage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rube on 4/15/15.
 */
public class ChatMessageListAdapter extends BaseAdapter {



        private Context context;
        private List<ChatMessage> messagesItems;

        public ChatMessageListAdapter(Context context, List<ChatMessage> navDrawerItems) {
            this.context = context;
            this.messagesItems = navDrawerItems;
        }

        public void refresh(ArrayList<ChatMessage> items)
        {
            this.messagesItems.clear();
            this.messagesItems.addAll(items);
            notifyDataSetChanged();
        }

        public void update(ArrayList<ChatMessage> items)
        {
            this.messagesItems.addAll(items);
            notifyDataSetChanged();
        }

        public void  updateSend(int position, boolean send){
            ChatMessage chatMessage = (ChatMessage)getItem(position);
            chatMessage.setSent(send);
            this.messagesItems.set(position, chatMessage);
            notifyDataSetChanged();
        }

        public void  updateDelivered(int position, boolean delivered){
            ChatMessage chatMessage = (ChatMessage)getItem(position);
            chatMessage.setDelivered(delivered);
            this.messagesItems.set(position, chatMessage);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return messagesItems.size();
        }

        @Override
        public Object getItem(int position) {
            return messagesItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            /**
             * The following list not implemented reusable list items as list items
             * are showing incorrect data Add the solution if you have one
             * */

            ChatMessage m = messagesItems.get(position);

            LayoutInflater mInflater = (LayoutInflater) context
                    .getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

            // Identifying the message owner
            if (messagesItems.get(position).isSelf()) {
                // message belongs to you, so load the right aligned layout
                convertView = mInflater.inflate(R.layout.chat_list_item_message_right,
                        null);
            } else {
                // message belongs to other person, load the left aligned layout
                convertView = mInflater.inflate(R.layout.chat_list_item_message_left,
                        null);
            }

            ImageView sendIconImage = (ImageView)convertView.findViewById(R.id.sent_success_image_icon);

            ImageView imgMsgFrom = (ImageView) convertView.findViewById(R.id.imageMsgFrom);
            TextView txtMsg = (TextView) convertView.findViewById(R.id.message_text);
            TextView chatDate = (TextView) convertView.findViewById(R.id.time_text);

            txtMsg.setText(m.getMessage());
            //imgMsgFrom.setImageResource(m.getProfileIcon());
           // chatDate.setText(m.getMessageTime());

            if(m.isDelivered()){
                sendIconImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_action_green));
                sendIconImage.setVisibility(View.VISIBLE);
            }else  if(m.isSent()){
                sendIconImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_action));
                sendIconImage.setVisibility(View.VISIBLE);
            }else{
                sendIconImage.setImageDrawable(this.context.getResources().getDrawable(R.drawable.ic_action_dot));
                sendIconImage.setVisibility(View.VISIBLE);
            }

            return convertView;
        }

}
