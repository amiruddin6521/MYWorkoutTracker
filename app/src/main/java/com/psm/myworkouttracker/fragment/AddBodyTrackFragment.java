package com.psm.myworkouttracker.fragment;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.adapter.MeasureAdapter;
import com.psm.myworkouttracker.services.WebServiceCallArr;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AddBodyTrackFragment extends Fragment {

    private EditText dateMeasure, currMeasure;
    private LineChart mChart;
    private FloatingActionButton fab;
    private ListView listMeasure;
    private View progMeasure, fragMeasure;
    private JSONObject jsnObj = new JSONObject();
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private JSONArray jsnArr = new JSONArray();
    private List<String> mId, date, measure, mDate, mMeasure;
    private String id, btId;
    private MeasureAdapter measureAdapter;
    private TextView txtBMIValue, txtBMIStatus;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bodytrack_add, container, false);

        mChart = v.findViewById(R.id.chartMeasure);
        dateMeasure = v.findViewById(R.id.dateMeasure);
        currMeasure = v.findViewById(R.id.currMeasure);
        listMeasure = v.findViewById(R.id.listMeasure);
        progMeasure = v.findViewById(R.id.progMeasure);
        fragMeasure = v.findViewById(R.id.fragMeasure);
        txtBMIValue = v.findViewById(R.id.txtBMIValue);
        txtBMIStatus = v.findViewById(R.id.txtBMIStatus);

        dateMeasure.setOnFocusChangeListener(touchRazEdit);

        MainActivity activity = (MainActivity) getActivity();
        id = activity.getMyData();

        Bundle bundle = getArguments();
        btId = bundle.getString("idBody");

        loadMeasureGraph();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateMeasure.setText(date);

        fab = v.findViewById(R.id.btnAddMeasure);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dateMeasure.getText().toString();
                String measure = currMeasure.getText().toString();
                if(!date.equals("") && !measure.equals("")) {
                    updateMeasure(date, measure);
                    String currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    dateMeasure.setText(currDate);
                    currMeasure.setText("");
                    hideKeyboard(currMeasure);
                    currMeasure.clearFocus();
                } else {
                    Toast.makeText(getActivity(), "Date and measure form should not left empty!", Toast.LENGTH_LONG).show();
                }
            }
        });

        return v;
    }

    private View.OnFocusChangeListener touchRazEdit = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.dateMeasure:
                        calenderPicker();
                        break;

                }
            }
        }
    };

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
                        dateMeasure.setText(convertDate(year) + "-" + convertDate(month) + "-" + convertDate(dayOfMonth));
                        hideKeyboard(dateMeasure);
                        dateMeasure.clearFocus();

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

    public void hideKeyboard(View view) {
        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    private boolean haveNetwork() {
        boolean haveWiFi = false;
        boolean haveMobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getActivity().getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();

        for(NetworkInfo info : networkInfo) {
            if(info.getTypeName().equalsIgnoreCase("WIFI")) {
                if(info.isConnected()) {
                    haveWiFi = true;
                }
            }
            if(info.getTypeName().equalsIgnoreCase("MOBILE")) {
                if(info.isConnected()) {
                    haveMobileData = true;
                }
            }
        }
        return haveMobileData || haveWiFi;
    }

    public void loadMeasureGraph() {
        progMeasure.setVisibility(View.VISIBLE);
        fragMeasure.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnMeasureGraph"));
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("btId", btId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    mId = new ArrayList<>();
                    mDate = new ArrayList<>();
                    mMeasure = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("_id");
                                String data1 = jsnObj.getString("btDate");
                                String data2 = jsnObj.getString("bodymeasure");
                                mId.add(data);
                                mDate.add(data1);
                                mMeasure.add(data2);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (mDate != null) {
                                drawLineChart();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    public void drawLineChart() {
        mChart.getDescription().setText("Measure/Date");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < mMeasure.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(mMeasure.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, "Measure");
        lineDataSet.setColor(Color.parseColor("#002f87"));
        lineDataSet.setValueTextSize(10f);
        lineDataSet.setLineWidth(2f);
        lineDataSet.setCircleColor(Color.parseColor("#002f87"));
        lineDataSet.setCircleSize(4f);

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(lineDataSet);

        LineData lineData = new LineData(dataSets);
        mChart.setData(lineData);

        List<String> newDate = new ArrayList<>();
        for(int i = 0; i < mDate.size(); i++){
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date inputDate = fmt.parse(mDate.get(i));

                fmt = new SimpleDateFormat("dd-MMM");
                newDate.add(fmt.format(inputDate));
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        XAxis xAxis = mChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setValueFormatter(new IndexAxisValueFormatter(newDate));

        mChart.notifyDataSetChanged();
        mChart.invalidate();

        loadMeasureList();
    }

    public void loadMeasureList() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnMeasureList"));
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("btId", btId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    mId = new ArrayList<>();
                    date = new ArrayList<>();
                    measure = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("_id");
                                String data1 = jsnObj.getString("btDate");
                                String data2 = jsnObj.getString("bodymeasure");
                                mId.add(data);
                                date.add(data1);
                                measure.add(data2);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(date != null)
                            {
                                loadList();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    public void loadList() {
        measureAdapter = new MeasureAdapter(getActivity(), mId, date, measure);
        listMeasure.setAdapter(measureAdapter);
        listMeasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemID = measureAdapter.getItemID(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Delete Measure Record");
                alertDialog.setMessage("Are you sure you want to delete?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteMeasureList(itemID);
                        Toast.makeText(getActivity(),itemID,Toast.LENGTH_SHORT).show();
                    }
                });

                alertDialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
            }
        });
        progMeasure.setVisibility(View.GONE);
        fragMeasure.setVisibility(View.VISIBLE);
    }

    //Delete exercise data for body building
    public void deleteMeasureList(final String id) {
        progMeasure.setVisibility(View.VISIBLE);
        fragMeasure.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnDeleteMeasureList"));
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("btId", btId));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadMeasureGraph();
                                Toast.makeText(getActivity(), "Measure record successfully deleted!", Toast.LENGTH_LONG).show();
                                progMeasure.setVisibility(View.GONE);
                                fragMeasure.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    //Update weight data
    public void updateMeasure(final String date, final String measure) {
        progMeasure.setVisibility(View.VISIBLE);
        fragMeasure.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnSaveMeasure"));
                    params.add(new BasicNameValuePair("varId", id));
                    params.add(new BasicNameValuePair("varDate", date));
                    params.add(new BasicNameValuePair("varMeasure", measure));
                    params.add(new BasicNameValuePair("btId", btId));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadMeasureGraph();
                                Toast.makeText(getActivity(), "Measure successfully recorded!", Toast.LENGTH_SHORT).show();
                                progMeasure.setVisibility(View.GONE);
                                fragMeasure.setVisibility(View.VISIBLE);
                            } else {
                                Toast.makeText(getActivity(), "Test: "+strRespond, Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }
}
