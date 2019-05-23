package com.psm.myworkouttracker.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.fragment.AboutFragment;
import com.psm.myworkouttracker.fragment.BodyTrackFragment;
import com.psm.myworkouttracker.fragment.ExercisesFragment;
import com.psm.myworkouttracker.fragment.WeightTrackFragment;
import com.psm.myworkouttracker.fragment.WorkoutFragment;
import com.psm.myworkouttracker.services.WebServiceCallObj;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String id = "1"; //1
    private ImageView imageView1;
    private TextView txtName, txtEmail;
    private WebServiceCallObj wsc = new WebServiceCallObj();
    private JSONObject jsnObj = new JSONObject();
    private Fragment frag = null;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private View profile_nav_progress, profile_nav, header;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the id from login activity. //2
        /*Intent intent = getIntent();
        id = intent.getStringExtra("id");*/

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        /*FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });*/

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        header= navigationView.getHeaderView(0);
        txtName = header.findViewById(R.id.txtName1);
        txtEmail = header.findViewById(R.id.txtEmail1);
        imageView1 = header.findViewById(R.id.imageView1);
        profile_nav_progress = header.findViewById(R.id.profile_nav_progress);
        profile_nav = header.findViewById(R.id.profile_nav);

        imageView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProfileActivity.class);
                intent.putExtra("id", id);
                startActivity(intent);
                DrawerLayout drawer = findViewById(R.id.drawer_layout);
                drawer.closeDrawer(GravityCompat.START);
            }
        });

        loadProfile();

        if (savedInstanceState == null) {
            frag = new WorkoutFragment();
            if(frag != null) {
                FragmentManager manager = getSupportFragmentManager();
                manager.beginTransaction()
                        .replace(R.id.fragment_container, frag)
                        .commit();
                navigationView.setCheckedItem(R.id.nav_workout);
            }
        }
    }

    public String getMyData() {
        return id;
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

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            int count = getSupportFragmentManager().getBackStackEntryCount();

            if (count == 0) {
                exitDialog();
            } else {
                getSupportFragmentManager().popBackStack();
            }
        }
    }

    public void exitDialog() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setIcon(R.drawable.ic_warning_black_24dp);
        alertDialog.setTitle("Exit Application");
        alertDialog.setMessage("Are you sure you want to logout?");
        alertDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                startActivity(intent);
                MainActivity.this.finish();
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        getSupportFragmentManager().popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);

        if (id == R.id.nav_workout) {
            frag = new WorkoutFragment();
        } else if (id == R.id.nav_exercises) {
            frag = new ExercisesFragment();
        } else if (id == R.id.nav_weighttrack) {
            frag = new WeightTrackFragment();
        } else if (id == R.id.nav_bodytrack) {
            frag = new BodyTrackFragment();
        } else if (id == R.id.nav_about) {
            frag = new AboutFragment();
        } else if (id == R.id.nav_logout) {
            exitDialog();
        }

        if(frag != null) {
            FragmentManager manager = getSupportFragmentManager();
            manager.beginTransaction()
                    .replace(R.id.fragment_container, frag)
                    .commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadProfile() {
        profile_nav_progress.setVisibility(header.VISIBLE);
        profile_nav.setVisibility(header.GONE);
        if(haveNetwork()) {
            Runnable run = new Runnable()
            {
                String img64, strRespond, name, email;
                @Override
                public void run()
                {
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("selectFn", "fnLoadNavHeader"));
                    params.add(new BasicNameValuePair("_id", id));

                    try{
                        jsnObj = wsc.makeHttpRequest(wsc.fnGetURL(), "POST", params);
                        name = jsnObj.getString("name");
                        email = jsnObj.getString("email");
                        img64 = jsnObj.getString("encoded");
                        strRespond = jsnObj.getString("respond");

                    } catch (Exception e){
                        e.printStackTrace();
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if(strRespond.equals("True") && img64.equals("")) {
                                txtName.setText(name);
                                txtEmail.setText(email);
                                imageView1.setImageResource(R.drawable.person);
                                profile_nav_progress.setVisibility(header.GONE);
                                profile_nav.setVisibility(header.VISIBLE);
                            } else if(strRespond.equals("True")){
                                byte[] data = Base64.decode(img64, Base64.DEFAULT);
                                Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
                                imageView1.setImageBitmap(decodedByte);
                                txtName.setText(name);
                                txtEmail.setText(email);
                                profile_nav_progress.setVisibility(header.GONE);
                                profile_nav.setVisibility(header.VISIBLE);
                            } else {
                                Toast.makeText(MainActivity.this, "Something wrong. Please check your internet connection.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            };
            Thread thr = new Thread(run);
            thr.start();
        } else if(!haveNetwork()) {
            Toast.makeText(MainActivity.this,R.string.interneterror,Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        loadProfile();
    }
}
