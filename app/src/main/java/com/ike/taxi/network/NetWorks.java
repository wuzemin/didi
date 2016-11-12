package com.ike.taxi.network;

/**
 * Created by Min on 2016/11/11.
 */

public class NetWorks extends RetrofitUtils {
    //创建实现接口调用
    protected  static final NetService service=getRetrofit().create(NetService.class);
    //设置缓存有效期为1天
    protected static final long CACHE_STALE_SEC=60 * 60 * 24 * 1;
    //查询缓存的Cache-Control设置，使用缓存
    protected static final String CACHE_CONTROL_CACHE="only-if-cached, max-stale="+CACHE_STALE_SEC;
    //查询网络的Cache-Control设置，不使用缓存
    protected static final String CACHE_CONTROL_NETWORK="max-age=0";

    private interface NetService{
        //post请求
    }
}
