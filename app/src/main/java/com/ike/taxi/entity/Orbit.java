package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/9/24.
 */

public class Orbit implements Serializable {
    private double latitude;
    private double longitude;

    public Orbit() {
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}