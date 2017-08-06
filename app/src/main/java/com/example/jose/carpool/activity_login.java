package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.net.URL;

import butterknife.ButterKnife;
import butterknife.Bind;

public class activity_login extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int REQUEST_SIGNUP = 0;
    private static final String BaseURL = "placeholder";
    private String userEmail;
    private String userPass;

    @Bind(R.id.input_email) EditText _emailText;
    @Bind(R.id.input_password) EditText _passwordText;
    @Bind(R.id.btn_login) Button _loginButton;
    @Bind(R.id.link_signup) TextView _signupLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

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


    public void login(){
        Log.d(TAG, "Login");

        if (!validate()) {
            onLoginFailed();
            return;
        }

        _loginButton.setEnabled(false);
        _signupLink.setEnabled(false);


        userEmail = _emailText.getText().toString();
        userPass = _passwordText.getText().toString();

        //progress bar aqui mientras busca en la bd
        //buscar en base de datos


        onLoginSuccess();

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

    public void onLoginSuccess() {
        Log.d(TAG, "login success");

        _loginButton.setEnabled(true);
        String email = _emailText.getText().toString();

        SharedPreferences.Editor editor = getSharedPreferences("SessionToken", MODE_PRIVATE).edit();
        editor.putString("SessionUser", "email");
        editor.putInt("SessionState", 1);
        editor.apply();
        finish();
    }

    public void onLoginFailed() {
        Toast.makeText(getBaseContext(), "Login failed", Toast.LENGTH_LONG).show();

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

        ret = ret+"?email="+userEmail+"&pass="+userPass;

        Log.d(TAG, ret);

        return ret;

    }

    private class ValidationAT extends AsyncTask<URL, Void, User>{

        @Override
        protected User doInBackground(URL... urls){
            String formedurl = formUrl();

            URL url = UrlUtils.createUrl(formedurl);

            String jsonResponse = "";
            try {
                jsonResponse = UrlUtils.makeHttpRequest(url);
            } catch (IOException e) {
                Log.e(TAG, "Problem making the HTTP request.", e);
            }

            // Extract relevant fields from the JSON response and create an {@link Event} object

            //TODO:PARSEAR RESPUESTA JSON A OBJETO USANDO GSON;
            // Return the {@link Event} object as the result fo the {@link TsunamiAsyncTask}
            Gson gson = new Gson();
            User user = gson.fromJson(jsonResponse, User.class);

            return user;
        }

        @Override
        protected void onPostExecute(User nana){

        }

    }

}
