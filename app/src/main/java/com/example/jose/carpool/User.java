package com.example.jose.carpool;

import java.io.Serializable;

/**
 * Created by jose on 7/27/17.
 */

public class User implements Serializable {

    private String mID;
    private String mCorreo;
    private String mNombre;
    private String mApellido;
    private String mTelefono;
    private String mPass;//solo usado en el envio de nuevo ususrio a base de datos


    public User(String _id, String _correo, String _nombre, String _apellido, String _telefono, String _pass) {
        this.mID = _id;
        this.mCorreo = _correo;
        this.mNombre = _nombre;
        this.mApellido = _apellido;
        this.mTelefono = _telefono;
        this.mPass = _pass;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public String getCorreo() {
        return mCorreo;
    }

    public void setCorreo(String mCorreo) {
        this.mCorreo = mCorreo;
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getApellido() {
        return mApellido;
    }

    public void setApellido(String mApellido) {
        this.mApellido = mApellido;
    }

    public String getTelefono() {
        return mTelefono;
    }

    public void setTelefono(String mTelefono) {
        this.mTelefono = mTelefono;
    }

    public String getPassword() {
        return mPass;
    }

    public String getFullName() {
        return getNombre() + " " + getApellido();
    }
}
