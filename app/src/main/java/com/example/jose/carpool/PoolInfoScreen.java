package com.example.jose.carpool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class PoolInfoScreen extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CPInfoActivity";
    private GoogleMap mMap;
    private SlidingUpPanelLayout slidingLayout;
    private LatLng PUCP = new LatLng(-12.06902418, -77.07927883);
    private CarPool _CP;

    @Bind(R.id.nom_org) TextView _nom_org;
    @Bind(R.id.nom_dest) TextView _nom_dest;
    @Bind(R.id.dist_org) TextView _dist_org;
    @Bind(R.id.dist_dest) TextView _dist_dest;
    @Bind(R.id.hsal) TextView _hsal;
    @Bind(R.id.nasien) TextView _nasien;
    @Bind(R.id.precio) TextView _precio;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pool_info_screen);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        _CP = gson.fromJson(getIntent().getStringExtra("CarPoolInfo"), CarPool.class);

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


        updateui();


    }

    private void updateui() {
        _nom_dest.setText(_CP.getNomDestino());
        _nom_org.setText(_CP.getNomOrigen());
        _dist_org.setText(_CP.getDistOrigen());
        _dist_dest.setText(_CP.getDistDestino());
        _hsal.setText(_CP.getHsalida());
        _precio.setText("S/"+_CP.getCosto());

        int asientos = _CP.getNasientos();
        _nasien.setText(String.valueOf(asientos));




    }

    public void setRoute(){

        String ruta = _CP.getmRoute();


        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(decodePolyLines(ruta));
        lineOptions.width(15);
        lineOptions.color(Color.BLUE);
        lineOptions.geodesic(true);

        mMap.addPolyline(lineOptions);

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) { //&& ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.d(TAG, "hay permiso");
            mMap.setMyLocationEnabled(true);
        } else {
            Log.d(TAG, "no hay permiso");
            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0
            );

            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mMap.setMyLocationEnabled(true);
            } else return;
        }


        //List<LatLng> coords = decodePolyLines("reshAlb|uM~@dAP?x@SqA_ElAYLCMc@cAeDYsAEUQY]W_@Is@CcDP}@B[C_@Q[[wBiCmEkF}@yAmBoFo@cBgAwBm@oAs@aAcAiAuCaC}EqDcAo@kKgGcAk@a@IkEcCs@a@@SC}DDCLIRIVIZC\\Cf@QXOZo@Fi@C}@I]S]MIo@Ma@AYB]HWPQVQn@A^Bb@?`AAXK\\oAnCoFeDeBgBs@gAk@iAu@eBk@wB}@aDu@eCcAyDoA{EgEyO[{Bi@wBm@{B]_AUk@u@oDq@mC}@sCIUK@cCHgKd@cRv@mA?{@CAsBAu@Dk@b@_DZkArD{NbAeEP{@VcEn@uNXmFJu@bCuMr@iEjBmLl@{DJuBCuBKaB}AuT[aFUeC_AkFq@kDA[EaB@mBCe@GUQ@k@DYF_@PiEPq@?q@CoAOaEBsBFcBN_F`AeCh@yCfAsSdIwNtFwEhB_GpCcIpDwChAo@HyBViAd@aAp@CXDTPL\\F~@QjAi@rCwBVOn@SrAk@hDyAhD_BrI}DvDuAzKgEdCcAlOcGhBo@|@WnBa@|A[rASxBMx@?`AFfBRjAFt@Ct@ItAUx@SjEUtAEz@M|@ClKe@~K_@pJc@bEM`AGvFER@RCjIMrDWtDY~BUpJe@dJ_@jHc@dN_@tDMjFF`C@T@BEAIACwCCqFI?KEw@UeDYoGw@mPw@qR]cIIkBOaBOeC?WCeADWHs@E_Bc@wFSwA@oA[qCk@sDI_AE}@Je@JSNOv@e@pE{@xAQjEw@|B]rBUdE_@lDQfEI`DA`@Bb@RTNH^A^INIF`@@");

        mMap.moveCamera(CameraUpdateFactory.newLatLng(PUCP));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        setRoute();


    }

    public static List<LatLng> decodePolyLines(String poly){
        int len = poly.length();
        int index = 0;
        List<LatLng> decoded = new ArrayList<LatLng>();
        int lat = 0;
        int lng = 0;

        while (index < len){
            int b;
            int shift = 0;
            int result = 0;
            do{
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = poly.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            }while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >>1));
            lng += dlng;

            decoded.add(new LatLng(
                    lat / 100000d,
                    lng / 100000d
            ));
        }
        return decoded;
    }

}
