package com.ike.taxi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.ike.taxi.MainActivity;
import com.ike.taxi.R;

public class LogoActivity extends AppCompatActivity {
    private SharedPreferences sharedPreferences;
    private boolean login_message;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);
        sharedPreferences=getSharedPreferences("login_message",MODE_PRIVATE);
        login_message=sharedPreferences.getBoolean("login",false);
        if(login_message){
            Intent intent=new Intent(this, MainActivity.class);
            startActivity(intent);
        }else {
            Intent intent=new Intent(this,LoginActivity.class);
            startActivity(intent);
        }
    }
}
