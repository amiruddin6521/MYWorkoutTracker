package com.psm.myworkouttracker.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
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
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.provider.MediaStore;
import android.widget.Toast;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.services.WebServiceCallObj;
import com.theartofdev.edmodo.cropper.CropImage;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class RegisterActivity extends AppCompatActivity {

    private CircularImageView roundProfile;
    private FloatingActionButton photoButton;
    private EditText txtName, txtEmail, txtPassword, txtWeight, txtHeight;
    private TextView txtBirthday;
    private RadioGroup radGender;
    private RadioButton radMale;
    private Button btnSave;
    private ImageButton btnCalender;
    private int mYear, mMonth, mDay;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private JSONObject jsnObj = new JSONObject();
    private String encoded_string, image_name, currentPhotoPath = "";
    private View mProgressView, mRegisterFormView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        roundProfile =  findViewById(R.id.photo);
        photoButton = findViewById(R.id.actionCamera);
        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPassword = findViewById(R.id.txtPassword);
        txtWeight = findViewById(R.id.txtWeight);
        txtHeight =  findViewById(R.id.txtHeight);
        txtBirthday = findViewById(R.id.txtBirthday);
        btnSave = findViewById(R.id.btnSave);
        btnCalender = findViewById(R.id.btnCalender);
        radGender = findViewById(R.id.radGender);
        mProgressView = findViewById(R.id.login_progress);
        mRegisterFormView = findViewById(R.id.register_form);

        String date = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        txtBirthday.setHint("e.g. "+date);

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attemptRegister();
            }
        });

        btnCalender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                calenderPicker();
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

            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
            mRegisterFormView.animate().setDuration(shortAnimTime).alpha(
                    show ? 0 : 1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
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
            mRegisterFormView.setVisibility(show ? View.GONE : View.VISIBLE);
        }
    }

    //Validation for register form
    private void attemptRegister() {
        String email, password, name, weight, height, birthday;

        // Reset errors.
        txtEmail.setError(null);
        txtPassword.setError(null);
        txtName.setError(null);
        txtWeight.setError(null);
        txtHeight.setError(null);
        txtBirthday.setError(null);

        // Store values at the time of the login attempt.
        email = txtEmail.getText().toString();
        password = txtPassword.getText().toString();
        name = txtName.getText().toString();
        weight = txtWeight.getText().toString();
        height = txtHeight.getText().toString();
        birthday = txtBirthday.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (TextUtils.isEmpty(password)) {
            txtPassword.setError(getString(R.string.error_field_required));
            focusView = txtPassword;
            cancel = true;
        } else if (!isPasswordValid(password)) {
            txtPassword.setError(getString(R.string.error_invalid_password));
            focusView = txtPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.error_field_required));
            focusView = txtEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            txtEmail.setError(getString(R.string.error_invalid_email));
            focusView = txtEmail;
            cancel = true;
        }

        if (TextUtils.isEmpty(name)) {
            txtName.setError(getString(R.string.error_field_required));
            focusView = txtName;
            cancel = true;
        }

        if (TextUtils.isEmpty(weight)) {
            txtWeight.setError(getString(R.string.error_field_required));
            focusView = txtWeight;
            cancel = true;
        }

        if (TextUtils.isEmpty(height)) {
            txtHeight.setError(getString(R.string.error_field_required));
            focusView = txtHeight;
            cancel = true;
        }

        if (TextUtils.isEmpty(birthday)) {
            txtBirthday.setError(getString(R.string.error_field_required));
            focusView = txtBirthday;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Check for an email address and password in database.
            checkRegisterData();
        }
    }

    //Validation for email
    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return email.contains("@");
    }

    //Validation for password
    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic
        return password.length() >= 8;
    }

    //Get birthday date
    private void calenderPicker() {
        // Get Current Date
        final Calendar c = Calendar.getInstance();
        mYear = c.get(Calendar.YEAR);
        mMonth = c.get(Calendar.MONTH);
        mDay = c.get(Calendar.DAY_OF_MONTH);


        DatePickerDialog datePickerDialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {

                        txtBirthday.setText(year + "-" + (monthOfYear + 1) + "-" + dayOfMonth);

                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    //Get gender data
    private String getRadioGender() {
        // get selected radio button from radioGroup
        int selectedId = radGender.getCheckedRadioButtonId();

        // find the radiobutton by returned id
        radMale = findViewById(selectedId);

        String result = radMale.getText().toString();


        return result;
    }

    //Dialog box with the three options
    private void selectImage() {
        final CharSequence[] items = { "Take new picture", "Choose from library",
                "Remove current picture", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);
        builder.setTitle("Add Picture!");
        builder.setIcon(R.drawable.ic_menu_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take new picture")) {
                    //cameraIntent();
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from library")) {
                    galleryIntent();
                } else if (items[item].equals("Remove current picture")) {
                    encoded_string = "";
                    image_name = "";
                    roundProfile.setImageResource(R.drawable.person);
                    dialog.dismiss();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    //Calling an implicit intent to open the gallery
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"),2);
    }

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
                roundProfile.setImageURI(resultUri);
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
                encoded_string = Base64.encodeToString(array, 0);
                image_name = fileName.getName();
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        } else if (requestCode == 2) {
            Uri selectedImageUri = data.getData();
            CropImage.activity(selectedImageUri).start(this);
        } else if (requestCode == 1) {
            File file = new File(currentPhotoPath);
            Uri selectedImageUri = Uri.fromFile(file);
            CropImage.activity(selectedImageUri).start(this);
        }
    }

    //Check valid email
    public void checkRegisterData() {
        showProgress(true);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                String email;
                @Override
                public void run()
                {
                    email = txtEmail.getText().toString();
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnCheckEmail"));
                    params.add(new BasicNameValuePair("varEmail", email));

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
                                saveRegisterData();
                            } else {
                                showProgress(false);
                                Toast.makeText(RegisterActivity.this, "This email has been used. Please use another email.", Toast.LENGTH_LONG).show();
                                txtEmail.setFocusable(true);
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(RegisterActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    //Save register information into database
    public void saveRegisterData() {
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String strRespond = "";
                String email, password, name, bdate, gender, weigth, height;
                @Override
                public void run()
                {
                    email = txtEmail.getText().toString();
                    password = txtPassword.getText().toString();
                    name = txtName.getText().toString();
                    bdate = txtBirthday.getText().toString();
                    gender = getRadioGender();
                    weigth = txtWeight.getText().toString();
                    height = txtHeight.getText().toString();

                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnRegister"));
                    params.add(new BasicNameValuePair("varEmail", email));
                    params.add(new BasicNameValuePair("varPassword", password));
                    params.add(new BasicNameValuePair("varName", name));
                    params.add(new BasicNameValuePair("varBdate", bdate));
                    params.add(new BasicNameValuePair("varGender", gender));
                    params.add(new BasicNameValuePair("varWeight", weigth));
                    params.add(new BasicNameValuePair("varHeight", height));
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
                                showProgress(false);
                                Toast.makeText(RegisterActivity.this, "You have successfully registered!", Toast.LENGTH_LONG).show();
                                RegisterActivity.this.finish();
                            } else {
                                showProgress(false);
                                Toast.makeText(RegisterActivity.this, "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(RegisterActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }
}
