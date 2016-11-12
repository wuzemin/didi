package com.ike.taxi.chat.activity;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.TextView;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.base.BaseRecyclerAdapter;
import com.ike.taxi.base.BaseRecyclerHolder;
import com.ike.taxi.chat.bean.NewFriendList;
import com.ike.taxi.utils.CommonUtils;
import com.ike.taxi.utils.T;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 系统信息--新朋友列表
 */
public class NewFriendListActivity extends BaseActivity {

    @BindView(R.id.tv_new_friends)
    TextView tvNewFriends;
    @BindView(R.id.rv_new_friend)
    RecyclerView rvNewFriend;

    private BaseRecyclerAdapter<NewFriendList> adapter;
    private List<NewFriendList> lists;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_friend_list);
        ButterKnife.bind(this);
        if(!CommonUtils.isNetConnect(mContext)){
            T.showShort(mContext,R.string.no_network);
            return;
        }
        initAdapter();
        initData();
    }

    private void initData() {

    }

    private void initAdapter() {
        adapter=new BaseRecyclerAdapter<NewFriendList>(mContext,lists,R.layout.new_friend_list) {

            @Override
            public void convert(BaseRecyclerHolder holder, NewFriendList item, int position, boolean isScrolling) {
                holder.setImageByUrl(R.id.new_header,item.getResults().get(position).getUser().getPortraitUri());
                holder.setText(R.id.ship_name,item.getResults().get(position).getUser().getNickname());
                holder.setText(R.id.ship_message,item.getResults().get(position).getMessage());
                int mState=item.getResults().get(position).getStatus();
                switch (mState) {
                    case 11: //收到了好友邀请
                        holder.setText(R.id.ship_state,"接受");
                        break;
                    case 10: // 发出了好友邀请
                        holder.setText(R.id.ship_state,"已请求");
                        break;
                    case 21: // 忽略好友邀请
                        holder.setText(R.id.ship_state,"已忽略");
                        break;
                    case 20: // 已是好友
                        holder.setText(R.id.ship_state,"已添加");
                        break;
                    case 30: // 删除了好友关系
                        holder.setText(R.id.ship_state,"已删除");
                        break;
                }
            }
        };
        rvNewFriend.setAdapter(adapter);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext,LinearLayoutManager.VERTICAL,false);
        rvNewFriend.setLayoutManager(linearLayoutManager);
    }
}
