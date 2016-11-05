package com.ike.taxi.chat.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.RequiresApi;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.ike.taxi.R;
import com.ike.taxi.activity.LoginActivity;
import com.ike.taxi.base.BaseActivity;
import com.ike.taxi.chat.AppContext;
import com.ike.taxi.chat.bean.ConversationUser;
import com.ike.taxi.utils.L;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.eventbus.EventBus;
import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imkit.widget.InputView;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

/**
 * 会话页面
 */


public class ConversationActivity extends BaseActivity implements RongIM.UserInfoProvider {

    private List<ConversationUser> list;

    private String TAG = ConversationActivity.class.getSimpleName();
    //对方id
    private String mTargetId;
    //会话类型
    private Conversation.ConversationType mConversationType;
    //是否在讨论组内，如果不在讨论组内，则进入不到讨论组设置页面
    private boolean isFromPush = false;

    private SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        sp = getSharedPreferences("config", MODE_PRIVATE);

        Intent intent = getIntent();
        //用户头像
        list=new ArrayList<>();
        list.add(new ConversationUser("swk", "孙悟空", "http://pic37.nipic.com/20140120/9885883_125934577000_2.jpg"));
        list.add(new ConversationUser("ts", "唐僧", "http://news.mbalib.com/uploads/image/2015/0914/2015091442543ea2be07a6eb37e054bcc419daea.jpg"));
        list.add(new ConversationUser("zbj","猪八戒","http://img5.mypsd.com.cn/20111121/Mypsd_67401_201111210849430010B.jpg"));
        list.add(new ConversationUser("ss","沙僧","http://img3.redocn.com/20100623/20100623_25983d787b4f8608d140qd11qhbbtGNK.jpg"));
        RongIM.setUserInfoProvider(this, true);


        if (intent == null || intent.getData() == null)
            return;

