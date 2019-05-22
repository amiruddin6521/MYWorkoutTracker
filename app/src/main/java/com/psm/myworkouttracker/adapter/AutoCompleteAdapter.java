package com.psm.myworkouttracker.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import com.psm.myworkouttracker.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {

    private ArrayList<String> fullList;
    private ArrayList<String> mOriginalValues;
    private ArrayFilter mFilter;
    private ArrayList<String> descData;
    private ArrayList<String> pictureData;
    private LayoutInflater mInflater;
    private int pos;

    public AutoCompleteAdapter(Context context, List<String> objects, List<String> data1, List<String> data2) {

        super(context, 0, objects);
        fullList = (ArrayList<String>) objects;
        mOriginalValues = new ArrayList<String>(fullList);
        descData = (ArrayList<String>) data1 ;
        pictureData = (ArrayList<String>) data2 ;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return fullList.size();
    }

    @Override
    public String getItem(int position) {
        return fullList.get(position);
    }

    public void setFilterPos(int pos) {
        this.pos = pos;
    }

    public int getFilterPos() {
        return pos;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.exercises_list, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.txtExercisesName = (TextView) convertView.findViewById(R.id.txtExercise);
            holder.txtDesc = (TextView) convertView.findViewById(R.id.txtDescription);
            holder.imgData = (ImageView) convertView.findViewById(R.id.imageMachine);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtExercisesName.setText(fullList.get(position));
        holder.txtDesc.setText(descData.get(getFilterPos()));
        if(!pictureData.get(position).equals("")) {
            byte[] data = Base64.decode(pictureData.get(getFilterPos()), Base64.DEFAULT);
            Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
            holder.imgData.setImageBitmap(decodedByte);
        } else {
            holder.imgData.setImageResource(R.drawable.person);
        }
        return convertView;
    }

    static class ViewHolder {
        TextView txtExercisesName;
        TextView txtDesc;
        ImageView imgData;
    }

    @Override
    public Filter getFilter() {
        if (mFilter == null) {
            mFilter = new ArrayFilter();
        }
        return mFilter;
    }


    private class ArrayFilter extends Filter {
        private Object lock;

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();

            if (mOriginalValues == null) {
                synchronized (lock) {
                    mOriginalValues = new ArrayList<String>(fullList);
                }
            }

            if (prefix == null || prefix.length() == 0) {
                synchronized (lock) {
                    ArrayList<String> list = new ArrayList<String>(mOriginalValues);
                    results.values = list;
                    results.count = list.size();
                }
            } else {
                final String prefixString = prefix.toString().toLowerCase();

                ArrayList<String> values = mOriginalValues;
                int count = values.size();

                ArrayList<String> newValues = new ArrayList<String>(count);

                for (int i = 0; i < count; i++) {
                    String item = values.get(i);
                    if (item.toLowerCase().contains(prefixString)) {
                        newValues.add(item);
                        setFilterPos(i);
                    }

                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if(results.values!=null){
                fullList = (ArrayList<String>) results.values;
            } else {
                fullList = new ArrayList<String>();
            }
            if (results.count > 0) {
                notifyDataSetChanged();
            } else {
                notifyDataSetInvalidated();
            }
        }
    }
}
