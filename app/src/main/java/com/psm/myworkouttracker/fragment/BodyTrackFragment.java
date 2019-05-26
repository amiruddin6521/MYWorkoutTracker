package com.psm.myworkouttracker.fragment;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.adapter.BodyTrackAdapter;
import com.psm.myworkouttracker.services.WebServiceCallArr;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class BodyTrackFragment extends Fragment {

    private ListView listBody;
    private View fragBodyTrack, progBodyTrack;
    private List<Integer> imgBody;
    private List<String> nameBody, measureBody;
    private JSONObject jsnObj = new JSONObject();
    private WebServiceCallArr wsc2 = new WebServiceCallArr();
    private JSONArray jsnArr = new JSONArray();
    private String id;
    private BodyTrackAdapter bodyTrackAdapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_bodytrack, container, false);

        listBody =  v.findViewById(R.id.listBody);
        fragBodyTrack = v.findViewById(R.id.fragBodyTrack);
        progBodyTrack =  v.findViewById(R.id.progBodyTrack);

        MainActivity activity = (MainActivity) getActivity();
        id = activity.getMyData();

        addListData();

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

    public void addListData() {
        imgBody = new ArrayList<>();
        imgBody.add(R.drawable.ic_arm_measure);
        imgBody.add(R.drawable.ic_arm_measure);
        imgBody.add(R.drawable.ic_chest_measure);
        imgBody.add(R.drawable.ic_waist_measure);
        imgBody.add(R.drawable.ic_buttock_measure);
        imgBody.add(R.drawable.ic_tight_measure);
        imgBody.add(R.drawable.ic_tight_measure);
        imgBody.add(R.drawable.ic_calve_measure);
        imgBody.add(R.drawable.ic_calve_measure);

        nameBody = new ArrayList<>();
        nameBody.add("Left biceps");
        nameBody.add("Right biceps");
        nameBody.add("Chest");
        nameBody.add("Waist");
        nameBody.add("Hips");
        nameBody.add("Left thigh");
        nameBody.add("Right thigh");
        nameBody.add("Left calves");
        nameBody.add("Right calves");

        measureBody = new ArrayList<>();
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");
        measureBody.add("");

        loadMeasureData();
    }

    public void loadMeasureData() {
        progBodyTrack.setVisibility(View.VISIBLE);
        fragBodyTrack.setVisibility(View.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnBodyMeasureList"));
                    params.add(new BasicNameValuePair("id", id));

                    jsnArr = wsc2.makeHttpRequest(wsc2.fnGetURL(), "POST", params);
                    jsnObj = null;
                    //measureBody = new ArrayList<>();

                    try{
                        if (jsnArr != null) {
                            for (int i = 0; i < jsnArr.length(); i++) {
                                jsnObj = jsnArr.getJSONObject(i);

                                String data = jsnObj.getString("bodypart");
                                String data1 = jsnObj.getString("bodymeasure");

                                if(!data.equals("")) {
                                    measureBody.set(Integer.parseInt(data),data1);
                                }
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
                            if(measureBody != null)
                            {
                                loadMeasureList();
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

    public void loadMeasureList() {
        bodyTrackAdapter = new BodyTrackAdapter(getActivity(), imgBody, nameBody, measureBody);
        listBody.setAdapter(bodyTrackAdapter);
        listBody.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String idBody = Integer.toString(position);

                Bundle bundle = new Bundle();
                bundle.putString("idBody", idBody);

                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                AddBodyTrackFragment fragment = new AddBodyTrackFragment();
                fragment.setArguments(bundle);
                fragmentManager.beginTransaction()
                        .addToBackStack(null)
                        .replace(R.id.fragment_container,fragment)
                        .commit();
            }
        });
        progBodyTrack.setVisibility(View.GONE);
        fragBodyTrack.setVisibility(View.VISIBLE);
    }
}
