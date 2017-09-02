package com.example.jose.carpool;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.sothree.slidinguppanel.SlidingUpPanelLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class RegistrarPool extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "CPInfoActivity";


    private GoogleMap mMap;
    private SlidingUpPanelLayout slidingLayout;


    private List<LatLng> coords = new ArrayList<>();
    private List<routeHash> mRouteHash = new ArrayList<>();


    private int voyovengo = 0; //flag para saber si va o viene a cato: 0 se va y 1 viene.
    private LatLng PUCP = new LatLng(-12.06902418, -77.07927883);

    @Bind(R.id.spinner) Spinner _spinner;
    @Bind(R.id.floatinbtn) FloatingActionButton button;
    @Bind(R.id.delbtn) FloatingActionButton delbtn;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_pool);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                button.setEnabled(false);

                if(coords.isEmpty()){
                    Toast.makeText(getBaseContext(), "Debes seleccionar tu destino u origen",
                            Toast.LENGTH_LONG).show();

                    button.setEnabled(true);
                    return;
                }else{
                    Log.d("lala", "entre al boton");
                    PolylinesAT task = new PolylinesAT();
                    task.execute();

                }
                return;
            }
        });

        delbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mMap.clear();
                coords.clear();
                mRouteHash.clear();
                mMap.addMarker(new MarkerOptions().position(PUCP).title("PUCP"));

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


        _spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                if(item.equalsIgnoreCase("PUCP")){
                    voyovengo = 0;
                }else{
                    voyovengo = 1;
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        updateui();


    }

    private void updateui() {

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

                int polyclickflag = polylineClick(arg0);
                if(polyclickflag != -1){
                    Toast.makeText(getBaseContext(), "nueva ruta seleccionada", Toast.LENGTH_SHORT).show();

                    for(routeHash rh : mRouteHash){
                        rh.UnsetActive();
                    }

                    mRouteHash.get(polyclickflag).SetActive();

                    mMap.clear();
                    for(LatLng coord : coords){
                        mMap.addMarker(new MarkerOptions().position(coord));
                    }

                    for(int i=0; i<mRouteHash.size(); i++){

                        if(!mRouteHash.get(i).isActive()){

                            PolylineOptions lineOptions = GetPolyOptions(mRouteHash.get(i).HashR);
                            mMap.addPolyline(lineOptions);

                        }

                    }
                    PolylineOptions lineOptions = GetPolyOptions(mRouteHash.get(polyclickflag).HashR);
                    lineOptions.color(Color.RED);

                    mMap.addPolyline(lineOptions);


                }else{
                    mMap.addMarker(new MarkerOptions().position(arg0));
                    coords.add(arg0);

                }

            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(final Marker marker) {

                LatLng aux = marker.getPosition();

                for (int i=0; i<coords.size(); i++) {
                    LatLng aux2 = coords.get(i);

                    if((aux2.latitude == aux.latitude) && (aux2.longitude == aux.longitude) ){
                        coords.remove(i);
                        Toast.makeText(getBaseContext(), "Marcador Eliminado", Toast.LENGTH_SHORT).show();
                        break;
                    }

                }
                marker.remove();

                return false;
            }

        });

        mMap.addMarker(new MarkerOptions().position(PUCP).title("PUCP"));

        mMap.moveCamera(CameraUpdateFactory.newLatLng(PUCP));
        googleMap.animateCamera(CameraUpdateFactory.zoomIn());
        googleMap.animateCamera(CameraUpdateFactory.zoomTo(12), 2000, null);


    }

    private class routeHash{
        public String HashR;
        public int HashActive;

        public routeHash(PolylineOptions PO, String R){
            HashR = R;
            HashActive = 0;
        }

        public boolean isActive(){
            return HashActive == 1;
        }

        public void SetActive(){
            HashActive = 1;
        }

        public void UnsetActive(){
            HashActive = 0;
        }
    }

    private int polylineClick(LatLng click){

        for(int i=0; i<mRouteHash.size(); i++){
            PolylineOptions polyline = GetPolyOptions(mRouteHash.get(i).HashR);


            for(LatLng polycoords : polyline.getPoints()){
                float[] results = new float[1];
                Location.distanceBetween(click.latitude, click.longitude, polycoords.latitude, polycoords.longitude, results);

                if(results[0] < 120){//Margen de error en distancia para seleccionar ruta
                    return i;
                }
            }

        }

        return -1;
    }


    public void updateMap(List<String> polylines){



        for(int i=polylines.size()-1; i>=0; i--){

            PolylineOptions lineOptions = GetPolyOptions(polylines.get(i));

            if(i == 0) lineOptions.color(Color.RED);

            mMap.addPolyline(lineOptions);

            routeHash newroutehash = new routeHash(lineOptions, polylines.get(i));
            if(i == 0) newroutehash.SetActive();
            mRouteHash.add(newroutehash);
        }

        button.setEnabled(true);

        return;
    }

    public PolylineOptions GetPolyOptions(String route){

        List<LatLng> coordenadas = decodePolyLines(route);

        PolylineOptions lineOptions = new PolylineOptions();
        lineOptions.addAll(coordenadas);
        lineOptions.width(15);
        lineOptions.color(Color.DKGRAY);
        lineOptions.geodesic(true);

        return lineOptions;
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


        url = url + destino + wp + "&alternatives=true";// + key;


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

                    JSONArray routes = baseJsonResponse.getJSONArray("routes");

                    List<String> polylines = new ArrayList<>();

                    for(int i=0; i<routes.length(); i++){
                        JSONObject r = routes.getJSONObject(i);
                        JSONObject poly = r.getJSONObject("overview_polyline");
                        polylines.add(poly.getString("points"));

                    }

                    Log.d(TAG, "Se obtuvo info de rutas");

                    if(routes.length() == 0){
                        Toast.makeText(getBaseContext(), "No hay rutas disponibles", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if(routes.length() == 1) Toast.makeText(getBaseContext(), "Se obtuvo " + routes.length() + " ruta", Toast.LENGTH_SHORT).show();
                    else Toast.makeText(getBaseContext(), "Se obtuvieron " + routes.length() + " rutas", Toast.LENGTH_SHORT).show();

                    updateMap(polylines);

                    return;

                }else{

                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the routes JSON results", e);
            }

        }

    }

}