        //展示如何从 Intent 中得到 融云会话页面传递的 Uri
        mTargetId = intent.getData().getQueryParameter("targetId");
        intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData()
                .getLastPathSegment().toUpperCase(Locale.getDefault()));

        isPushMessage(intent);

        // android 6.0 以上版本，监听SDK权限请求，弹出对应请求框。
        initPerssion();

        AppContext.getInstance().pushActivity(this);

    }

    /**
     * 判断是否是 Push 消息，判断是否需要做 connect 操作
     */
    private void isPushMessage(Intent intent) {

        if (intent == null || intent.getData() == null)
            return;

        //push
        if (intent.getData().getScheme().equals("rong") && intent.getData().getQueryParameter("isFromPush") != null) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("isFromPush").equals("true")) {
                //只有收到系统消息和不落地 push 消息的时候，pushId 不为 null。而且这两种消息只能通过 server 来发送，客户端发送不了。
//                RongIM.getInstance().getRongIMClient().recordNotificationEvent(id);
                isFromPush = true;
            } else if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                    RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, ChatActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    finish();
                    return;
                }
                enterActivity();
            } else {
                if (intent.getData().getPath().contains("conversation/system")) {
                    Intent intent1 = new Intent(mContext, ChatActivity.class);
                    intent1.putExtra("systemconversation", true);
                    startActivity(intent1);
                    finish();
                    return;
                }
                enterFragment(mConversationType, mTargetId);
            }

        } else {
            if (RongIM.getInstance().getCurrentConnectionStatus().equals(
                    RongIMClient.ConnectionStatusListener.ConnectionStatus.DISCONNECTED)) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        enterActivity();
                    }
                }, 300);
            } else {
                enterFragment(mConversationType, mTargetId);
            }
        }
    }


    /**
     * 收到 push 消息后，选择进入哪个 Activity
     * 如果程序缓存未被清理，进入 MainActivity
     * 程序缓存被清理，进入 LoginActivity，重新获取token
     * <p/>
     * 作用：由于在 manifest 中 intent-filter 是配置在 ConversationActivity 下面，所以收到消息后点击notifacition 会跳转到 DemoActivity。
     * 以跳到 MainActivity 为例：
     * 在 ConversationActivity 收到消息后，选择进入 MainActivity，这样就把 MainActivity 激活了，当你读完收到的消息点击 返回键 时，程序会退到
     * MainActivity 页面，而不是直接退回到 桌面。
     */
    private void enterActivity() {

        String token = sp.getString("loginToken", "");//loginToken

        if (token.equals("default")) {
            L.e("ConversationActivity push", "push2");
            startActivity(new Intent(ConversationActivity.this, LoginActivity.class));
            finish();
        } else {
            L.e("ConversationActivity push", "push3");
            reconnect(token);
        }
    }

    private void reconnect(String token) {

        RongIM.connect(token, new RongIMClient.ConnectCallback() {
            @Override
            public void onTokenIncorrect() {
                Log.e(TAG, "---onTokenIncorrect--");
            }

            @Override
            public void onSuccess(String s) {
                Log.i(TAG, "---onSuccess--" + s);
                L.e("ConversationActivity push", "push4");

                Intent intent = new Intent();
                intent.setClass(ConversationActivity.this, ChatActivity.class);
                intent.putExtra("PUSH_CONVERSATIONTYPE", mConversationType.toString());
                intent.putExtra("PUSH_TARGETID", mTargetId);
                startActivity(intent);
                finish();

                enterFragment(mConversationType, mTargetId);
            }

            @Override
            public void onError(RongIMClient.ErrorCode e) {
                Log.e(TAG, "---onError--" + e);
                enterFragment(mConversationType, mTargetId);
            }
        });

    }

    private ConversationFragment fragment;

    /**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType 会话类型
     * @param mTargetId         会话 Id
     */


    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        fragment = new ConversationFragment();

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
        fragment.setInputBoardListener(new InputView.IInputBoardListener() {
            @Override
            public void onBoardExpanded(int height) {
                L.e(TAG, "onBoardExpanded h : " + height);
            }

            @Override
            public void onBoardCollapsed() {
                L.e(TAG, "onBoardCollapsed");
            }
        });

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        //xxx 为你要加载的 id
        transaction.add(R.id.rong_content, fragment);
        transaction.commitAllowingStateLoss();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 501) {
            finish();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        if (isFromPush) {
            isFromPush = false;
            startActivity(new Intent(this, ChatActivity.class));
            finish();
        }

    }

    @Override
    protected void onDestroy() {
        if ("ConversationActivity".equals(this.getClass().getSimpleName()))
            EventBus.getDefault().unregister(this);
        RongIM.getInstance().setGroupMembersProvider(null);
        RongIM.getInstance().setRequestPermissionListener(null);
        RongIMClient.setTypingStatusListener(null);
        super.onDestroy();
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (KeyEvent.KEYCODE_BACK == event.getKeyCode()) {
            if (fragment != null && !fragment.onBackPressed()) {
                if (isFromPush) {
                    isFromPush = false;
                    startActivity(new Intent(this, ChatActivity.class));
                }
                AppContext.getInstance().popAllActivity();
            }
        }
        return false;
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for (ConversationUser i : list) {
            if (i.getUserid().equals(s)) {
                UserInfo userInfo = new UserInfo(i.getUserid(), i.getUserName(), Uri.parse(i.getPortraitUri()));
                return userInfo;
            }
        }
        return null;
    }

    private void initPerssion() {
        if (Build.VERSION.SDK_INT >= 23) {
            RongIM.getInstance().setRequestPermissionListener(new RongIM.RequestPermissionsListener() {
                @RequiresApi(api = Build.VERSION_CODES.M)
                @Override
                public void onPermissionRequest(String[] permissions, final int requestCode) {
                    for (final String permission : permissions) {
                        if (shouldShowRequestPermissionRationale(permission)) {
                            requestPermissions(new String[]{permission}, requestCode);
                        } else {
                            int isPermissionGranted = checkSelfPermission(permission);
                            if (isPermissionGranted != PackageManager.PERMISSION_GRANTED) {
                                new AlertDialog.Builder(ConversationActivity.this)
                                        .setMessage("你需要在设置里打开以下权限:" + permission)
                                        .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                requestPermissions(new String[]{permission}, requestCode);
                                            }
                                        })
                                        .setNegativeButton("取消", null)
                                        .create().show();
                            }
                            return;
                        }
                    }
                }
            });
        }
    }
}

/**
     * 设置会话页面 Title
     *
     * @param conversationType 会话类型
     * @param targetId         目标 Id
     *//*

     */
