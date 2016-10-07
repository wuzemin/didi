package com.ike.taxi.entity;

import com.amap.api.services.core.LatLonPoint;

import java.io.Serializable;

/**
 * Created by Min on 2016/8/31.
 * 封装关于驾车路线位置的起点，终点 实体
 */
public class Route implements Serializable{
    private LatLonPoint mStartPoint;
    private LatLonPoint mEndPoint;
    private String address;

    public LatLonPoint getmStartPoint() {
        return mStartPoint;
    }

    public void setmStartPoint(LatLonPoint mStartPoint) {
        this.mStartPoint = mStartPoint;
    }

    public LatLonPoint getmEndPoint() {
        return mEndPoint;
    }

    public void setmEndPoint(LatLonPoint mEndPoint) {
        this.mEndPoint = mEndPoint;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Route(LatLonPoint mStartPoint, LatLonPoint mEndPoint, String address) {
        this.mStartPoint = mStartPoint;
        this.mEndPoint = mEndPoint;
        this.address = address;
    }

}
