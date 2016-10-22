package com.ike.taxi.activity;

import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Button;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;

public class Test2Activity extends BaseActivity{
    private Button button;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test2);
        /*button= (Button) findViewById(R.id.button);
        button.setOnClickListener(this);*/
    }

    /*@Override
    public void onClick(View view) {
        if(view==button){
//            throw new RuntimeException("自定义异常");
//            changeIcon();
        }
    }*/

    private void changeIcon() {
        PackageManager pm = getApplicationContext().getPackageManager();
        System.out.println(getComponentName());
        pm.setComponentEnabledSetting(getComponentName(),
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP);
        pm.setComponentEnabledSetting(new ComponentName(
                        getBaseContext(),
                        "com.ike.taxi.chat.activity.ChatAcitivity"),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP);
    }
}
