package com.psm.myworkouttracker.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.github.mikephil.charting.listener.OnChartGestureListener;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.psm.myworkouttracker.R;

import java.util.ArrayList;

public class GraphTabFragment extends Fragment {

    private static final String TAG = "GraphTabFragment";
    private LineChart mChart;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graphtab, container, false);

        mChart = v.findViewById(R.id.chartExercise);

        /*mChart.setOnChartGestureListener(getActivity());
        mChart.setOnChartValueSelectedListener(getActivity());*/

        mChart.setDragEnabled(true);
        mChart.setScaleEnabled(true);
        mChart.getDescription().setText("Description of my chart");

        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yValues = new ArrayList<>();
        yValues.add(new Entry(0, 50f));
        yValues.add(new Entry(1, 50f));
        yValues.add(new Entry(2, 70f));
        yValues.add(new Entry(3, 30f));
        yValues.add(new Entry(4, 50f));
        yValues.add(new Entry(5, 60f));
        yValues.add(new Entry(6, 65f));

        LineDataSet set1 = new LineDataSet(yValues, "Data Set 1");

        set1.setFillAlpha(110);
        set1.setColor(Color.parseColor("#2E3192"));
        set1.setLabel("This is test");
        set1.setValueTextSize(10f);
        set1.setCircleColor(Color.parseColor("#2E3192"));
        set1.setCircleSize(4f);
        set1.setLineWidth(2f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        mChart.setData(data);

        final String[] values = new String[] {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "July", "Aug"};

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(values));

        return v;
    }

    public class  MyXAxisValueFormatter implements IAxisValueFormatter {
        private String[] mValues;
        public MyXAxisValueFormatter(String[] values){
            mValues = values;
        }

        @Override
        public String getFormattedValue(float value, AxisBase axis) {
            return mValues[(int)value];
        }
    }
}
