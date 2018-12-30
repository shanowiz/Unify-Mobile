package com.example.shano.unify;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.RadioButton;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.Map;

public class MapActivity extends FragmentActivity implements OnMapReadyCallback, CompoundButton.OnCheckedChangeListener {

    static final int MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION = 1;
    private GoogleMap mMap;
    private static int mapTypeMarker = 0;
    private RadioButton normalRadioButton;
    private RadioButton satelliteRadioButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_layout);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.enlargeMap);
        mapFragment.getMapAsync(this);

        initializeComponents();

        Button button = (Button) findViewById(R.id.closeMapButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick (View view) {
                MapActivity.this.finish();
            }

        });
    }

    public void initializeComponents () {
        normalRadioButton = (RadioButton) findViewById(R.id.normalRadioButtonEnlarge);
        satelliteRadioButton = (RadioButton) findViewById(R.id.satelliteRadioButtonEnlarge);
        normalRadioButton.setOnCheckedChangeListener(this);
        satelliteRadioButton.setOnCheckedChangeListener(this);
        normalRadioButton.setChecked(true);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        LatLng coordinates = new LatLng(0, 0);
        LatLng cameraCoordinates = new LatLng(18.018422, -76.743948);
        double latitude;
        double longitude;

        Bundle extras = getIntent().getExtras();
        latitude = extras.getDouble("lat");
        longitude = extras.getDouble("long");
        if (latitude>0 || longitude>0) {
            coordinates = new LatLng(latitude, longitude);
            cameraCoordinates = new LatLng(latitude, longitude);
        }

        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {

            mMap.setMyLocationEnabled(true);
            mMap.addMarker(new MarkerOptions().position(coordinates));
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(cameraCoordinates,18));
        } else {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                    MY_PERMISSIONS_REQUEST_ACCESS_FINE_LOCATION);
            finish();
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

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
}
