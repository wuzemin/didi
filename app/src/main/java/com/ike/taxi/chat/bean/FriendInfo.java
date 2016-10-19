package com.ike.taxi.chat.bean;

import android.text.TextUtils;

import java.io.Serializable;

/**
 * Created by Min on 2016/10/19.
 * 用户信息
 */

public class FriendInfo implements Serializable {
    private String userId;
    private String name;
    private String portraitUri;
    private String displayName;
    private String status;
    private Long timestamp;
    private String letters;

    public String getLetters() {
        return letters;
    }

    public void setLetters(String letters) {
        this.letters = letters;
    }

    public FriendInfo() {
    }

    public FriendInfo(String userId) {
        this.userId = userId;
    }

    public FriendInfo(String userId, String name, String portraitUri, String displayName) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
    }


    public FriendInfo(String userId, String name, String portraitUri, String displayName, String letters) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.letters = letters;
    }

    public FriendInfo(String userId, String name, String portraitUri, String displayName, String status, Long timestamp) {
        this.userId = userId;
        this.name = name;
        this.portraitUri = portraitUri;
        this.displayName = displayName;
        this.status = status;
        this.timestamp = timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(String portraitUri) {
        this.portraitUri = portraitUri;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public boolean isExitsDisplayName() {
        if (TextUtils.isEmpty(getDisplayName())) {
            return false;
        }
        return true;
    }
}
