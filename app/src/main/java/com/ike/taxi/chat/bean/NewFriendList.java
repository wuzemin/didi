package com.ike.taxi.chat.bean;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Min on 2016/11/8.
 */

public class NewFriendList implements Serializable {
    /**
     * code : 200
     * result : [{"displayName":"",
     * "message":"手机号:18622222222昵称:的用户请求添加你为好友","status":11,"updatedAt":"2016-01-07T06:22:55.000Z",
     * "user":{"id":"i3gRfA1ml","nickname":"nihaoa","portraitUri":""}}]
     */
    private String code;
    private List<Result> results;

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<Result> getResults() {
        return results;
    }

    public void setResults(List<Result> results) {
        this.results = results;
    }

    public static class Result implements Comparable{
        private String display;
        private String message;
        private int status;
        private String updatedAt;
        private UserResult user;

        public Result(String display, String message, int status, String updatedAt, UserResult user) {
            this.display = display;
            this.message = message;
            this.status = status;
            this.updatedAt = updatedAt;
            this.user = user;
        }

        public String getDisplay() {
            return display;
        }

        public void setDisplay(String display) {
            this.display = display;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public int getStatus() {
            return status;
        }

        public void setStatus(int status) {
            this.status = status;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public UserResult getUser() {
            return user;
        }

        public void setUser(UserResult user) {
            this.user = user;
        }

        @Override
        public int compareTo(Object o) {
            return 0;
        }
    }

    public static class UserResult{
        private String id;
        private String nickname;
        private String portraitUri;

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getNickname() {
            return nickname;
        }

        public void setNickname(String nickname) {
            this.nickname = nickname;
        }

        public String getPortraitUri() {
            return portraitUri;
        }

        public void setPortraitUri(String portraitUri) {
            this.portraitUri = portraitUri;
        }
    }
}
