package com.psm.myworkouttracker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.services.WebServiceCallObj;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class ProfileActivity extends AppCompatActivity {

    private String id, currentPhotoPath;
    private ImageView imgPhoto;
    private TextView txtEmail, txtName, txtDob, txtWeight, txtHeight, txtGender, txtPdate;
    private ImageButton edtName, edtDob, edtWeight, edtHeight, edtGender, edtPassword;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private JSONObject jsnObj = new JSONObject();
    private Dialog dialog;
    private EditText edtUpdate, edtCurrPass, edtNewPass;
    private Button btnSave;
    private RadioGroup radGender;
    private RadioButton radMale, radFemale;
    private View mProgressView, mProfileFormView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        FloatingActionButton fab = findViewById(R.id.actionCamera);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetwork()) {
                    selectImage();
                } else if(!haveNetwork()) {
                    Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                }
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
        mProgressView = findViewById(R.id.profile_progress);
        mProfileFormView = findViewById(R.id.profile_form);

        loadProfile();

        edtName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_name);
                edtUpdate = dialog.findViewById(R.id.edtUpdate);
                btnSave = dialog.findViewById(R.id.btnSave);
                edtUpdate.setText(txtName.getText().toString());
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(haveNetwork()) {
                            boolean cancel = false;
                            View focusView = null;
                            String data = edtUpdate.getText().toString();
                            String attr = "name";
                            if (TextUtils.isEmpty(data)) {
                                edtUpdate.setError(getString(R.string.error_field_required));
                                focusView = edtUpdate;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {

                                updateData(id,attr,data);
                                dialog.cancel();
                            }
                        } else if(!haveNetwork()) {
                            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edtDob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderPicker();
            }
        });

        edtWeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_weight_height);
                edtUpdate = dialog.findViewById(R.id.edtUpdate);
                btnSave = dialog.findViewById(R.id.btnSave);
                edtUpdate.setText(txtWeight.getText().toString());
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(haveNetwork()) {
                            boolean cancel = false;
                            View focusView = null;
                            String data = edtUpdate.getText().toString();
                            if (TextUtils.isEmpty(data)) {
                                edtUpdate.setError(getString(R.string.error_field_required));
                                focusView = edtUpdate;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {

                                updateWeight(id,data);
                                dialog.cancel();
                            }
                        } else if(!haveNetwork()) {
                            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edtHeight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_weight_height);
                edtUpdate = dialog.findViewById(R.id.edtUpdate);
                btnSave = dialog.findViewById(R.id.btnSave);
                edtUpdate.setText(txtHeight.getText().toString());
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(haveNetwork()) {
                            boolean cancel = false;
                            View focusView = null;
                            String data = edtUpdate.getText().toString();
                            String attr = "height";
                            if (TextUtils.isEmpty(data)) {
                                edtUpdate.setError(getString(R.string.error_field_required));
                                focusView = edtUpdate;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {

                                updateData(id,attr,data);
                                dialog.cancel();
                            }
                        } else if(!haveNetwork()) {
                            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edtGender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_gender);
                radGender = dialog.findViewById(R.id.radGender);
                radMale = dialog.findViewById(R.id.radMale);
                radFemale = dialog.findViewById(R.id.radFemale);
                btnSave = dialog.findViewById(R.id.btnSave);
                String gend = txtGender.getText().toString();
                if(gend.equals("Male")) {
                    radMale.setChecked(true);
                } else {
                    radFemale.setChecked(true);
                }
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(haveNetwork()) {
                            int selectedId = radGender.getCheckedRadioButtonId();
                            radMale = dialog.findViewById(selectedId);
                            String data = radMale.getText().toString();
                            String attr = "gender";
                            updateData(id,attr,data);
                            dialog.cancel();
                        } else if(!haveNetwork()) {
                            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });

        edtPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog = new Dialog(ProfileActivity.this);
                dialog.setCancelable(true);
                dialog.setContentView(R.layout.dialog_password);
                edtCurrPass = dialog.findViewById(R.id.edtCurrPass);
                edtNewPass = dialog.findViewById(R.id.edtNewPass);
                btnSave = dialog.findViewById(R.id.btnSave);
                dialog.show();

                btnSave.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(haveNetwork()) {
                            boolean cancel = false;
                            View focusView = null;
                            String data1 = edtCurrPass.getText().toString();
                            String data2 = edtNewPass.getText().toString();
                            if (TextUtils.isEmpty(data2)) {
                                edtNewPass.setError(getString(R.string.error_field_required));
                                focusView = edtNewPass;
                                cancel = true;
                            } else if (!isPasswordValid(data2)) {
                                edtNewPass.setError(getString(R.string.error_invalid_password));
                                focusView = edtNewPass;
                                cancel = true;
                            } else if (data2.equals(data1)) {
                                edtNewPass.setError(getString(R.string.error_same_password));
                                focusView = edtNewPass;
                                cancel = true;
                            }
                            if (TextUtils.isEmpty(data1)) {
                                edtCurrPass.setError(getString(R.string.error_field_required));
                                focusView = edtCurrPass;
                                cancel = true;
                            } else if (!isPasswordValid(data1)) {
                                edtCurrPass.setError(getString(R.string.error_invalid_password));
                                focusView = edtCurrPass;
                                cancel = true;
                            }
                            if (cancel) {
                                focusView.requestFocus();
                            } else {
                                getCurrPassword(data1,data2);
                                dialog.cancel();
                            }
                        } else if(!haveNetwork()) {
                            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
    }

    private boolean haveNetwork() {
        boolean haveWiFi = false;
        boolean haveMobileData = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
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

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress(final boolean show) {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mProfileFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
                }
            });

            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    show ? 1 : 0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(show ? View.VISIBLE : View.GONE);
            mProfileFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Validation for password
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }

    //Dialog box with the three options
    private void selectImage() {
        final CharSequence[] items = { "Take new picture", "Choose from library",
                "Delete current picture", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setTitle("Update Picture!");
        builder.setIcon(R.drawable.ic_menu_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take new picture")) {
                    //cameraIntent();
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from library")) {
                    galleryIntent();
                } else if (items[item].equals("Delete current picture")) {
                    deleteImage();
                    imgPhoto.setImageResource(R.drawable.person);
                    dialog.dismiss();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //Calling Implicit Intent to open the camera application on user's phone
    /*private void cameraIntent()
    {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, 1);
    }*/

    //Calling an implicit intent to open the gallery
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"),2);
    }

    //Create image file name and file path
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }

    //Take picture and transfer picture data into intent
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "com.psm.myworkouttracker.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    //Handle the result we have received by calling startActivityForResult()
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Bitmap bitmap = null;
        ByteArrayOutputStream bytes = null;
        File fileName = null;
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                Uri resultUri = result.getUri();
                //imgPhoto.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), resultUri);
                    bytes = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
                    fileName = new File(Environment.getExternalStorageDirectory(),
                            System.currentTimeMillis() + ".jpg");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                byte[] array = bytes.toByteArray();
                String encoded_string = Base64.encodeToString(array, 0);
                String image_name = fileName.getName();
                updateImage(encoded_string, image_name);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == 2) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                CropImage.activity(selectedImageUri).start(this);
            }
        } else if (requestCode == 1) {
            if (currentPhotoPath != null) {
                File file = new File(currentPhotoPath);
                Uri selectedImageUri = Uri.fromFile(file);
                CropImage.activity(selectedImageUri).start(this);
            }
        }


        /*try {
            switch (requestCode) {
                case 1: {
                    if (resultCode == RESULT_OK) {
                        File file = new File(currentPhotoPath);
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), Uri.fromFile(file));
                        if (bitmap != null) {
                            bytes = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                            fileName = new File(Environment.getExternalStorageDirectory(),System.currentTimeMillis() + ".jpg");
                            byte[] array = bytes.toByteArray();
                            String encoded_string = Base64.encodeToString(array, 0);
                            String image_name = fileName.getName();
                            updateImage(encoded_string, image_name);
                        }
                    }
                    break;
                }
                case 2: {
                    onSelectFromGalleryResult(data);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }*/

        /*if (resultCode == Activity.RESULT_OK) {
            if (requestCode == 2)
                onSelectFromGalleryResult(data);
            else if (requestCode == 1)
                onCaptureImageResult(data);
        }*/
    }

    //Handle for select from gallery
    /*private void onSelectFromGalleryResult(Intent data) {
        Bitmap bitmap = null;
        ByteArrayOutputStream bytes = null;
        File fileName = null;
        if (data != null) {
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), data.getData());
                bytes = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                fileName = new File(Environment.getExternalStorageDirectory(),
                        System.currentTimeMillis() + ".jpg");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        byte[] array = bytes.toByteArray();
        String encoded_string = Base64.encodeToString(array, 0);
        String image_name = fileName.getName();
        updateImage(encoded_string, image_name);
    }*/

    //Handle for image capture from camera
    /*private void onCaptureImageResult(Intent data) {
        Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        thumbnail.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        File destination = new File(Environment.getExternalStorageDirectory(),
                System.currentTimeMillis() + ".jpg");
        FileOutputStream fo;
        try {
            destination.createNewFile();
            fo = new FileOutputStream(destination);
            fo.write(bytes.toByteArray());
            fo.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        byte[] array = bytes.toByteArray();
        String encoded_string = Base64.encodeToString(array, 0);
        String image_name = destination.getName();
        updateImage(encoded_string, image_name);
        imgPhoto.setImageBitmap(thumbnail);
    }*/

    //Dialog pick dob
    private void calenderPicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        int mYear = c.get(Calendar.YEAR);
        int mMonth = c.get(Calendar.MONTH);
        int mDay = c.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        String dob = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
                        saveDob(dob);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //Save dob date
    private void saveDob(String data) {
        String attr = "bdate";
        updateData(id,attr,data);
    }

    //Update image
    public void updateImage(final String encoded_string, final String image_name) {
        showProgress(true);
        Runnable run = new Runnable()
        {
            String strRespond = "";
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnUpdateImage"));
                params.add(new BasicNameValuePair("id", id));
                params.add(new BasicNameValuePair("encoded_string", encoded_string));
                params.add(new BasicNameValuePair("image_name", image_name));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    strRespond = jsnObj.getString("respond");

                } catch (Exception e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            loadProfile();
                            Toast.makeText(ProfileActivity.this, "Picture successfully changed!", Toast.LENGTH_LONG).show();
                        } else {
                            showProgress(false);
                            Toast.makeText(ProfileActivity.this, "Picture failed to change.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    //Delete image
    public void deleteImage() {
        showProgress(true);
        Runnable run = new Runnable()
        {
            String strRespond = "";
            @Override
            public void run()
            {
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("selectFn", "fnDeleteImage"));
                params.add(new BasicNameValuePair("id", id));

                try{
                    jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                    strRespond = jsnObj.getString("respond");

                } catch (Exception e){
                    e.printStackTrace();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            loadProfile();
                            Toast.makeText(ProfileActivity.this, "Picture successfully removed!", Toast.LENGTH_LONG).show();
                        } else {
                            showProgress(false);
                            Toast.makeText(ProfileActivity.this, "There is no picture to remove.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }

    //Load all data from user profile
    public void loadProfile() {
        showProgress(true);
        if(haveNetwork()) {
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

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True") && img64.equals("")) {
                                txtEmail.setText(email);
                                txtName.setText(name);
                                txtDob.setText(dob);
                                txtWeight.setText(weight);
                                txtHeight.setText(height);
                                txtGender.setText(gender);
                                txtPdate.setText(passworddate);
                                imgPhoto.setImageResource(R.drawable.person);
                                showProgress(false);
                            } else if(strRespond.equals("True")){
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
                                showProgress(false);
                            } else {
                                Toast.makeText(ProfileActivity.this, "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                                showProgress(false);
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
            showProgress(false);
        }
    }

    //Update specific data
    public void updateData(final String idS, final String attrS, final String data) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnUpdateProfile"));
                    params.add(new BasicNameValuePair("varId", idS));
                    params.add(new BasicNameValuePair("varAttr", attrS));
                    params.add(new BasicNameValuePair("varData", data));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadProfile();
                                Toast.makeText(ProfileActivity.this, "Data successfully changed!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Data failed to save.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    //Update weight data
    public void updateWeight(final String idS, final String data) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnUpdateWeight"));
                    params.add(new BasicNameValuePair("varId", idS));
                    params.add(new BasicNameValuePair("varData", data));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadProfile();
                                Toast.makeText(ProfileActivity.this, "Data successfully changed!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Data failed to save.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    //Change new password
    public void updatePassword(final String data) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnUpdatePassword"));
                    params.add(new BasicNameValuePair("varId", id));
                    params.add(new BasicNameValuePair("varData", data));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                loadProfile();
                                Toast.makeText(ProfileActivity.this, "Password successfully changed!", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(ProfileActivity.this, "Password failed to change.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    //Check current password
    public void getCurrPassword(final String currPass, final String newPass) {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnGetCurrPassword"));
                    params.add(new BasicNameValuePair("varId", id));
                    params.add(new BasicNameValuePair("varPassword", currPass));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True")) {
                                updatePassword(newPass);
                            } else {
                                Toast.makeText(ProfileActivity.this, "You has entered wrong current password.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(ProfileActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }
}
