package com.psm.myworkouttracker.fragment;

import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AddExercisesFragment extends Fragment {

    private RadioButton radB;
    private RadioGroup radGrpExe;
    private Button btnSaveExe, btnBackExe, btnResetExe;
    private String uId;
    private EditText edtNameExe, edtDescExe;
    private CircularImageView roundProfile;
    private FloatingActionButton photoButton;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private JSONObject jsnObj = new JSONObject();
    private View v;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_exercises_add, container, false);

        btnSaveExe = v.findViewById(R.id.btnSaveExe);
        btnBackExe = v.findViewById(R.id.btnBackExe);
        btnResetExe = v.findViewById(R.id.btnResetExe);
        radGrpExe = v.findViewById(R.id.radGrpExe);
        roundProfile =  v.findViewById(R.id.imgPhoto2);
        photoButton = v.findViewById(R.id.actionCamera2);
        edtNameExe =  v.findViewById(R.id.edtNameExe2);
        edtDescExe = v.findViewById(R.id.edtDescExe2);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        btnSaveExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
                alertDialog.setTitle("Save Exercise");
                alertDialog.setMessage("Are you sure you want to save?");
                alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveMachineData();
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

        btnResetExe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                resetData();
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

    //Get gender data
    private String getRadioGender() {
        // get selected radio button from radioGroup
        int selectedId = radGrpExe.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radB = getActivity().findViewById(selectedId);

        String result = radB.getText().toString();


        return result;
    }

    public void resetData() {
        edtNameExe.setText("");
        edtDescExe.setText("");
        radB = v.findViewById(R.id.radB);
        radB.setChecked(true);
    }

    //Save machine data
    public void saveMachineData() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                String name, description, type;
                @Override
                public void run()
                {
                    name = edtNameExe.getText().toString();
                    description = edtDescExe.getText().toString();
                    type = getRadioGender();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnAddMachine"));
                    params.add(new BasicNameValuePair("varName", name));
                    params.add(new BasicNameValuePair("varDesc", description));
                    params.add(new BasicNameValuePair("varType", type));
                    params.add(new BasicNameValuePair("varUser", uId));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (JSONException e){
                        e.printStackTrace();
                    }

                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                Toast.makeText(getActivity(),"Exercise successfully added!",Toast.LENGTH_LONG).show();
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
