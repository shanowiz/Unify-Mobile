package com.example.shano.unify;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.FragmentManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.shano.unify.m_DataObject.UnifyEvent;
import com.example.shano.unify.m_MySQL.DataParser;
import com.example.shano.unify.m_MySQL.Downloader;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;

import static com.example.shano.unify.MainActivity.eventUrlAddress;


/**
 * Created by shano on 5/15/2017.
 */

public class EachEventActivity extends FragmentActivity implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener, DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {

    private  LatLng coordinates;
    private GoogleMap mMap;
    private int zoomLevel = 7;
    private static String track = "";
    private static String type = "";
    private String location = "";
    private Switch reminderSwitch;
    private RadioButton normalRadioButton;
    private RadioButton satelliteRadioButton;
    private String reminderName;
    private String reminderStatus="";
    private String reminderId;
    private int eventCount;
    private static String receiverReminderId="";
    private int finalDay, finalMonth,finalYear, finalHour, finalMinute;
    private static UnifyEvent unifyEvent = new UnifyEvent();
    private String eventName;
    private String eventDetails;
    private String eventVenue;
    private String eventDate;
    private String eventTime;
    private String eventCost;
    private String eventImg;
    private String eventId;
    private static int mapTypeMarker = 0;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setContentView(R.layout.each_event_layout);

        interstitialAd = new InterstitialAd(this);

        // set the ad unit ID
        interstitialAd.setAdUnitId("ca-app-pub-8174203385221426/1986203351");

        AdRequest adRequest = new AdRequest.Builder()
                .build();

        //AdRequest adRequest = new AdRequest.Builder().addTestDevice(AdRequest.DEVICE_ID_EMULATOR).addTestDevice("084449F61408C8B45223C01A997EF5B3").build();


        // Load ads into Interstitial Ads
        interstitialAd.loadAd(adRequest);

        interstitialAd.setAdListener(new AdListener() {
            public void onAdLoaded() {
                showInterstitial();
            }
        });

