package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.ThreadLocalRandom;

import butterknife.Bind;
import butterknife.ButterKnife;

public class activity_emailverification extends AppCompatActivity {

    @Bind(R.id.btn_signup) Button _signupbutton;
    @Bind(R.id.ver_code) EditText _vercode;
    @Bind(R.id.email_veriTV) TextView _emailveri;

    private static final String TAG = "EmailVerActivity";

    private final String _baseVCurl = "http://200.16.7.170/api/users/registro/verificacion/";
    private final String _baseREurl = "http://200.16.7.170/api/users/registro/agregar_usuario/";

    private int _covv;

    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_emailverification);
        ButterKnife.bind(this);

        Gson gson = new Gson();
        user = gson.fromJson(getIntent().getStringExtra("newUser"), User.class);

        RandomInt();

        _emailveri.setText(user.getCorreo());

        _signupbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verify();
            }
        });

        SendCode();

    }

    public void failmessage(int flag){

        //0: Error de usuario/contrasena
        //-1: Error de Red/base de datos
        //2: Error correo no es puc
        if(flag == 0){

            Snackbar.make(findViewById(R.id.loginview), "Error de mierda", Snackbar.LENGTH_SHORT).show();

        }
        if(flag == -1){
            Snackbar.make(findViewById(R.id.loginview), "Eror en base de datos", Snackbar.LENGTH_SHORT).show();

        }
        if(flag == 2){
            Snackbar.make(findViewById(R.id.loginview), "Campos llenados incorrectamente", Snackbar.LENGTH_SHORT).show();
        }
        if(flag == 3){
            Snackbar.make(findViewById(R.id.loginview), "No hay conexion o problemas en servidor", Snackbar.LENGTH_SHORT).show();
        }

    }


    public void SendCode(){
        Thread thread = new Thread() {
            @Override
            public void run() {
                //creacion del Json con los datos
                JSONObject jsonObject= new JSONObject();
                try {
                    jsonObject.put("correo", user.getCorreo());
                    jsonObject.put("codigo", String.valueOf(_covv));
                } catch (JSONException e) {
                    Log.e(TAG, "Problem creating JsonObject", e);
                    e.printStackTrace();
                }


                //conexion con bd
                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;
                String jsonResponse = "";
                try {
                    URL url = new URL(_baseVCurl);
                    String message = jsonObject.toString();
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout( 10000 /*milliseconds*/ );
                    conn.setConnectTimeout( 15000 /* milliseconds */ );
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
                    jsonResponse = UrlUtils.readFromStream(is);


                    try {
                        JSONObject baseJsonResponse = new JSONObject(jsonResponse);
                        int codigo = baseJsonResponse.getInt("codigo");

                        if (codigo == 1) {//enviocorrecto
                            Log.d(TAG, "envio de email correcto");
                        }
                    } catch (JSONException e) {
                        Log.e(TAG, "Problem parsing the JSON code results", e);
                    }


                } catch (IOException e) {
                    Log.e(TAG, "Problem retrieving the JSON results.", e);
                }
                finally {
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
        };

        thread.start();

    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void verify(){

        //Integer.parseInt(_vercode.getText().toString())).
        String auxcode = _vercode.getText().toString();

        if(auxcode.isEmpty()){
            _vercode.setError("Campo vacio");
            return;
        }

        int codeinput = Integer.parseInt(auxcode);



        if(_covv == codeinput){

            //TODO: se envia el registro a la base de datos,
            RegistrationAT task = new RegistrationAT();
            task.execute();


        }
        else{
            _vercode.setError("Codigo invalido");
            return;
        }
    }

    public void SaveSession(){

        //Gson gson = new Gson();
        //User user = gson.fromJson(getIntent().getStringExtra("newUser"), User.class);

        String userinfo = getIntent().getStringExtra("newUser");

        SharedPreferences.Editor editor = getSharedPreferences("SessionToken", MODE_PRIVATE).edit();
        editor.putString("SessionUser", userinfo);
        editor.putInt("SessionState", 1);
        editor.apply();
    }

    public void RandomInt(){
        _covv = ThreadLocalRandom.current().nextInt(111111, 999997 + 1);

    }


    //#################################################################33
    //AsyncTask para mandar correo y enviar registro

    private class RegistrationAT extends AsyncTask<URL, Void, String> {

        @Override
        protected String doInBackground(URL... urls){

            JSONObject jsonObject= new JSONObject();
            try {
                jsonObject.put("correo", user.getCorreo());
                jsonObject.put("password", user.getPassword());
                jsonObject.put("nombre", user.getNombre());
                jsonObject.put("apellido", user.getApellido());
                jsonObject.put("telefono", user.getTelefono());
            } catch (JSONException e) {
                Log.e(TAG, "Problem creating JsonObject", e);
                e.printStackTrace();
            }

            OutputStream os = null;
            InputStream is = null;
            HttpURLConnection conn = null;
            String jsonResponse = "";
            try {
                URL url = new URL(_baseREurl);
                String message = jsonObject.toString();
                conn = (HttpURLConnection) url.openConnection();
                conn.setReadTimeout( 10000 /*milliseconds*/ );
                conn.setConnectTimeout( 15000 /* milliseconds */ );
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
                jsonResponse = UrlUtils.readFromStream(is);



            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object

            //TODO:PARSEAR RESPUESTA JSON A OBJETO USANDO GSON;
            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            //Gson gson = new Gson();
            //User user = gson.fromJson(jsonResponse, User.class);

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String userinfoJSON){

            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(userinfoJSON)) {
                failmessage(3);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(userinfoJSON);
                int codigo = baseJsonResponse.getInt("codigo");

                if (codigo == 1) {//registro correcto
                    SaveSession();
                    setResult(RESULT_OK, null);
                    finish();
                    Log.d(TAG, "Registro correcto");

                    return;

                }else{
                    failmessage(codigo);
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }

        }

    }


}
