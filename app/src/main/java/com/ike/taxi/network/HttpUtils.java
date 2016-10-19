package com.ike.taxi.network;

import android.content.Context;

import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import com.zhy.http.okhttp.cookie.CookieJarImpl;
import com.zhy.http.okhttp.cookie.store.CookieStore;
import com.zhy.http.okhttp.cookie.store.PersistentCookieStore;

import okhttp3.OkHttpClient;

/**
 * Created by Min on 2016/9/10.
 */
public class HttpUtils {
    private static final String baseUrl="http://192.168.1.123:8080";

    /**
     * 设置cookie
     * @param context
     */
    public static void setCookie(Context context){
        CookieStore cookieStore=new PersistentCookieStore(context);
        CookieJarImpl cookieJar=new CookieJarImpl(cookieStore);
        OkHttpClient client=new OkHttpClient.Builder().cookieJar(cookieJar).build();
        OkHttpUtils.initClient(client);
    }

    //普通的GET请求，根据泛型Bean返回值也是Bean
    public static void getRequest(String url, StringCallback callback) {
        OkHttpUtils.get().url(baseUrl+url).build().execute(callback);
    }

    //普通的POST请求，根据泛型Bean返回值也是Bean
    public static void postRequest(String url, StringCallback callback){
        OkHttpUtils.post().url(baseUrl+url).build().execute(callback);
    }

    //普通Get上传
    public static void sendGetRequest(String url,String username,String password,StringCallback callback){
        OkHttpUtils.get().url(baseUrl+url)
                .addParams("username",username)
                .addParams("password",password)
                .build().execute(callback);
    }

    //普通Post上传----用户登录
    public static void sendPostRequest(String url,String username,String password,StringCallback callback){
        OkHttpUtils.post().url(baseUrl+url)
                .addParams("username",username)
                .addParams("password",password)
                .build().execute(callback);
    }


    //直接上传String类型的文本
    public static void sendPostStringRequest(String url,Object object,StringCallback callback){
        OkHttpUtils.postString().url(baseUrl+url).content(object.toString()).build().execute(callback);
    }


}
