package com.ike.taxi.chat.activity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ike.taxi.R;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.bean.FriendInfo;
import com.ike.taxi.widget.CircleImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.rong.imageloader.core.ImageLoader;
import io.rong.imkit.RongIM;

/**
 * 好友信息（好友界面-->好友-->好友信息）
 */
public class FriendDetailsActivity extends BaseActivity implements View.OnClickListener {

    @BindView(R.id.civ_user_head)
    CircleImageView civUserHead;
    @BindView(R.id.tv_username)
    TextView tvUsername;
    @BindView(R.id.tv_occupation)
    TextView tvOccupation;
    @BindView(R.id.ll_call)
    LinearLayout llCall;
    @BindView(R.id.ll_send_sms)
    LinearLayout llSendSms;
    @BindView(R.id.tv_telephone)
    TextView tvTelephone;
    @BindView(R.id.tv_email)
    TextView tvEmail;
    @BindView(R.id.btn_send_message)
    Button btnSendMessage;
    @BindView(R.id.ll_send_email)
    LinearLayout llSendEmail;

    private String userid;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_friend_details);
        ButterKnife.bind(this);
        Intent intent=getIntent();
        FriendInfo info= (FriendInfo) intent.getSerializableExtra("FriendDetails");
        userid=info.getUserId();
        username=info.getName();
        if(info!=null){
            ImageLoader.getInstance().displayImage(info.getPortraitUri(),civUserHead);
            tvUsername.setText(username);
        }
        initView();

    }

    private void initView() {
        llCall.setOnClickListener(this);
        llSendSms.setOnClickListener(this);
        llSendEmail.setOnClickListener(this);
        btnSendMessage.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_call:  //打电话
                final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
                builder.setTitle("18819493906");
                builder.setPositiveButton("呼叫", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        Intent intent = new Intent(Intent.ACTION_CALL);
                        Uri uri = Uri.parse("tel:" + "18819493906");
                        intent.setData(uri);
                        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        startActivity(intent);
                    }
                });
                builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                });
                builder.show();
                break;
            case R.id.ll_send_sms:  //发短信
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.parse("smsto:"+"18819493906"));
                intent.putExtra("sms_body", "");
                startActivity(intent);
                break;
            case R.id.ll_send_email:  //发邮件
                Intent intent1=new Intent(Intent.ACTION_SENDTO);
                intent1.setData(Uri.parse("mailto:2535383282@qq.com"));
                intent1.putExtra(Intent.EXTRA_SUBJECT,"标题");
                intent1.putExtra(Intent.EXTRA_TEXT,"");
                startActivity(intent1);
                break;
            case R.id.btn_send_message:
                RongIM.getInstance().startPrivateChat(mContext,userid,username);
                break;
            default:
                break;
        }
    }
}
