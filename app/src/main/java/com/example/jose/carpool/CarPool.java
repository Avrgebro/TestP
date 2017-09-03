package com.example.jose.carpool;

/**
 * Created by jose on 7/27/17.
 */

import java.util.ArrayList;
import java.util.List;

public class CarPool {
    private String mIDusuario;
    private String mIDvehiculo;
    private int mNasientos;
    private String mCosto;
    private String mFcreacion;
    private String mFsalida;
    private String mNomOrigen;
    private String mDistOrigen;
    private String mDirOrigen;
    private String mNomDestino;
    private String mDistDestino;
    private String mDirDestino;
    private int mEstado;
    private String mIDservicio;
    private String mHcreacion;
    private String mHsalida;
    private ArrayList<map_points> mCoords;



    public CarPool(String mIDusuario, String mIDvehiculo, int mNasientos, String mCosto, String mFcreacion, String mFsalida, String mNomOrigen, String mDistOrigen, String mDirOrigen, String mNomDestino, String mDistDestino, String mDirDestino, int mEstado, String mIDservicio, String mHcreacion, String mHsalida, ArrayList<map_points> mCoords) {
        this.mIDusuario = mIDusuario;
        this.mIDvehiculo = mIDvehiculo;
        this.mNasientos = mNasientos;
        this.mCosto = mCosto;
        this.mFcreacion = mFcreacion;
        this.mFsalida = mFsalida;
        this.mNomOrigen = mNomOrigen;
        this.mDistOrigen = mDistOrigen;
        this.mDirOrigen = mDirOrigen;
        this.mNomDestino = mNomDestino;
        this.mDistDestino = mDistDestino;
        this.mDirDestino = mDirDestino;
        this.mEstado = mEstado;
        this.mIDservicio = mIDservicio;
        this.mHcreacion = mHcreacion;
        this.mHsalida = mHsalida;
        this.mCoords = mCoords;
    }

    public String getIDusuario() {
        return mIDusuario;
    }

    public void setIDusuario(String mIDusuario) {
        this.mIDusuario = mIDusuario;
    }

    public String getIDvehiculo() {
        return mIDvehiculo;
    }

    public void setIDvehiculo(String mIDvehiculo) {
        this.mIDvehiculo = mIDvehiculo;
    }

    public int getNasientos() {
        return mNasientos;
    }

    public void setNasientos(int mNasientos) {
        this.mNasientos = mNasientos;
    }

    public String getCosto() {
        return mCosto;
    }

    public void setCosto(String mCosto) {
        this.mCosto = mCosto;
    }

    public String getFcreacion() {
        return mFcreacion;
    }

    public void setFcreacion(String mFcreacion) {
        this.mFcreacion = mFcreacion;
    }

    public String getFsalida() {
        return mFsalida;
    }

    public void setFsalida(String mFsalida) {
        this.mFsalida = mFsalida;
    }

    public String getNomOrigen() {
        return mNomOrigen;
    }

    public void setNomOrigen(String mNomOrigen) {
        this.mNomOrigen = mNomOrigen;
    }

    public String getDistOrigen() {
        return mDistOrigen;
    }

    public void setDistOrigen(String mDistOrigen) {
        this.mDistOrigen = mDistOrigen;
    }

    public String getDirOrigen() {
        return mDirOrigen;
    }

    public void setDirOrigen(String mDirOrigen) {
        this.mDirOrigen = mDirOrigen;
    }

    public String getNomDestino() {
        return mNomDestino;
    }

    public void setNomDestino(String mNomDestino) {
        this.mNomDestino = mNomDestino;
    }

    public String getDistDestino() {
        return mDistDestino;
    }

    public void setDistDestino(String mDistDestino) {
        this.mDistDestino = mDistDestino;
    }

    public String getDirDestino() {
        return mDirDestino;
    }

    public void setDirDestino(String mDirDestino) {
        this.mDirDestino = mDirDestino;
    }

    public int getEstado() {
        return mEstado;
    }

    public void setEstado(int mestado) {
        this.mEstado = mestado;
    }

    public String getIDservicio() {
        return mIDservicio;
    }

    public void setIDservicio(String mIDservicio) {
        this.mIDservicio = mIDservicio;
    }

    public String getHcreacion() {
        return mHcreacion;
    }

    public void setHcreacion(String mHcreacion) {
        this.mHcreacion = mHcreacion;
    }

    public String getHsalida() {
        return mHsalida;
    }

    public void setHsalida(String mHsalida) {
        this.mHsalida = mHsalida;
    }

    public ArrayList<map_points> getCoords() {
        return mCoords;
    }

    public void setCoords(ArrayList<map_points> mCoords) {
        this.mCoords = mCoords;
    }

}
