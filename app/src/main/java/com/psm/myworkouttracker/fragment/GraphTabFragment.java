package com.psm.myworkouttracker.fragment;

import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.services.WebServiceCallArr;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class GraphTabFragment extends Fragment {

    private LineChart mChart;
    private Spinner spnExercise, spnDisplay;
    private JSONObject jsnObj = new JSONObject();
    private JSONArray jsnArr = new JSONArray();
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private View progGraph, fragGraph, txtNoHistory;
    private List<String> bDate, cDate, weight, dist, durr, exValues, speed;
    private String uId, mId, tId;
    private String status;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_graph_tab, container, false);

        mChart = v.findViewById(R.id.chartExercise);
        progGraph = v.findViewById(R.id.progGraph);
        fragGraph = v.findViewById(R.id.fragGraph);
        spnExercise = v.findViewById(R.id.spnExercise);
        spnDisplay = v.findViewById(R.id.spnDisplay);
        txtNoHistory = v.findViewById(R.id.txtNoHistory);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        loadExerciseData();

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = parent.getItemAtPosition(position).toString();
                spnDisplay.setAdapter(null);
                loadMachine(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDisplay.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (view != null) {
                    if(tId.equals("Cardio")){
                        if(parent.getItemIdAtPosition(position) == 0){
                            getDistance();
                        } else if(parent.getItemIdAtPosition(position) == 1) {
                            getDuration();
                        } else {
                            getSpeed();
                        }
                    } else {
                        if(parent.getItemIdAtPosition(position) == 0){
                           getMaxRep1();
                        } else if(parent.getItemIdAtPosition(position) == 1) {
                           getMaxRep5();
                        } else {
                            getSum();
                        }
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        return v;
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

    public void loadExerciseData() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
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
                    exValues = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("name");
                                exValues.add(data);
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
                            if(exValues.size() > 0)
                            {
                                loadExerciseSpn();
                            } else {
                                progGraph.setVisibility(View.GONE);
                                fragGraph.setVisibility(View.GONE);
                                txtNoHistory.setVisibility(View.VISIBLE);
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

    public void loadExerciseSpn() {
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, exValues);
        spnExercise.setAdapter(adapter);
        if(spnExercise != null && spnExercise.getSelectedItem() != null) {
            String exercise = spnExercise.getSelectedItem().toString();
            if (!exercise.equals("")) {
                loadMachine(exercise);
            }
        }
    }

    public void loadMachine(final String name) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnGetMachine"));
                    params.add(new BasicNameValuePair("id", uId));
                    params.add(new BasicNameValuePair("name", name));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        tId = jsnObj.getString("type");
                        mId = jsnObj.getString("id");
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
                                checkDisplayData();
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

    public void checkDisplayData() {
        if(tId.equals("Cardio")) {
            if(haveNetwork()) {
                Runnable run = new Runnable()
                {
                    String strRespond;
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnCheckCGraph"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

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
                                    status = "True";
                                    loadDisplayData();
                                } else {
                                    status = "False";
                                    loadDisplayData();
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
        } else {
            if(haveNetwork()) {
                Runnable run = new Runnable()
                {
                    String strRespond;
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnCheckBGraph"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

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
                                    status = "True";
                                    loadDisplayData();
                                } else {
                                    status = "False";
                                    loadDisplayData();
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

    public void loadDisplayData() {
        if(status.equals("True")) {
            if (tId.equals("Cardio")) {
                String[] cardioData = {"Distance", "Duration", "Speed"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, cardioData);
                spnDisplay.setAdapter(adapter);
                txtNoHistory.setVisibility(View.GONE);
            } else {
                String[] bodybuldingData = {"Maximum (Reps >= 1)", "Maximum (Reps >= 5)", "Sum"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, bodybuldingData);
                spnDisplay.setAdapter(adapter);
                txtNoHistory.setVisibility(View.GONE);
            }
        } else {
            progGraph.setVisibility(View.GONE);
            fragGraph.setVisibility(View.GONE);
            txtNoHistory.setVisibility(View.VISIBLE);
        }
    }

    public void getMaxRep1() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateB1"));
                    params.add(new BasicNameValuePair("mId", mId));
                    params.add(new BasicNameValuePair("uId", uId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    bDate = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);
                                String data = jsnObj.getString("bDate");

                                bDate.add(data);
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
                            if(bDate != null)
                            {
                                getMaxWeightRep1();
                            } else {
                                Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getMaxWeightRep1() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;

                @Override
                public void run()
                {
                    weight = new ArrayList<>();
                    int count = bDate.size();
                    for(int i = weight.size(); i < count; i++){
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutWeightB1"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", bDate.get(i)));

                        try{
                            jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                            String data = jsnObj.getString("weight");
                            strRespond = jsnObj.getString("respond");

                            weight.add(data);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                if(weight.size() == bDate.size()) {
                                    loadMaxRep1();
                                } else {
                                    getMaxWeightRep1();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something wrong.", Toast.LENGTH_LONG).show();
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

    public void loadMaxRep1() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Maximum (Reps >= 1)(kg)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < weight.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(weight.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        for(int i = 0; i < bDate.size(); i++){
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date inputDate = fmt.parse(bDate.get(i));

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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

    public void getMaxRep5() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
            if(haveNetwork()) {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateB5"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        bDate = new ArrayList<>();

                        try{
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);
                                    String data = jsnObj.getString("bDate");

                                    bDate.add(data);
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
                                if(bDate != null)
                                {
                                    getMaxWeightRep5();
                                } else {
                                    Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getMaxWeightRep5() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    weight = new ArrayList<>();
                    int count = bDate.size();
                    for(int i = weight.size(); i < count; i++){
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutWeightB5"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", bDate.get(i)));

                        try{
                            jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                            String data = jsnObj.getString("weight");
                            strRespond = jsnObj.getString("respond");

                            weight.add(data);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                if(weight.size() == bDate.size()) {
                                    loadMaxRep5();
                                } else {
                                    getMaxWeightRep5();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something wrong.", Toast.LENGTH_LONG).show();
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

    public void loadMaxRep5() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Maximum (Reps >= 5)(kg)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < weight.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(weight.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        for(int i = 0; i < bDate.size(); i++){
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date inputDate = fmt.parse(bDate.get(i));

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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

    public void getSum() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateBS"));
                    params.add(new BasicNameValuePair("mId", mId));
                    params.add(new BasicNameValuePair("uId", uId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    bDate = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);
                                String data = jsnObj.getString("bDate");

                                bDate.add(data);
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
                            if(bDate != null)
                            {
                                getSumWeight();
                            } else {
                                Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getSumWeight(){
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    weight = new ArrayList<>();
                    for(int c = weight.size(); c < bDate.size(); c++) {
                        double sumWeight = 0;
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutSumBS"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", bDate.get(c)));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;

                        try {
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);

                                    String data1 = jsnObj.getString("reps");
                                    String data2 = jsnObj.getString("weight");
                                    double total = Integer.parseInt(data1) * Double.parseDouble(data2);
                                    sumWeight += total;
                                }
                                weight.add(String.format("%.1f", sumWeight));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weight != null)
                            {
                                if(weight.size() == bDate.size()) {
                                    loadSum();
                                } else {
                                    getSumWeight();
                                }
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

    public void loadSum() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Sum(kg)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < weight.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(weight.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        for(int i = 0; i < bDate.size(); i++){
            try {
                SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");
                Date inputDate = fmt.parse(bDate.get(i));

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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

    public void getDistance() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateCD"));
                    params.add(new BasicNameValuePair("mId", mId));
                    params.add(new BasicNameValuePair("uId", uId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    cDate = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);
                                String data = jsnObj.getString("cDate");

                                cDate.add(data);
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
                            if(cDate != null)
                            {
                                getSumDistance();
                            } else {
                                Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getSumDistance(){
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    dist = new ArrayList<>();
                    int count = cDate.size();
                    for(int i = dist.size(); i < count; i++){
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutSumCD"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", cDate.get(i)));

                        try{
                            jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                            String data = jsnObj.getString("distance");
                            strRespond = jsnObj.getString("respond");

                            dist.add(data);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                if(dist.size() == cDate.size()) {
                                    loadDistance();
                                } else {
                                    getSumDistance();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something wrong.", Toast.LENGTH_LONG).show();
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

    public void loadDistance() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Distance(km)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < dist.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(dist.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

    public void getDuration() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateCDD"));
                    params.add(new BasicNameValuePair("mId", mId));
                    params.add(new BasicNameValuePair("uId", uId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    cDate = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);
                                String data = jsnObj.getString("cDate");

                                cDate.add(data);
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
                            if(cDate != null)
                            {
                                getSumDuration();
                            } else {
                                Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getSumDuration(){
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    durr = new ArrayList<>();
                    for(int c = durr.size(); c < cDate.size(); c++) {
                        //String sumDuration = "00:00";
                        int duration = 0;
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutSumCDD"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", cDate.get(c)));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;

                        try {
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);
                                    String data = jsnObj.getString("duration");

                                    String[] units = data.split(":");
                                    int hours = Integer.parseInt(units[0]);
                                    int minutes = Integer.parseInt(units[1]);
                                    duration += 60 * hours + minutes;
                                }
                                durr.add(String.valueOf(duration));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weight != null)
                            {
                                if(durr.size() == cDate.size()) {
                                    loadDuration();
                                } else {
                                    getSumDuration();
                                }
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

    public void loadDuration() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Duration(min)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < durr.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(durr.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

    public void getSpeed() {
        progGraph.setVisibility(View.VISIBLE);
        fragGraph.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnWorkoutDateCS"));
                    params.add(new BasicNameValuePair("mId", mId));
                    params.add(new BasicNameValuePair("uId", uId));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    cDate = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);
                                String data = jsnObj.getString("cDate");

                                cDate.add(data);
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
                            if(cDate != null)
                            {
                                getSumDistanceS();
                            } else {
                                Toast.makeText(getActivity(),"No data",Toast.LENGTH_LONG).show();
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

    public void getSumDistanceS(){
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    dist = new ArrayList<>();
                    int count = cDate.size();
                    for(int i = dist.size(); i < count; i++){
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutSumCD"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", cDate.get(i)));

                        try{
                            jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                            String data = jsnObj.getString("distance");
                            strRespond = jsnObj.getString("respond");

                            dist.add(data);

                        } catch (Exception e){
                            e.printStackTrace();
                        }
                    }


                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                if(dist.size() == cDate.size()) {
                                    getSumDurationS();
                                } else {
                                    getSumDistanceS();
                                }
                            } else {
                                Toast.makeText(getActivity(), "Something wrong.", Toast.LENGTH_LONG).show();
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

    public void getSumDurationS(){
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    durr = new ArrayList<>();
                    for(int c = durr.size(); c < cDate.size(); c++) {
                        int duration = 0;
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnWorkoutSumCDD"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", cDate.get(c)));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;

                        try {
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);
                                    String data = jsnObj.getString("duration");

                                    String[] units = data.split(":");
                                    int hours = Integer.parseInt(units[0]);
                                    int minutes = Integer.parseInt(units[1]);
                                    duration += 60 * hours + minutes;
                                }
                                durr.add(String.valueOf(duration));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(weight != null)
                            {
                                if(durr.size() == cDate.size()) {
                                    getSumSpeed();
                                } else {
                                    getSumDurationS();
                                }
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

    public void getSumSpeed() {
        speed = new ArrayList<>();
        for(int i = 0; i < cDate.size(); i++) {
            double di = Double.parseDouble(dist.get(i));
            double du = Double.parseDouble(durr.get(i));
            double total = di / (du/60);
            speed.add(String.format("%.1f", total));
            if(speed.size() == cDate.size()) {
                loadSpeed();
                break;
            }
        }
    }

    public void loadSpeed() {
        String exercise = spnExercise.getSelectedItem().toString();
        mChart.getDescription().setText(exercise+"/Speed(km/h)");
        mChart.getAxisRight().setEnabled(false);

        ArrayList<Entry> yData = new ArrayList<>();
        for(int i = 0; i < speed.size(); i++) {
            yData.add(new Entry(i, Float.parseFloat(speed.get(i))));
        }

        LineDataSet lineDataSet = new LineDataSet(yData, exercise);
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
        progGraph.setVisibility(View.GONE);
        fragGraph.setVisibility(View.VISIBLE);
    }

}
