package com.example.jose.carpool;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;

public class activity_PerfilUsuario extends AppCompatActivity {
    EditText txtNombre;
    EditText txtApellidoP;
    EditText txtApellidoM;
    EditText txtTelefono;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity__perfil_usuario);
        Intent intent = getIntent();
        User usuario = (User)intent.getSerializableExtra("sampleObject");

        txtNombre = (EditText) findViewById(R.id.txtNombre);
        txtApellidoP = (EditText) findViewById(R.id.txtApP);
        txtApellidoM = (EditText) findViewById(R.id.txtApM);
        txtTelefono = (EditText) findViewById(R.id.txtTelefono);

        txtNombre.setHint(usuario.getNombre());
        txtApellidoP.setHint(usuario.getApPat());
        txtApellidoM.setHint(usuario.getApMat());
        txtTelefono.setHint(usuario.getmTelefono());
    }

    public void EditarNombre(View view){
        txtNombre.setFocusableInTouchMode(true);
        txtNombre.setBackgroundColor(Color.WHITE);
    }

    public void EditarApellidoP(View view){
        txtApellidoP.setFocusableInTouchMode(true);
        txtApellidoP.setBackgroundColor(Color.WHITE);
    }

    public void EditarApellidoM(View view){
        txtApellidoM.setFocusableInTouchMode(true);
        txtApellidoM.setBackgroundColor(Color.WHITE);
    }

    public void EditarNumero(View view){
        txtTelefono.setFocusableInTouchMode(true);
        txtTelefono.setBackgroundColor(Color.WHITE);
    }




}
