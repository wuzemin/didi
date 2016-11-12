package com.ike.taxi.chat.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.bean.FriendInfo;
import com.ike.taxi.chat.bean.Group;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.L;
import com.ike.taxi.utils.T;
import com.ike.taxi.widget.image.SelectableRoundedImageView;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 创建群组
 */
public class CreateGroupActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.siv_group_icon)
    SelectableRoundedImageView sivGroupIcon;
    @BindView(R.id.et_group_name)
    EditText etGroupName;
    @BindView(R.id.btn_confirm)
    Button btnConfirm;

    private List<FriendInfo> memberList =new ArrayList<>(0);
    private List<String> groupIdList=new ArrayList<>(0);
    private String groupId,groupName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_group);
        ButterKnife.bind(this);
//        memberList = (List<FriendInfo>) getIntent().getSerializableExtra("group_member");
        initView();

    }

    private void initView() {
        tvBack.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.siv_group_icon:  //头像
                T.showShort(mContext,"touxiang");
                break;
            case R.id.btn_confirm:
                String groupid=getIntent().getStringExtra("group_id");
                memberList.add(new FriendInfo(groupid));
                if(memberList !=null && memberList.size()>0){
                    groupIdList.add(getSharedPreferences("confing",MODE_PRIVATE).getString("loginid",""));
//            for(FriendInfo friendInfo: memberList){
                    groupIdList.add(groupid);
//            }
                }
                groupName=etGroupName.getText().toString().trim();
                if(TextUtils.isEmpty(groupName)){
                    T.showShort(mContext,"群名不能为空");
                    break;
                }
                if(groupIdList.size()>0){
                    creategroup();
                }
                break;
            default:
                break;
        }
    }

    private void creategroup() {
        HttpUtils.senddPostListRequest("/group/create", groupName, groupIdList, new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<Group>(){}.getType();
                Group group=gson.fromJson(response,type);
                if(group.getCode()==200){
                    groupId=group.getMsg().getId();
                    L.e("-=-=-=-=-=-=",groupId);
                }

            }
        });
    }
}
