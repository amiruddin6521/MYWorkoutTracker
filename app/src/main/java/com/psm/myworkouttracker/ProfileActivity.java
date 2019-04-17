package com.psm.myworkouttracker;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String id;
    private ImageView imgPhoto;
    private TextView txtEmail, txtName, txtDob, txtWeight, txtHeight, txtGender, txtPdate;
    private ImageButton edtName, edtDob, edtWeight, edtHeight, edtGender, edtPassword;
    private WebServiceCall wsc = new WebServiceCall();
    private JSONObject jsnObj = new JSONObject();
    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // Get the id from login activity.
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        FloatingActionButton fab = findViewById(R.id.actionCamera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(ProfileActivity.this,"Edit Button. ID = "+id, Toast.LENGTH_SHORT).show();
            }
        });

        txtEmail =  findViewById(R.id.txtEmail);
        txtName =  findViewById(R.id.txtName);
        txtDob = findViewById(R.id.txtDob);
        txtWeight = findViewById(R.id.txtWeight);
        txtHeight = findViewById(R.id.txtHeight);
        txtGender = findViewById(R.id.txtGender);
        txtPdate = findViewById(R.id.txtPdate);
        imgPhoto = findViewById(R.id.imgPhoto);
        edtName =  findViewById(R.id.edtName);
        edtDob = findViewById(R.id.edtDob);
        edtWeight = findViewById(R.id.edtWeight);
        edtHeight = findViewById(R.id.edtHeight);
        edtGender = findViewById(R.id.edtGender);
        edtPassword = findViewById(R.id.edtPassword);

        loadProfile();

        edtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_templete);
                final EditText edtUpdate = dialog.findViewById(R.id.edtUpdate);
                Button btnSave = dialog.findViewById(R.id.btnSave);
                dialog.show();


                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(ProfileActivity.this, "Saved",Toast.LENGTH_SHORT).show();
                        String name = edtUpdate.getText().toString();
                        String attr = "name";
                        updateData(id,attr,name);
                    }
                });

            }
        });

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        edtWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        edtHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void loadProfile() {
        Runnable run = new Runnable()
        {
            String strRespond, name, email, dob, weight, height, gender, passworddate, img64;
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnLoadProfile"));
                params.add(new BasicNameValuePair("id", id));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    email = jsnObj.getString("email");
                    name = jsnObj.getString("name");
                    dob = jsnObj.getString("dob");
                    weight = jsnObj.getString("weight");
                    height = jsnObj.getString("height");
                    gender = jsnObj.getString("gender");
                    passworddate = jsnObj.getString("pdate");
                    img64 = jsnObj.getString("encoded");
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    //if fail to get from server, get from local mobile time
                    String strMsg = "No internet connection, please turn on your Mobile Data/WiFi.";
                    Toast.makeText(ProfileActivity.this, strMsg, Toast.LENGTH_LONG).show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            txtEmail.setText(email);
                            txtName.setText(name);
                            txtDob.setText(dob);
                            txtWeight.setText(weight);
                            txtHeight.setText(height);
                            txtGender.setText(gender);
                            txtPdate.setText(passworddate);
                            byte[] data = Base64.decode(img64, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imgPhoto.setImageBitmap(decodedByte);
                        } else {
                            Toast.makeText(ProfileActivity.this, "Something wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    public void updateData(final String id, final String attr, final String data) {
        Runnable run = new Runnable()
        {
            String strRespond = "";
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnUpdateProfile"));
                params.add(new BasicNameValuePair("varId", id));
                params.add(new BasicNameValuePair("varAttr", attr));
                params.add(new BasicNameValuePair("varData", data));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    strRespond = jsnObj.getString("respond");

                } catch (JSONException e){
                    //if fail to get from server, get from local mobile time
                    String strMsg = "No internet connection, please turn on your Mobile Data/WiFi.";
                    Toast.makeText(ProfileActivity.this, strMsg, Toast.LENGTH_LONG).show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            Toast.makeText(ProfileActivity.this, "You have successfully upload image!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, strRespond, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }
}
