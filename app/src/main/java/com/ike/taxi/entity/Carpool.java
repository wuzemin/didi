package com.ike.taxi.entity;

/**
 * Created by Min on 2016/8/17.
 * 拼车
 */
public class Carpool {
    private String image_head;
    private String user;
    private String startPoint;
    private String endPoint;
    private String time;
    private String distance;
    private String text;
    private String image_content;

    public Carpool(String image_head, String user, String startPoint, String endPoint,
                   String time, String distance, String text, String image_content) {
        this.image_head = image_head;
        this.user = user;
        this.startPoint = startPoint;
        this.endPoint = endPoint;
        this.time = time;
        this.distance = distance;
        this.text = text;
        this.image_content = image_content;
    }

    public String getImage_head() {
        return image_head;
    }

    public void setImage_head(String image_head) {
        this.image_head = image_head;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(String startPoint) {
        this.startPoint = startPoint;
    }

    public String getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(String endPoint) {
        this.endPoint = endPoint;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDistance() {
        return distance;
    }

    public void setDistance(String distance) {
        this.distance = distance;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getImage_content() {
        return image_content;
    }

    public void setImage_content(String image_content) {
        this.image_content = image_content;
    }
}
