package com.psm.myworkouttracker.fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.adapter.AutoCompleteAdapter;
import com.psm.myworkouttracker.adapter.ExercisesAdapter;
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
import java.util.TimeZone;

public class WorkoutTabFragment extends Fragment {

    private TextView txtType, edtDurr;
    private EditText tvDate, edtSets, edtReps, edtWeight, edtDist, dateUpd;
    private Button addBtn;
    private ImageButton btnPlusB1, btnPlusB2, btnPlusB3, btnMinusB1, btnMinusB2, btnMinusB3, btnPlusC1, btnMinusC1, btnTime, btnListMachine;
    private AutoCompleteTextView edtMachine;
    private AlertDialog myDialog;
    private View bodybuilding, cardio;
    private JSONObject jsnObj = new JSONObject();
    private JSONArray jsnArr = new JSONArray();
    private List<String> dataValues = new ArrayList<>();
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private ExercisesAdapter mExercisesAdapter;
    private AutoCompleteAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workouttab, container, false);

        bodybuilding = v.findViewById(R.id.bodybuilding);
        cardio = v.findViewById(R.id.cardio);
        txtType = v.findViewById(R.id.txtType);
        tvDate = v.findViewById(R.id.dateUpd);
        addBtn = v.findViewById(R.id.addPreff);
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
        edtWeight = v.findViewById(R.id.edtWeight);
        edtDist = v.findViewById(R.id.edtDist);
        edtDurr = v.findViewById(R.id.edtDurr);
        dateUpd = v.findViewById(R.id.dateUpd);
        btnListMachine = v.findViewById(R.id.btnListMachine);
        edtMachine = v.findViewById(R.id.edtMachine);

        loadListData();
        mExercisesAdapter = new ExercisesAdapter(getActivity(), dataValues);

        adapter = new AutoCompleteAdapter(getActivity(), android.R.layout.simple_dropdown_item_1line, android.R.id.text1, getData());
        edtMachine.setAdapter(adapter);
        edtMachine.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(getActivity(),"Value: "+ parent.getItemAtPosition(position), Toast.LENGTH_SHORT).show();
            }
        });

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(date);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnListMachine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlert();
            }
        });

        dateUpd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderPicker();
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
                int minteger;
                if (edtWeight.getText().toString().equals("")) {
                    edtWeight.setText("1");
                    minteger = Integer.parseInt(edtWeight.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtWeight.getText().toString());
                }

                minteger = minteger + 1;
                edtWeight.setText("" + minteger);
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
                int minteger;
                if (edtWeight.getText().toString().equals("")) {
                    edtWeight.setText("1");
                    minteger = Integer.parseInt(edtWeight.getText().toString());
                } else {
                    minteger = Integer.parseInt(edtWeight.getText().toString());
                }

                if (minteger > 1) {
                    minteger = minteger - 1;
                    edtWeight.setText("" + minteger);
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

    private void showTimePicker() {
        Calendar mcurrentTime = Calendar.getInstance();
        int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
        int minute = mcurrentTime.get(Calendar.MINUTE);
        TimePickerDialog mTimePicker;
        mTimePicker = new TimePickerDialog(getActivity(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                edtDurr.setPaintFlags(edtDurr.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
                edtDurr.setText( convertDate(selectedHour) + ":" + convertDate(selectedMinute));
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
                            dataValues.add(data);
                        }
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {

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
                params.add(new BasicNameValuePair("name", name));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    type = jsnObj.getString("type");
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    e.printStackTrace();
                }

                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            edtMachine.setText(name);
                            txtType.setText(type);
                            changeUI(type);
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

    public void changeUI(String name) {
        if(name.equals("Cardio")) {
            bodybuilding.setVisibility(View.GONE);
            cardio.setVisibility(View.VISIBLE);
        } else {
            cardio.setVisibility(View.GONE);
            bodybuilding.setVisibility(View.VISIBLE);
        }
    }

    private List<String> getData(){
        List<String> dataList = new ArrayList<String>();
        dataList.add("Fashion Men");
        dataList.add("Fashion Women");
        dataList.add("Baby");
        dataList.add("Kids");
        dataList.add("Electronics");
        dataList.add("Appliance");
        dataList.add("Travel");
        dataList.add("Bags");
        dataList.add("FootWear");
        dataList.add("Jewellery");
        dataList.add("Sports");
        dataList.add("Electrical");
        dataList.add("Sports Kids");
        return dataList;
    }
}
