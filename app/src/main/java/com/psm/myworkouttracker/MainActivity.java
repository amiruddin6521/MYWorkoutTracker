package com.psm.myworkouttracker;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
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

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private String id;
    private ImageView imageView1;
    private TextView txtName, txtEmail;
    private WebServiceCall wsc = new WebServiceCall();
    private JSONObject jsnObj = new JSONObject();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the id from login activity.
        Intent intent = getIntent();
        id = intent.getStringExtra("id");

        loadProfile();

        Toolbar toolbar = findViewById(R.id.toolbar);
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

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header= navigationView.getHeaderView(0);
        txtName = header.findViewById(R.id.txtName1);
        txtEmail = header.findViewById(R.id.txtEmail1);
        imageView1 = header.findViewById(R.id.imageView1);

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

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new WorkoutFragment()).commit();
            navigationView.setCheckedItem(R.id.nav_workout);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
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

        if (id == R.id.nav_workout) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WorkoutFragment()).commit();
        } else if (id == R.id.nav_exercises) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new ExercisesFragment()).commit();
        } else if (id == R.id.nav_weighttrack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new WeightTrackFragment()).commit();
        } else if (id == R.id.nav_bodytrack) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new BodyTrackFragment()).commit();
        } else if (id == R.id.nav_setting) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new SettingFragment()).commit();
        } else if (id == R.id.nav_about) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,
                    new AboutFragment()).commit();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void loadProfile() {
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

                } catch (JSONException e){
                    //if fail to get from server, get from local mobile time
                    String strMsg = "No internet connection, please turn on your Mobile Data/WiFi.";
                    Toast.makeText(MainActivity.this, strMsg, Toast.LENGTH_LONG).show();
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(strRespond.equals("True")) {
                            byte[] data = Base64.decode(img64, Base64.DEFAULT);
                            Bitmap decodedByte = BitmapFactory.decodeByteArray(data, 0, data.length);
                            imageView1.setImageBitmap(decodedByte);
                            txtName.setText(name);
                            txtEmail.setText(email);
                        } else {
                            Toast.makeText(MainActivity.this, "Something wrong.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        };
        Thread thr = new Thread(run);
        thr.start();
    }
}
