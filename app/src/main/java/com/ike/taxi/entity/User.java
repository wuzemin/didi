package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/9/9.
 */
public class User implements Serializable {
    private int id;
    private String user;
    private String pwd;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPwd() {
        return pwd;
    }

    public void setPwd(String pwd) {
        this.pwd = pwd;
    }
}
