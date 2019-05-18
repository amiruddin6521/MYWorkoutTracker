package com.psm.myworkouttracker.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.LoginActivity;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.activity.ProfileActivity;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class ExercisesTabFragment extends Fragment {

    private String uId, exerciseName, mId;
    private EditText edtNameExe, edtDescExe;
    private TextView edtTypeExe;
    private CircularImageView roundProfile;
    private FloatingActionButton photoButton;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private JSONObject jsnObj = new JSONObject();
    private View progExercises, fragExercises;
    private Button btnUpdateExe, btnDeleteExe, btnBackExe;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_exercises_tab, container, false);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        Bundle bundle = getArguments();
        exerciseName = bundle.getString("exercise");

        loadExercise(uId, exerciseName);

        roundProfile =  v.findViewById(R.id.imgPhoto1);
        photoButton = v.findViewById(R.id.actionCamera1);
        edtNameExe =  v.findViewById(R.id.edtNameExe);
        edtDescExe = v.findViewById(R.id.edtDescExe);
        edtTypeExe =  v.findViewById(R.id.edtTypeExe);
        progExercises = v.findViewById(R.id.profile_progress);
        fragExercises = v.findViewById(R.id.exetab_form);
        btnUpdateExe =  v.findViewById(R.id.btnUpdateExe);
        btnDeleteExe = v.findViewById(R.id.btnDeleteExe);
        btnBackExe = v.findViewById(R.id.btnBackExe);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //selectImage();
            }
        });

        btnUpdateExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Update Exercise");
                alertDialog.setMessage("Are you sure you want to update?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        updateMachineData();
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

        btnDeleteExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Delete Exercise");
                alertDialog.setMessage("Are you sure you want to delete?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteExercise(mId);
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

        btnBackExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().popBackStack();
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

    //Load data for machine
    public void loadExercise(final String id, final String name) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond, type, description, img64;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnLoadExerciseData"));
                    params.add(new BasicNameValuePair("id", id));
                    params.add(new BasicNameValuePair("name", name));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        mId = jsnObj.getString("id");
                        description = jsnObj.getString("description");
                        type = jsnObj.getString("type");
                        img64 = jsnObj.getString("encoded");
                        strRespond = jsnObj.getString("respond");

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    if(getActivity() == null)
                        return;

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            progExercises.setVisibility(View.GONE);
                            fragExercises.setVisibility(View.VISIBLE);
                            if(strRespond.equals("True") && img64.equals("")) {
                                edtNameExe.setText(exerciseName);
                                edtDescExe.setText(description);
                                edtTypeExe.setText(type);
                                roundProfile.setImageResource(R.drawable.person);
                            } else if(strRespond.equals("True")){
                                edtNameExe.setText(exerciseName);
                                edtDescExe.setText(description);
                                edtTypeExe.setText(type);
                                byte[] data = Base64.decode(img64, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
                                roundProfile.setImageBitmap(decodedByte);
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

    //Delete data for machine
    public void deleteExercise(final String id) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnDeleteExercise"));
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
                                Toast.makeText(getActivity(), "Exercise successfully deleted!", Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().popBackStack();
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

    //Update machine data
    public void updateMachineData() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                String name, description;
                @Override
                public void run()
                {
                    name = edtNameExe.getText().toString();
                    description = edtDescExe.getText().toString();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnUpdateMachine"));
                    params.add(new BasicNameValuePair("varName", name));
                    params.add(new BasicNameValuePair("varDesc", description));
                    params.add(new BasicNameValuePair("varId", mId));

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
                                Toast.makeText(getActivity(),"Exercise successfully updated!",Toast.LENGTH_LONG).show();
                                getActivity().getSupportFragmentManager().popBackStack();
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
}
