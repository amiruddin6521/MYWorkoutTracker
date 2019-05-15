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

public class WorkoutTabAdapterB extends BaseAdapter implements Filterable {
    private List<String> originalData, filteredData, bMachine, bDate, bTime, bSets, bReps, bWeight;
    private LayoutInflater mInflater;
    private ItemFilter mFilter = new ItemFilter();

    public WorkoutTabAdapterB(Context context, List<String> data1, List<String> data2, List<String> data3, List<String> data4, List<String> data5, List<String> data6) {
        this.filteredData = data1 ;
        this.originalData = data1 ;
        this.bMachine = data1 ;
        this.bDate = data2 ;
        this.bTime = data3 ;
        this.bSets = data4 ;
        this.bReps = data5 ;
        this.bWeight = data6 ;
        mInflater = LayoutInflater.from(context);
    }

    public int getCount() {
        return this.filteredData.size();
    }

    public Object getItem(int position) {
        return filteredData.get(position);
    }

    public String getItemName(int position) {
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
            convertView = mInflater.inflate(R.layout.curr_exercises_listb, null);

            // Creates a ViewHolder and store references to the two children views
            // we want to bind data to.
            holder = new ViewHolder();
            holder.txtMachine = (TextView) convertView.findViewById(R.id.edtMachinesB);
            holder.txtDate = (TextView) convertView.findViewById(R.id.edtDateB);
            holder.txtTime = (TextView) convertView.findViewById(R.id.edtTimeB);
            holder.txtSets = (TextView) convertView.findViewById(R.id.edtSetsB);
            holder.txtReps = (TextView) convertView.findViewById(R.id.edtRepsB);
            holder.txtWeight = (TextView) convertView.findViewById(R.id.edtWeightB);

            // Bind the data efficiently with the holder.

            convertView.setTag(holder);
        } else {
            // Get the ViewHolder back to get fast access to the TextView
            // and the ImageView.
            holder = (ViewHolder) convertView.getTag();
        }

        // If weren't re-ordering this you could rely on what you set last time
        holder.txtMachine.setText(bMachine.get(position));
        holder.txtDate.setText(bDate.get(position));
        holder.txtTime.setText(bTime.get(position));
        holder.txtSets.setText(bSets.get(position));
        holder.txtReps.setText(bReps.get(position));
        holder.txtWeight.setText(bWeight.get(position));

        return convertView;
    }

    static class ViewHolder {
        TextView txtMachine;
        TextView txtDate;
        TextView txtTime;
        TextView txtSets;
        TextView txtReps;
        TextView txtWeight;
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
