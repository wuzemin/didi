package com.ike.taxi.entity;

/**
 * Created by Min on 2016/8/11.
 * 评论
 */
public class Comment {
    private String user;
    private String time;
    private String text;
    private int imageId;
    private String imageUrl;


    public Comment(){}

    public Comment(String text, int imageId) {
        this.text = text;
        this.imageId = imageId;
    }

    public Comment(String text, String imageUrl) {
        this.imageUrl = imageUrl;
        this.text = text;
    }

    /*public Comment(String text, String time, String imageUrl) {
        this.text = text;
        this.time = time;
        this.imageUrl = imageUrl;
    }*/

    public Comment(String user,String time,String text, String imageUrl ) {
        this.text = text;
        this.imageUrl = imageUrl;
        this.time = time;
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
}
