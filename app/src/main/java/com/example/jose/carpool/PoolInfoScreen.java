package com.example.jose.carpool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import java.util.ArrayList;

public class PoolInfoScreen extends FragmentActivity implements OnMapReadyCallback {

    private static final String TAG = "CPInfoActivity";
    private GoogleMap mMap;
    private SlidingUpPanelLayout slidingLayout;
    private CarPool _CP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_info_screen);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        slidingLayout = (SlidingUpPanelLayout) findViewById(R.id.sliding_layout);
        LinearLayout dzone = (LinearLayout) findViewById(R.id.dragzone);
        slidingLayout.setDragView(dzone);

        slidingLayout.addPanelSlideListener(new SlidingUpPanelLayout.PanelSlideListener() {
            @Override
            public void onPanelSlide(View panel, float slideOffset) {

            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

                ImageView arrow = (ImageView) findViewById(R.id.arrow);

                arrow.setRotation(180);

            }
        });


        Gson gson = new Gson();
        _CP = gson.fromJson(getIntent().getStringExtra("CarPoolInfo"), CarPool.class);

        updateui();



    }

    private void updateui(){
        Toast.makeText(this, _CP.getNomDestino(),
                Toast.LENGTH_LONG).show();
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){ //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "hay permiso");
            mMap.setMyLocationEnabled(true);
        }else{
            Log.d(TAG, "no hay permiso");
            ActivityCompat.requestPermissions(
                    this,
                    new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
                    0
            );

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED){
                mMap.setMyLocationEnabled(true);
            }else return;
        }

        // Add a marker in Sydney and move the camera
        LatLng PUCP = new LatLng(-12.06902418, -77.07927883);
        LatLng oval = new LatLng(-12.05224744, -77.13910818);
        LatLng casa = new LatLng(-12.06383074, -77.15378523);

        ArrayList points = new ArrayList();

        points.add(PUCP);
        points.add(oval);
        points.add(casa);

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(points);
        lineOptions.width(12);
        lineOptions.color(Color.RED);
        lineOptions.geodesic(true);
        //mMap.addMarker(new MarkerOptions().position(PUCP).title("PUCP"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(PUCP));
        //googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        // Zoom out to zoom level 10, animating with a duration of 2 seconds.
        //googleMap.animateCamera(CameraUpdateFactory.zoomTo(15), 2000, null);

        mMap.addPolyline(lineOptions);
    }
}
