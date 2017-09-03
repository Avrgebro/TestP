package com.example.jose.carpool;

/**
 * Created by Johnny on 2/09/2017.
 */

public class map_points {
    private double mlat;
    private double mlon;

    public map_points(double lat, double lon){
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
