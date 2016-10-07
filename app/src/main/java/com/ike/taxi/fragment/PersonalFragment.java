package com.ike.taxi.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.ike.taxi.R;
import com.ike.taxi.activity.CommentActivity;
import com.ike.taxi.activity.LoginActivity;
import com.ike.taxi.activity.Test2Activity;
import com.ike.taxi.activity.TestActivity;
import com.ike.taxi.personal.activity.HeadSettingActivity;
import com.ike.taxi.widget.CircleImageView;

public class PersonalFragment extends Fragment implements View.OnClickListener {
    private View view;
    private CircleImageView civ_icon;
    private LinearLayout ll_test;
    private LinearLayout ll_test2;
    private LinearLayout ll_usr_login;
    private LinearLayout ll_comment;
    private Button btn_exit;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_personal, container, false);
        initView();
        return view;
    }

    private void initView() {
        civ_icon= (CircleImageView) view.findViewById(R.id.civ_icon);
        civ_icon.setOnClickListener(this);
        ll_test = (LinearLayout) view.findViewById(R.id.ll_test);
        ll_test2= (LinearLayout) view.findViewById(R.id.ll_test2);
        ll_usr_login= (LinearLayout) view.findViewById(R.id.ll_user_login);
        ll_comment= (LinearLayout) view.findViewById(R.id.ll_comment);
        btn_exit= (Button) view.findViewById(R.id.btn_exit);
        ll_test.setOnClickListener(this);
        ll_test2.setOnClickListener(this);
        ll_usr_login.setOnClickListener(this);
        ll_comment.setOnClickListener(this);
        btn_exit.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent=new Intent();
        switch (view.getId()){
            case R.id.civ_icon: //头像
                intent.setClass(getActivity(), HeadSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_test:
                intent.setClass(getActivity(), TestActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_test2:
                intent.setClass(getActivity(), Test2Activity.class);
                startActivity(intent);
                break;
            case R.id.ll_user_login:  //登录
                intent.setClass(getActivity(), LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.ll_comment:  //评论
                intent.setClass(getActivity(), CommentActivity.class);
                startActivity(intent);
                break;
            case R.id.btn_exit:  //退出
                getActivity().finish();
                break;
            default:
                break;
        }
    }
}
