package com.psm.myworkouttracker.fragment;

import android.content.DialogInterface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.adapter.WorkoutTabAdapterB;
import com.psm.myworkouttracker.adapter.WorkoutTabAdapterC;
import com.psm.myworkouttracker.services.WebServiceCallArr;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class HistoryTabFragment extends Fragment {

    private String uId, mId, tId;
    private Spinner spnExercise, spnDate;
    private ListView listHistory;
    private List<String> idB, idC, bMachine, cMachine, bDate, bTime, cDate, cTime, sets, reps, weight, dist, durr, exValues, dtValues;
    private WorkoutTabAdapterB bAdapter;
    private WorkoutTabAdapterC cAdapter;
    private JSONObject jsnObj = new JSONObject();
    private JSONArray jsnArr = new JSONArray();
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private View progHistory, fragHistory, txtNoHistory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_historytab, container, false);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        spnExercise = v.findViewById(R.id.spnExercise);
        spnDate = v.findViewById(R.id.spnDate);
        listHistory = v.findViewById(R.id.listHistory);
        progHistory = v.findViewById(R.id.progHistory);
        fragHistory = v.findViewById(R.id.fragHistory);
        txtNoHistory = v.findViewById(R.id.txtNoHistory);

        loadExerciseData();

        spnExercise.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progHistory.setVisibility(View.VISIBLE);
                fragHistory.setVisibility(View.GONE);
                String selected = parent.getItemAtPosition(position).toString();
                spnDate.setAdapter(null);
                loadMachine(selected);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spnDate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                progHistory.setVisibility(View.VISIBLE);
                fragHistory.setVisibility(View.GONE);
                String selected = parent.getItemAtPosition(position).toString();
                String exercise = spnExercise.getSelectedItem().toString();
                if(tId.equals("Cardio")){
                    loadWorkoutDataC(exercise, selected);
                } else {
                    loadWorkoutDataB(exercise, selected);
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

    /*if(haveNetwork()) {
    } else if(!haveNetwork()) {
        Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
    }*/

    public void loadExerciseData() {
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
                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(exValues != null)
                            {
                                loadExerciseSpn();
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
        String exercise = spnExercise.getSelectedItem().toString();
        if(!exercise.equals("")) {
            loadMachine(exercise);
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

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadDateData();
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
            loadExerciseData();
        }
    }

    public void loadDateData() {
        if(haveNetwork()) {
            if(tId.equals("Cardio")){
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutDateC"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        dtValues = new ArrayList<>();
                        dtValues.add("All");
                        try{
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);

                                    String data = jsnObj.getString("cDate");
                                    dtValues.add(data);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        if(getActivity() == null)
                            return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dtValues != null)
                                {
                                    loadDateSpn();
                                }
                            }
                        });
                    }
                };
                Thread thr = new Thread(run);
                thr.start();
            } else {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutDateB"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        dtValues = new ArrayList<>();
                        dtValues.add("All");
                        try{
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);

                                    String data = jsnObj.getString("bDate");
                                    dtValues.add(data);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        if(getActivity() == null)
                            return;

                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if(dtValues != null)
                                {
                                    loadDateSpn();
                                }
                            }
                        });
                    }
                };
                Thread thr = new Thread(run);
                thr.start();
            }
        } else if(!haveNetwork()) {
            loadExerciseData();
        }
    }

    public void loadDateSpn() {
        if(dtValues.size() > 1) {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, dtValues);
            spnDate.setAdapter(adapter);
            spnDate.setSelection(1);
            String exercise = spnExercise.getSelectedItem().toString();
            String date = spnDate.getSelectedItem().toString();
            if(tId.equals("Cardio")){
                loadWorkoutDataC(exercise, date);
            } else {
                loadWorkoutDataB(exercise, date);
            }
            txtNoHistory.setVisibility(View.GONE);
        } else {
            progHistory.setVisibility(View.GONE);
            txtNoHistory.setVisibility(View.VISIBLE);
        }
    }

    public void loadWorkoutDataB(final String machine, final String date) {
        if(haveNetwork()) {
            if(date.equals("All")) {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListB"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        idB = new ArrayList<>();
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
                                    String data0 = jsnObj.getString("_id");
                                    String data1 = jsnObj.getString("bDate");
                                    String data2 = jsnObj.getString("bTime");
                                    String data3 = jsnObj.getString("sets");
                                    String data4 = jsnObj.getString("reps");
                                    String data5 = jsnObj.getString("weight");

                                    bMachine.add(machine);
                                    idB.add(data0);
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

                        if(getActivity() == null)
                            return;

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
            } else {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListDateB"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", date));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        idB = new ArrayList<>();
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
                                    String data0 = jsnObj.getString("_id");
                                    String data1 = jsnObj.getString("bDate");
                                    String data2 = jsnObj.getString("bTime");
                                    String data3 = jsnObj.getString("sets");
                                    String data4 = jsnObj.getString("reps");
                                    String data5 = jsnObj.getString("weight");

                                    bMachine.add(machine);
                                    idB.add(data0);
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

                        if(getActivity() == null)
                            return;

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
        } else if(!haveNetwork()) {
            loadExerciseData();
        }
    }

    public void loadDataB() {
        bAdapter = new WorkoutTabAdapterB(getActivity(), idB, bMachine, bDate, bTime, sets, reps, weight);
        listHistory.setAdapter(bAdapter);
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemID = bAdapter.getItemID(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Delete Exercise Record");
                alertDialog.setMessage("Are you sure you want to delete?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExerciseB(itemID);
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
        progHistory.setVisibility(View.GONE);
        fragHistory.setVisibility(View.VISIBLE);
    }

    public void loadWorkoutDataC(final String machine, final String date) {
        if(haveNetwork()) {
            if(date.equals("All")) {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListC"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        idC = new ArrayList<>();
                        cMachine = new ArrayList<>();
                        cDate = new ArrayList<>();
                        cTime = new ArrayList<>();
                        dist = new ArrayList<>();
                        durr = new ArrayList<>();

                        try{
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);
                                    String data0 = jsnObj.getString("_id");
                                    String data1 = jsnObj.getString("cDate");
                                    String data2 = jsnObj.getString("cTime");
                                    String data3 = jsnObj.getString("distance");
                                    String data4 = jsnObj.getString("duration");

                                    cMachine.add(machine);
                                    idC.add(data0);
                                    cDate.add(data1);
                                    cTime.add(data2);
                                    dist.add(data3);
                                    durr.add(data4);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        if(getActivity() == null)
                            return;

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
            } else {
                Runnable run = new Runnable()
                {
                    @Override
                    public void run()
                    {
                        List<NameValuePair> params = new ArrayList<NameValuePair>();
                        params.add(new BasicNameValuePair("selectFn", "fnMWorkoutListDateC"));
                        params.add(new BasicNameValuePair("mId", mId));
                        params.add(new BasicNameValuePair("uId", uId));
                        params.add(new BasicNameValuePair("date", date));

                        jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                        jsnObj = null;
                        idC = new ArrayList<>();
                        cMachine = new ArrayList<>();
                        cDate = new ArrayList<>();
                        cTime = new ArrayList<>();
                        dist = new ArrayList<>();
                        durr = new ArrayList<>();

                        try{
                            if (jsnArr != null) {
                                for (int i = 0; i < jsnArr.length(); i++) {
                                    jsnObj = jsnArr.getJSONObject(i);
                                    String data0 = jsnObj.getString("_id");
                                    String data1 = jsnObj.getString("cDate");
                                    String data2 = jsnObj.getString("cTime");
                                    String data3 = jsnObj.getString("distance");
                                    String data4 = jsnObj.getString("duration");


                                    cMachine.add(machine);
                                    idC.add(data0);
                                    cDate.add(data1);
                                    cTime.add(data2);
                                    dist.add(data3);
                                    durr.add(data4);
                                }
                            }
                        } catch (JSONException e){
                            e.printStackTrace();
                        }

                        if(getActivity() == null)
                            return;

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
        } else if(!haveNetwork()) {
            loadExerciseData();
        }
    }

    public void loadDataC() {
        cAdapter = new WorkoutTabAdapterC(getActivity(), idC, cMachine, cDate, cTime, dist, durr);
        listHistory.setAdapter(cAdapter);
        listHistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final String itemID = cAdapter.getItemID(position);

                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Delete Exercise Record");
                alertDialog.setMessage("Are you sure you want to delete?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExerciseC(itemID);
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
        progHistory.setVisibility(View.GONE);
        fragHistory.setVisibility(View.VISIBLE);
    }

    //Delete exercise data for body building
    public void deleteExerciseB(final String id) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnDeleteExerciseB"));
                    params.add(new BasicNameValuePair("id", id));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadExerciseData();
                                Toast.makeText(getActivity(), "Exercise data successfully deleted!", Toast.LENGTH_LONG).show();
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

    //Delete exercise data for cardio
    public void deleteExerciseC(final String id) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnDeleteExerciseC"));
                    params.add(new BasicNameValuePair("id", id));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadExerciseData();
                                Toast.makeText(getActivity(), "Exercise data successfully deleted!", Toast.LENGTH_LONG).show();
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

    @Override
    public void onResume() {
        super.onResume();
        loadExerciseData();
    }
}
