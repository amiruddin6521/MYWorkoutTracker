package com.psm.myworkouttracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.psm.myworkouttracker.R;

import java.util.ArrayList;
import java.util.List;

public class MeasureAdapter extends BaseAdapter implements Filterable {
    private List<String> originalData;
    private List<String> filteredData;
    private List<String> id;
    private List<String> dateData;
    private List<String> measureData;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public MeasureAdapter(Context context, List<String> data, List<String> data1, List<String> data2) {
        this.filteredData = data ;
        this.originalData = data ;
        this.dateData = data1 ;
        this.measureData = data2 ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public String getItemID(int position) {
        return filteredData.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        // A ViewHolder keeps references to children views to avoid unnecessary calls
        // to findViewById() on each row.
        ViewHolder holder;

        // When convertView is not null, we can reuse it directly, there is no need
        // to reinflate it. We only inflate a new View when the convertView supplied
        // by ListView is null.
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.curr_measure_list, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.txtDate = (TextView) convertView.findViewById(R.id.edtDateMeasure);
            holder.txtMeasure = (TextView) convertView.findViewById(R.id.edtValueMeasure);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtDate.setText(dateData.get(position));
        holder.txtMeasure.setText(measureData.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView txtDate;
        TextView txtMeasure;
    }

    public Filter getFilter() {
        return mFilter;
    }

    private class ItemFilter extends Filter {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {

            String filterString = constraint.toString().toLowerCase();

            FilterResults results = new FilterResults();

            final List<String> list = originalData;

            int count = list.size();
            final ArrayList<String> nlist = new ArrayList<String>(count);

            String filterableString ;

            for (int i = 0; i < count; i++) {
                filterableString = list.get(i);
                if (filterableString.toLowerCase().contains(filterString)) {
                    nlist.add(filterableString);
                }
            }

            results.values = nlist;
            results.count = nlist.size();

            return results;
        }

        @SuppressWarnings("unchecked")
        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredData = (ArrayList<String>) results.values;
            notifyDataSetChanged();
        }

    }
}
