package com.ike.taxi.listener;

import com.ike.taxi.entity.LocationEntity;

/**
 * Created by Min on 2016/9/1.
 * 逆地理编码或者定位完成后回调接口
 */
public interface OnLocationGetListener {

    void onLocationGet(LocationEntity entity);

    void onRegecodeGet(LocationEntity entity);
}
