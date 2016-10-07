package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/9/20.
 */

public class LoginEntity implements Serializable{
    private String errno;
    private String msg;

    public LoginEntity(String errno, String msg) {
        this.errno = errno;
        this.msg = msg;
    }

    public String getErrno() {
        return errno;
    }

    public void setErrno(String errno) {
        this.errno = errno;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
