package com.example.jose.carpool;

/**
 * Created by jose on 8/28/17.
 */

public class MapPoints {
    private double mlat;
    private double mlon;

    MapPoints(double lat, double lon){
        setMlat(lat);
        setMlon(lon);
    }

    public double getMlat() {
        return mlat;
    }

    public void setMlat(double mlat) {
        this.mlat = mlat;
    }

    public double getMlon() {
        return mlon;
    }

    public void setMlon(double mlon) {
        this.mlon = mlon;
    }
}