        Button enlargeMapButton = (Button) findViewById(R.id.enlargeMapButton);
        enlargeMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                Intent intent = new Intent(EachEventActivity.this, MapActivity.class);
                intent.putExtra("lat",coordinates.latitude);
                intent.putExtra("long",coordinates.longitude);
                startActivity(intent);
            }

        });

        final FragmentManager fragmentManager = getFragmentManager();
        final ImageFragment imageFragment = new ImageFragment();
        ImageButton imageButton = (ImageButton) findViewById(R.id.eachEventImage);
        imageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                imageFragment.show(fragmentManager,"Image Fragment");
                imageFragment.setImgUrl(eventImg);
                imageFragment.setContext(EachEventActivity.this);
            }

        });



        adjustLayout();
        addContent();
        expiredReminder();
        setReminderSwitch();

    }

    @Override
    protected void onStart() {
        MainActivity.eachEventActivityRunning = true;
        super.onStart();
    }

    private void showInterstitial() {
        if (interstitialAd.isLoaded()) {
            interstitialAd.show();
        }
    }

    public void adjustLayout () {

        // final RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.eachEventLayout);
        final ImageView imageView = (ImageView) findViewById(R.id.eachEventImage);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.eachEventScrollview);
        final RelativeLayout map = (RelativeLayout) findViewById(R.id.map_layout);

        DisplayMetrics metrics = getResources().getDisplayMetrics();

        //int deviceWidth = metrics.widthPixels;
        int deviceHeight = (metrics.heightPixels/3);

        String height = Integer.toString(imageView.getMeasuredWidth());
        //Toast.makeText(EachEventActivity.this,height,Toast.LENGTH_LONG).show();

        ViewGroup.LayoutParams imgParams = imageView.getLayoutParams();
        imgParams.height = deviceHeight-150;
        //imgParams.width = 600;
        imageView.setLayoutParams(imgParams);

        ViewGroup.LayoutParams scrollViewParams = scrollView.getLayoutParams();
        scrollViewParams.height = deviceHeight+30;
        scrollView.setLayoutParams(scrollViewParams);

        ViewGroup.LayoutParams mapParams = map.getLayoutParams();
        mapParams.height = deviceHeight-150;
        map.setLayoutParams(mapParams);

    }
    public void addContent () {

        TextView title = (TextView) findViewById(R.id.eventTitle);
        TextView event = (TextView) findViewById(R.id.eachEventName);
        TextView details = (TextView) findViewById(R.id.eachEventDetails);
        TextView venue = (TextView) findViewById(R.id.eachEventVenue);
        TextView cost = (TextView) findViewById(R.id.eachEventCost);
        TextView costLabel = (TextView) findViewById(R.id.eachEventCostLabel);
        TextView time = (TextView) findViewById(R.id.eachEventTime);
        TextView timeLabel = (TextView) findViewById(R.id.eachEventTimeLabel);
        TextView date = (TextView) findViewById(R.id.eachEventDate);
        ImageView img = (ImageView) findViewById(R.id.eachEventImage);
        reminderSwitch = (Switch) findViewById(R.id.reminderSwitch);
        normalRadioButton = (RadioButton) findViewById(R.id.normalRadioButton);
        satelliteRadioButton = (RadioButton) findViewById(R.id.satelliteRadioButton);
        reminderSwitch.setOnCheckedChangeListener(this);
        normalRadioButton.setOnCheckedChangeListener(this);
        normalRadioButton.setChecked(true);
        satelliteRadioButton.setOnCheckedChangeListener(this);

        //ArrayList<UnifyEvent> list = new ArrayList<>();
        ArrayList<UnifyEvent> list;

        if (track.isEmpty()) {

            Bundle extras = getIntent().getExtras();
            type = extras.getString("type");

            if (type.equals("reminder")) {

                title.setText(extras.getString("reminderName"));
                eventCount = extras.getInt("reminderId",0);
                reminderId = extras.getString("reminderId");
                reminderName = extras.getString("reminderName");
                receiverReminderId = extras.getString("reminderId");
                eventName = extras.getString("reminderName");
                eventDetails = extras.getString("eventDetails");
                eventDate = extras.getString("eventDate");
                eventCost = extras.getString("eventCost");
                eventTime = extras.getString("eventTime");
                eventImg = extras.getString("eventImg");
                eventVenue = extras.getString("eventVenue");
                location = extras.getString("eventVenue");
                eventId = extras.getString("reminderId");

                event.setText(eventName);
                details.setText(eventDetails);
                cost.append(eventCost);
                time.setText(eventTime);
                date.setText(eventDate);
                if (eventImg.isEmpty()) {
                    img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
                } else {
                    PicassoClient.downloadImage(EachEventActivity.this, eventImg, img, "");
                }
                if (eventVenue.toLowerCase().contains("a")) {
                    venue.setText(eventVenue + " (Ground Floor)");
                } else if (eventVenue.toLowerCase().contains("b")) {
                    venue.setText(eventVenue + " (1st Floor)");
                } else if (eventVenue.toLowerCase().contains("c")) {
                    venue.setText(eventVenue + " (2st Floor)");
                } else {
                    venue.setText(eventVenue);
                }
            }else if (type.equals("eventNotification")) {
                title.setText(extras.getString("eventName"));
                eventName = extras.getString("eventName");
                eventDetails = extras.getString("eventDetails");
                eventDate = extras.getString("eventDate");
                eventCost = extras.getString("eventCost");
                eventTime = extras.getString("eventTime");
                eventImg = extras.getString("eventImg");
                eventVenue = extras.getString("eventVenue");
                location = extras.getString("eventVenue");

                event.setText(eventName);
                details.setText(eventDetails);
                cost.append(eventCost);
                time.setText(eventTime);
                date.setText(eventDate);
                if (eventImg.isEmpty()) {
                    img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
                } else {
                    PicassoClient.downloadImage(EachEventActivity.this, eventImg, img, "");
                }
                if (eventVenue.toLowerCase().contains("a")) {
                    venue.setText(eventVenue + " (Ground Floor)");
                } else if (eventVenue.toLowerCase().contains("b")) {
                    venue.setText(eventVenue + " (1st Floor)");
                } else if (eventVenue.toLowerCase().contains("c")) {
                    venue.setText(eventVenue + " (2st Floor)");
                } else {
                    venue.setText(eventVenue);
                }

            }else if (type.equals("discountNotification")) {

                String discountName = extras.getString("discountName");
                String discountDetails = extras.getString("discountDetails");
                String discountDate = extras.getString("discountDate");
                String discountImg = extras.getString("discountImg");
                String discountVenue = extras.getString("discountVenue");
                location = extras.getString("discountVenue");

                title.setText(extras.getString("discountName"));
                event.setText(discountName);
                details.setText(discountDetails);
                date.setText(discountDate);
                cost.setVisibility(View.GONE);
                costLabel.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                timeLabel.setVisibility(View.GONE);
                reminderSwitch.setVisibility(View.GONE);
                if (eventImg.isEmpty()) {
                    img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
                } else {
                    PicassoClient.downloadImage(EachEventActivity.this, discountImg, img, "");
                }
                if (discountVenue.toLowerCase().contains("a")) {
                    venue.setText(discountVenue + " (Ground Floor)");
                } else if (discountVenue.toLowerCase().contains("b")) {
                    venue.setText(discountVenue + " (1st Floor)");
                } else if (discountVenue.toLowerCase().contains("c")) {
                    venue.setText(discountVenue + " (2st Floor)");
                } else {
                    venue.setText(discountVenue);
                }

            }

        }else {
            list = DataParser.getEventsArray();
            int track1 = Integer.parseInt(track);
            track = "";
            unifyEvent = list.get(track1);

            title.setText(unifyEvent.getName());
            eventName = unifyEvent.getName();
            eventDetails = unifyEvent.getDetails();
            eventDate = unifyEvent.getDate();
            eventCost = unifyEvent.getCost();
            eventTime = unifyEvent.getTime();
            eventImg = unifyEvent.getImageUrl();
            eventVenue = unifyEvent.getLocation();
            eventId = unifyEvent.getId();

            if (type.equals("discount")) {
                event.setText(eventName);
                details.setText(eventDetails);
                date.setText(eventDate);
                cost.setVisibility(View.GONE);
                costLabel.setVisibility(View.GONE);
                time.setVisibility(View.GONE);
                timeLabel.setVisibility(View.GONE);
                reminderSwitch.setVisibility(View.GONE);
                if (eventImg.isEmpty()) {
                    img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
                } else {
                    PicassoClient.downloadImage(EachEventActivity.this, eventImg, img, "");
                }
                if (eventVenue.toLowerCase().contains("a")) {
                    venue.setText(eventVenue + " (Ground Floor)");
                } else if (eventVenue.toLowerCase().contains("b")) {
                    venue.setText(eventVenue + " (1st Floor)");
                } else if (eventVenue.toLowerCase().contains("c")) {
                    venue.setText(eventVenue + " (2st Floor)");
                } else {
                    venue.setText(eventVenue);
                }
            } else {
                event.setText(eventName);
                details.setText(eventDetails);
                cost.append(eventCost);
                time.setText(eventTime);
                date.setText(eventDate);
                if (eventImg.isEmpty()) {
                    img.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.no_image));
                } else {
                    PicassoClient.downloadImage(EachEventActivity.this, eventImg, img, "");
                }
                if (eventVenue.toLowerCase().contains("a")) {
                    venue.setText(eventVenue + " (Ground Floor)");
                } else if (eventVenue.toLowerCase().contains("b")) {
                    venue.setText(eventVenue + " (1st Floor)");
                } else if (eventVenue.toLowerCase().contains("c")) {
                    venue.setText(eventVenue + " (2st Floor)");
                } else {
                    venue.setText(eventVenue);
                }
            }

            eventCount = unifyEvent.getEventCount();
            reminderId = unifyEvent.getId();
            reminderName = eventName;
            location = venue.getText().toString();
        }

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button backButton = (Button) findViewById(R.id.eachEventBackButton);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {

                if (MainActivity.running == true ) {
                    EachEventActivity.this.finish();
                }else {
                    Intent i = new Intent(EachEventActivity.this, MainActivity.class);
                    startActivity(i);
                    EachEventActivity.this.finish();
                }

            }

        });

    }
    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;

    public static UnifyEvent getEvent() {return unifyEvent;}

    public static void setEvent(UnifyEvent event) {EachEventActivity.unifyEvent = event;}

    public static String getTrack() {
        return track;
    }

    public static void setTrack(String track1) {
        track = track1;
    }

    public static String getType() {
        return type;
    }

    public static void setType(String type1) {
        type = type1;
    }



    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        //events locations
        coordinates = new LatLng(0, 0);
        LatLng cameraCooridinates = new LatLng(18.018422, -76.743948);
        LatLng building1CoordinatesEngineering = new LatLng(18.018555, -76.742571);
        LatLng building2CoordinatesScit = new LatLng(18.018306, -76.743136);
        LatLng building22CoordinatesSoba = new LatLng(18.018181, -76.743766);
        LatLng building5CoordinatesSoba = new LatLng(18.017955, -76.743772);
        LatLng building18CoordinatesShtm = new LatLng(18.019603, -76.743603);
        LatLng building8CoordinatesFels = new LatLng(18.018596, -76.743480);
        LatLng lTCoordinatesFels = new LatLng(18.018436, -76.743298);
        LatLng building3CoordinatesHealthScience = new LatLng(18.017460, -76.743527);
        LatLng building4CoordinatesBuiltEnvironment = new LatLng(18.017709, -76.744130);
        LatLng drawingroomCoordinates = new LatLng(18.019151, -76.743105);
        LatLng nursingCoordinates = new LatLng(18.020145, -76.744050);
        LatLng fossCoordinates = new LatLng(18.016586, -76.745979);
        LatLng chapelCoordinates = new LatLng(18.019161, -76.743423);
        LatLng libraryCoordinates = new LatLng(18.016575, -76.743511);

        //discounts locations
        LatLng juciBeefCoordinates = new LatLng(18.019748, -76.743170);
        LatLng bookStroreCoordinates = new LatLng(18.019288, -76.743623);
        LatLng burgerkingCoordinates = new LatLng(18.019363, -76.743985);
        LatLng johnShopCoordinates = new LatLng(18.019260, -76.744009);
        LatLng liliansRestaurantCoordinates = new LatLng(18.019088, -76.743101);

        if (location.toLowerCase().contains("barn") || location.toLowerCase().contains("student activity centre")) {

            coordinates = new LatLng(18.0196895, -76.7446277);
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("greenhouse") || location.toLowerCase().contains("students activity centre") || location.toLowerCase().contains("student's activity centre") || location.toLowerCase().contains("student activity centre") ) {

            coordinates = new LatLng( 18.019313, -76.744184);
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("auditorium")) {

            coordinates = new LatLng( 18.020658, -76.743274);
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("sculpture park") || location.toLowerCase().contains("sculpturepark")) {

            coordinates = new LatLng( 18.018942, -76.743360);
            cameraCooridinates = coordinates;
        }else if (location.toLowerCase().contains("1a") || location.toLowerCase().contains("1b") || location.toLowerCase().contains("1c")) {

            coordinates = building1CoordinatesEngineering;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("2b")) {

            coordinates = building2CoordinatesScit;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("3a") || location.toLowerCase().contains("3b") || location.toLowerCase().contains("3c")) {

            coordinates = building3CoordinatesHealthScience;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("4a") || location.toLowerCase().contains("4b") || location.toLowerCase().contains("4c")) {

            coordinates = building4CoordinatesBuiltEnvironment;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("5a") || location.toLowerCase().contains("5b") || location.toLowerCase().contains("5c")) {

            coordinates = building5CoordinatesSoba;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("22a") || location.toLowerCase().contains("22b") || location.toLowerCase().contains("22c")) {

            coordinates = building22CoordinatesSoba;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("8a") || location.toLowerCase().contains("8b") || location.toLowerCase().contains("8c")) {

            coordinates = building8CoordinatesFels;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("lt10a") || location.toLowerCase().contains("lt10b")) {

            coordinates = building4CoordinatesBuiltEnvironment;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("4a") || location.toLowerCase().contains("4b") || location.toLowerCase().contains("4c")) {

            coordinates = lTCoordinatesFels;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("18a") || location.toLowerCase().contains("18b") || location.toLowerCase().contains("18c")) {

            coordinates = building18CoordinatesShtm;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("drawing")) {

            coordinates = drawingroomCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("nurse") || location.toLowerCase().contains("nursing")) {

            coordinates = nursingCoordinates;
            cameraCooridinates = coordinates;

        } else if (location.toLowerCase().contains("foss") || location.toLowerCase().contains("faculty of science and sport") || location.toLowerCase().contains("faculty of science & sport")) {

            coordinates = fossCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("chapel")) {

            coordinates = chapelCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("library")) {

            coordinates = libraryCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("juci")) {

            coordinates = juciBeefCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("library")) {

            coordinates = libraryCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("book store") || location.toLowerCase().contains("bookstore") || location.toLowerCase().contains("bookStore")) {

            coordinates = bookStroreCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("burger king") || location.toLowerCase().contains("burgerking") || location.toLowerCase().contains("bookStore")) {

            coordinates = burgerkingCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("john") || location.toLowerCase().contains("john's") || location.toLowerCase().contains("johns")) {

            coordinates = johnShopCoordinates;
            cameraCooridinates = coordinates;

        }else if (location.toLowerCase().contains("lillian") || location.toLowerCase().contains("lillian's") || location.toLowerCase().contains("lillians")) {

            coordinates = liliansRestaurantCoordinates;
            cameraCooridinates = coordinates;

        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.addMarker(new MarkerOptions().position(coordinates).title(location));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraCooridinates,17));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

        int day, month, year;

        if (reminderSwitch.isChecked()) {

            if (!getReminder().equals(reminderName)) {

                Calendar calendar = Calendar.getInstance ();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog datePickerDialog = new DatePickerDialog(EachEventActivity.this,EachEventActivity.this,year,month,day);
                datePickerDialog.show();
                datePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        if (which == DialogInterface.BUTTON_NEGATIVE) {
                            reminderSwitch.setChecked(false);
                        }
                    }
                });
            }
        }else {
            cancelReminder();
        }

        if (satelliteRadioButton.isChecked()) {
            normalRadioButton.setTextColor(Color.WHITE);
            satelliteRadioButton.setTextColor(Color.WHITE);
            mMap.setMapType(mMap.MAP_TYPE_HYBRID);
            mapTypeMarker = 1;
        }else if ( normalRadioButton.isChecked() && mapTypeMarker == 1){
            normalRadioButton.setTextColor(Color.BLACK);
            satelliteRadioButton.setTextColor(Color.BLACK);
            mMap.setMapType(mMap.MAP_TYPE_NORMAL);
            mapTypeMarker = 0;
        }
    }

    public void storeReminder(String reminder) {

        String finalReminder = reminder;

        try {

            FileOutputStream fileOutputStream = openFileOutput(reminderName+"Reminder",MODE_PRIVATE);
            fileOutputStream.write(finalReminder.getBytes());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getReminder () {

        String reminder="";

        try {
            String message ="";
            FileInputStream fileInputStream = openFileInput(reminderName+"Reminder");
            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            while ((message = bufferedReader.readLine()) != null) {
                reminder = message;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return reminder;
    }

    public void setReminderSwitch (){

        String reminder = getReminder();

        if (reminder.equals(reminderName)) {
            reminderSwitch.setChecked(true);
        }else {
            reminderSwitch.setChecked(false);
        }

    }

    public String getReminderStatus() {
        return reminderStatus;
    }

    public void setReminderStatus(String reminderStatus) {
        this.reminderStatus = reminderStatus;
    }

    public void cancelReminder () {

        File file = this.getFileStreamPath(reminderName+"Reminder");

        if(file.exists()) {

            String reminder = getReminder();

            if (reminder.equals(reminderName)) {
                storeReminder("");

                AlarmManager alarmManger = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent intent = new Intent(getApplicationContext(), ReminderNotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), eventCount, intent, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManger.cancel(pendingIntent);
                pendingIntent.cancel();
            }
        }
    }

    public void expiredReminder () {

        if (reminderId == receiverReminderId) {

            File file = this.getFileStreamPath(reminderName+"Reminder");

            if(file.exists()) {

                String reminder = getReminder();

                if (reminder.equals(reminderName)) {
                    storeReminder("");
                }
            }
        }
    }


    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {

        int hour, minute;

        finalDay = dayOfMonth;
        finalMonth = month;
        finalYear = year;

        Calendar calendar = Calendar.getInstance();
        hour = calendar.get(Calendar.HOUR_OF_DAY);
        minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(EachEventActivity.this,EachEventActivity.this,hour,minute, android.text.format.DateFormat.is24HourFormat(this));
        timePickerDialog.show();
        timePickerDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                if (which == DialogInterface.BUTTON_NEGATIVE) {
                    reminderSwitch.setChecked(false);
                }
            }
        });



    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

        finalHour = hourOfDay;
        finalMinute = minute;
        storeReminder(reminderName);
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.MONTH, finalMonth);
        calendar.set(Calendar.DAY_OF_MONTH, finalDay);
        calendar.set(Calendar.YEAR, finalYear);
        calendar.set(Calendar.HOUR_OF_DAY, finalHour);
        calendar.set(Calendar.MINUTE, finalMinute);
        calendar.set(Calendar.MILLISECOND, 01);

        Intent intent = new Intent(getApplicationContext(), ReminderNotificationReceiver.class);
        intent.putExtra("reminderName",reminderName);
        intent.putExtra("eventCount",eventCount);
        intent.putExtra("reminderId",reminderId);
        intent.putExtra("eventDetails",eventDetails);
        intent.putExtra("eventDate",eventDate);
        intent.putExtra("eventTime",eventTime);
        intent.putExtra("eventCost",eventCost);
        intent.putExtra("eventVenue",eventVenue);
        intent.putExtra("eventImg",eventImg);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), eventCount, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        AlarmManager alarmManger = (AlarmManager) getSystemService(ALARM_SERVICE);
        alarmManger.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);


        View v = this.findViewById(android.R.id.content);
        Snackbar snackbar = Snackbar.make(v, Html.fromHtml("<font color=\"#ffffff\">Reminder Set</font>"),Snackbar.LENGTH_LONG);
        View snackbarView = snackbar.getView();
        snackbarView.setBackgroundColor(Color.parseColor("#1a75ff"));
        snackbar.show();

    }

    public String getReceiverReminderId() {
        return receiverReminderId;
    }

    public static void setReceiverReminderId(String receiverReminderId1) {
        receiverReminderId = receiverReminderId1;
    }

}
