package com.ike.taxi.chat.bean;

/**
 * Created by Min on 2016/11/8.
 */

public class ContactNotificationMessageData {
    /**
     * sourceUserNickname : 赵哈哈
     * version : 1456282826213
     */
    private String sourceNickName;
    private long version;

    public String getSourceNickName() {
        return sourceNickName;
    }

    public void setSourceNickName(String sourceNickName) {
        this.sourceNickName = sourceNickName;
    }

    public long getVersion() {
        return version;
    }

    public void setVersion(long version) {
        this.version = version;
    }
}
