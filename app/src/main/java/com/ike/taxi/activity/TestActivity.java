package com.ike.taxi.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.entity.TestEntity;
import com.ike.taxi.network.HttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import okhttp3.Call;

/**
 * 测试
 */
public class TestActivity extends BaseActivity {
    private List<Map<String,String>> map=new ArrayList<Map<String,String>>();
    private TextView textView;
    private String url="http://192.168.1.103:8888/Nearby";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        initImage();
//        initHttp();
//        initAsyncHttp();
//        initOkhttpUtils();
        initHttpUtils();

    }

    private void initHttpUtils() {
        HttpUtils.getRequest(url, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                Toast.makeText(TestActivity.this,"连接失败",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(String response, int id) {
                /*Gson gson=new Gson();
                Type type=new TypeToken<String>(){}.getType();
                String sss=gson.fromJson(response,type);
//                user=gson.fromJson(response,type);
                double latitude=sss.
                double longitude=testEntity.getLongitude();
                Log.e("-------=",latitude+","+longitude);
                Toast.makeText(TestActivity.this, "连接成功", Toast.LENGTH_SHORT).show();*/
                try {
                    JSONArray jsonArray=new JSONArray(response);
                    for(int i=0;i<jsonArray.length();i++){
                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                        double latitude=jsonObject.getDouble("latitude");
                        double longitude=jsonObject.getDouble("longitude");
                        TestEntity testEntity=new TestEntity();
                        testEntity.setLatitude(jsonObject.getDouble("latitude"));
                        testEntity.setLongitude(jsonObject.getDouble("longitude"));
                        Log.e("----------==",latitude+","+longitude);
                        Toast.makeText(TestActivity.this, "连接成功", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
//                initView();
            }
        });
    }

   /* private void initView() {
        textView= (TextView) findViewById(R.id.text);
        for(int i=0;i<user.size();i++){
            textView.append("1:"+user.get(i).getUser()+":"+user.get(i).getPwd()+"\n");
        }
    }*/

    private void initImage() {
        ImageView imageView = (ImageView) findViewById(R.id.image);
//        String imageUrl="http://img1.imgtn.bdimg.com/it/u=3597870150,64868101&fm=21&gp=0.jpg";
//        MyBitmapUtils myBitmapUtils= new MyBitmapUtils();
//        myBitmapUtils.disPlay(imageView,imageUrl);
    }
}