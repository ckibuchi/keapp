package in.co.madhur.chatbubblesdemo.adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import in.co.madhur.chatbubblesdemo.R;

import java.util.ArrayList;

/**
 * Created by rube on 15/10/15.
 */
public class ImageAdapter  extends BaseAdapter {

        private Context context;
        private final ArrayList<Bitmap> imageList;
        private static LayoutInflater inflater=null;

        public ImageAdapter(Context context, ArrayList<Bitmap> imageList) {
            this.context = context;
            this.imageList = imageList;
            this.inflater = inflater = ( LayoutInflater )context.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void update( ArrayList<Bitmap> imageList){
            this.imageList.addAll(imageList);
            notifyDataSetChanged();
        }

        public void refresh(ArrayList<Bitmap> items)
        {
            this.imageList.clear();
            this.imageList.addAll(items);
            notifyDataSetChanged();
        }

        public class ViewHolder {
            private ImageView imageView;

        }

        public View getView(int position, View convertView, ViewGroup parent) {

            View gridView;

            View vi = convertView;
            ViewHolder holder;

            if(convertView==null){
                vi = inflater.inflate(R.layout.image_display, null);

                holder = new ViewHolder();
                holder.imageView =(ImageView)vi.findViewById(R.id.image_view);
                vi.setTag( holder );
            }else {
                holder =  (ViewHolder) vi.getTag();
            }

            Bitmap image = (Bitmap) imageList.get( position );

            holder.imageView.setImageBitmap(image);
            holder.imageView.buildDrawingCache();

            return vi;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public Object getItem(int position) {
            return imageList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }


}
