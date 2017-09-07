package com.example.jose.carpool;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by jose on 9/7/17.
 */

public class fragment_pendiente extends Fragment{

    @Bind(R.id.nom_org) TextView _nom_org;
    @Bind(R.id.nom_dest) TextView _nom_dest;
    @Bind(R.id.dist_org) TextView _dist_org;
    @Bind(R.id.dist_dest) TextView _dist_dest;
    @Bind(R.id.infohora) TextView _hsal;
    @Bind(R.id.infofecha) TextView _fsal;
    @Bind(R.id.precio) TextView _precio;
    @Bind(R.id.infonombre) TextView _nombre;
    @Bind(R.id.infoapellido) TextView _apellido;
    @Bind(R.id.infomodelo) TextView _modelo;
    @Bind(R.id.infoplaca) TextView _placa;
    @Bind(R.id.infoconductorpic) ImageView _condpic;
    @Bind(R.id.infocarropic) ImageView _carpic;
    @Bind(R.id.telefCond) TextView _telefcond;

    private static final String TAG = "PendienteFragment";

    private String[] monthsname = {"Ene", "Feb", "Mar", "Abr", "May", "Jun", "Jul", "Ago", "Sep", "Oct", "Nov", "Dic"};
    private String[] daysname = {"Dom", "Lun", "Mar", "Mie", "Jue", "Vie", "Sab"};


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);

        Log.d(TAG, "creacion del fragment de pendientes");
        View view = inflater.inflate(R.layout.fragment_pendiente, container, false);
        ButterKnife.bind(this, view);

        GetPendienteAT task = new GetPendienteAT();
        task.execute();


        return view;
    }

    public void UpdateUI(JSONObject _myObj ){

        try{

            String aux = "%.2f";
            String auxCost = String.format(aux, Float.parseFloat(_myObj.getString("costo")));
            _precio.setText(auxCost);

            _nom_dest.setText(_myObj.getString("nombre_destino"));
            _nom_org.setText(_myObj.getString("nombre_origen"));
            _dist_org.setText(_myObj.getString("distrito_origen"));
            _dist_dest.setText(_myObj.getString("distrito_destino"));
            _hsal.setText(_myObj.getString("hora_salida").substring(0, _myObj.getString("hora_salida").length()-3));
            _nombre.setText(_myObj.getString("nombre"));
            _apellido.setText(_myObj.getString("apellido"));
            _modelo.setText(_myObj.getString("modelo") + ", ");//_myObj.getString("marca"));
            _placa.setText(_myObj.getString("placa"));
            _fsal.setText(formatDate(_myObj.getString("fecha_salida")));
            _telefcond.setText(_myObj.getString("telefono"));

            Glide.with(getActivity())
                    .load(_myObj.getString("imgPerfil"))
                    .placeholder(R.drawable.ic_menu_foto_56dp)
                    .error(R.drawable.ic_menu_foto_56dp)
                    .into(_condpic);

            Glide.with(getActivity())
                    .load(_myObj.getString("imgAuto"))
                    .placeholder(R.drawable.ic_sentiment_satisfied_black_24dp)
                    .error(R.drawable.ic_sentiment_satisfied_black_24dp)
                    .into(_carpic);



        }catch(JSONException e){
            e.printStackTrace();
        }

        LinearLayout root = (LinearLayout) getActivity().findViewById(R.id.rootlinear);
        root.setVisibility(View.VISIBLE);

    }

    public String formatDate(String fecha){
        String[] tokens = fecha.split("-");

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH);
        try{
            cal.setTime(sdf.parse(fecha));
        }catch(ParseException e){
            e.printStackTrace();
        }

        return daysname[cal.get(Calendar.DAY_OF_WEEK)-1] + ", " + monthsname[cal.get(Calendar.MONTH)+1] + " " + cal.get(Calendar.DAY_OF_MONTH);

    }


    private class GetPendienteAT extends AsyncTask<Void, Void, String> {
        @Override
        protected String doInBackground(Void... voids) {
            Log.d(TAG, "entre a do in background");

            SharedPreferences prefs = getActivity().getSharedPreferences("SessionToken", MODE_PRIVATE);
            String userJSON = prefs.getString("SessionUser", "");

            Gson gson = new Gson();
            User user = gson.fromJson(userJSON, User.class);

            String userid = user.getID();


            String jsonresponse = "";

            URL url = UrlUtils.createUrl("http://200.16.7.170/api/pools/obtener_pools_pasajero/" + userid + "/0");
            Log.d(TAG, "http://200.16.7.170/api/pools/obtener_pools_pasajero/" + userid + "/0");
            try{
                jsonresponse = UrlUtils.makeHttpRequestGet(url);
            }catch (IOException e){
                e.printStackTrace();
                jsonresponse = "Error";
            }
            return jsonresponse;
        }

        @Override
        protected void onPostExecute(String s) {

            Log.d(TAG, "entre a post");

            if(s.equals("Error")){
                Toast.makeText(getActivity(), "Error de conexion", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "error");
                return;
            }

            if(s.isEmpty()){
                Toast.makeText(getActivity(), "No tienes viajes pendientes", Toast.LENGTH_SHORT).show();
                Log.d(TAG, "json vacio");
                return;
            }

            System.out.println(s);
            JSONObject jsonObject;
            try{
                JSONArray array = new JSONArray(s);
                jsonObject = array.getJSONObject(0);

            }catch (JSONException e){
                e.printStackTrace();
                Log.d(TAG, "error creacion json");
                return;
            }

            Log.d(TAG, "obtuve data");
            UpdateUI(jsonObject);

        }
    }
}
