package com.rube.tt.keapp.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.rube.tt.keapp.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rube on 10/10/15.
 */
public class SpinnerItemAdapter extends BaseAdapter
{

    private static final String TAG = SpinnerItemAdapter.class.getSimpleName();
    private Activity activity;
    private List<String> listData;
    private static LayoutInflater inflater=null;


    public SpinnerItemAdapter(Activity activity, List<String> listData){
        this.activity = activity;                //have seen earlier
        this.listData = listData;

        this.inflater = inflater = ( LayoutInflater )activity.
                getSystemService(Context.LAYOUT_INFLATER_SERVICE);


    }

    @Override
    public int getCount() {
        return listData.size();
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
        private TextView itemName;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        View vi = convertView;
        ViewHolder holder;

        if(convertView==null){
            vi = inflater.inflate(R.layout.simple_spinner_dropdown_item, null);

            holder = new ViewHolder();
            holder.itemName=(TextView)vi.findViewById(R.id.spinner_item_name);
            vi.setTag( holder );
        }
        else {
            holder = (ViewHolder) vi.getTag();
        }


        if(!listData.isEmpty()){
            /***** Get each Model object from Arraylist ********/
            String item = (String) listData.get( position );
            /************  Set Model values in Holder elements ***********/
            holder.itemName.setText(item);

        }
        return vi;
    }

}
