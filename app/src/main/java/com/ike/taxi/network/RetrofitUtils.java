package com.ike.taxi.network;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by Min on 2016/11/11.
 */

public class RetrofitUtils {
    //服务器路径
    private static final String BaseURI="http://192.168.1.135:8080";
    private static Retrofit retrofit;
    private static OkHttpClient mOkHttpClient;

    protected static Retrofit getRetrofit(){
        if(null==retrofit){
            if(null==mOkHttpClient){
                mOkHttpClient=OkHttp3Utils.getOkHttpClient();
            }
            retrofit = new Retrofit.Builder()
                    .baseUrl(BaseURI)   //设置服务器路径
                    .addConverterFactory(GsonConverterFactory.create())//添加转化库，默认是Gson
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create()) //添加回调库，采用RxJava
                    .client(mOkHttpClient)  //设置使用okhttp网络请求
                    .build();
        }
        return retrofit;
    }
}
