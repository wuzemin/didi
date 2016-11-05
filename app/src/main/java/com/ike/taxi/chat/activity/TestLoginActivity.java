package com.ike.taxi.chat.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.application.App;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.DemoContext;
import com.ike.taxi.chat.bean.LoginResult;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.T;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;
import okhttp3.Call;

public class TestLoginActivity extends BaseActivity {

    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.bt1)
    Button bt1;
    @BindView(R.id.et2)
    EditText et2;
/**
     * token 的主要作用是身份授权和安全，因此不能通过客户端直接访问融云服务器获取 token，
     * 您必须通过 Server API 从融云服务器 获取 token 返回给您的 App，并在之后连接时使用
     */

    private String swk_token = "UwTQ9FByoUwK0PFRbOdb0gG+H82kq8V0XNWtsNbFJfb\n" +
        "+zzkRavevEC1z3f6agUuyYBJ/eeCKrHw=";
    private String ts_token = "tjvDl1ZCAFv9K8kK9Qaj20AykM59qe1p\n" +
            "+0C40JiOBPJVDxfHCGgi1ywgwKaAdeXBA7mbFhnP3VQClIckTn2P6A==";
    private String zbj_token = "1F+HfXzztpLrsdngQ9KsvAG+H82kq8V0XNWtsNbFJfb+zzkRavevEHAkQ9XzNw+8aS\n" +
            "+M0INstcI=";

    private String user;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        ButterKnife.bind(this);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);
        return true;
    }

    private void initView() {
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = et1.getText().toString().trim();
                password=et2.getText().toString().trim();
                initToken();
            }
        });
    }

    private void initToken() {
        HttpUtils.sendGetRequest("/token", user, password, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(TestLoginActivity.this,"连接错误！");
            }

            @Override
            public void onResponse(String response, int id) {
                HttpUtils.setCookie(TestLoginActivity.this);
                Gson gson=new Gson();
                Type type=new TypeToken<LoginResult>(){}.getType();
                LoginResult loginResult=gson.fromJson(response,type);
                int code=loginResult.getCode();
                if(code==200){
                    String uid=loginResult.getMsg().getId();
                    String token=loginResult.getMsg().getToken();
                    String nickName=loginResult.getMsg().getNickname();
                    String portraitUri=loginResult.getMsg().getPortrait();
                    connect(token);
                    SharedPreferences.Editor editor= DemoContext.getInstance().getSharedPreferences().edit();
                    editor.putString("loginToken", token);
                    editor.putString("loginphone", user);
                    editor.putString("loginpassword", password);
                    editor.putString("loginid",uid);
                    editor.putString("loginnickname",nickName);
                    editor.putString("loginPortrait",portraitUri);
                    editor.apply();
                }
            }
        });
    }


/**
     * 建立与融云服务器的连接
     *
     * @param token
     */

    private void connect(String token) {
        final Message message=new Message();

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {


/**
             * IMKit SDK调用第二步,建立与服务器的连接
             */

            RongIM.connect(token, new RongIMClient.ConnectCallback() {


/**
                 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
                 */

                @Override
                public void onTokenIncorrect() {
                    message.what=0;
                    handler.sendMessage(message);
                }


/**
                 * 连接融云成功
                 * @param userid 当前 token
                 */

                @Override
                public void onSuccess(String userid) {
                    message.what=1;
                    message.obj=userid;
                    handler.sendMessage(message);
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
                 */

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {
                    message.what=1;
                    message.obj=errorCode;
                    handler.sendMessage(message);
//                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }
    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 0:
                    T.showShort(TestLoginActivity.this,"Token 错误，Token 已经过期");
                    break;
                case 1:
                    T.showShort(TestLoginActivity.this, "--onSuccess--" + msg.obj);
//                    Log.d("LoginActivity", "--onSuccess" + userid);
                    startActivity(new Intent(TestLoginActivity .this, ChatActivity.class));
                    finish();
                    break;
                case 2:
                    T.showShort(TestLoginActivity.this,"--"+msg.obj);
                    break;
                default:
                    break;
            }
        }
    };
}
/*package com.ike.taxi.chat.activity;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ike.taxi.R;
import com.ike.taxi.application.App;
import com.ike.taxi.chat.DemoContext;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imkit.RongIM;
import io.rong.imlib.RongIMClient;

public class TestLoginActivity extends Activity {

    @BindView(R.id.et1)
    EditText et_1;
    @BindView(R.id.bt1)
    Button bt1;
    *//**
 * token 的主要作用是身份授权和安全，因此不能通过客户端直接访问融云服务器获取 token，
 * 您必须通过 Server API 从融云服务器 获取 token 返回给您的 App，并在之后连接时使用
 *//*
    private String swk_token = "UwTQ9FByoUwK0PFRbOdb0gG+H82kq8V0XNWtsNbFJfb" +
            "+zzkRavevEC1z3f6agUuyYBJ/eeCKrHw=";
    private String ts_token = "tjvDl1ZCAFv9K8kK9Qaj20AykM59qe1p" +
            "+0C40JiOBPJVDxfHCGgi1ywgwKaAdeXBA7mbFhnP3VQClIckTn2P6A==";
    private String zbj_token = "1F+HfXzztpLrsdngQ9KsvAG+H82kq8V0XNWtsNbFJfb+zzkRavevEHAkQ9XzNw" +
            "+8aS+M0INstcI=";
    private String ss_token="qIgGBT2emNGHTu8nNk7FBEAykM59qe1p" +
            "+0C40JiOBPJVDxfHCGgi10mhw7e71IuvgjysYdTn/WQClIckTn2P6A==";

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_login);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                user = et_1.getText().toString().trim();
                if ("swk".equals(user)) {
                    connect(swk_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", swk_token);
                    edit.apply();
//                    edit.commit();
                }
                if ("ts".equals(user)) {
                    connect(ts_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", ts_token);
                    edit.apply();
                }
                if ("zbj".equals(user)) {
                    connect(zbj_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", zbj_token);
                    edit.apply();
                }
                if ("ss".equals(user)) {
                    connect(ss_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", ss_token);
                    edit.apply();
                }
            }
        });
    }

    *//**
 * 建立与融云服务器的连接
 *
 * @param token
 *//*
    private void connect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            *//**
 * IMKit SDK调用第二步,建立与服务器的连接
 *//*
            RongIM.connect(token, new RongIMClient.ConnectCallback() {

                *//**
 * Token 错误，在线上环境下主要是因为 Token 已经过期，您需要向 App Server 重新请求一个新的 Token
 *//*
                @Override
                public void onTokenIncorrect() {

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                *//**
 * 连接融云成功
 * @param userid 当前 token
 *//*
                @Override
                public void onSuccess(String userid) {
                    Log.e("===========", userid);

                    Log.d("LoginActivity", "--onSuccess" + userid);
                    startActivity(new Intent(TestLoginActivity .this, ChatActivity.class));
                    finish();
                }

                *//**
 * 连接融云失败
 * @param errorCode 错误码，可到官网 查看错误码对应的注释
 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
 *//*
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }
}*/