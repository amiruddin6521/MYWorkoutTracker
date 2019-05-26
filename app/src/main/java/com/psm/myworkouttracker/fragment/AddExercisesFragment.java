package com.psm.myworkouttracker.fragment;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.activity.MainActivity;
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
import java.util.Date;
import java.util.List;

import static android.content.Context.CONNECTIVITY_SERVICE;

public class AddExercisesFragment extends Fragment {

    private RadioButton radB;
    private RadioGroup radGrpExe;
    private Button btnSaveExe, btnResetExe;
    private String uId, currentPhotoPath, encoded_string, image_name;
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
        btnResetExe = v.findViewById(R.id.btnResetExe);
        radGrpExe = v.findViewById(R.id.radGrpExe);
        roundProfile =  v.findViewById(R.id.imgPhoto2);
        photoButton = v.findViewById(R.id.actionCamera2);
        edtNameExe =  v.findViewById(R.id.edtNameExe2);
        edtDescExe = v.findViewById(R.id.edtDescExe2);

        MainActivity activity = (MainActivity) getActivity();
        uId = activity.getMyData();

        photoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(haveNetwork()) {
                    selectImage();
                } else if(!haveNetwork()) {
                    Toast.makeText(getActivity(),R.string.interneterror,Toast.LENGTH_LONG).show();
                }
            }
        });

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

    //Dialog box with the three options
    private void selectImage() {
        final CharSequence[] items = { "Take new picture", "Choose from library",
                "Remove current picture", "Cancel"};
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Add Picture!");
        builder.setIcon(R.drawable.ic_menu_camera);
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
                if (items[item].equals("Take new picture")) {
                    dispatchTakePictureIntent();
                } else if (items[item].equals("Choose from library")) {
                    galleryIntent();
                } else if (items[item].equals("Remove current picture")) {
                    encoded_string = "";
                    image_name = "";
                    roundProfile.setImageResource(R.drawable.ic_machine);
                    dialog.dismiss();
                } else if (items[item].equals("Cancel")) {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";

        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(getActivity(),
                        "com.psm.myworkouttracker.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, 1);
            }
        }
    }

    //Calling an implicit intent to open the gallery
    private void galleryIntent()
    {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_PICK);//ACTION_GET_CONTENT
        startActivityForResult(Intent.createChooser(intent, "Select File"),2);
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
            if (resultCode == Activity.RESULT_OK) {
                Uri resultUri = result.getUri();
                roundProfile.setImageURI(resultUri);
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), resultUri);
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
            CropImage.activity(selectedImageUri).start(getContext(), this);
        } else if (requestCode == 1) {
            File file = new File(currentPhotoPath);
            Uri selectedImageUri = Uri.fromFile(file);
            CropImage.activity(selectedImageUri).start(getContext(), this);
        }
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
                    params.add(new BasicNameValuePair("encoded_string", encoded_string));
                    params.add(new BasicNameValuePair("image_name", image_name));

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
