package com.ike.taxi.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;

public class Test2Activity extends BaseActivity implements View.OnClickListener {
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        button= (Button) findViewById(R.id.button);
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view==button){
            throw new RuntimeException("自定义异常");
        }
    }
}
