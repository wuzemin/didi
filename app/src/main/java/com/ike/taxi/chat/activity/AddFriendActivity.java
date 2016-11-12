package com.ike.taxi.chat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.bean.LoginResult;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.AMUtils;
import com.ike.taxi.utils.T;
import com.ike.taxi.widget.image.SelectableRoundedImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imageloader.core.ImageLoader;
import okhttp3.Call;

/**
 * 添加好友
 */
public class AddFriendActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.et_search_friends)
    EditText etSearchFriends;
    @BindView(R.id.siv_friend)
    SelectableRoundedImageView sivFriend;
    @BindView(R.id.tv_friend)
    TextView tvFriend;
    @BindView(R.id.ll_friend)
    LinearLayout llFriend;

    private String phone;
    private String headUri;
    private String userName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_friend);
        ButterKnife.bind(this);
        llFriend.setOnClickListener(this);
        etSearchFriends.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(charSequence.length()==11){
                    phone=charSequence.toString().trim();
                    if(!AMUtils.isMobile(phone)){
                        T.showShort(mContext,"非手机号码");
                        return;
                    }
                    earchFriends();
                }else {
                    llFriend.setVisibility(View.GONE);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
    }

    private void earchFriends() {
        HttpUtils.sendPostTextRequest("/friend", phone, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<LoginResult>(){}.getType();
                LoginResult loginResult=gson.fromJson(response,type);
                headUri=loginResult.getMsg().getPortrait();
                userName=loginResult.getMsg().getNickname();
                if(loginResult.getCode()==200) {
                    llFriend.setVisibility(View.VISIBLE);
                    ImageLoader.getInstance().displayImage(loginResult.getMsg().getPortrait(), sivFriend);
                    tvFriend.setText(phone);
                }else {
                    T.showShort(mContext,"用户不存在");
                }
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent(mContext,AddFriendDetailsActivity.class);
        intent.putExtra("hearUri",headUri);
        intent.putExtra("userName",userName);
        startActivity(intent);
    }
}