/*private void setActionBarTitle(Conversation.ConversationType conversationType, String targetId) {

        if (conversationType == null)
            return;

        if (conversationType.equals(Conversation.ConversationType.PRIVATE)) {
            setPrivateActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.GROUP)) {
            setGroupActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.DISCUSSION)) {
            setDiscussionActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CHATROOM)) {
            setTitle(title);
        } else if (conversationType.equals(Conversation.ConversationType.SYSTEM)) {
            setTitle(R.string.de_actionbar_system);
        } else if (conversationType.equals(Conversation.ConversationType.APP_PUBLIC_SERVICE)) {
            setAppPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.PUBLIC_SERVICE)) {
            setPublicServiceActionBar(targetId);
        } else if (conversationType.equals(Conversation.ConversationType.CUSTOMER_SERVICE)) {
            setTitle(R.string.main_customer);
        } else {
            setTitle(R.string.de_actionbar_sub_defult);
        }
    }

    //设置群聊界面 ActionBar
    // @param targetId 会话 Id

    private void setGroupActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }


     // 设置应用公众服务界面 ActionBar

    private void setAppPublicServiceActionBar(String targetId) {
        if (targetId == null)
            return;

        RongIM.getInstance().getPublicServiceProfile(Conversation.PublicServiceType.APP_PUBLIC_SERVICE
                , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        setTitle(publicServiceProfile.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    //设置公共服务号 ActionBar
    private void setPublicServiceActionBar(String targetId) {

        if (targetId == null)
            return;


        RongIM.getInstance().getPublicServiceProfile(Conversation.PublicServiceType.PUBLIC_SERVICE
                , targetId, new RongIMClient.ResultCallback<PublicServiceProfile>() {
                    @Override
                    public void onSuccess(PublicServiceProfile publicServiceProfile) {
                        setTitle(publicServiceProfile.getName());
                    }

                    @Override
                    public void onError(RongIMClient.ErrorCode errorCode) {

                    }
                });
    }

    //设置讨论组界面 ActionBar
    private void setDiscussionActionBar(String targetId) {

        if (targetId != null) {

            RongIM.getInstance().getDiscussion(targetId
                    , new RongIMClient.ResultCallback<Discussion>() {
                        @Override
                        public void onSuccess(Discussion discussion) {
                            setTitle(discussion.getName());
                        }

                        @Override
                        public void onError(RongIMClient.ErrorCode e) {
                            if (e.equals(RongIMClient.ErrorCode.NOT_IN_DISCUSSION)) {
                                setTitle("不在讨论组中");
                                supportInvalidateOptionsMenu();
                            }
                        }
                    });
        } else {
            setTitle("讨论组");
        }
    }


    //设置私聊界面 ActionBar
    private void setPrivateActionBar(String targetId) {
        if (!TextUtils.isEmpty(title)) {
            setTitle(title);
        } else {
            setTitle(targetId);
        }
    }
    *//*


}


        */
