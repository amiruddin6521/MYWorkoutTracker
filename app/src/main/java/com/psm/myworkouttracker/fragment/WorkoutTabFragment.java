package com.psm.myworkouttracker.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.myworkouttracker.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class WorkoutTabFragment extends Fragment {

    private TextView excType;
    private EditText tvDate, edtSets, edtReps, edtWeight, edtDist, edtDurr;
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

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnPlusB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtSets.getText().toString());
                minteger = minteger + 1;
                edtSets.setText("" + minteger);
            }
        });

        btnPlusB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtReps.getText().toString());
                minteger = minteger + 1;
                edtReps.setText("" + minteger);
            }
        });

        btnPlusB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtWeight.getText().toString());
                minteger = minteger + 1;
                edtWeight.setText("" + minteger);
            }
        });

        btnMinusB1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtSets.getText().toString());
                if (minteger > 1) {
                    minteger = minteger - 1;
                    edtSets.setText("" + minteger);
                }
            }
        });

        btnMinusB2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtReps.getText().toString());
                if (minteger > 1) {
                    minteger = minteger - 1;
                    edtReps.setText("" + minteger);
                }
            }
        });

        btnMinusB3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int minteger = Integer.parseInt(edtWeight.getText().toString());
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

            }
        });

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        tvDate.setText(date);

        return v;
    }
}
