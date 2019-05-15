package com.psm.myworkouttracker.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.adapter.AutoCompleteAdapter;
import com.psm.myworkouttracker.adapter.ExercisesAdapter;
import com.psm.myworkouttracker.adapter.WorkoutTabAdapterB;
import com.psm.myworkouttracker.adapter.WorkoutTabAdapterC;
import com.psm.myworkouttracker.services.WebServiceCallArr;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class WorkoutTabFragment extends Fragment {

    private TextView txtType, edtDurr;
    private EditText edtSets, edtReps, edtWeight, edtDist, dateUpd;
    private Button addBtn;
    private ListView listWorkout;
    private ImageButton btnPlusB1, btnPlusB2, btnPlusB3, btnMinusB1, btnMinusB2, btnMinusB3, btnPlusC1, btnMinusC1, btnTime, btnListMachine;
    private AutoCompleteTextView edtMachine;
    private AlertDialog myDialog;
    private View bodybuilding, cardio;
    private JSONObject jsnObj = new JSONObject();
    private JSONArray jsnArr = new JSONArray();
    private List<String> dataValues, bMachine, cMachine, bDate, bTime, cDate, cTime, sets, reps, weight, dist, durr;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private ExercisesAdapter mExercisesAdapter;
    private AutoCompleteAdapter adapter;
    private WorkoutTabAdapterB bAdapter;
    private WorkoutTabAdapterC cAdapter;
    private String uId, mId;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workouttab, container, false);

        bodybuilding = v.findViewById(R.id.bodybuilding);
        cardio = v.findViewById(R.id.cardio);
        txtType = v.findViewById(R.id.txtType);
        addBtn = v.findViewById(R.id.addPreff);
        listWorkout = v.findViewById(R.id.listWorkout);
        btnPlusB1 = v.findViewById(R.id.btnPlusB1);
        btnPlusB2 = v.findViewById(R.id.btnPlusB2);
        btnPlusB3 = v.findViewById(R.id.btnPlusB3);
        btnMinusB1 = v.findViewById(R.id.btnMinusB1);
        btnMinusB2 = v.findViewById(R.id.btnMinusB2);
        btnMinusB3 = v.findViewById(R.id.btnMinusB3);
        btnPlusC1 = v.findViewById(R.id.btnPlusC1);
        btnMinusC1 = v.findViewById(R.id.btnMinusC1);
        btnTime = v.findViewById(R.id.btnTime);
        edtSets = v.findViewById(R.id.edtSets);
        edtReps = v.findViewById(R.id.edtReps);
        edtWeight = v.findViewById(R.id.edtWeightT);
        edtDist = v.findViewById(R.id.edtDist);
        edtDurr = v.findViewById(R.id.edtDurr);
        dateUpd = v.findViewById(R.id.dateUpd);
        btnListMachine = v.findViewById(R.id.btnListMachine);
        edtMachine = v.findViewById(R.id.edtMachine);

        edtMachine.setOnFocusChangeListener(touchRazEdit);
        dateUpd.setOnFocusChangeListener(touchRazEdit);
        edtSets.setOnFocusChangeListener(touchRazEdit);
        edtReps.setOnFocusChangeListener(touchRazEdit);
        edtWeight.setOnFocusChangeListener(touchRazEdit);
        edtDist.setOnFocusChangeListener(touchRazEdit);
        edtDurr.setOnFocusChangeListener(touchRazEdit);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        loadListData();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateUpd.setText(date);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String type = txtType.getText().toString();
                if(type.equals("Cardio")) {
                    saveCData();
                } else if(type.equals("Body Building")) {
                    saveBData();
                } else {
                    Toast.makeText(getActivity(),"Please choose your exercise first!",Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnListMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
                hideKeyboard(btnListMachine);
            }
        });

        btnPlusB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger;
                if (edtSets.getText().toString().equals("")) {
                    edtSets.setText("1");
                    minteger = Integer.parseInt(edtSets.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtSets.getText().toString());
                }

                minteger = minteger + 1;
                edtSets.setText("" + minteger);
            }
        });

        btnPlusB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger;
                if (edtReps.getText().toString().equals("")) {
                    edtReps.setText("1");
                    minteger = Integer.parseInt(edtReps.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtReps.getText().toString());
                }

                minteger = minteger + 1;
                edtReps.setText("" + minteger);
            }
        });

        btnPlusB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double mdouble;
                if (edtWeight.getText().toString().equals("")) {
                    edtWeight.setText("0.1");
                    mdouble = Double.parseDouble(edtWeight.getText().toString());
                } else {
                    mdouble = Double.parseDouble(edtWeight.getText().toString());
                }

                mdouble = mdouble + 0.1;
                edtWeight.setText("" + String.format("%.1f", mdouble));
            }
        });

        btnMinusB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger;
                if (edtSets.getText().toString().equals("")) {
                    edtSets.setText("1");
                    minteger = Integer.parseInt(edtSets.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtSets.getText().toString());
                }

                if (minteger > 1) {
                    minteger = minteger - 1;
                    edtSets.setText("" + minteger);
                }
            }
        });

        btnMinusB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger;
                if (edtReps.getText().toString().equals("")) {
                    edtReps.setText("1");
                    minteger = Integer.parseInt(edtReps.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtReps.getText().toString());
                }

                if (minteger > 1) {
                    minteger = minteger - 1;
                    edtReps.setText("" + minteger);
                }
            }
        });

        btnMinusB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double mdouble;
                if (edtWeight.getText().toString().equals("")) {
                    edtWeight.setText("0.1");
                    mdouble = Double.parseDouble(edtWeight.getText().toString());
                } else {
                    mdouble = Double.parseDouble(edtWeight.getText().toString());
                }

                if(mdouble > 0.1) {
                    mdouble = mdouble - 0.1;
                    edtWeight.setText("" + String.format("%.1f", mdouble));
                }
            }
        });

        btnPlusC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double mdouble;
                if (edtDist.getText().toString().equals("")) {
                    edtDist.setText("0.1");
                    mdouble = Double.parseDouble(edtDist.getText().toString());
                } else {
                    mdouble = Double.parseDouble(edtDist.getText().toString());
                }

                mdouble = mdouble + 0.1;
                edtDist.setText("" + String.format("%.1f", mdouble));
            }
        });

        btnMinusC1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                double mdouble;
                if (edtDist.getText().toString().equals("")) {
                    edtDist.setText("0.1");
                    mdouble = Double.parseDouble(edtDist.getText().toString());
                } else {
                    mdouble = Double.parseDouble(edtDist.getText().toString());
                }

                if(mdouble > 0.1) {
                    mdouble = mdouble - 0.1;
                    edtDist.setText("" + String.format("%.1f", mdouble));
                }
            }
        });

        btnTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePicker();
            }
        });

        return v;
    }

    private View.OnFocusChangeListener touchRazEdit = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.edtMachine:
                        clearUIData();
                        break;
                    case R.id.dateUpd:
                        calenderPicker();
                        break;
                    case R.id.edtSets:
                        edtSets.setText("");
                        break;
                    case R.id.edtReps:
                        edtReps.setText("");
                        break;
                    case R.id.edtWeightT:
                        edtWeight.setText("");
                        break;
                    case R.id.edtDist:
                        edtDist.setText("");
                        break;
                    case R.id.edtDurr:
                        showTimePicker();
                        break;
                }
            } /*else if (!hasFocus) {
                switch (v.getId()) {
                    case R.id.edtMachine:
                        setCurrentMachine(edtMachine.getText().toString());
                        break;
                }
            }*/
        }
    };

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtDurr.setText( convertDate(selectedHour) + ":" + convertDate(selectedMinute));
                edtDurr.clearFocus();
            }
        }, 0, 0, true);//Yes 24 hour time
        mTimePicker.setTitle("Select Time");
        mTimePicker.show();
    }

    private void calenderPicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(),
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        int month = monthOfYear + 1;
                        dateUpd.setText(convertDate(year) + "-" + convertDate(month) + "-" + convertDate(dayOfMonth));
                        hideKeyboard(dateUpd);
                        dateUpd.clearFocus();

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    public String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    public void showAlert() {
        AlertDialog.Builder myBuilder = new AlertDialog.Builder(getActivity());
        myBuilder.setTitle("Select an existing exercises: ");
        mExercisesAdapter = new ExercisesAdapter(getActivity(), dataValues);
        myBuilder.setAdapter(mExercisesAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int position) {
                String itemName = mExercisesAdapter.getItemName(position);
                loadMachine(itemName);
            }
        });
        myBuilder.setNegativeButton("Cancel", null);

        myDialog = myBuilder.create();
        myDialog.show();
    }

    public void loadWorkoutDataB(final String machine) {
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListB"));
                params.add(new BasicNameValuePair("uId", uId));
                params.add(new BasicNameValuePair("mId", mId));

                jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                jsnObj = null;
                bMachine = new ArrayList<>();
                bDate = new ArrayList<>();
                bTime = new ArrayList<>();
                sets = new ArrayList<>();
                reps = new ArrayList<>();
                weight = new ArrayList<>();

                try{
                    if (jsnArr != null) {
                        for (int i = 0; i < jsnArr.length(); i++) {
                            jsnObj = jsnArr.getJSONObject(i);
                            String data1 = jsnObj.getString("bDate");
                            String data2 = jsnObj.getString("bTime");
                            String data3 = jsnObj.getString("sets");
                            String data4 = jsnObj.getString("reps");
                            String data5 = jsnObj.getString("weight");


                            bMachine.add(machine);
                            bDate.add(data1);
                            bTime.add(data2);
                            sets.add(data3);
                            reps.add(data4);
                            weight.add(data5);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(bDate != null)
                        {
                            loadDataB();
                        } else {
                            Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void loadDataB() {
        bAdapter = new WorkoutTabAdapterB(getActivity(), bMachine, bDate, bTime, sets, reps, weight);
        listWorkout.setAdapter(bAdapter);
        listWorkout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(),"Test: "+itemName,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadWorkoutDataC(final String machine) {
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListC"));
                params.add(new BasicNameValuePair("uId", uId));
                params.add(new BasicNameValuePair("mId", mId));

                jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                jsnObj = null;
                cMachine = new ArrayList<>();
                cDate = new ArrayList<>();
                cTime = new ArrayList<>();
                dist = new ArrayList<>();
                durr = new ArrayList<>();

                try{
                    if (jsnArr != null) {
                        for (int i = 0; i < jsnArr.length(); i++) {
                            jsnObj = jsnArr.getJSONObject(i);
                            String data1 = jsnObj.getString("cDate");
                            String data2 = jsnObj.getString("cTime");
                            String data3 = jsnObj.getString("distance");
                            String data4 = jsnObj.getString("duration");


                            cMachine.add(machine);
                            cDate.add(data1);
                            cTime.add(data2);
                            dist.add(data3);
                            durr.add(data4);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(cDate != null)
                        {
                            loadDataC();
                        } else {
                            Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
                        }

                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void loadDataC() {
        cAdapter = new WorkoutTabAdapterC(getActivity(), cMachine, cDate, cTime, dist, durr);
        listWorkout.setAdapter(cAdapter);
        listWorkout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String itemName = parent.getItemAtPosition(position).toString();
                Toast.makeText(getActivity(),"Test: "+itemName,Toast.LENGTH_LONG).show();
            }
        });
    }

    public void loadListData() {
        Runnable run = new Runnable()
        {
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnMachineList"));
                params.add(new BasicNameValuePair("id", uId));

                jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                jsnObj = null;
                dataValues = new ArrayList<>();

                try{
                    if (jsnArr != null) {
                        for (int i = 0; i < jsnArr.length(); i++) {
                            jsnObj = jsnArr.getJSONObject(i);
                            String data = jsnObj.getString("name");
                            dataValues.add(data);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(dataValues != null) {
                            adapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, dataValues);
                            edtMachine.setAdapter(adapter);
                            edtMachine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                    String itemName = parent.getItemAtPosition(position).toString();
                                    loadMachine(itemName);
                                    hideKeyboard(edtMachine);
                                    edtMachine.clearFocus();
                                }
                            });
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void loadMachine(final String name) {
        Runnable run = new Runnable()
        {
            String strRespond, type;
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnGetMachine"));
                params.add(new BasicNameValuePair("id", uId));
                params.add(new BasicNameValuePair("name", name));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    type = jsnObj.getString("type");
                    mId = jsnObj.getString("id");
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            setUIData(name, type);
                        } else {
                            Toast.makeText(getActivity(), "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void setUIData(String name, String type) {
        if(type.equals("Cardio")) {
            bodybuilding.setVisibility(View.GONE);
            cardio.setVisibility(View.VISIBLE);
            edtMachine.setText(name);
            txtType.setText(type);
            edtSets.setText("1");
            edtReps.setText("1");
            edtWeight.setText("5.0");
            hideKeyboard(edtMachine);
            edtMachine.clearFocus();
            loadWorkoutDataC(name);
        } else {
            cardio.setVisibility(View.GONE);
            bodybuilding.setVisibility(View.VISIBLE);
            edtMachine.setText(name);
            txtType.setText(type);
            edtDist.setText("5.0");
            edtDurr.setText("00:10");
            hideKeyboard(edtMachine);
            edtMachine.clearFocus();
            loadWorkoutDataB(name);
        }
    }

    public void clearUIData() {
        cardio.setVisibility(View.GONE);
        bodybuilding.setVisibility(View.VISIBLE);
        edtMachine.setText("");
        edtSets.setText("1");
        edtReps.setText("1");
        edtWeight.setText("5.0");
        edtDist.setText("5.0");
        edtDurr.setText("00:10");
        txtType.setText("-");
        mId = "";
        listWorkout.setAdapter(null);
        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateUpd.setText(date);
        //achineImage.setImageResource(R.drawable.ic_machine);
    }

    //Save bodybuilding data
    public void saveBData() {

        Runnable run = new Runnable()
        {
            String strRespond = "";
            String sets, reps, weight, date;
            @Override
            public void run()
            {
                sets = edtSets.getText().toString();
                reps = edtReps.getText().toString();
                weight = edtWeight.getText().toString();
                date = dateUpd.getText().toString();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnAddWorkoutB"));
                params.add(new BasicNameValuePair("varSets", sets));
                params.add(new BasicNameValuePair("varReps", reps));
                params.add(new BasicNameValuePair("varWeight", weight));
                params.add(new BasicNameValuePair("varDate", date));
                params.add(new BasicNameValuePair("varMachine", mId));
                params.add(new BasicNameValuePair("varUser", uId));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            String name = edtMachine.getText().toString();
                            Toast.makeText(getActivity(),name,Toast.LENGTH_LONG).show();
                            loadWorkoutDataB(name);
                        } else {
                            Toast.makeText(getActivity(), "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    //Save bodybuilding data
    public void saveCData() {

        Runnable run = new Runnable()
        {
            String strRespond = "";
            String dist, durr, date;
            @Override
            public void run()
            {
                dist = edtDist.getText().toString();
                durr = edtDurr.getText().toString();
                date = dateUpd.getText().toString();

                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnAddWorkoutC"));
                params.add(new BasicNameValuePair("varDist", dist));
                params.add(new BasicNameValuePair("varDurr", durr));
                params.add(new BasicNameValuePair("varDate", date));
                params.add(new BasicNameValuePair("varMachine", mId));
                params.add(new BasicNameValuePair("varUser", uId));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            String name = edtMachine.getText().toString();
                            Toast.makeText(getActivity(),name,Toast.LENGTH_LONG).show();
                            loadWorkoutDataC(name);
                        } else {
                            Toast.makeText(getActivity(), "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
