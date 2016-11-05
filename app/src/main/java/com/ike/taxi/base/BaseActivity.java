package com.ike.taxi.base;

/**
 * Created by Min on 2016/8/16.
 *
 */

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.ike.taxi.network.async.AsyncTaskManager;
import com.ike.taxi.network.async.OnDataListener;

public class BaseActivity extends AppCompatActivity {
    public AsyncTaskManager mAsyncTaskManager;
    protected Context mContext;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=this;

    }
    //设置APP字体不根据系统字体的大小改变而改变
    @Override
    public Resources getResources() {
        Resources resources = super.getResources();
        Configuration configuration = new Configuration();
        configuration.setToDefaults();
        resources.updateConfiguration(configuration,resources.getDisplayMetrics());
        return resources;
    }

    /**
     * 发送请求
     *
     * @param requestCode    请求码
     * @param isCheckNetwork 是否需检查网络，true检查，false不检查
     */
    public void request(int requestCode, boolean isCheckNetwork) {
        if (mAsyncTaskManager != null) {
            mAsyncTaskManager.request(requestCode, isCheckNetwork, (OnDataListener) this);
        }
    }
}
