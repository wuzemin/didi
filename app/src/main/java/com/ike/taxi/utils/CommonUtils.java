package com.ike.taxi.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Created by Min on 2016/11/8.
 * 辅助类
 */

public class CommonUtils {

    private static final String tag=CommonUtils.class.getSimpleName();
    //网咯类型
    private static final String NETTYPE_WIFI="0x01";
    private static final String NETTYPE_CMWAP="0x02";
    private static final String NETTYPE_CMNET="0x03";

    /**
     * 检查网咯是否可用
     */
    public static boolean isNetConnect(Context context){
        ConnectivityManager cm= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo=cm.getActiveNetworkInfo();
        return networkInfo!=null && networkInfo.isConnectedOrConnecting();
    }

}
