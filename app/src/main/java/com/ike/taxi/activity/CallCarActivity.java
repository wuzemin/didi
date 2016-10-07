package com.ike.taxi.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.services.core.LatLonPoint;
import com.ike.taxi.R;

public class CallCarActivity extends AppCompatActivity implements View.OnTouchListener {
    //叫车
    private EditText et_call_start;
    private EditText et_call_end;
    private EditText et_call_number;
    private Button btn_call_car;
    private final int REQUEST_CODE=1;

    private double startLatitude;
    private double startLongitude;
    private double endLatitude;
    private double endLongitude;
    private LatLonPoint mStartPoint;  //起点
    private LatLonPoint mEndPoint;    //终点


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_call_car);
        Intent intent=getIntent();
        String mstartPoint=intent.getStringExtra("startpoint");
        et_call_start.setText(mstartPoint);
        initCallCar();
    }
    private void initCallCar() {
        et_call_start = (EditText) findViewById(R.id.et_call_start);
        et_call_end = (EditText) findViewById(R.id.et_call_end);
        et_call_number = (EditText) findViewById(R.id.et_number);
        btn_call_car = (Button) findViewById(R.id.btn_call_car);
        et_call_end.setOnTouchListener(this);

        btn_call_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String call_start = et_call_start.getText().toString();
                String call_end = et_call_end.getText().toString();
                String call_number = et_call_number.getText().toString();
                if ("".equals(call_start) || "".equals(call_end) || "".equals(call_number)) {
                    Toast.makeText(getApplicationContext(), "不能为空", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getApplicationContext(), "成功", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onTouch(View view, MotionEvent event) {
        if(event.getAction()==MotionEvent.ACTION_DOWN) {
            Intent intent = new Intent(getApplicationContext(), EndActivity.class);
            startActivityForResult(intent, REQUEST_CODE);
        }
        return true;
    }
    //回调事件--获取终点事件
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode==REQUEST_CODE){
            Bundle bundle=data.getExtras();
            String address=bundle.getString("addr");
            String lat=bundle.getString("lat");
            String[] strPoint;
            if(!"".equals(lat)){
                strPoint=lat.split(",");
                endLatitude=Double.parseDouble(strPoint[0]);
                endLongitude=Double.parseDouble(strPoint[1]);
                et_call_end.setText(address);
                mEndPoint=new LatLonPoint(endLatitude,endLongitude);
//                Log.v("-------=====",address+"------"+lat+"----"+mEndPoint);
            }else {
                et_call_end.setText("");
            }

        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
