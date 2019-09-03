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
import android.widget.ImageButton;
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

public class WeightTrackFragment extends Fragment {
    private EditText dateMeasure, currMeasure;
    private LineChart mChart;
    private FloatingActionButton fab;
    private ListView listMeasure;
    private View progMeasure, fragMeasure;
    private JSONObject jsnObj = new JSONObject();
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private JSONArray jsnArr = new JSONArray();
    private List<String> mId, date, weight, cDate, cWeight;
    private String id;
    private MeasureAdapter measureAdapter;
    private TextView txtBMIValue, txtBMIStatus;
    private ImageButton btnInfo;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_weight_track, container, false);

        mChart = v.findViewById(R.id.chartMeasure);
        dateMeasure = v.findViewById(R.id.dateMeasure);
        currMeasure = v.findViewById(R.id.currMeasure);
        listMeasure = v.findViewById(R.id.listMeasure);
        progMeasure = v.findViewById(R.id.progMeasure);
        fragMeasure = v.findViewById(R.id.fragMeasure);
        txtBMIValue = v.findViewById(R.id.txtBMIValue);
        txtBMIStatus = v.findViewById(R.id.txtBMIStatus);
        btnInfo = v.findViewById(R.id.btnInfo);

        dateMeasure.setOnFocusChangeListener(touchRazEdit);

        MainActivity activity = (MainActivity) getActivity();
        id = activity.getMyData();

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        dateMeasure.setText(date);

        loadWeightGraph();

        fab = v.findViewById(R.id.btnAddMeasure);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String date = dateMeasure.getText().toString();
                String weight = currMeasure.getText().toString();
                if(!date.equals("") && !weight.equals("")) {
                    updateWeight(date, weight);
                    String currDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                    dateMeasure.setText(currDate);
                    currMeasure.setText("");
                    hideKeyboard(currMeasure);
                    currMeasure.clearFocus();
                } else {
                    Toast.makeText(getActivity(), "Date and weight form should not leave empty!", Toast.LENGTH_LONG).show();
                }
            }
        });

        btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setTitle("Body Mass Index (BMI)");
                alertDialog.setMessage(R.string.status);
                alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                alertDialog.show();
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

    public void loadWeightList() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWeightList"));
                    params.add(new BasicNameValuePair("id", id));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    mId = new ArrayList<>();
                    date = new ArrayList<>();
                    weight = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("_id");
                                String data1 = jsnObj.getString("cDate");
                                String data2 = jsnObj.getString("weight");
                                mId.add(data);
                                date.add(data1);
                                weight.add(data2);
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
        measureAdapter = new MeasureAdapter(getActivity(), mId, date, weight);
        listMeasure.setAdapter(measureAdapter);
        listMeasure.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemID = measureAdapter.getItemID(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Delete Weight Record");
                alertDialog.setMessage("Are you sure you want to delete?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(weight.size() <= 1){
                            Toast.makeText(getActivity(), "Sorry, you cannot delete the last record of your weight.", Toast.LENGTH_LONG).show();
                        } else {
                            deleteWeightList(itemID);
                        }
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
        loadBMI();
    }

    //Delete exercise data for body building
    public void deleteWeightList(final String id) {
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
                    params.add(new BasicNameValuePair("selectFn", "fnDeleteWeightList"));
                    params.add(new BasicNameValuePair("id", id));

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
                                loadWeightGraph();
                                Toast.makeText(getActivity(), "Weight record successfully deleted!", Toast.LENGTH_LONG).show();
                                progMeasure.setVisibility(View.GONE);
                                fragMeasure.setVisibility(View.VISIBLE);
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
    public void updateWeight(final String date, final String weight) {
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
                    params.add(new BasicNameValuePair("selectFn", "fnSaveWeight"));
                    params.add(new BasicNameValuePair("varId", id));
                    params.add(new BasicNameValuePair("varDate", date));
                    params.add(new BasicNameValuePair("varWeight", weight));

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
                                loadWeightGraph();
                                Toast.makeText(getActivity(), "Weight successfully recorded!", Toast.LENGTH_SHORT).show();
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

    public void loadWeightGraph() {
        progMeasure.setVisibility(View.VISIBLE);
        fragMeasure.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWeightGraph"));
                    params.add(new BasicNameValuePair("id", id));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    mId = new ArrayList<>();
                    cDate = new ArrayList<>();
                    cWeight = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("_id");
                                String data1 = jsnObj.getString("cDate");
                                String data2 = jsnObj.getString("weight");
                                mId.add(data);
                                cDate.add(data1);
                                cWeight.add(data2);
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
                            if (cDate != null) {
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
        mChart.getDescription().setText("Weight/Date");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < cWeight.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(cWeight.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, "Weight");
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
        for(int i = 0; i < cDate.size(); i++){
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date inputDate = fmt.parse(cDate.get(i));

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

        loadWeightList();
    }

    //Load BMI data
    public void loadBMI() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond, weight, height;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnLoadBMI"));
                    params.add(new BasicNameValuePair("id", id));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        weight = jsnObj.getString("weight");
                        height = jsnObj.getString("height");
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")){
                                checkBMI(weight, height);
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

    public void checkBMI(String weight, String height) {
        double weightV = Double.parseDouble(weight);
        double heightV = Double.parseDouble(height)/100;
        double bmiValue =  weightV / (heightV * heightV);

        String status;
        if (bmiValue < 16) {
            status = "Severely underweight";
        } else if (bmiValue < 18.5) {

            status = "Underweight";
        } else if (bmiValue < 25) {

            status = "Normal";
        } else if (bmiValue < 30) {

            status = "Overweight";
        } else {
            status = "Obesity";
        }

        txtBMIValue.setText(String.format("%.1f", bmiValue));
        txtBMIStatus.setText(status);
        progMeasure.setVisibility(View.GONE);
        fragMeasure.setVisibility(View.VISIBLE);
    }
}
