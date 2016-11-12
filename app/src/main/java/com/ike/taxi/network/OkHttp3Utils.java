package com.ike.taxi.network;

import com.ike.taxi.application.App;

import java.io.File;
import java.net.CookieManager;
import java.util.concurrent.TimeUnit;

import okhttp3.Cache;
import okhttp3.CookieJar;
import okhttp3.OkHttpClient;

/**
 * Created by Min on 2016/11/11.
 */

public class OkHttp3Utils {
    private static OkHttpClient mOkHttpClient;
    //设置缓存目录
    private static File cacheFile=new File(
            App.getsInstance().getApplicationContext().getCacheDir().getAbsolutePath(),"MyCache");
    private static Cache cache=new Cache(cacheFile,10 * 1024 * 1024);

    /**
     * 获取OkHttpClient对象
     */
    public static OkHttpClient getOkHttpClient(){
        if(null==mOkHttpClient){
            mOkHttpClient=new OkHttpClient.Builder()
                    .cookieJar((CookieJar) new CookieManager()) //设置一个自动管理cookies的管理器
//                    .addInterceptor(new MyInterceptor()) //设置拦截器
                    .connectTimeout(10, TimeUnit.SECONDS)//超时
                    .writeTimeout(30,TimeUnit.SECONDS)
                    .readTimeout(30,TimeUnit.SECONDS)
                    .cache(cache)
                    .build();
        }
        return mOkHttpClient;
    }
}
