package com.example.jose.carpool;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jose on 8/28/17.
 */

public class pool_fragment extends Fragment {

    private static final String TAG = "CarPoolFragment";
    private static final String _baseCPurl = "http://200.16.7.170/api/pools/obtener_pools";
    private ListView lv;
    private ArrayList<CarPool> pools;
    private SwipeRefreshLayout swipeView;
    //private CarPoolAdapter cpAdptr;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //return super.onCreateView(inflater, container, savedInstanceState);
        Log.d(TAG, "creacion del fragment de pools");
        View view = inflater.inflate(R.layout.pool_fragment, container, false);

        lv = (ListView) view.findViewById(R.id.pool_list);

        swipeView = (SwipeRefreshLayout) view.findViewById(R.id.strlayout);

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
                        pools.clear();
                        PoolsAT task = new PoolsAT();
                        task.execute();


                    }
                }, 1000);
            }
        });

        FloatingActionButton newPoolbtn = (FloatingActionButton) view.findViewById(R.id.NewPoolBtn);
        newPoolbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //intent para nuevo pool
                Intent intent = new Intent(getActivity(), RegistrarPool.class);
                startActivity(intent);
            }
        });

        if(pools == null || pools.isEmpty()){
            if(pools == null) pools = new ArrayList<CarPool>();
            //asyncTask para obtener pools
            PoolsAT task = new PoolsAT();
            task.execute();

        }
        return view;



    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }


    private void failmessage(int flag){
        if(flag == 1){
            Toast.makeText(getActivity(), "Error de conexion",
                    Toast.LENGTH_LONG).show();

        }
        else{
            Toast.makeText(getActivity(), "que else!",
                    Toast.LENGTH_LONG).show();
        }
    }

    private void UpdateUI(){
        final CarPoolAdapter cpAdptr = new CarPoolAdapter(getActivity(), pools);

        lv.setAdapter(cpAdptr);
        swipeView.setRefreshing(false);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener(){

            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l){
                CarPool curCP = cpAdptr.getItem(position);

                Gson gson = new Gson();

                String jsonInString = gson.toJson(curCP);

                //Intent para abrir la ventana de info del carpool

                Intent CPInf = new Intent(getActivity(), PoolInfoScreen.class);
                CPInf.putExtra("CarPoolInfo", jsonInString);
                startActivity(CPInf);


            }
        });

        return;
    }

    //###################################################
    //AsyncTask para obtener pools

    private class PoolsAT extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){

            URL CPurl = UrlUtils.createUrl(_baseCPurl);
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
                failmessage(1);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONArray poolArray = new JSONArray(CPinfoJSON);
                Log.d(TAG, "se obtuvieron pools de la db");
                for(int i=0; i<poolArray.length(); i++){
                    JSONObject pool = poolArray.getJSONObject(i);

                    String IDusuario = pool.getString("idUsuario");
                    String IDvehiculo = pool.getString("idVehiculo");
                    int Nasientos = pool.getInt("num_asientos");
                    String Costo = pool.getString("costo");
                    String Fcreacion = pool.getString("fecha_creacion");
                    String Fsalida = pool.getString("fecha_salida");
                    String NomOrigen = pool.getString("nombre_origen");
                    String DistOrigen = pool.getString("distrito_origen");
                    String DirOrigen = pool.getString("direccion_origen");
                    String NomDestino = pool.getString("nombre_destino");
                    String DistDestino = pool.getString("distrito_destino");
                    String DirDestino = pool.getString("direccion_destino");
                    int Estado = pool.getInt("estado");
                    String IDservicio = pool.getString("idServicio");
                    String Hcreacion = pool.getString("hora_creacion");
                    String Hsalida = pool.getString("hora_salida");
                    String ruta = pool.getString("rutaMapa");

                    CarPool auxPool = new CarPool(IDusuario, IDvehiculo, Nasientos, Costo, Fcreacion, Fsalida, NomOrigen, DistOrigen, DirOrigen, NomDestino, DistDestino, DirDestino, Estado, IDservicio, Hcreacion, Hsalida, ruta);
                    auxPool.setmJson(pool);
                    pools.add(auxPool);
                }

                UpdateUI();
                return;
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }

        }

    }
}
