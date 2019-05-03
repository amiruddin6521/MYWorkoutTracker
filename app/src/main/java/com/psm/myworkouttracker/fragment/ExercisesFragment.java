package com.psm.myworkouttracker.fragment;

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.database.DbAdapter;
import com.psm.myworkouttracker.database.DbHelper;

public class ExercisesFragment extends Fragment {

    private EditText filterExercises;
    private ListView exercises_list;
    private Button btnAddExercise;
    private DbAdapter dbA;
    private Cursor c;
    private SimpleCursorAdapter sca;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exercises, container, false);

        dbA = new DbAdapter(getActivity());

        filterExercises = v.findViewById(R.id.filterExercises);
        exercises_list = v.findViewById(R.id.exercises_list);
        btnAddExercise = v.findViewById(R.id.btnAddExercise);

        String[] from = { DbHelper.NAME_MACHINE};
        int[] to = {R.id.txtExercise};
        c = dbA.getExercisesData();
        sca = new SimpleCursorAdapter(getContext(),
                R.layout.exercises_list, c, from, to);
        sca.setFilterQueryProvider(new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {
                String partialValue = constraint.toString();
                return dbA.getExercisesDataValue(partialValue);

            }
        });

        exercises_list.setAdapter(sca);
        exercises_list.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                Bundle b = new Bundle();
                c = (Cursor) arg0.getItemAtPosition(arg2);
                int keyid = c.getInt(c
                        .getColumnIndex(DbHelper.ID_MACHINE));
                b.putInt("keyid", keyid);
                /*Intent i = new Intent(ListCategoryActivity.this,
                        EditCategoryActivity.class);
                i.putExtras(b);
                startActivity(i);*/
            }
        });

        filterExercises.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                sca.getFilter().filter(s);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }
}
