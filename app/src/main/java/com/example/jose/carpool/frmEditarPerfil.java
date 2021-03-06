package com.example.jose.carpool;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;

public class frmEditarPerfil extends MainActivity {

    User usuario;
    EditText txtNombre;
    EditText txtApellido;
    EditText txtTelefono;

    String nombre;
    String apellido;
    String telefono;
    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frm_editar_perfil);
        Intent intent = getIntent();

        FrameLayout contentFrameLayout = (FrameLayout) findViewById(R.id.drawer_layout); //Remember this is the FrameLayout area within your activity_main.xml
        getLayoutInflater().inflate(R.layout.activity_main, contentFrameLayout);


        usuario = (User)intent.getSerializableExtra("sampleObject");
        txtNombre = (EditText) findViewById(R.id.txtEditNombre);
        txtApellido = (EditText) findViewById(R.id.txtEditApellido);
        txtTelefono = (EditText) findViewById(R.id.txtEditTelefono);

        nombre= usuario.getNombre();
        apellido = usuario.getApellido();
        telefono = usuario.getTelefono();

        txtNombre.setHint(usuario.getNombre());
        txtApellido.setHint(usuario.getApellido());
        txtTelefono.setHint(usuario.getTelefono());

    }

    @Override
    protected void onStart() {
        super.onStart();


    }

    public void EditarNombre(View view){
        txtNombre.setFocusableInTouchMode(true);
        txtNombre.setBackgroundColor(Color.WHITE);
    }

    public void EditarApellidoP(View view){
        txtApellido.setFocusableInTouchMode(true);
        txtApellido.setBackgroundColor(Color.WHITE);
    }


    public void EditarNumero(View view){
        txtTelefono.setFocusableInTouchMode(true);
        txtTelefono.setBackgroundColor(Color.WHITE);
    }

    public void EditarDatos(View view){
        Intent intent=new Intent();

        if(txtNombre.length() == 0)
            usuario.setNombre(txtNombre.getHint().toString());
        else
            usuario.setNombre(txtNombre.getText().toString());


        if(txtApellido.length() == 0)
            usuario.setApellido(txtApellido.getHint().toString());
        else
            usuario.setApellido(txtApellido.getText().toString());

        if(txtTelefono.length() == 0)
            usuario.setTelefono(txtTelefono.getHint().toString());
        else
            usuario.setTelefono(txtTelefono.getText().toString());

        intent.putExtra("UsuarioRet", usuario);
        setResult(RESULT_OK,intent);
        finish();

    }
    public void Atras(View view){
        finish();
    }

}
