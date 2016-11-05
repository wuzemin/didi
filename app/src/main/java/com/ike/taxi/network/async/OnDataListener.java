package com.ike.taxi.network.async;


import com.ike.taxi.network.http.HttpException;

/**
 * Created by Min on 2016/11/2.
 */

public interface OnDataListener{
        /**
         * 异步耗时方法
         * @String parameter 请求传参,可不填
         * @param requestCode 请求码
         * @return
         * @throws HttpException
         */
        public Object doInBackground(int requestCode, String parameter) throws HttpException;
        /**
         * 成功方法（可直接更新UI）
         * @param requestCode 请求码
         * @param result 返回结果
         */
        public void onSuccess(int requestCode, Object result);

        /**
         * 失败方法（可直接更新UI）
         * @param requestCode 请求码
         * @param state 返回状态
         * @param result 返回结果
         */
        public void onFailure(int requestCode, int state, Object result);
}
