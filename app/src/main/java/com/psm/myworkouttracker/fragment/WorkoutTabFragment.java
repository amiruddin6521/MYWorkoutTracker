package com.psm.myworkouttracker.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.graphics.Paint;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.TimePicker;

import com.psm.myworkouttracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class WorkoutTabFragment extends Fragment {

    private TextView excType, edtDurr;
    private EditText tvDate, edtSets, edtReps, edtWeight, edtDist, dateUpd;
    private Button addBtn;
    private ImageButton btnPlusB1, btnPlusB2, btnPlusB3, btnMinusB1, btnMinusB2, btnMinusB3, btnPlusC1, btnMinusC1, btnTime;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_workouttab, container, false);

        excType = v.findViewById(R.id.excType);
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

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(date);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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
}
