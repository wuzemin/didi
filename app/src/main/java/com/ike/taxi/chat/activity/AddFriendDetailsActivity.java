package com.ike.taxi.chat.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.bean.LoginResult;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.T;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

/**
 * 添加好友时信息
 */
public class AddFriendDetailsActivity extends BaseActivity {

    @BindView(R.id.iv_friend_head)
    ImageView ivFriendHead;
    @BindView(R.id.tv_friend_name)
    TextView tvFriendName;
    @BindView(R.id.btn_add_friend)
    Button btnAddFriend;

    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend_details);
        ButterKnife.bind(this);
        Intent intent = getIntent();
        String hearUri=intent.getStringExtra("headUri");
        userName=intent.getStringExtra("userName");
        if(TextUtils.isEmpty(hearUri)){
            ivFriendHead.setImageResource(R.mipmap.rc_default_portrait);
        }else {
            ImageLoader.getInstance().displayImage(hearUri, ivFriendHead);
        }
        tvFriendName.setText(userName);
        btnAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder=new AlertDialog.Builder(mContext);
                builder.setTitle("加为好友");
                final EditText editText=new EditText(mContext);
                builder.setView(editText);
                builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        HttpUtils.sendPostTextRequest("/friend_request", userName, new StringCallback() {
                            @Override
                            public void onError(Call call, Exception e, int id) {
                                T.showShort(mContext,"连接错误");
                            }

                            @Override
                            public void onResponse(String response, int id) {
                                Gson gson=new Gson();
                                Type type=new TypeToken<LoginResult>(){}.getType();
                                LoginResult loginResult=gson.fromJson(response,type);
                                if(loginResult.getCode()==200){
                                    T.showShort(mContext,"请求成功，等待对方同意");
                                }else {
                                    T.showShort(mContext,"请求失败，请检查网络是否完好");
                                }
                            }
                        });
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
            }
        });
    }
}
