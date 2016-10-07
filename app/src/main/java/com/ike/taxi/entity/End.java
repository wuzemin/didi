package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/7/21.
 * 终点
 */
public class End implements Serializable {
    private String title;
    private String distance;
    private String address;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
