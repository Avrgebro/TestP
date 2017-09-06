package com.example.jose.carpool;

/**
 * Created by jose on 9/2/17.
 */

public class Vehiculo {

    private String mPlaca;
    private int mAsientos;
    private String mModelo;
    private String mMarca;
    private String mColor;
    private String mID;
    private String mUrlPic;

    public Vehiculo(String mPlaca, int mAsientos, String mModelo, String mMarca, String mColor, String mID, String mUrlPic) {
        this.setmPlaca(mPlaca);
        this.setmAsientos(mAsientos);
        this.setmModelo(mModelo);
        this.setmMarca(mMarca);
        this.setmColor(mColor);
        this.setmID(mID);
        this.setmUrlPic(mUrlPic);
    }

    public Vehiculo(String mPlaca, int mAsientos, String mID) {
        this.mPlaca = mPlaca;
        this.mAsientos = mAsientos;
        this.mID = mID;
    }


    public String getmPlaca() {
        return mPlaca;
    }

    public void setmPlaca(String mPlaca) {
        this.mPlaca = mPlaca;
    }

    public int getmAsientos() {
        return mAsientos;
    }

    public void setmAsientos(int mAsientos) {
        this.mAsientos = mAsientos;
    }

    public String getmModelo() {
        return mModelo;
    }

    public void setmModelo(String mModelo) {
        this.mModelo = mModelo;
    }

    public String getmMarca() {
        return mMarca;
    }

    public void setmMarca(String mMarca) {
        this.mMarca = mMarca;
    }

    public String getmColor() {
        return mColor;
    }

    public void setmColor(String mColor) {
        this.mColor = mColor;
    }

    public String getmID() {
        return mID;
    }

    public void setmID(String mID) {
        this.mID = mID;
    }

    public String getmUrlPic() {
        return mUrlPic;
    }

    public void setmUrlPic(String mUrlPic) {
        this.mUrlPic = mUrlPic;
    }
}
