package com.example.jose.carpool;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.AsyncTask;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import butterknife.Bind;
import butterknife.ButterKnife;



public class RegistrarPool extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "RegistrarPool";


    private GoogleMap mMap;
    private SlidingUpPanelLayout slidingLayout;


    private List<LatLng> coords = new ArrayList<>();
    private List<routeHash> mRouteHash = new ArrayList<>();


    private int voyovengo = 0; //flag para saber si va o viene a cato: 0 se va y 1 viene.
    private LatLng PUCP = new LatLng(-12.06902418, -77.07927883);



    //Butterknife injection
    @Bind(R.id.spinner) Spinner _spinnerorigen;
    @Bind(R.id.floatinbtn) FloatingActionButton button;
    @Bind(R.id.delbtn) FloatingActionButton delbtn;
    @Bind(R.id.arrow) ImageView _arrow;
    @Bind(R.id.timetext) TextView timetxt;//de aca saco la hora
    @Bind(R.id.time) ImageView clock;
    @Bind(R.id.spinnerCarro) Spinner _spinnercarro;//de aca saco la placa cuando la necesite
    @Bind(R.id.spinnerDia) Spinner _spinnerdia;//de aca saco el dia
    @Bind(R.id.OrigenET) EditText _OrigenET;
    @Bind(R.id.DestinoET) EditText _DestinoET;
    @Bind(R.id.btn_crearCP) Button _crearCPBTN;
    @Bind(R.id.crearCPPB) ProgressBar _crearCP;
    @Bind(R.id.PrecioET) EditText _precioET;
    @Bind(R.id.SeatsN) TextView _seatsN;





    //variables del carpool
    private List<Vehiculo> vehiculos = new ArrayList<>();
    private Calendar fechaHoy = Calendar.getInstance(TimeZone.getTimeZone("GMT-5:00"));
    private String finroute = "";
    private Vehiculo finv;
    private String _userid;
    private CarPool _myPool;
    private String _hoy;
    private String _manana;
    private List<String> _dists = new ArrayList<>();

    //auxiliare de mierda
    private String hoyaux;
    private String mananaaux;

    //traducciones meses
    private String[] monthsname = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    private String[] daysname = {"Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrar_pool);
        ButterKnife.bind(this);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //Log.d(TAG, fechaHoy);

        _crearCPBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(!validate()){
                    return;
                }

                String auxplaca = _spinnercarro.getSelectedItem().toString();
                _crearCPBTN.setEnabled(true);
                _crearCP.setVisibility(View.VISIBLE);
                for(Vehiculo v : vehiculos){
                    if(v.getPlaca().equals(auxplaca)){
                        finv = v;
                        break;
                    }
                }
                //creo el objeto carpool
                String aux = "%02d:%02d";

                String horacrea = String.format(aux, fechaHoy.get(Calendar.HOUR_OF_DAY), fechaHoy.get(Calendar.MINUTE));

                String diapool = "";

                if(_spinnerdia.getSelectedItem().toString().equals(hoyaux)){
                    diapool = ""+_hoy;
                }else diapool = "" +_manana;

                _myPool = new CarPool(_userid,
                        finv.getID(),
                        finv.getAsientos(),
                        _precioET.getText().toString(),
                        _hoy,
                        diapool,
                        _OrigenET.getText().toString(),
                        _DestinoET.getText().toString(),
                        horacrea,
                        timetxt.getText().toString(),
                        finroute);


                //asyncTask con tokio

                GetDistritosAT task = new GetDistritosAT();
                task.execute();

            }
        });

        clock.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar mcurrentTime = Calendar.getInstance();
                int hour = mcurrentTime.get(Calendar.HOUR_OF_DAY);
                int minute = mcurrentTime.get(Calendar.MINUTE);
                TimePickerDialog mTimePicker;
                mTimePicker = new TimePickerDialog(RegistrarPool.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int selectedHour, int selectedMinute) {
                        String timeset = String.format("%02d", selectedHour)+":"+String.format("%02d", selectedMinute);
                        timetxt.setText(timeset);
                    }
                }, hour, minute, true);//Yes 24 hour time
                mTimePicker.show();

            }
        });


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

                    /*if(!finroute.isEmpty()){
                        mMap.clear();
                        coords.clear();
                        mRouteHash.clear();
                        mMap.addMarker(new MarkerOptions().position(PUCP).title("PUCP"));
                    }*/

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
                _arrow.setRotation(180*slideOffset);
            }

            @Override
            public void onPanelStateChanged(View panel, SlidingUpPanelLayout.PanelState previousState, SlidingUpPanelLayout.PanelState newState) {

            }

        });


        _spinnerorigen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String item = adapterView.getItemAtPosition(i).toString();

                if(item.equalsIgnoreCase("PUCP")){
                    voyovengo = 0;

                    _OrigenET.setText("PUCP");
                    _OrigenET.setEnabled(false);
                    _DestinoET.getText().clear();
                    _DestinoET.setHint("Destino");
                    _DestinoET.setEnabled(true);

                }else{
                    voyovengo = 1;

                    _DestinoET.setText("PUCP");
                    _DestinoET.setEnabled(false);
                    _OrigenET.getText().clear();
                    _OrigenET.setHint("Origen");
                    _OrigenET.setEnabled(true);
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        PopulateSpinnerVehiculos();
        PopulateSpinnerDia();

    }



    private boolean validate(){
        boolean validation = true;


        if(vehiculos.isEmpty()){
            Toast.makeText(getBaseContext(), "No cuentas con carros registrados!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(_DestinoET.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Ingresa el nombre del destino!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(_OrigenET.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Ingresa el nombre del Origen!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(_precioET.getText().toString().isEmpty()){
            Toast.makeText(getBaseContext(), "Ingresa el precio del servicio!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if(timetxt.equals("--:--")){
            Toast.makeText(getBaseContext(), "Selecciona la hora salida!", Toast.LENGTH_SHORT).show();
            return false;
        }

        String placaSelect = _spinnercarro.getSelectedItem().toString();
        int asientosDisp = 0;
        for(Vehiculo v : vehiculos){
            if(v.getPlaca().equalsIgnoreCase(placaSelect)){
                asientosDisp = v.getAsientos();
            }
        }

        if(Integer.parseInt(_seatsN.getText().toString()) > asientosDisp ){
            Toast.makeText(getBaseContext(), "No cuentas con tantos asientos!", Toast.LENGTH_SHORT).show();
            return false;
        }

        if(finroute.isEmpty()){
            Toast.makeText(getBaseContext(), "Debes seleccionar tu ruta!", Toast.LENGTH_SHORT).show();
            return false;
        }

        int h = fechaHoy.get(Calendar.HOUR_OF_DAY);
        int m = fechaHoy.get(Calendar.MINUTE);

        String[] info = timetxt.getText().toString().split(":");

        if(_spinnerdia.getSelectedItem().toString().equals(hoyaux)){

            if(Integer.parseInt(info[0]) < h){
                Toast.makeText(getBaseContext(), "Hora invalida", Toast.LENGTH_SHORT).show();
                return false;
            }
            else if((Integer.parseInt(info[0]) == h) && (Integer.parseInt(info[1]) < m)){
                Toast.makeText(getBaseContext(), "Hora invalida", Toast.LENGTH_SHORT).show();
                return false;
            }

        }


        return validation;
    }

    private void PopulateSpinnerVehiculos(){


        SharedPreferences prefs = getSharedPreferences("SessionToken", MODE_PRIVATE);
        String userJSON = prefs.getString("SessionUser", "");

        Gson gson = new Gson();
        User user = gson.fromJson(userJSON, User.class);

        _userid = user.getID();

        final String urlvehiculos = "http://200.16.7.170/api/vehiculos/obtener_vehiculos/"+_userid;
        Log.d(TAG, urlvehiculos);

        Thread task = new Thread(){
            @Override
            public void run(){


                URL url = UrlUtils.createUrl(urlvehiculos);

                String jsonResponse = "";
                try {
                    jsonResponse = UrlUtils.makeHttpRequestGet(url);
                } catch (IOException e) {
                    Log.e(TAG, "Problem making the HTTP request.", e);
                }


                try{
                    JSONArray carros = new JSONArray(jsonResponse);

                    for(int i=0; i<carros.length(); i++){
                        JSONObject carro = carros.getJSONObject(i);

                        String placa = carro.getString("placa");
                        int asientos = carro.getInt("numAsientos");
                        String id = carro.getString("idVehiculo");

                        vehiculos.add(new Vehiculo(placa, asientos, id));
                    }

                }catch (JSONException e){
                    Log.e(TAG, "Problem parsing the cars Json", e);
                }

                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        setSpinnerVADPTR();
                    }
                });

            }
        };

        task.start();

    }

    public void setSpinnerVADPTR(){

        List<String> SpinnerPlacas = new ArrayList<>();

        if(vehiculos.size() == 0){
            SpinnerPlacas.add("---");

        }else{
            for(Vehiculo v :vehiculos){
                SpinnerPlacas.add(v.getPlaca());
            }
        }

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, SpinnerPlacas); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnercarro.setAdapter(spinnerArrayAdapter);
    }

    private void PopulateSpinnerDia(){

        Thread task = new Thread(){
            @Override
            public void run(){

                List<String> days = new ArrayList<>();

                int dia = fechaHoy.get(Calendar.DAY_OF_WEEK);
                int ano = fechaHoy.get(Calendar.YEAR);
                int mes = fechaHoy.get(Calendar.MONTH);
                int num = fechaHoy.get(Calendar.DAY_OF_MONTH);

                fechaHoy.add(Calendar.DAY_OF_YEAR, 1);

                int diam = fechaHoy.get(Calendar.DAY_OF_WEEK);
                int anom = fechaHoy.get(Calendar.YEAR);
                int mesm = fechaHoy.get(Calendar.MONTH);
                int numm = fechaHoy.get(Calendar.DAY_OF_MONTH);

                days.add(daysname[dia-1] + ", " + monthsname[mes] + " " + num);
                days.add(daysname[diam-1] + ", " + monthsname[mesm] + " " + (numm));

                hoyaux = daysname[dia-1] + ", " + monthsname[mes] + " " + num;
                mananaaux = daysname[diam-1] + ", " + monthsname[mesm] + " " + (numm);

                String aux = "%04d-%02d-%02d";
                _hoy = String.format(aux, ano, mes+1, num);
                _manana = String.format(aux, anom, mesm+1, numm);

                setSpinnerDADPTR(days);

            }
        };

        task.start();

    }

    public void setSpinnerDADPTR(List<String> days){

        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, days); //selected item will look like a spinner set from XML
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        _spinnerdia.setAdapter(spinnerArrayAdapter);

    }

    public void UpdateSeats(View view){

        int clicked = view.getId();

        TextView seatsTV = (TextView) findViewById(R.id.SeatsN);
        String seats = seatsTV.getText().toString();
        int ns = Integer.parseInt(seats);

        //if(ns == 1 || ns == vehiculos.getAsientos()) return;

        if(clicked == R.id.PlusSeats) ns++;
        else ns--;

        seatsTV.setText(""+ns);

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
                    finroute = mRouteHash.get(polyclickflag).HashR;

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

            if(i == 0){
                lineOptions.color(Color.RED);
                finroute = polylines.get(i);
            }

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
        lineOptions.color(Color.GRAY);
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

                    Toast.makeText(getBaseContext(), "Error Google Maps", Toast.LENGTH_SHORT).show();
                    button.setEnabled(true);


                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the routes JSON results", e);
            }

        }

    }


    private class CrearCPAT extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){
            //creacion del Json con los datos


            JSONObject jsonObject= new JSONObject();
            try {

                jsonObject.put("idConductor", _myPool.getIDusuario());
                jsonObject.put("idVehiculo",_myPool.getIDvehiculo());
                jsonObject.put("costo",_myPool.getCosto());
                jsonObject.put("num_asientos",_myPool.getNasientos());
                jsonObject.put("fecha_creacion",_myPool.getFcreacion());
                jsonObject.put("hora_creacion",_myPool.getHcreacion());
                jsonObject.put("fecha_salida",_myPool.getFsalida());
                jsonObject.put("hora_salida",_myPool.getHsalida());
                jsonObject.put("distrito_origen",_dists.get(0));
                jsonObject.put("distrito_destino",_dists.get(_dists.size()-1));
                jsonObject.put("nombre_origen",_myPool.getNomOrigen());
                jsonObject.put("nombre_destino",_myPool.getNomDestino());
                jsonObject.put("rutaMapa",_myPool.getmRoute());

                JSONArray distritos = new JSONArray(_dists);

                jsonObject.put("distritos", distritos);

                System.out.println(jsonObject.toString());


            } catch (JSONException e) {
                Log.e(TAG, "Problem creating JsonObject", e);
                e.printStackTrace();
            }



            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestPost("http://200.16.7.170/api/pools/crear_pool", jsonObject);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                int codigo = baseJsonResponse.getInt("codigo");

                if (codigo == 1) {//enviocorrecto
                    Log.d(TAG, "creacion de CarPool correcta");
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the JSON code results", e);
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
                int codigo = baseJsonResponse.getInt("codigo");

                if (codigo == 1) {

                    finish();

                    return;

                }else{
                    Toast.makeText(getBaseContext(), "Error de cracion de pool", Toast.LENGTH_SHORT).show();
                    _crearCPBTN.setEnabled(true);
                    _crearCP.setVisibility(View.INVISIBLE);
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the JSON results", e);
            }

        }

    }

    private class GetDistritosAT extends AsyncTask<Void, Void, List<String>> {

        @Override
        protected List<String> doInBackground(Void... voids) {

            List<LatLng> cooraux= decodePolyLines(finroute);
            final String geocode_url = "https://maps.googleapis.com/maps/api/geocode/json?latlng=";

            List<String> dists = new ArrayList<>();

            int step = cooraux.size()/5;

            for(int i=0; i<cooraux.size(); i+=step){

                String jsonresponse = "";

                try{
                    jsonresponse = UrlUtils.makeHttpRequestGet(UrlUtils.createUrl(geocode_url + cooraux.get(i).latitude +","+cooraux.get(i).longitude));

                }catch(IOException e){
                    Log.e(TAG, "error formacion url");
                    e.printStackTrace();
                }

                if(!jsonresponse.isEmpty()){
                    //saco el distrito y lo meto al array
                    dists.add(getDistrito(jsonresponse));
                }

            }

            return dists;
        }

        @Override
        protected void onPostExecute(List<String> dists) {


            if(dists.isEmpty()){
                Toast.makeText(getBaseContext(), "Error obteniendo distritos", Toast.LENGTH_SHORT).show();
                return;
            }

            for(String s : dists){
                if(!_dists.contains(s)){
                    _dists.add(s);
                }
            }

            CrearCPAT task = new CrearCPAT();
            task.execute();

            return;

        }




        private String getDistrito(String json){
            String distrito = "";

            try{
                JSONObject object = new JSONObject(json);

                JSONArray results = object.getJSONArray("results");

                JSONObject data = results.getJSONObject(2);
                String address = data.getString("formatted_address");

                distrito = address.substring(0, address.indexOf(","));


            }catch (JSONException e){
                Log.e(TAG, "Problem parsing the JSON results", e);
            }

            return distrito;


        }
    }





}
