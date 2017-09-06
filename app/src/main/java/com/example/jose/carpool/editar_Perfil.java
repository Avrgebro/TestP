package com.example.jose.carpool;

/**
 * Created by Johnny on 14/08/2017.
 */

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;


public class editar_Perfil extends Fragment {

    EditText txtNombre;
    EditText txtApellido;
    EditText txtTelefono;
    TextInputLayout textinputNombre;
    TextInputLayout textinputApellido;
    TextInputLayout textinputTelefono;
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

        textinputNombre = (TextInputLayout) getView().findViewById(R.id.textNombre);
        textinputApellido = (TextInputLayout) getView().findViewById(R.id.textApellido);
        textinputTelefono = (TextInputLayout) getView().findViewById(R.id.textTelefono);


        textinputNombre.setHint(usuario.getNombre().toString());
        textinputApellido.setHint(usuario.getApellido().toString());
        textinputTelefono.setHint(usuario.getTelefono());

        ImageView editarAll = (ImageView) getView().findViewById(R.id.btnEditar);
        editarAll.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                txtNombre = textinputNombre.getEditText();
                txtApellido = textinputApellido.getEditText();
                txtTelefono = textinputTelefono.getEditText();
                if(txtNombre.length() != 0) {
                    MainActivity.user.setNombre(txtNombre.getText().toString());
                }

                if(txtApellido.length() != 0) {
                    MainActivity.user.setApellido(txtApellido.getText().toString());
                }

                if(txtTelefono.length() != 0) {
                    MainActivity.user.setTelefono(txtTelefono.getText().toString());
                }

                TextView nombreMain = (TextView) MainActivity._nomnavbar;
                nombreMain.setText(MainActivity.user.getNombre() + " " + MainActivity.user.getApellido());
                //cargarInicio();

                //LLamar a la funcion actualizar BD
                actualizarUsuario();
            }
        });

    }


    public static void actualizarUsuario() {
        //subir user
        //String rspt = postJSONObject(BaseURL, jsonObject);


        new Thread(new Runnable() {
            @Override
            public void run() {
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("idUsuario", MainActivity.user.getID());
                    jsonObject.put("nombre", MainActivity.user.getNombre());
                    jsonObject.put("apellido", MainActivity.user.getApellido());
                    jsonObject.put("telefono", MainActivity.user.getTelefono());
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                String BaseURL = "http://200.16.7.170/api/users/actualizar_perfil";//solo se agrega "correo/pass"
                OutputStream os = null;
                InputStream is = null;
                HttpURLConnection conn = null;
                try {
                    //constants
                    URL url = new URL(BaseURL);
                    String message = jsonObject.toString();
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setReadTimeout(10000 /*milliseconds*/);
                    conn.setConnectTimeout(15000 /* milliseconds */);
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
                    //String contentAsString = readIt(is,len);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
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
        }).start();

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

