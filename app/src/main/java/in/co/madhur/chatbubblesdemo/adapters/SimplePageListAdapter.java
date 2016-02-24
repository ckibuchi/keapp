package in.co.madhur.chatbubblesdemo.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import in.co.madhur.chatbubblesdemo.R;
import in.co.madhur.chatbubblesdemo.models.SimplePageModel;

import java.util.ArrayList;

/**
 * Created by rube on 4/8/15.
 */
public class SimplePageListAdapter extends BaseAdapter {

        private static final int TYPE_HEADER = 0;  // Declaring Variable to Understand which View is being worked on
        // IF the view under inflation and population is header or Item
        private static final int TYPE_ITEM = 1;

        private static final String TAG = SimplePageListAdapter.class.getSimpleName();


        private Activity activity;
        private ArrayList<SimplePageModel> simplePageListData;
        private static LayoutInflater inflater=null;


        public SimplePageListAdapter(Activity activity, ArrayList<SimplePageModel> simplePageListData){
            this.activity = activity;                //have seen earlier
            this.simplePageListData = simplePageListData;

            this.inflater = inflater = ( LayoutInflater )activity.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);


        }

        public void refresh(ArrayList<SimplePageModel> items)
        {
            this.simplePageListData.clear();
            this.simplePageListData.addAll(items);
            notifyDataSetChanged();
        }

        public void update(ArrayList<SimplePageModel> items)
        {
            this.simplePageListData.addAll(items);
            notifyDataSetChanged();
        }


        @Override
        public int getCount() {
            return simplePageListData.size();
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

            private TextView name;
            private ImageView pic;
            private TextView date;
            private TextView category;


        }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.simple_page_item, null);

            holder = new ViewHolder();
            holder.pic = (ImageView) vi.findViewById(R.id.page_profile_icon);
            holder.name=(TextView)vi.findViewById(R.id.page_name);
            holder.date=(TextView)vi.findViewById(R.id.page_date);
            holder.category=(TextView)vi.findViewById(R.id.page_category);



            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }


        if(!simplePageListData.isEmpty()){

            /***** Get each Model object from Arraylist ********/
            SimplePageModel simplePageModel = (SimplePageModel) simplePageListData.get( position );

            holder.pic.setImageBitmap(simplePageModel.getPic());
            holder.name.setText(simplePageModel.getName());
            holder.date.setText(simplePageModel.getDate());
            holder.category.setText(simplePageModel.getCategory());

        }
        return vi;
    }

}


