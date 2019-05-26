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
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.psm.myworkouttracker.R;

import java.util.ArrayList;
import java.util.List;

public class AutoCompleteExercisesAdapter extends ArrayAdapter<ExercisesItem> {
    private List<ExercisesItem> exercisesListFull;

    public AutoCompleteExercisesAdapter(Context context, List<ExercisesItem> nameList) {
        super(context, 0, nameList);
        exercisesListFull = new ArrayList<>(nameList);
    }

    public String getItemName(int position) {
        ExercisesItem exercisesItem = getItem(position);
        return exercisesItem.getExerciseName();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.exercises_list, parent, false);
        }

        TextView txtName = convertView.findViewById(R.id.txtExercise);
        TextView txtDesc = convertView.findViewById(R.id.txtDescription);
        CircularImageView imgExercise = convertView.findViewById(R.id.imageMachine);

        ExercisesItem exercisesItem = getItem(position);

        if (exercisesItem != null) {
            txtName.setText(exercisesItem.getExerciseName());
            if (!exercisesItem.getExerciseDesc().equals("")) {
                txtDesc.setText(exercisesItem.getExerciseDesc());
            } else {
                txtDesc.setText("-");
            }

            if (!exercisesItem.getExerciseImg().equals("")) {
                byte[] decodeValue = Base64.decode(exercisesItem.getExerciseImg(), Base64.DEFAULT);
                Bitmap decodedByte = BitmapFactory.decodeByteArray(decodeValue, 0, decodeValue.length);
                imgExercise.setImageBitmap(decodedByte);
            } else {
                imgExercise.setImageResource(R.drawable.ic_machine);
            }

        }

        return convertView;
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

    public Filter nameFilter = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            List<ExercisesItem> suggestions = new ArrayList<>();

            if (constraint == null || constraint.length() == 0) {
                suggestions.addAll(exercisesListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();

                for (ExercisesItem item : exercisesListFull) {
                    if (item.getExerciseName().toLowerCase().contains(filterPattern)) {
                        suggestions.add(item);
                    }
                }
            }
            results.values = suggestions;
            results.count = suggestions.size();

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            clear();
            addAll((List)results.values);
            notifyDataSetChanged();
        }

        @Override
        public CharSequence convertResultToString(Object resultValue) {
            return ((ExercisesItem) resultValue).getExerciseName();
        }
    };
}
