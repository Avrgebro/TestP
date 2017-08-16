package com.example.jose.carpool;

/**
 * Created by Johnny on 14/08/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;


public class editar_Perfil extends Fragment {

    EditText txtNombre;
    EditText txtApellido;
    EditText txtTelefono;
    public User usuario;


    public editar_Perfil(){}

    public editar_Perfil(User usuario2){
        usuario = usuario2;

    }
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        //returning our layout file

        return inflater.inflate(R.layout.fragment_editarperfil, container, false);
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //you can set the title for your toolbar here for different fragments different titles
        getActivity().setTitle("Menu 1");

        txtNombre = (EditText) getView().findViewById(R.id.txtEditNombre);
        txtApellido = (EditText) getView().findViewById(R.id.txtEditApellido);
        txtTelefono = (EditText) getView().findViewById(R.id.txtEditTelefono);

        txtNombre.setHint(usuario.getNombre().toString());
        txtApellido.setHint(usuario.getApellido().toString());
        txtTelefono.setHint(usuario.getTelefono());

        ImageView editNombre = (ImageView) getView().findViewById(R.id.btnEditNombre);
        editNombre.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtNombre.setFocusableInTouchMode(true);
                txtNombre.setBackgroundColor(Color.WHITE);
            }
        });

        ImageView editApellido = (ImageView) getView().findViewById(R.id.btnEditApellido);
        editApellido.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtApellido.setFocusableInTouchMode(true);
                txtApellido.setBackgroundColor(Color.WHITE);
            }
        });

        ImageView editTelefono = (ImageView) getView().findViewById(R.id.btnEditTelefono);
        editTelefono.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtTelefono.setFocusableInTouchMode(true);
                txtTelefono.setBackgroundColor(Color.WHITE);
            }
        });

        ImageView editarAll = (ImageView) getView().findViewById(R.id.btnEditar);
        editarAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String nombreR="";
                if(txtNombre.length() == 0) {
                    nombreR = txtNombre.getHint().toString();
                    MainActivity.usuario.setNombre(txtNombre.getHint().toString());
                }else {
                    MainActivity.usuario.setNombre(txtNombre.getText().toString());
                    nombreR = txtNombre.getText().toString();
                }

                if(txtApellido.length() == 0) {
                    nombreR = nombreR + (txtApellido.getHint().toString());
                    MainActivity.usuario.setApellido(txtApellido.getHint().toString());
                }else {
                    nombreR = nombreR + (txtApellido.getText().toString());
                    MainActivity.usuario.setApellido(txtApellido.getText().toString());
                }

                if(txtTelefono.length() == 0) {
                    MainActivity.usuario.setTelefono(txtTelefono.getHint().toString());
                }else{
                    MainActivity.usuario.setTelefono(txtTelefono.getText().toString());
                }

                TextView nombreMain = (TextView) MainActivity._nomNavbar;
                nombreMain.setText(nombreR);

                cargarInicio();
            }
        });

    }


    void cargarInicio(){
        txtNombre.setFocusable(false);
        txtNombre.setBackgroundColor(Color.GRAY);

        txtApellido.setFocusable(false);
        txtApellido.setBackgroundColor(Color.GRAY);

        txtTelefono.setFocusable(false);
        txtTelefono.setBackgroundColor(Color.GRAY);
    }

}

