package com.psm.myworkouttracker.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.adapter.ExercisesAdapter;
import com.psm.myworkouttracker.services.WebServiceCallArr;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ExercisesFragment extends Fragment {

    private EditText filterExercises;
    private ListView listExercises;
    private Button btnAddExercise;
    private JSONObject jsnObj = new JSONObject();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private JSONArray jsnArr = new JSONArray();
    private List<String> values = new ArrayList<>();
    private View progExercises, fragExercises;
    private ExercisesAdapter mExercisesAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exercises, container, false);

        filterExercises = v.findViewById(R.id.filterExercises);
        listExercises = v.findViewById(R.id.listExercises);
        btnAddExercise = v.findViewById(R.id.btnAddExercise);
        progExercises = v.findViewById(R.id.progExercises);
        fragExercises = v.findViewById(R.id.fragExercises);

        loadListData();

        btnAddExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        return v;
    }

    public void loadListData() {
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnMachineList"));

                jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                jsnObj = null;

                try{
                    if (jsnArr != null) {
                        for (int i = 0; i < jsnArr.length(); i++) {
                            jsnObj = jsnArr.getJSONObject(i);

                            String data = jsnObj.getString("name");
                            values.add(data);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(values != null)
                        {
                            loadList();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void loadList() {
        progExercises.setVisibility(View.GONE);
        fragExercises.setVisibility(View.VISIBLE);

        mExercisesAdapter = new ExercisesAdapter(getContext(), values);
        listExercises.setAdapter(mExercisesAdapter);
        listExercises.setTextFilterEnabled(true);

        filterExercises.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mExercisesAdapter.getFilter().filter(s.toString());
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
}
