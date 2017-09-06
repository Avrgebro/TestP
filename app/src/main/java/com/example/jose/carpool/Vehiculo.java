package com.example.jose.carpool;

public class Vehiculo {

    private String mPlaca;
    private int mAsientos;
    private String mModelo;
    private String mMarca;
    private String mColor;
    private String mID;
    private String mUrlPic;

    public Vehiculo(String mPlaca, int mAsientos, String mModelo, String mMarca, String mColor, String mID, String mUrlPic) {
        this.setPlaca(mPlaca);
        this.setAsientos(mAsientos);
        this.setModelo(mModelo);
        this.setMarca(mMarca);
        this.setColor(mColor);
        this.setID(mID);
        this.setUrlPic(mUrlPic);
    }

    public Vehiculo(String mPlaca, int mAsientos, String mID) {
        this.mPlaca = mPlaca;
        this.mAsientos = mAsientos;
        this.mID = mID;
    }

    public Vehiculo(
            String id,
            String placa,
            String modelo,
            String marca,
            String color,
            int numAsientos,
            String imageUrl
    ) {
        this(placa, numAsientos, modelo, marca, color, id, imageUrl);
    }


    public String getPlaca() {
        return mPlaca;
    }

    public void setPlaca(String mPlaca) {
        this.mPlaca = mPlaca;
    }

    public int getAsientos() {
        return mAsientos;
    }

    public void setAsientos(int mAsientos) {
        this.mAsientos = mAsientos;
    }

    public String getModelo() {
        return mModelo;
    }

    public void setModelo(String mModelo) {
        this.mModelo = mModelo;
    }

    public String getMarca() {
        return mMarca;
    }

    public void setMarca(String mMarca) {
        this.mMarca = mMarca;
    }

    public String getColor() {
        return mColor;
    }

    public void setColor(String mColor) {
        this.mColor = mColor;
    }

    public String getID() {
        return mID;
    }

    public void setID(String mID) {
        this.mID = mID;
    }

    public String getUrlPic() {
        return mUrlPic;
    }

    public void setUrlPic(String mUrlPic) {
        this.mUrlPic = mUrlPic;
    }
}
