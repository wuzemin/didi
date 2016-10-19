package com.ike.taxi.entity;

import java.io.Serializable;

/**
 * Created by Min on 2016/9/20.
 */

public class LoginEntity implements Serializable{
    private String code;
    private String msg;

    public LoginEntity(String code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
