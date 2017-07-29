package com.example.jose.carpool;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;

import butterknife.Bind;
import butterknife.ButterKnife;

public class activity_signup extends AppCompatActivity {
    private static final String TAG = "SignUpActivity";
    private static final int FINISH_SIGNAL = 0;

    @Bind(R.id.name_signupet) EditText _nombreET;
    @Bind(R.id.appat_signupet) EditText _appatET;
    @Bind(R.id.apmat_signupet) EditText _apmatET;
    @Bind(R.id.codepucp_signupet) EditText _codeET;
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
        String appat = _appatET.getText().toString();
        String apmat = _apmatET.getText().toString();
        String codigo = _codeET.getText().toString();
        String email = _emailET.getText().toString();
        String passw = _passET.getText().toString();
        String passw2 = _pass2ET.getText().toString();

        if(!validate()){
            return;
        }

        //populate new user
        User usuarioRegistro = new User(nombre, appat, apmat, email, codigo, passw);

        //create Json from new user
        Gson gson = new Gson();
        String json = gson.toJson(usuarioRegistro);


        //send new user to emailverification activity
        Intent intent = new Intent(getApplicationContext(), activity_emailverification.class);
        intent.putExtra("newUser", json);
        startActivityForResult(intent, FINISH_SIGNAL);
        overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == FINISH_SIGNAL) {
            if (resultCode == RESULT_OK) {
                this.finish();
            }
        }
    }

    public boolean validate(){
        boolean emptyfield = false;
        boolean valid = true;

        String nombre = _nombreET.getText().toString();
        String appat = _appatET.getText().toString();
        String apmat = _apmatET.getText().toString();
        String codigo = _codeET.getText().toString();
        String email = _emailET.getText().toString();
        String passw = _passET.getText().toString();
        String passw2 = _pass2ET.getText().toString();

        if(nombre.isEmpty()){
            _nombreET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(appat.isEmpty()){
            _appatET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(apmat.isEmpty()){
            _apmatET.setError("Campo Vacio");
            emptyfield = true;
        }
        if(codigo.isEmpty()){
            _codeET.setError("Campo Vacio");
            emptyfield = true;
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
}