/*Intent intent = getIntent();
        //头像
        list=new ArrayList<>();
        list.add(new ConversationUser("lb", "刘备", "http://att.bbs.duowan.com/forum/month_1103/20110314_14b6eaa3cade15c3f50d9IkwhGFK7tbI.jpg"));
        list.add(new ConversationUser("zgl", "诸葛亮", "http://www.qq1234.org/uploads/allimg/150323/1H5024459-11.jpg"));
        list.add(new ConversationUser("ike","张三","http://www.qq1234.org/uploads/allimg/150323/1H5024459-11.jpg"));
        RongIM.setUserInfoProvider(this,true);

        setActionBar();

        getIntentDate(intent);

        isReconnect(intent);

    }

    *//*
*/
/**
     * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
     *//*
*/
/*
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().
        getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
        setActionBarTitle(mTargetId);
    }


    *//*
*/
/**
     * 加载会话页面 ConversationFragment
     *
     * @param mConversationType
     * @param mTargetId
     *//*
*/
/*
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment =
                (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }


    *//*
*/
/**
     * 判断消息是否是 push 消息
     *//*
*/
/*
    private void isReconnect(Intent intent) {


        String token = null;

        if (DemoContext.getInstance() != null) {

            token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
        }

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {

                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    *//*
*/
/**
     * 设置 actionbar 事件
     *//*
*/
/*
    private void setActionBar() {

        mTitle = (TextView) findViewById(R.id.txt1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    *//*
*/
/**
     * 设置 actionbar title
     *//*
*/
/*
    private void setActionBarTitle(String targetid) {

        mTitle.setText(targetid);
    }

    *//*
*/
/**
     * 重连
     *
     * @param token
     *//*
*/
/*
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }


}
*/
/*package com.ike.taxi.chat.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ike.taxi.R;
import com.ike.taxi.application.App;
import com.ike.taxi.chat.DemoContext;
import com.ike.taxi.chat.bean.ConversationUser;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import io.rong.imkit.RongIM;
import io.rong.imkit.fragment.ConversationFragment;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.UserInfo;

*//**
 * 会话页面
 *//*

public class ConversationActivity extends FragmentActivity implements RongIM.UserInfoProvider {

    private TextView mTitle;
    private RelativeLayout mBack;

    private String mTargetId;

    *//**
 * 刚刚创建完讨论组后获得讨论组的id 为targetIds，需要根据 为targetIds 获取 targetId
 *//*
    private String mTargetIds;

    //头像
    private List<ConversationUser> list;

    *//**
 * 会话类型
 *//*
    private Conversation.ConversationType mConversationType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_conversation);
        Intent intent = getIntent();

        //头像
        list=new ArrayList<>();
        list=new ArrayList<>();
        list.add(new ConversationUser("swk", "孙悟空", "http://pic37.nipic.com/20140120/9885883_125934577000_2.jpg"));
        list.add(new ConversationUser("ts", "唐僧", "http://news.mbalib.com/uploads/image/2015/0914/2015091442543ea2be07a6eb37e054bcc419daea.jpg"));
        list.add(new ConversationUser("zbj","猪八戒","http://img5.mypsd.com.cn/20111121/Mypsd_67401_201111210849430010B.jpg"));
        list.add(new ConversationUser("ss","沙僧","http://img3.redocn.com/20100623/20100623_25983d787b4f8608d140qd11qhbbtGNK.jpg"));
        RongIM.setUserInfoProvider(this,true);

        setActionBar();

        getIntentDate(intent);

        isReconnect(intent);

    }

    *//**
 * 展示如何从 Intent 中得到 融云会话页面传递的 Uri
 *//*
    private void getIntentDate(Intent intent) {

        mTargetId = intent.getData().getQueryParameter("targetId");
        mTargetIds = intent.getData().getQueryParameter("targetIds");
        //intent.getData().getLastPathSegment();//获得当前会话类型
        mConversationType = Conversation.ConversationType.valueOf(intent.getData().getLastPathSegment().toUpperCase(Locale.getDefault()));

        enterFragment(mConversationType, mTargetId);
        setActionBarTitle(mTargetId);
    }


    *//**
 * 加载会话页面 ConversationFragment
 *
 * @param mConversationType
 * @param mTargetId
 *//*
    private void enterFragment(Conversation.ConversationType mConversationType, String mTargetId) {

        ConversationFragment fragment =
                (ConversationFragment) getSupportFragmentManager().findFragmentById(R.id.conversation);

        Uri uri = Uri.parse("rong://" + getApplicationInfo().packageName).buildUpon()
                .appendPath("conversation").appendPath(mConversationType.getName().toLowerCase())
                .appendQueryParameter("targetId", mTargetId).build();

        fragment.setUri(uri);
    }


    *//**
 * 判断消息是否是 push 消息
 *//*
    private void isReconnect(Intent intent) {


        String token = null;

        if (DemoContext.getInstance() != null) {

            token = DemoContext.getInstance().getSharedPreferences().getString("DEMO_TOKEN", "default");
        }

        //push或通知过来
        if (intent != null && intent.getData() != null && intent.getData().getScheme().equals("rong")) {

            //通过intent.getData().getQueryParameter("push") 为true，判断是否是push消息
            if (intent.getData().getQueryParameter("push") != null
                    && intent.getData().getQueryParameter("push").equals("true")) {

                reconnect(token);
            } else {
                //程序切到后台，收到消息后点击进入,会执行这里
                if (RongIM.getInstance() == null || RongIM.getInstance().getRongIMClient() == null) {

                    reconnect(token);
                } else {
                    enterFragment(mConversationType, mTargetId);
                }
            }
        }
    }

    *//**
 * 设置 actionbar 事件
 *//*
    private void setActionBar() {

        mTitle = (TextView) findViewById(R.id.txt1);
        mBack = (RelativeLayout) findViewById(R.id.back);

        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }



    *//**
 * 设置 actionbar title
 *//*
    private void setActionBarTitle(String targetid) {

        mTitle.setText(targetid);
    }

    *//**
 * 重连
 *
 * @param token
 *//*
    private void reconnect(String token) {

        if (getApplicationInfo().packageName.equals(App.getCurProcessName(getApplicationContext()))) {

            RongIM.connect(token, new RongIMClient.ConnectCallback() {
                @Override
                public void onTokenIncorrect() {

                }

                @Override
                public void onSuccess(String s) {

                    enterFragment(mConversationType, mTargetId);
                }

                @Override
                public void onError(RongIMClient.ErrorCode errorCode) {

                }
            });
        }
    }

    @Override
    public UserInfo getUserInfo(String s) {
        for(ConversationUser i:list){
            if(i.getUserid().equals(s)){
                UserInfo userInfo=new UserInfo(i.getUserid(),i.getUserName(),Uri.parse(i.getPortraitUri()));
                return userInfo;
            }
        }
        return null;
    }
}*/
