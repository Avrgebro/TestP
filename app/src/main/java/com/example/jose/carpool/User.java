package com.example.jose.carpool;

import java.io.Serializable;

/**
 * Created by jose on 7/27/17.
 */

public class User implements Serializable {

    private String mNombre;
    private String mApPat;
    private String mApMat;
    private String mCorreo;
    private String mCodigo;
    private String mPass;
    private String mTelefono;

    public User(){}
    public User(String nom, String appat, String apmat, String cor, String cod, String pass){
        setNombre(nom);
        setApPat(appat);
        setApMat(apmat);
        setCorreo(cor);
        setCodigo(cod);
        setPass(pass);
    }

    public User(String nom, String appat, String apmat, String cor, String cod, String pass, String mTelefono){
        setNombre(nom);
        setApPat(appat);
        setApMat(apmat);
        setCorreo(cor);
        setCodigo(cod);
        setPass(pass);
        setmTelefono(mTelefono);
    }

    public String getNombre() {
        return mNombre;
    }

    public void setNombre(String mNombre) {
        this.mNombre = mNombre;
    }

    public String getApPat() {
        return mApPat;
    }

    public void setApPat(String mApPat) {
        this.mApPat = mApPat;
    }

    public String getApMat() {
        return mApMat;
    }

    public void setApMat(String mApMat) {
        this.mApMat = mApMat;
    }

    public String getCorreo() {
        return mCorreo;
    }

    public void setCorreo(String mCorreo) {
        this.mCorreo = mCorreo;
    }

    public String getCodigo() {
        return mCodigo;
    }

    public void setCodigo(String mCodigo) {
        this.mCodigo = mCodigo;
    }

    public String getPass() {
        return mPass;
    }

    public void setPass(String mPass) {
        this.mPass = mPass;
    }

    public String getmTelefono() {
        return mTelefono;
    }

    public void setmTelefono(String mTelefono) {
        this.mTelefono = mTelefono;
    }
}
