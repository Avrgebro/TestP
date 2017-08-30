package com.example.jose.carpool;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatButton;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


/**
 * Created by Johnny on 26/08/2017.
 */

public class tus_vehiculos extends Fragment {
    private static final String TAG = "VehiculoFragment";
    private static final String _baseVehiculo = "http://200.16.7.170/api/vehiculos/obtener_vehiculos/" + MainActivity.user.getID().toString();
    private static String _baseDelVehi = "http://200.16.7.170/api/vehiculos/eliminar_vehiculo/";
    private ArrayList<Vehiculo> lstVehi;
    private ListView listView;
    private SwipeRefreshLayout swipeView;
    private int auxI;

    TextInputLayout textInputplaca;
    TextInputLayout textInputmodelo;
    TextInputLayout textInputmarca;
    TextInputLayout textInputcolor;
    TextInputLayout textInputnasientos;


    EditText txtPlaca;
    EditText txtModelo;
    EditText txtMarca;
    EditText txtColor;
    EditText txtNasientos;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file
        View view = inflater.inflate(R.layout.fragment_vehiculos,container,false);
        listView = (ListView) view.findViewById(R.id.listViewV);

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.strlayoutVehi);

        swipeView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeView.setRefreshing(true);
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    public void run() {
                        // Actions to do after 10 seconds
                        //Toast.makeText(getActivity(), "Refresh",
                        //Toast.LENGTH_LONG).show();
                        lstVehi.clear();
                        VehiculosAT task = new VehiculosAT();
                        task.execute();


                    }
                }, 3000);
            }
        });
        if(lstVehi == null || lstVehi.isEmpty()){
            if(lstVehi == null) lstVehi = new ArrayList<Vehiculo>();
            //asyncTask para obtener pools
            VehiculosAT task = new VehiculosAT();
            task.execute();

        }


        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int posicion=i;
                createAndShowAlertDialog(i);
                return false;
            }
        });

        return view;

    }


    private void createAndShowAlertDialog(final int i) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Eliminar vehiculo");
        builder.setMessage("¿Seguro que quieres eliminar el vehiculo de placa ?" + lstVehi.get(i).getPlaca());
        builder.setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                EliminarVehiculo ee = new EliminarVehiculo();
                auxI = i;
                ee.execute();
                dialog.dismiss();
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //TODO
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }


    private class EliminarVehiculo extends   AsyncTask<URL, Void, String> {
        @Override
        protected String doInBackground(URL... urls){
            _baseDelVehi = _baseDelVehi + lstVehi.get(auxI).getIdVehiculo();

            String formedurl = _baseDelVehi;

            URL url = UrlUtils.createUrl(formedurl);
            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(url); ///////
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }
            Gson gson = new Gson();
            User user = gson.fromJson(jsonResponse, User.class);

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String userinfoJSON){
            if (TextUtils.isEmpty(userinfoJSON)) {
                Log.d(TAG, "Json vacio");
                return;
            }
            try {
                JSONObject baseJsonResponse = new JSONObject(userinfoJSON);
                int codigo = baseJsonResponse.getInt("codigo");
                if (codigo == 1) {
                    String mensaje = baseJsonResponse.getString("mensaje");
                    Log.d(TAG, mensaje);
                    UpdateUI();
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    private void UpdateUI(){
        vehiculo_adapter cpAdptr = new vehiculo_adapter(getActivity(), lstVehi);

        listView.setAdapter(cpAdptr);
        swipeView.setRefreshing(false);

        return;
    }


    private class VehiculosAT extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){

            URL CPurl = UrlUtils.createUrl(_baseVehiculo);
            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(CPurl);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            //Toast.makeText(getActivity(), jsonResponse,
            //Toast.LENGTH_LONG).show();

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String CPinfoJSON){

            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(CPinfoJSON)) {
                //failmessage(1);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONArray vehiArray = new JSONArray(CPinfoJSON);
                Log.d(TAG, "se obtuviero los vehiculos de la db");
                for(int i=0; i<vehiArray.length(); i++){
                    JSONObject pool = vehiArray.getJSONObject(i);

                    int IDusuario = pool.getInt("idUsuario");
                    String Placa = pool.getString("placa");
                    String Modelo = pool.getString("modelo");
                    String Marca = pool.getString("marca");
                    String Color = pool.getString("color");
                    int Numasientos = pool.getInt("numAsientos");
                    boolean Estado = pool.getBoolean("estado");
                    String Imagen = pool.getString("img");
                    int IDvehiculo = pool.getInt("idVehiculo");

                    Vehiculo auxVehiculo = new Vehiculo(Integer.toString(IDvehiculo),Placa,Modelo,Marca,Color,Integer.toString(Numasientos));
                    lstVehi.add(auxVehiculo);
                }

                UpdateUI();
                return;
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }

        }

    }





    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");



        ImageView addCar = (ImageView) getView().findViewById(R.id.btnAgregarVehiculo);
        addCar.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {


                    final Dialog dialog = new Dialog(getActivity());
                    dialog.setContentView(R.layout.dialog_addcar);
                    dialog.setTitle("Perfil");
                    dialog.show();


                final AppCompatButton btnAdd= (AppCompatButton)dialog.findViewById(R.id.btn_AddCar);
                btnAdd.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //textinputNombre = (TextInputLayout) getView().findViewById(R.id.textNombre);
                        //txtNombre = textinputNombre.getEditText();

                        textInputplaca = dialog.findViewById(R.id.txtInputPlaca);
                        textInputmodelo = dialog.findViewById(R.id.txtInputModelo);
                        textInputmarca = dialog.findViewById(R.id.txtInputMarca);
                        textInputcolor = dialog.findViewById(R.id.txtInputColor);
                        textInputnasientos = dialog.findViewById(R.id.txtInputNasientos);

                        txtPlaca = textInputplaca.getEditText();
                        txtModelo = textInputmodelo.getEditText();
                        txtMarca = textInputmarca.getEditText();
                        txtColor = textInputcolor.getEditText();
                        txtNasientos = textInputnasientos.getEditText();

                        new Thread(new Runnable() {
                            @Override
                            public void run() {


                                JSONObject jsonObject = new JSONObject();
                                try {
                                    jsonObject.put("idUsuario", Integer.parseInt(MainActivity.user.getID()));
                                    jsonObject.put("placa", txtPlaca.getText());
                                    jsonObject.put("modelo", txtModelo.getText());
                                    jsonObject.put("marca", txtMarca.getText());
                                    jsonObject.put("color", txtColor.getText());
                                    jsonObject.put("nasientos", Integer.parseInt(txtNasientos.getText().toString()));
                                    jsonObject.put("img", "0");
                                } catch (JSONException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }

                                String BaseURL = "http://200.16.7.170/api/vehiculos/agregar_vehiculo";//solo se agrega "correo/pass"
                                OutputStream os = null;
                                InputStream is = null;
                                HttpURLConnection conn = null;
                                try {
                                    //constants
                                    URL url = new URL(BaseURL);
                                    String message = jsonObject.toString();
                                    conn = (HttpURLConnection) url.openConnection();
                                    conn.setReadTimeout(10000 /*milliseconds*/);
                                    conn.setConnectTimeout(15000 /* milliseconds */);
                                    conn.setRequestMethod("POST");
                                    conn.setDoInput(true);
                                    conn.setDoOutput(true);
                                    conn.setFixedLengthStreamingMode(message.getBytes().length);

                                    //make some HTTP header nicety
                                    conn.setRequestProperty("Content-Type", "application/json;charset=utf-8");
                                    conn.setRequestProperty("X-Requested-With", "XMLHttpRequest");

                                    //open
                                    conn.connect();

                                    //setup send
                                    os = new BufferedOutputStream(conn.getOutputStream());
                                    os.write(message.getBytes());
                                    //clean up
                                    os.flush();

                                    //do somehting with response
                                    is = conn.getInputStream();
                                    //String contentAsString = readIt(is,len);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    //clean up
                                    try {
                                        os.close();
                                        is.close();
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    conn.disconnect();
                                }
                            }
                        }).start();
                        dialog.dismiss();
                    }
                });
            }
            });
    }


}
