package com.ike.taxi.chat.activity;

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
    /**
     * token 的主要作用是身份授权和安全，因此不能通过客户端直接访问融云服务器获取 token，
     * 您必须通过 Server API 从融云服务器 获取 token 返回给您的 App，并在之后连接时使用
     */
    private String ike_token = "A8c81qVrwSUIKbG9Hy8m/UAykM59qe1p+0C40JiOBPIsnOKidMQgg/BGijhlLj1kHqrIcJtCjtQClIckTn2P6A==";
    private String lb_token = "lq9/15hsC3aE/Y++gipntkAykM59qe1p+0C40JiOBPIsnOKidMQggxJ0CfqakfOGEY+TkbcYznYClIckTn2P6A==";
    private String zgl_token = "7z0G6Y57qYiQwKZkQBx+eEAykM59qe1p+0C40JiOBPIsnOKidMQggydOmzsQ/9C2gWLXV9wXGGUClIckTn2P6A==";

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
                if ("ike".equals(user)) {
                    connect(ike_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", ike_token);
                    edit.apply();
//                    edit.commit();
                }
                if ("lb".equals(user)) {
                    connect(lb_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", lb_token);
                    edit.apply();
                }
                if ("zgl".equals(user)) {
                    connect(zgl_token);
                    SharedPreferences.Editor edit = DemoContext.getInstance().getSharedPreferences().edit();
                    edit.putString("DEMO_TOKEN", zgl_token);
                    edit.apply();
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

                    Log.d("LoginActivity", "--onTokenIncorrect");
                }

                /**
                 * 连接融云成功
                 * @param userid 当前 token
                 */
                @Override
                public void onSuccess(String userid) {
                    Log.e("===========", userid);

                    Log.d("LoginActivity", "--onSuccess" + userid);
                    startActivity(new Intent(TestLoginActivity .this, ChatActivity.class));
                    finish();
                }

                /**
                 * 连接融云失败
                 * @param errorCode 错误码，可到官网 查看错误码对应的注释
                 *                  http://www.rongcloud.cn/docs/android.html#常见错误码
                 */
                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                    Log.d("LoginActivity", "--onError" + errorCode);
                }
            });
        }
    }
}
