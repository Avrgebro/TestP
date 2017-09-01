package com.example.jose.carpool;

/**
 * Created by Johnny on 30/08/2017.
 */

import android.Manifest;
import android.app.Fragment;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.gson.Gson;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;

public class crear_carpool extends AppCompatActivity  implements OnMapReadyCallback {

    private static final String TAG = "CPInfoActivity";
    private GoogleMap mMap;
    private SlidingUpPanelLayout slidingLayout;
    private List<LatLng> coords = new ArrayList<>();
    private FloatingActionButton button;
    private int voyovengo = 0; //flag para saber si va o viene a cato: 0 se va y 1 viene.
    private LatLng PUCP = new LatLng(-12.06902418, -77.07927883);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mapa);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        button = (FloatingActionButton) findViewById(R.id.floatinbtn);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);

                if(coords.isEmpty()){
                    Toast.makeText(getBaseContext(), "Debes seleccionar almenos 1 punto",
                            Toast.LENGTH_LONG).show();
                    return;
                }else{
                    Log.d("lala", "entre al boton");
                    if(voyovengo == 1) coords.add(PUCP);
                    PolylinesAT task = new PolylinesAT();
                    task.execute();
                }
                return;
            }
        });

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


        /*Gson gson = new Gson();
        _CP = gson.fromJson(getIntent().getStringExtra("CarPoolInfo"), CarPool.class);*/

        updateui();


    }

    private void updateui() {
       /* _nom_dest.setText(_CP.getNomDestino());
        _nom_org.setText(_CP.getNomOrigen());
        _dist_org.setText(_CP.getDistOrigen());
        _dist_dest.setText(_CP.getDistDestino());
        _hsal.setText(_CP.getHsalida());
        _precio.setText("S/"+_CP.getCosto());

        int asientos = _CP.getNasientos();
        _nasien.setText(String.valueOf(asientos));*/


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

        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {

            @Override
            public void onMapClick(LatLng arg0) {

                /*if(coords.isEmpty() && voyovengo==0){
                    mMap.addMarker(new MarkerOptions().position(arg0).title("ORIGEN"));
                }else{
                    mMap.addMarker(new MarkerOptions().position(arg0));
                }*/

                mMap.addMarker(new MarkerOptions().position(arg0));
                coords.add(arg0);
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                // Retrieve the data from the marker.
                LatLng aux = marker.getPosition();

                for (int i=0; i<coords.size(); i++) {
                    LatLng aux2 = coords.get(i);

                    if((aux2.latitude == aux.latitude) && (aux2.longitude == aux.longitude) ){
                        coords.remove(i);
                        break;
                    }

                }
                marker.remove();

                // Return false to indicate that we have not consumed the event and that we wish
                // for the default behavior to occur (which is for the camera to move such that the
                // marker is centered and for the marker's info window to open, if it has one).
                return false;
            }

        });

        if(voyovengo == 0){
            mMap.addMarker(new MarkerOptions().position(PUCP).title("ORIGEN"));
            coords.add(PUCP);
        }else{
            mMap.addMarker(new MarkerOptions().position(PUCP).title("DESTINO"));
        }



    }

    public void updateMap(String polyline){
        List<LatLng> coordenadas = decodePolyLines(polyline);

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(coordenadas);
        lineOptions.width(10);
        lineOptions.color(Color.BLUE);
        lineOptions.geodesic(true);

        mMap.addPolyline(lineOptions);

        mMap.moveCamera(CameraUpdateFactory.newLatLng(coords.get(0)));
        mMap.animateCamera(CameraUpdateFactory.zoomIn());
        mMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);

        button.setEnabled(true);

        return;
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


    private String formUrl(){
        String destino = "&destination=";
        final String key = "&key=AIzaSyD_cCfO3657tosUzJXKaNQMSfILJn9vTCw";
        String wp = "&waypoints=";
        String url = "http://maps.googleapis.com/maps/api/directions/json?origin=";

        if(voyovengo == 1){
            url = url + coords.get(0).latitude + "," + coords.get(0).longitude;
            destino = destino + PUCP.latitude + "," + PUCP.longitude;

            for(int i=1; i<coords.size(); i++){
                wp = wp + coords.get(i).latitude + "," +coords.get(i).longitude + "|";
            }
            wp = wp.substring(0, wp.length() - 1);

        }else{
            url = url + PUCP.latitude + "," + PUCP.longitude;
            destino = destino + coords.get(coords.size()-1).latitude + "," + coords.get(coords.size()-1).longitude;

            for (int i=0; i<coords.size()-1; i++){
                wp = wp + coords.get(i).latitude + "," +coords.get(i).longitude + "|";
            }
            wp = wp.substring(0, wp.length() - 1);
        }


        url = url + destino + wp;// + key;


        return url;
    }

    //########################################################3
    //Ya tu sabe que es esto 1 2 3 4 mr worldwide
    private class PolylinesAT extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){
            String formedurl = formUrl();

            URL url = UrlUtils.createUrl(formedurl);

            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(url);
                Log.d(TAG, "entre a doinbackground");
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            if(jsonResponse.isEmpty()){
                Log.d(TAG, "jasonaasodi");
            }
            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String userinfoJSON){

            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(userinfoJSON)) {

                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(userinfoJSON);
                String codigo = baseJsonResponse.getString("status");

                if (codigo.equalsIgnoreCase("OK")) {

                    /*String id = baseJsonResponse.getString("idUsuario");
                    String nombre = baseJsonResponse.getString("nombre");
                    //Log.d(TAG, nombre);
                    String apellido = baseJsonResponse.getString("apellido");
                    String telefono = baseJsonResponse.getString("telefono");
                    String correo = baseJsonResponse.getString("correo");*/

                    JSONArray routes = baseJsonResponse.getJSONArray("routes");
                    JSONObject r = routes.getJSONObject(0);
                    JSONObject poly = r.getJSONObject("overview_polyline");
                    String line = poly.getString("points");


                    //Log.d(TAG, loguser.getNombre()+" "+loguser.getCorreo());
                    Log.d(TAG, "creacion de usuario correcta");

                    updateMap(line);

                    return;

                }else{

                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }

        }

    }

}

