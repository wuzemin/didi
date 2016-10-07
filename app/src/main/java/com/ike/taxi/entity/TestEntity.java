package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/9/14.
 */
public class TestEntity implements Serializable {
    private double latitude;
    private double longitude;

    public TestEntity() {
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

    public TestEntity(double latitude, double longitude) {
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
