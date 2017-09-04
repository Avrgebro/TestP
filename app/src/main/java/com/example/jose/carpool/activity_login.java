package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.graphics.drawable.Drawable;
import com.google.gson.Gson;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Bind;

public class activity_login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String BaseURL = "http://200.16.7.170/api/users/login/";//solo se agrega "correo/pass"
    private String userEmail;
    private String userPass;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;
    @Bind(R.id.imgBack) ImageView _imgbg;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        //_imgbg.setVisibility(View.GONE);
        String mDrawableName = "loginbg2";
        int imageID = getResources().getIdentifier(mDrawableName, "drawable", getPackageName());
        _imgbg.setBackground(resizeImage(imageID));

        //getWindow().setBackgroundDrawableResource(imageID) ;

        _loginButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                login();
            }
        });

        _signupLink.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                // Start the Signup activity
                Intent intent = new Intent(getApplicationContext(), activity_signup.class);
                startActivityForResult(intent, REQUEST_SIGNUP);
                finish();
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
            }
        });


    }

    public Bitmap getResizedBitmap(Bitmap bm, int newHeight, int newWidth) {

        int width = bm.getWidth();
        int height = bm.getHeight();

        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;

        // create a matrix for the manipulation
        Matrix matrix = new Matrix();

        // resize the bit map
        matrix.postScale(scaleWidth, scaleHeight);

        // recreate the new Bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(bm, 0, 0, width, height,
                matrix, false);

        return resizedBitmap;
    }
    public Drawable resizeImage(int imageResource) {// R.drawable.icon
        // Get device dimensions
        Display display = getWindowManager().getDefaultDisplay();
        double deviceWidth = display.getWidth();

        BitmapDrawable bd = (BitmapDrawable) this.getResources().getDrawable(
                imageResource);
        double imageHeight = bd.getBitmap().getHeight();
        double imageWidth = bd.getBitmap().getWidth();

        double ratio = deviceWidth / imageWidth;
        int newImageHeight = (int) (imageHeight * ratio);



        Bitmap bMap = BitmapFactory.decodeResource(getResources(), imageResource);
        Drawable drawable = new BitmapDrawable(this.getResources(),
                getResizedBitmap(bMap, newImageHeight, (int) deviceWidth));

        return drawable;
    }

    public void login(){
        Log.d(TAG, "Login");

        ProgressBar PB = (ProgressBar) findViewById(R.id.logPB);
        PB.setVisibility(View.VISIBLE);



        if (!validate()) {
            onLoginFailed(2);
            return;
        }

        _loginButton.setEnabled(false);
        _signupLink.setEnabled(false);


        userEmail = _emailText.getText().toString();
        userPass = _passwordText.getText().toString();

        //progress bar aqui mientras busca en la bd
        //buscar en base de datos
        //async Task

        ValidationAT task = new ValidationAT();
        task.execute();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_SIGNUP) {
            if (resultCode == RESULT_OK) {

                // TODO: Implement successful signup logic here
                // By default we just finish the Activity and log them in automatically
                this.finish();
            }
        }
    }

    @Override
    public void onBackPressed() {
        // Disable going back to the MainActivity
        moveTaskToBack(true);
    }

    public void onLoginSuccess(User userinfo) {
        Log.d(TAG, "login success");

        _loginButton.setEnabled(true);

        Gson gson = new Gson();
        String json = gson.toJson(userinfo);



        SharedPreferences.Editor editor = getSharedPreferences("SessionToken", MODE_PRIVATE).edit();
        editor.putString("SessionUser", json);
        editor.putInt("SessionState", 1);
        editor.apply();

        ProgressBar PB = (ProgressBar) findViewById(R.id.logPB);
        PB.setVisibility(View.INVISIBLE);

        finish();
    }

    public void onLoginFailed(int flag) {
        //0: Error de usuario/contrasena
        //-1: Error de Red/base de datos
        //2: Error correo no es puc
        if(flag == 0){

            Snackbar.make(findViewById(R.id.loginview), "Usuario y/o contrasena incorrecta", Snackbar.LENGTH_SHORT).show();

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

        ProgressBar PB = (ProgressBar) findViewById(R.id.logPB);
        PB.setVisibility(View.INVISIBLE);

        _loginButton.setEnabled(true);
        _signupLink.setEnabled(true);
    }

    public boolean validate() {
        boolean valid = true;

        String email = _emailText.getText().toString();
        String password = _passwordText.getText().toString();

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches() || !emailUtils.correoPucp(email)) {
            _emailText.setError("Correo invalido");
            valid = false;
        } else {
            _emailText.setError(null);
        }

        if (password.isEmpty() || password.length() < 4 || password.length() > 20) {
            _passwordText.setError("entre 4 y 20 caracteres alfanumericos");
            valid = false;
        } else {
            _passwordText.setError(null);
        }

        return valid;
    }


    //##################################################################
    //Asynctask para validacion

    public String formUrl(){
        String ret = BaseURL;

        ret = ret+userEmail+"/"+userPass;

        Log.d(TAG, ret);

        return ret;

    }

    private class ValidationAT extends AsyncTask<URL, Void, String>{

        @Override
        protected String doInBackground(URL... urls){
            String formedurl = formUrl();

            URL url = UrlUtils.createUrl(formedurl);

            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequestGet(url);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object

            //TODO:PARSEAR RESPUESTA JSON A OBJETO USANDO GSON;
            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            Gson gson = new Gson();
            User user = gson.fromJson(jsonResponse, User.class);

            return jsonResponse;
        }

        @Override
        protected void onPostExecute(String userinfoJSON){

            //aca lo convierto a un objeto json y verifico el codigo

            if (TextUtils.isEmpty(userinfoJSON)) {
                onLoginFailed(3);
                Log.d(TAG, "Json vacio");
                return;
            }

            try {
                JSONObject baseJsonResponse = new JSONObject(userinfoJSON);
                int codigo = baseJsonResponse.getInt("codigo");

                if (codigo == 1) {//se autoriza el logueo
                    // Extract out the first feature (which is an earthquake)
                    // Extract out the title, number of people, and perceived strength values
                    //String title = properties.getString("title");
                    //String numberOfPeople = properties.getString("felt");
                    //String perceivedStrength = properties.getString("cdi");

                    String id = baseJsonResponse.getString("idUsuario");
                    String nombre = baseJsonResponse.getString("nombre");
                    //Log.d(TAG, nombre);
                    String apellido = baseJsonResponse.getString("apellido");
                    String telefono = baseJsonResponse.getString("telefono");
                    String correo = baseJsonResponse.getString("correo");

                    User loguser = new User(id, correo, nombre, apellido, telefono, "xxxxxx");
                    //Log.d(TAG, loguser.getNombre()+" "+loguser.getCorreo());
                    Log.d(TAG, "creacion de usuario correcta");
                    onLoginSuccess(loguser);
                    return;

                }else{
                    onLoginFailed(codigo);
                    return;
                }
            } catch (JSONException e) {
                Log.e(TAG, "Problem parsing the earthquake JSON results", e);
            }

        }

    }

}