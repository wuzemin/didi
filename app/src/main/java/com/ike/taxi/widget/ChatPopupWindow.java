package com.ike.taxi.widget;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import com.ike.taxi.R;
import com.ike.taxi.chat.activity.AddFriendActivity;
import com.ike.taxi.chat.activity.SelectFriendsActivity;

/**
 * Created by Min on 2016/11/5.
 */

public class ChatPopupWindow extends PopupWindow implements View.OnClickListener {
    private Context context;

    public ChatPopupWindow(Context context) {
        this.context=context;
        LayoutInflater inflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view=inflater.inflate(R.layout.popupwindow_chat,null);
        //设置SelectPicPopupWindow的view
        this.setContentView(view);
        //设置宽,高
        this.setWidth(ViewGroup.LayoutParams.WRAP_CONTENT);
        this.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        //设置可点击
        this.setFocusable(true);
        //点击外面取消
        this.setOutsideTouchable(true);
        //刷新状态
        this.update();
        ColorDrawable color=new ColorDrawable(0000000000);
        this.setBackgroundDrawable(color);
        //动画
//        this.setAnimationStyle();

        LinearLayout ll_pop_chat= (LinearLayout) view.findViewById(R.id.ll_pop_chat);
        LinearLayout ll_pop_group= (LinearLayout) view.findViewById(R.id.ll_pop_group);
        LinearLayout ll_pop_add= (LinearLayout) view.findViewById(R.id.ll_pop_add);
        ll_pop_chat.setOnClickListener(this);
        ll_pop_group.setOnClickListener(this);
        ll_pop_add.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ll_pop_chat:
//                T.showShort(context,"发起聊天");
                context.startActivity(new Intent(context,SelectFriendsActivity.class));
                ChatPopupWindow.this.dismiss();
                break;
            case R.id.ll_pop_group:
//                T.showShort(context,"创建群组");
                Intent intent=new Intent(context,SelectFriendsActivity.class);
                intent.putExtra("isGroup",true);
                context.startActivity(intent);
                ChatPopupWindow.this.dismiss();
                break;
            case R.id.ll_pop_add:
//                T.showShort(context,"添加好友");
                context.startActivity(new Intent(context, AddFriendActivity.class));
                ChatPopupWindow.this.dismiss();
                break;
            default:
                break;
        }
    }

    public void showPopupWindow(View view){
        if(!this.isShowing()){
            //以下拉的方式显示
            this.showAsDropDown(view,0,0);
        }else {
            this.dismiss();
        }
    }
}
