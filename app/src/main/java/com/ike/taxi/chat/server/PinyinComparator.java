package com.ike.taxi.chat.server;

import com.ike.taxi.chat.bean.FriendInfo;

import java.util.Comparator;

/**
 * Created by Min on 2016/10/19.
 */

public class PinyinComparator implements Comparator<FriendInfo> {


    public static PinyinComparator instance = null;

    public static PinyinComparator getInstance() {
        if (instance == null) {
            instance = new PinyinComparator();
        }
        return instance;
    }

    public int compare(FriendInfo o1, FriendInfo o2) {
        if (o1.getLetters().equals("@")
                || o2.getLetters().equals("#")) {
            return -1;
        } else if (o1.getLetters().equals("#")
                || o2.getLetters().equals("@")) {
            return 1;
        } else {
            return o1.getLetters().compareTo(o2.getLetters());
        }
    }
}
