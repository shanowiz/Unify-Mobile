package com.example.shano.unify;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;


import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.StringRequest;
import com.example.shano.unify.m_MySQL.Connector;
import com.example.shano.unify.m_MySQL.DataParser;
import com.example.shano.unify.m_MySQL.Downloader;
import com.example.shano.unify.push_Notification.MySingleton;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;

import org.w3c.dom.Text;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, SearchView.OnQueryTextListener {

    public static boolean isActive = false;
    public static boolean running = false;
    public static boolean eachEventActivityRunning = false;
    private static final String TAG = MainActivity.class.getSimpleName();
    private static String activeNotification = "";
    private static boolean refreshTag = false;
    private String eventType = "";
    final static String eventUrlAddress = "http://10.0.2.2/unifyWeb/functions/unifyMobile/getMysqlDataMobile.php?type=eventfdsdl8s77f6sd66sf66sfs7fs6fs87f";
    final static String discountUrlAddress = "http://10.0.2.2/unifyWeb/functions/unifyMobile/getMysqlDataMobile.php?type=discountsdfsdf687sd7s77s67sd78f8sf";
    private String appServerUrl = "http://10.0.2.2/unifyWeb/functions/unifyMobile/fcm_insert.php";
    private static MainActivity refreshObj;
    static int track = 0;
    private ListView lv;
    private View contentView;
    private AdView bannerAdView;
    private CustomEventListActivity adapter = null;
    private  SearchView searchView;
    private int activeSearchMarker = 0;
    private ImageView noDataImg;

    public MainActivity() throws MalformedURLException {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        refreshObj = MainActivity.this;
        contentView = this.findViewById(android.R.id.content);
        setContentView(R.layout.main_activity);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        /*MobileAds.initialize(this, "ca-app-pub-8174203385221426~6162850575");
        bannerAdView = (AdView) findViewById(R.id.bannerAdView);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("084449F61408C8B45223C01A997EF5B3").build();
        bannerAdView.loadAd(adRequest);*/

        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.mainActivityRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                refreshContent();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences(getString(R.string.FCM_PREF), Context.MODE_PRIVATE);
        final String token = sharedPreferences.getString(getString(R.string.FCM_TOKEN), "");
        StringRequest stringRequest = new StringRequest(Request.Method.POST, appServerUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError volleyError) {

            }
        })
        {
            @Override
            protected Map <String, String> getParams () throws AuthFailureError {
               Map<String, String> params = new HashMap<String, String>();
                params.put("fcm_token", token);
                return params;
            }
        };

        MySingleton.getmInstance(MainActivity.this).addToRequestque(stringRequest);

        lv = (ListView) findViewById(R.id.happeningTodayView);
        this.setTitle("All Events");
        eventType = "allEvents";
        new Downloader(MainActivity.this, eventUrlAddress, lv, "allEventsOnCreate", "event",contentView).execute();
        lv.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position1, long id) {
                        String message = "" + position1 + "";
                        EachEventActivity.setTrack(message);
                        EachEventActivity.setType(eventType);
                        Intent i = new Intent(MainActivity.this, EachEventActivity.class);
                        startActivity(i);
                    }
                }
        );
        getPreference();

        noDataImg = (ImageView) findViewById(R.id.noDataImg);
        searchView = (SearchView) findViewById(R.id.searchInput);
        searchView.setVisibility(View.GONE);
        searchView.setOnQueryTextListener(this);

    }

    public CustomEventListActivity getAdapter() {
        return adapter;
    }

    public void setAdapter(CustomEventListActivity adapter) {
        this.adapter = adapter;
    }

    @Override
    protected void onStart() {
        super.onStart();
        isActive = true;
        running = true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    @Override
    protected void onPause() {
        //bannerAdView.pause();
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (eachEventActivityRunning != true) {
            refreshContent();
            eachEventActivityRunning = false;
        }
        //bannerAdView.resume();
    }

    @Override
    protected void onDestroy() {
        //bannerAdView.destroy();
        running = false;
        super.onDestroy();
    }

    public static MainActivity getInstance() {
        return refreshObj;
    }

    public void refreshContent () {
        noDataImg.setVisibility(View.INVISIBLE);
        if (eventType.equals("allEvents")) {
            new Downloader(MainActivity.this, eventUrlAddress, lv, "allEvents","event",contentView).execute();
        } else if (eventType.equals("happeningToday")) {
            new Downloader(MainActivity.this, eventUrlAddress, lv, "happeningToday","event",contentView).execute();
        }else if (eventType.equals("union")) {
            new Downloader(MainActivity.this, eventUrlAddress, lv, "union","event",contentView).execute();
        }else if (eventType.equals("university")) {
            new Downloader(MainActivity.this, eventUrlAddress, lv, "university","event",contentView).execute();
        }else if (eventType.equals("club")) {
            new Downloader(MainActivity.this, eventUrlAddress, lv, "club","event",contentView).execute();
        }else  if (eventType.equals("faculty")) {
            new Downloader(MainActivity.this,eventUrlAddress, lv, "faculty","event",contentView).execute();
        }else if (eventType.equals("discount")) {
            new Downloader(MainActivity.this, discountUrlAddress, lv, "discount","discount",contentView).execute();
        }
        refreshTag=false;
    }
    public void refreshActivity () {
        Intent i = new Intent(MainActivity.this, MainActivity.class);
        startActivity(i);
    }

    public void displayNoDataMsg() {
        noDataImg.setVisibility(View.VISIBLE);
        noDataImg.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.empty));

    }

    public static boolean isRefreshTag() {return refreshTag;}

    public static void setRefreshTag(boolean refreshTag) {MainActivity.refreshTag = refreshTag;}

    public static String getActiveNotification() {
        return activeNotification;
    }

    public static void setActiveNotification(String activeNotification) { MainActivity.activeNotification = activeNotification;}

    public static int getTrack() {
        return track;
    }

    public static void setTrack(int track) {
        MainActivity.track = track;
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_activity, menu);
        getMenuInflater().inflate(R.menu.search_menu_item, menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.set_preference) {
            Intent i = new Intent(MainActivity.this, SetPreferenceActivity.class);
            startActivity(i);
        }else if (id == R.id.searchButton) {
            if (activeSearchMarker == 0) {
                searchView.setVisibility(View.VISIBLE);
                searchView.setFocusable(true);
                searchView.setIconified(false);
                activeSearchMarker = 1;
            } else if (activeSearchMarker == 1) {
                searchView.setVisibility(View.GONE);
                activeSearchMarker = 0;
            }
        }

        return super.onOptionsItemSelected(item);
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();
        noDataImg.setVisibility(View.INVISIBLE);

        if (id == R.id.nav_discounts_layout) {

            this.setTitle("Store Discounts");
            eventType = "discount";
            new Downloader(MainActivity.this, discountUrlAddress, lv, "discount","discount",contentView).execute();
            EachEventActivity.setType("discount");
        }
        else if (id == R.id.nav_happening_today_layout) {

            this.setTitle("Happening today");
            eventType = "happeningToday";
            new Downloader(MainActivity.this, eventUrlAddress, lv, "happeningToday","event",contentView).execute();

        } else if (id == R.id.nav_faculty_layout) {

            this.setTitle("Faculty");
            eventType = "faculty";
            new Downloader(MainActivity.this,eventUrlAddress, lv, "faculty","event",contentView).execute();

        } else if (id == R.id.nav_students_union_layout) {

            this.setTitle("Students Union");
            eventType = "union";
            new Downloader(MainActivity.this,eventUrlAddress, lv, "union","event",contentView).execute();

        } else   if (id == R.id.nav_university_layout) {

            this.setTitle("University");
            eventType = "university";
            new Downloader(MainActivity.this,eventUrlAddress, lv, "university","event",contentView).execute();

        } else   if (id == R.id.nav_clubs_layout) {

            this.setTitle("Club");
            eventType = "club";
            new Downloader(MainActivity.this, eventUrlAddress, lv, "club","event",contentView).execute();

        }   else if (id == R.id.nav_all_events_layout) {

            this.setTitle("All Events");
            eventType = "allEvents";
            new Downloader(MainActivity.this, eventUrlAddress, lv, "allEvents","event",contentView).execute();

        } else if (id == R.id.nav_settings) {

            Intent i = new Intent (MainActivity.this, SetPreferenceActivity.class);
            startActivity(i);

        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    public void getPreference () {

        File file = MainActivity.this.getFileStreamPath("preference");
        if(file == null || !file.exists()) {

            Intent i = new Intent(MainActivity.this,SetPreferenceActivity.class);
            startActivity(i);
            Toast.makeText(MainActivity.this,"Select Notifications",Toast.LENGTH_LONG).show();

        }
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        ProgressBar progressBar = (ProgressBar) findViewById(R.id.progressBar);
        progressBar.setVisibility(View.VISIBLE);
        DataParser.customAdapter.getFilter().filter(newText);
        progressBar.setVisibility(View.GONE);

        return false;
    }
}




