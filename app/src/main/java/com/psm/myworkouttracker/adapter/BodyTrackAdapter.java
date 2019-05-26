package com.psm.myworkouttracker.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.psm.myworkouttracker.R;

import java.util.List;

public class BodyTrackAdapter extends BaseAdapter {

    private List<Integer> imgData;
    private List<String> nameData;
    private List<String> measureData;
    private LayoutInflater mInflater;

    public BodyTrackAdapter(Context context, List<Integer> data, List<String> data1, List<String> data2) {
        this.imgData = data ;
        this.nameData = data1 ;
        this.measureData = data2 ;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return measureData.size();
    }

    @Override
    public Object getItem(int position) {
        return measureData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.bodytrack_list, null);

            holder = new ViewHolder();
            holder.imgBody = convertView.findViewById(R.id.imageBody);
            holder.txtName = convertView.findViewById(R.id.txtBody);
            holder.txtMeasure = convertView.findViewById(R.id.txtMeasure);

            convertView.setTag(holder);
        } else {

            holder = (ViewHolder) convertView.getTag();
        }

        holder.imgBody.setImageResource(imgData.get(position));
        holder.txtName.setText(nameData.get(position));
        if(measureData.get(position).equals("")) {
            holder.txtMeasure.setText("-");
        } else {
            holder.txtMeasure.setText(measureData.get(position));
        }

        return convertView;
    }

    static class ViewHolder {
        ImageView imgBody;
        TextView txtName;
        TextView txtMeasure;
    }
}
