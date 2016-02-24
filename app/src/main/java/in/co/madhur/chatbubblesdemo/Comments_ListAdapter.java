package in.co.madhur.chatbubblesdemo;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import in.co.madhur.chatbubblesdemo.models.Comment;
import in.co.madhur.chatbubblesdemo.widgets.Emoji;

/**
 * Created by madhur on 17/01/15.
 */
public class Comments_ListAdapter extends BaseAdapter {

    private ArrayList<Comment> Comments;
    private Context context;


    public Comments_ListAdapter(ArrayList<Comment> Comments, Context context) {
        this.Comments = Comments;
        this.context = context;

    }


    @Override
    public int getCount() {
        return Comments.size();
    }

    @Override
    public Object getItem(int position) {
        return Comments.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = null;
        try {
            Comment comment = Comments.get(position);
            ViewHolder1 holder1;

            if (convertView == null) {
                // v = LayoutInflater.from(context).inflate(R.layout.chat_user1_item, null, false);
                v = LayoutInflater.from(context).inflate(in.co.madhur.chatbubblesdemo.R.layout.page_detail_list_item, null, false);
                holder1 = new ViewHolder1();


                holder1.commentTextView = (TextView) v.findViewById(in.co.madhur.chatbubblesdemo.R.id.page_detail_text);
                holder1.by = (TextView) v.findViewById(in.co.madhur.chatbubblesdemo.R.id.page_detail_profile_name);
                holder1.timeTextView = (TextView) v.findViewById(in.co.madhur.chatbubblesdemo.R.id.page_detail_text_date);
                holder1.pageTitle = (TextView) v.findViewById(in.co.madhur.chatbubblesdemo.R.id.page_detail_story_name);


                v.setTag(holder1);
            } else {
                v = convertView;
                holder1 = (ViewHolder1) v.getTag();

            }

            holder1.commentTextView.setText(Emoji.replaceEmoji(comment.getComment(), holder1.commentTextView.getPaint().getFontMetricsInt(), AndroidUtilities.dp(16)));
            holder1.timeTextView.setText(comment.getCommentTime());
            //holder1.UserImage.setImageResource();
            holder1.pageTitle.setText(comment.getPageTitle());
            holder1.by.setText(comment.getFrom());


        }
        catch(Exception e)
        {e.printStackTrace();}


        return v;
    }

    @Override
    public int getViewTypeCount() {
        return 2;
    }

  /*  @Override
    public int getItemViewType(int position) {
        Comment comment = Comments.get(position);
        return comment.getUserType().ordinal();
    }
*/
    private class ViewHolder1 {
        public TextView commentTextView;
        public TextView timeTextView;
        public ImageView UserImage;
        public TextView by;
        public TextView pageTitle;



    }

    private class ViewHolder2 {
        public ImageView commentStatus;
        public TextView commentTextView;
        public TextView timeTextView;

    }
}
