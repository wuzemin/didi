package com.ike.taxi.chat.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.adapter.SelectFriendAdapter;
import com.ike.taxi.chat.bean.FriendInfo;
import com.ike.taxi.chat.server.SideBar;
import com.ike.taxi.network.HttpUtils;
import com.ike.taxi.utils.T;
import com.zhy.http.okhttp.callback.StringCallback;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import okhttp3.Call;

/**
 * 选择好友
 */
public class SelectFriendsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.rv_select_friend)
    RecyclerView rvSelectFriend;
    @BindView(R.id.dis_sidebar)
    SideBar disSidebar;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_confirm)
    TextView tvConfirm;

//    private BaseRecyclerAdapter<FriendInfo> adapter;
    private List<FriendInfo> list=new ArrayList<>();
    private boolean isGroup;
    private SelectFriendAdapter adapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_friends);
        ButterKnife.bind(this);
        isGroup=getIntent().getBooleanExtra("isGroup",false);
        if(isGroup){
            tvBack.setText("选择群组成员");
        }
        initView();
        initList();

    }

    private void initList() {
        HttpUtils.postRequest("/friendinfo", new StringCallback() {
            @Override
            public void onError(Call call, Exception e, int id) {
                T.showShort(mContext,"网络连接错误");
            }

            @Override
            public void onResponse(String response, int id) {
                Gson gson=new Gson();
                Type type=new TypeToken<List<FriendInfo>>(){}.getType();
                List<FriendInfo> friendInfo=gson.fromJson(response,type);
                if(friendInfo!=null){
                    for(FriendInfo info:friendInfo){
                        list.add(new FriendInfo(info.getUserId(),info.getName(),info.getPortraitUri(),info.getDisplayName()));
                    }
                }
                initAdapter();
            }
        });
    }

    private void initAdapter() {
        /*adapter=new BaseRecyclerAdapter<FriendInfo>(mContext,list,R.layout.select_friend_item) {
            @Override
            public void convert(BaseRecyclerHolder holder, FriendInfo item, int position, boolean isScrolling) {
                holder.setImageByUrl(R.id.frienduri,item.getPortraitUri());
                holder.setText(R.id.friendname,item.getName());
            }
        };
        rvSelectFriend.setAdapter(adapter);
        final LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvSelectFriend.setLayoutManager(linearLayoutManager);
        adapter.setOnItemClickListener(new BaseRecyclerAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View view, int position) {
                L.e("============",list.get(position).getName());
                Intent intent=new Intent(mContext,CreateGroupActivity.class);
//                intent.putExtra("Group_member", (Serializable) list);
                intent.putExtra("group_name",list.get(position).getName());
                intent.putExtra("group_id",list.get(position).getUserId());
                startActivity(intent);
            }
        });*/
        adapter=new SelectFriendAdapter(mContext,list);
        rvSelectFriend.setAdapter(adapter);
        LinearLayoutManager lm=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvSelectFriend.setLayoutManager(lm);
    }

    private void initView() {
        tvBack.setOnClickListener(this);
        tvConfirm.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_confirm:
                T.showShort(mContext,"sdfasfdasjfd");
                break;
        }
    }
}
