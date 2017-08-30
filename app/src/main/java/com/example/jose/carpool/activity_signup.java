package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.Bind;
import butterknife.ButterKnife;

public class activity_signup extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int FINISH_SIGNAL = 0;
    private static final String _baseEUurl ="http://200.16.7.170/api/users/usuario_existe/";

    //private int _flag = 0;
    private User usuarioRegistro;

    @Bind(R.id.name_signupet) EditText _nombreET;
    @Bind(R.id.ap_signupet) EditText _apET;
    @Bind(R.id.telf_signupet) EditText _telET;
    @Bind(R.id.email_signupet) EditText _emailET;
    @Bind(R.id.pass_signupet) EditText _passET;
    @Bind(R.id.pass2_signupet) EditText _pass2ET;

    @Bind(R.id.btn_emailver) Button _emailverbutton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        ButterKnife.bind(this);

        _emailverbutton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                verification();
            }
        });
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        //moveTaskToBack(true);
        Intent intent = new Intent(getApplicationContext(), activity_login.class);
        startActivityForResult(intent, 0);
        finish();
        overridePendingTransition(R.anim.push_right_in, R.anim.push_right_out);
    }


    public void verification(){//verificar los campos y crear el usuario
        String nombre = _nombreET.getText().toString();
        String ap = _apET.getText().toString();
        String telf = _telET.getText().toString();
        String email = _emailET.getText().toString();
        String passw = _passET.getText().toString();
        String passw2 = _pass2ET.getText().toString();

        if(!validate()){
            return;
        }


        usuarioRegistro = new User("-1", email, nombre, ap, telf, passw);

        //aca hago la llamada al asynctask.
        String[] correoparam = {email};
        ExisteAT task = new ExisteAT();
        task.execute(correoparam);


        /*if(_flag == 1){
            //populate new user
            usuarioRegistro = new User("-1", email, nombre, ap, telf, passw);

            //create Json from new user
            Gson gson = new Gson();
            String json = gson.toJson(usuarioRegistro);


            //send new user to emailverification activity
            Intent intent = new Intent(getApplicationContext(), activity_emailverification.class);
            intent.putExtra("newUser", json);
            startActivityForResult(intent, FINISH_SIGNAL);
            overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        }else{
            Snackbar.make(findViewById(R.id.signupview), "Baia Baia", Snackbar.LENGTH_SHORT).show();
        }*/
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FINISH_SIGNAL) {
            if (resultCode == RESULT_OK) {

                setResult(RESULT_OK, null);
                Log.d(TAG, "obtuve resultado de email ver");
                this.finish();
                //this.finish();
            }
        }
    }

    public boolean validate(){
        boolean emptyfield = false;
        boolean valid = true;

        String nombre = _nombreET.getText().toString();
        String ap = _apET.getText().toString();
        String telf = _telET.getText().toString();
        String email = _emailET.getText().toString();
        String passw = _passET.getText().toString();
        String passw2 = _pass2ET.getText().toString();

        if(nombre.isEmpty()){
            _nombreET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(ap.isEmpty()){
            _apET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(telf.isEmpty()){
            _telET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(telf.length() < 8){
            _telET.setError("Numero Invalido");
        }
        if(email.isEmpty()){
            _emailET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(passw.isEmpty()){
            _passET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(passw2.isEmpty()){
            _pass2ET.setError("Campo Vacio");
            emptyfield = true;
        }

        if(emptyfield) return false;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !emailUtils.correoPucp(email)) {
            _emailET.setError("Correo invalido");
            valid = false;
        } else {
            _emailET.setError(null);
        }

        if (passw.isEmpty() || passw.length() < 4 || passw.length() > 20) {
            _passET.setError("entre 4 y 20 caracteres alfanumericos");
            valid = false;
        } else {
            _passET.setError(null);
        }

        if(!valid) return false;


        if(!passw.equals(passw2)){
            _pass2ET.setError("Contrasenas no coinciden");
            return false;
        }

        Log.d(TAG, "validation succesful");
        return true;

    }

    public void failmessage(int flag){

        //0: Error de usuario/contrasena
        //-1: Error de Red/base de datos
        //2: Error correo no es puc
        if(flag == 0){

            Snackbar.make(findViewById(R.id.signupview), "No Existe el usuario", Snackbar.LENGTH_SHORT).show();

        }
        if(flag == -1){
            Snackbar.make(findViewById(R.id.signupview), "Eror en base de datos", Snackbar.LENGTH_SHORT).show();

        }
        if(flag == 1){
            Snackbar.make(findViewById(R.id.signupview), "El usuario ya existe :c", Snackbar.LENGTH_SHORT).show();
            _emailET.setError("Correo ya registrado");
        }
        if(flag == 3){
            Snackbar.make(findViewById(R.id.signupview), "Error en la respuesta del servidor", Snackbar.LENGTH_SHORT).show();
        }

    }

    //###############################################################3
    //AsyncTask para ver si el usuario ya existe

    private class ExisteAT extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... strings){

            URL url = UrlUtils.createUrl(_baseEUurl+strings[0]);

            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(url);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String response){

            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(response)) {
                failmessage(3);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(response);
                int codigo = baseJsonResponse.getInt("codigo");

                if (codigo == 0) {//se autoriza el logueo
                    //_flag = 1;

                    //create Json from new user
                    Gson gson = new Gson();
                    String json = gson.toJson(usuarioRegistro);


                    //send new user to emailverification activity
                    Intent intent = new Intent(getApplicationContext(), activity_emailverification.class);
                    intent.putExtra("newUser", json);
                    startActivityForResult(intent, FINISH_SIGNAL);
                    overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    return;

                }else{
                    failmessage(codigo);
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the JSON results", e);
            }

        }

    }
}
