package com.ike.taxi.chat.bean;

/**
 * Created by Min on 2016/11/11.
 */

public class Group {
    private int code;
    private GroupResult msg;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public GroupResult getMsg() {
        return msg;
    }

    public void setMsg(GroupResult msg) {
        this.msg = msg;
    }

    public static class GroupResult{
        private String id;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }
    }
}
