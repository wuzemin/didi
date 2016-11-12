package com.ike.taxi.chat.bean;

import android.net.Uri;

import java.io.Serializable;

/**
 * Created by Min on 2016/11/8.
 */

public class UserInfo implements Serializable {
    private String id;
    private String name;
    private Uri portraitUri;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Uri getPortraitUri() {
        return portraitUri;
    }

    public void setPortraitUri(Uri portraitUri) {
        this.portraitUri = portraitUri;
    }
}
