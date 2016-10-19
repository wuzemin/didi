package com.ike.taxi.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.entity.LoginEntity;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.MD5Util;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;
import java.util.Random;

import cn.smssdk.SMSSDK;
import okhttp3.Call;

/**
 * A login screen that offers login via user/password.
 * 登录
 */
public class LoginActivity extends BaseActivity implements OnClickListener {

    private AutoCompleteTextView tv_user;
    private EditText tv_pwd;
    private TextView tv_register;
    private static String APPKEY="15cfe7a51e5c4";
    private static String APPSECRET="f9b566453fc559487eb0f6419aa42030";
    private Button btn_login;
    private String user;
    private String pwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        SMSSDK.initSDK(this,APPKEY,APPSECRET); //初始化
        initView();
//        loadData();
    }

    private void initView() {
        tv_user = (AutoCompleteTextView) findViewById(R.id.user);
        tv_pwd = (EditText) findViewById(R.id.password);
        tv_register= (TextView) findViewById(R.id.tv_register);
        btn_login = (Button) findViewById(R.id.sign_in_button);
        tv_register.setOnClickListener(this);
        btn_login.setOnClickListener(this);
        Button button= (Button) findViewById(R.id.btn_test);
        button.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this, TestActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_register:
                Intent intent=new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
                /*//注册手机号
                RegisterPage registerPage=new RegisterPage();
                //注册回调事件
                registerPage.setRegisterCallback(new EventHandler(){
                    @Override
                    public void afterEvent(int event, int result, Object data) {
                        if(result==SMSSDK.RESULT_COMPLETE){
                            HashMap<String,Object> maps= (HashMap<String, Object>) data;
                            String country= (String) maps.get("country");
                            String phone= (String) maps.get("phone");
                            submitUserInfo(country,phone);
                        }
                    }
                });
                //显示注册界面
                registerPage.show(LoginActivity.this);*/
                break;
            case R.id.sign_in_button:
                user=tv_user.getText().toString().trim();
                pwd=tv_pwd.getText().toString().trim();
                login(user,pwd);
                break;
            default:
                break;
        }
    }

    private void login(String user, String pwd) {
        if("".equals(user) || "".equals(pwd)) {
            Toast.makeText(LoginActivity.this,"用户名和密码不能为空",Toast.LENGTH_SHORT).show();
        }else if(user!=null && pwd!=null) {
            String url="/login";
            Log.e("==========",url+"?username="+user+"&password="+pwd);
            HttpUtils.sendGetRequest(url, user, pwd, new StringCallback() {
                //                    HttpUtils.sendPostRequest(url, user, pwd, new StringCallback() {
                @Override
                public void onError(Call call, Exception e, int id) {
                    Toast.makeText(LoginActivity.this, "连接错误", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onResponse(String response, int id) {
                    HttpUtils.setCookie(LoginActivity.this);
                    Gson gson=new Gson();
                    Type type=new TypeToken<LoginEntity>(){}.getType();
                    LoginEntity loginEntity=gson.fromJson(response,type);
                    String code=loginEntity.getCode();
//                            Log.e("----------=",code+"");
                    if("200".equals(code)){
                        Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                        finish();
                    }else {
                        Toast.makeText(LoginActivity.this, "登录失败,用户名或密码错误！", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }else {
            Toast.makeText(LoginActivity.this,"登录失败",Toast.LENGTH_SHORT).show();
        }
    }

    //存数据
    private void saveData() {
        SharedPreferences sharedPreferences = getSharedPreferences("LoginInfo",MODE_PRIVATE);
        SharedPreferences.Editor editor=sharedPreferences.edit();
        try {
            String md5_user= MD5Util.encryptMD5(user.getBytes());
            String md5_pwd=MD5Util.encryptMD5(pwd.getBytes());
            editor.putString("user",md5_user);
            editor.putString("pwd",md5_pwd);
            editor.commit();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }

    }
    //读取数据
    private void loadData(){
        SharedPreferences sharedPreferences=getSharedPreferences("LoginInfo",MODE_PRIVATE);
        tv_user.setText(sharedPreferences.getString("user",""));
        tv_pwd.setText(sharedPreferences.getString("pwd",""));
    }

    // 提交用户信息
    public void submitUserInfo(String country,String phone){
        Random random = new Random();
        String uid=Math.abs(random.nextInt())+"";
        String nickName="IKE";
        SMSSDK.submitUserInfo(uid,nickName,null,country,phone);
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

}

