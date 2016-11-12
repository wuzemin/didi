package com.ike.taxi.chat;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.ike.taxi.chat.activity.AMAPLocationActivity;
import com.ike.taxi.chat.activity.NewFriendListActivity;
import com.ike.taxi.chat.bean.ContactNotificationMessageData;
import com.ike.taxi.chat.server.ContactsProvider;
import com.ike.taxi.chat.server.SpeechProvider;
import com.ike.taxi.chat.server.TestEndProvider;
import com.ike.taxi.chat.server.TestProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.model.UIConversation;
import io.rong.imkit.widget.provider.CameraInputProvider;
import io.rong.imkit.widget.provider.FileInputProvider;
import io.rong.imkit.widget.provider.ImageInputProvider;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imkit.widget.provider.LocationInputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.imlib.model.Conversation;
import io.rong.imlib.model.Message;
import io.rong.imlib.model.MessageContent;
import io.rong.imlib.model.UserInfo;
import io.rong.message.ContactNotificationMessage;
import io.rong.message.ImageMessage;
import io.rong.message.LocationMessage;

/**
 * Created by Min on 2016/10/20.
 * 融云相关监听 事件集合类
 */

public class AppContext implements RongIMClient.ConnectionStatusListener,
        RongIM.LocationProvider, RongIM.ConversationBehaviorListener, RongIM.ConversationListBehaviorListener {
    /*public class AppContext implements RongIM.ConversationListBehaviorListener, RongIMClient.OnReceiveMessageListener,
            RongIM.GroupInfoProvider, RongIM.GroupUserInfoProvider,
            RongIMClient.ConnectionStatusListener, RongIM.LocationProvider, RongIM.ConversationBehaviorListener {*/


    public static final String UPDATE_FRIEND = "update_friend";
    public static final String UPDATE_RED_DOT = "update_red_dot";
    private Context mContext;

    private static AppContext mRongCloudInstance;

    private RongIM.LocationProvider.LocationCallback mLastLocationCallback;

    private static ArrayList<Activity> mActivities;

    public AppContext(Context mContext) {
        this.mContext = mContext;
        initListener();
        mActivities = new ArrayList<>();
    }

    /**
     * 初始化 RongCloud.
     *
     * @param context 上下文。
     */
    public static void init(Context context) {

        if (mRongCloudInstance == null) {

            synchronized (AppContext.class) {

                if (mRongCloudInstance == null) {
                    mRongCloudInstance = new AppContext(context);
                }
            }
        }

    }

    /**
     * 获取RongCloud 实例。
     *
     * @return RongCloud。
     */
    public static AppContext getInstance() {
        return mRongCloudInstance;
    }

    /**
     * init 后就能设置的监听
     */
    private void initListener() {
        RongIM.setConversationBehaviorListener(this);//设置会话界面操作的监听器。
//        RongIM.setUserInfoProvider(this,true);  //用户信息提供者
        RongIM.setConversationListBehaviorListener(this);  //会话列表界面
//        RongIM.setGroupInfoProvider(this, true);  //群组用户提供者
        RongIM.setLocationProvider(this);//设置地理位置提供者,不用位置的同学可以注掉此行代码
        setInputProvider();
//        setUserInfoEngineListener();
        setReadReceiptConversationType();
//        RongIM.setGroupUserInfoProvider(this, true);
    }

    private void setReadReceiptConversationType() {
        Conversation.ConversationType[] types = new Conversation.ConversationType[] {
                Conversation.ConversationType.PRIVATE,
                Conversation.ConversationType.GROUP,
                Conversation.ConversationType.DISCUSSION
        };
        RongIM.getInstance().setReadReceiptConversationTypeList(types);
    }

    private void setInputProvider() {

//        RongIM.setOnReceiveMessageListener(this);
        RongIM.setConnectionStatusListener(this);

        InputProvider.ExtendProvider[] singleProvider = {
                new ImageInputProvider(RongContext.getInstance()),
//                new RealTimeLocationInputProvider(RongContext.getInstance()), //带位置共享的地理位置
                new FileInputProvider(RongContext.getInstance())//文件消息
        };

        InputProvider.ExtendProvider[] muiltiProvider = {
                new ImageInputProvider(RongContext.getInstance()),
                new LocationInputProvider(RongContext.getInstance()),//地理位置
                new FileInputProvider(RongContext.getInstance())//文件消息
        };

        InputProvider.ExtendProvider[] provider= {
                new ImageInputProvider(RongContext.getInstance()), //图片
                new CameraInputProvider(RongContext.getInstance()), //相机
                new LocationInputProvider(RongContext.getInstance()), //位置
                new ContactsProvider(RongContext.getInstance()),  //自定义
                new SpeechProvider(RongContext.getInstance()),  //语音
                new TestProvider(RongContext.getInstance()),  //开始录音
                new TestEndProvider(RongContext.getInstance()) //结束录音
        };

        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE, singleProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.DISCUSSION, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.CUSTOMER_SERVICE, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.GROUP, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.CHATROOM, muiltiProvider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.PRIVATE,provider);
        RongIM.resetInputExtensionProvider(Conversation.ConversationType.GROUP,provider);
    }

    @Override
    public void onChanged(ConnectionStatus connectionStatus) {
        if (connectionStatus.getMessage().equals(ConnectionStatus.KICKED_OFFLINE_BY_OTHER_CLIENT)) {

        }
    }

    @Override
    public void onStartLocation(Context context, LocationCallback locationCallback) {
        /**
         * demo 代码  开发者需替换成自己的代码。
         */
        AppContext.getInstance().setLastLocationCallback(locationCallback);
        Intent intent = new Intent(context, AMAPLocationActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);

    }

    //会话界面操作
    @Override
    public boolean onUserPortraitClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        if (conversationType == Conversation.ConversationType.CUSTOMER_SERVICE || conversationType == Conversation.ConversationType.PUBLIC_SERVICE || conversationType == Conversation.ConversationType.APP_PUBLIC_SERVICE) {
            return false;
        }
        //点击头像
        /*if (userInfo != null) {
            Intent intent = new Intent(context, PersonalProfileActivity.class);
            intent.putExtra("conversationType", conversationType.getValue());
            intent.putExtra("userinfo", userInfo);
            context.startActivity(intent);
        }*/
        return true;
    }

    @Override
    public boolean onUserPortraitLongClick(Context context, Conversation.ConversationType conversationType, UserInfo userInfo) {
        return false;
    }

    //点击消息
    @Override
    public boolean onMessageClick(final Context context, final View view, final Message message) {

        //位置共享
        /*if (message.getContent() instanceof RealTimeLocationStartMessage) {
            RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

//            if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
//                startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
//            } else
            if (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_INCOMING) {


                final AlterDialogFragment alterDialogFragment = AlterDialogFragment.newInstance("", "加入位置共享", "取消", "加入");
                alterDialogFragment.setOnAlterDialogBtnListener(new AlterDialogFragment.AlterDialogBtnListener() {

                    @Override
                    public void onDialogPositiveClick(AlterDialogFragment dialog) {
                        RealTimeLocationConstant.RealTimeLocationStatus status = RongIMClient.getInstance().getRealTimeLocationCurrentState(message.getConversationType(), message.getTargetId());

                        if (status == null || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_IDLE) {
                            startRealTimeLocation(context, message.getConversationType(), message.getTargetId());
                        } else {
                            joinRealTimeLocation(context, message.getConversationType(), message.getTargetId());
                        }

                    }

                    @Override
                    public void onDialogNegativeClick(AlterDialogFragment dialog) {
                        alterDialogFragment.dismiss();
                    }
                });

                alterDialogFragment.show(((FragmentActivity) context).getSupportFragmentManager());
            } else {

                if (status != null && (status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_OUTGOING || status == RealTimeLocationConstant.RealTimeLocationStatus.RC_REAL_TIME_LOCATION_STATUS_CONNECTED)) {

                    Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
                    intent.putExtra("conversationType", message.getConversationType().getValue());
                    intent.putExtra("targetId", message.getTargetId());
                    context.startActivity(intent);
                }
            }
            return true;
        }*/

        //位置 end
        /**
         * demo 代码  开发者需替换成自己的代码。
        */
        if (message.getContent() instanceof LocationMessage) {
            Intent intent = new Intent(context, AMAPLocationActivity.class);
            intent.putExtra("location", message.getContent());
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        } else if (message.getContent() instanceof ImageMessage) {
//            Intent intent = new Intent(context, PhotoActivity.class);
//            intent.putExtra("message", message);
//            context.startActivity(intent);
        }

        return false;
    }
    /*private void startRealTimeLocation(Context context, Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().startRealTimeLocation(conversationType, targetId);

        Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
        intent.putExtra("conversationType", conversationType.getValue());
        intent.putExtra("targetId", targetId);
        context.startActivity(intent);
    }

    private void joinRealTimeLocation(Context context, Conversation.ConversationType conversationType, String targetId) {
        RongIMClient.getInstance().joinRealTimeLocation(conversationType, targetId);

        Intent intent = new Intent(((FragmentActivity) context), RealTimeLocationActivity.class);
        intent.putExtra("conversationType", conversationType.getValue());
        intent.putExtra("targetId", targetId);
        context.startActivity(intent);
    }*/

    @Override
    public boolean onMessageLinkClick(Context context, String s) {
        return false;
    }

    @Override
    public boolean onMessageLongClick(Context context, View view, Message message) {
        return false;
    }


    public RongIM.LocationProvider.LocationCallback getLastLocationCallback() {
        return mLastLocationCallback;
    }

    public void setLastLocationCallback(RongIM.LocationProvider.LocationCallback lastLocationCallback) {
        this.mLastLocationCallback = lastLocationCallback;
    }

    public void pushActivity(Activity activity) {
        mActivities.add(activity);
    }

    public void popActivity(Activity activity) {
        if (mActivities.contains(activity)) {
            activity.finish();
            mActivities.remove(activity);
        }
    }

    public void popAllActivity() {
        try {
            for (Activity activity : mActivities) {
                if (activity != null) {
                    activity.finish();
                }
            }
            mActivities.clear();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 需要 rongcloud connect 成功后设置的 listener
     */
    /*public void setUserInfoEngineListener() {
        UserInfoEngine.getInstance(mContext).setListener(new UserInfoEngine.UserInfoListener() {
            @Override
            public void onResult(UserInfo info) {
                if (info != null && RongIM.getInstance() != null) {
                    if (TextUtils.isEmpty(String.valueOf(info.getPortraitUri()))) {
                        info.setPortraitUri(Uri.parse(RongGenerate.generateDefaultAvatar(info.getName(), info.getUserId())));
                    }
                    NLog.e("UserInfoEngine", info.getName() + info.getPortraitUri());
                    RongIM.getInstance().refreshUserInfoCache(info);
                }
            }
        });
        GroupInfoEngine.getInstance(mContext).setmListener(new GroupInfoEngine.GroupInfoListeners() {
            @Override
            public void onResult(Group info) {
                if (info != null && RongIM.getInstance() != null) {
                    NLog.e("GroupInfoEngine:" + info.getId() + "----" + info.getName() + "----" + info.getPortraitUri());
                    if (TextUtils.isEmpty(String.valueOf(info.getPortraitUri()))) {
                        info.setPortraitUri(Uri.parse(RongGenerate.generateDefaultAvatar(info.getName(), info.getId())));
                    }
                    RongIM.getInstance().refreshGroupInfoCache(info);

                }
            }
        });
    }*/

    @Override
    public boolean onConversationPortraitClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationPortraitLongClick(Context context, Conversation.ConversationType conversationType, String s) {
        return false;
    }

    @Override
    public boolean onConversationLongClick(Context context, View view, UIConversation uiConversation) {
        return false;
    }

    @Override
    public boolean onConversationClick(Context context, View view, UIConversation uiConversation) {
        MessageContent messageContent = uiConversation.getMessageContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals("AcceptResponse")) {
                // 被加方同意请求后
                if (contactNotificationMessage.getExtra() != null) {
                    ContactNotificationMessageData bean = null;
                    contactNotificationMessage.getExtra();
                    Gson gson=new Gson();
                    Type type=new TypeToken<ContactNotificationMessageData>(){}.getType();
                    bean=gson.fromJson(contactNotificationMessage.getExtra(),type);
//                    ContactNotificationMessageData.class;
                    /*try {
//                        bean = JsonMananger.jsonToBean(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
                    } catch (HttpException e) {
                        e.printStackTrace();
                    }*/
                    RongIM.getInstance().startPrivateChat(context, uiConversation.getConversationSenderId(),
                            bean.getSourceNickName());

                }
            } else {
                context.startActivity(new Intent(context, NewFriendListActivity.class));
            }
            return true;
        }
        return false;
    }

    /*@Override
    public boolean onReceived(Message message, int i) {
        MessageContent messageContent = message.getContent();
        if (messageContent instanceof ContactNotificationMessage) {
            ContactNotificationMessage contactNotificationMessage = (ContactNotificationMessage) messageContent;
            if (contactNotificationMessage.getOperation().equals("Request")) {
                //对方发来好友邀请
                BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_RED_DOT);
            } else if (contactNotificationMessage.getOperation().equals("AcceptResponse")) {
                //对方同意我的好友请求
                ContactNotificationMessageData c = null;
                try {
                    c = JsonMananger.jsonToBean(contactNotificationMessage.getExtra(), ContactNotificationMessageData.class);
                } catch (HttpException e) {
                    e.printStackTrace();
                }
                if (c != null) {
                    DBManager.getInstance(mContext).getDaoSession().getFriendDao().insertOrReplace(new Friend(contactNotificationMessage.getSourceUserId(), c.getSourceUserNickname(), null, null, null, null));
                }
                BroadcastManager.getInstance(mContext).sendBroadcast(UPDATE_FRIEND);
                BroadcastManager.getInstance(mContext).sendBroadcast(SealAppContext.UPDATE_RED_DOT);
            }
//                // 发广播通知更新好友列表
//            BroadcastManager.getInstance(mContext).sendBroadcast(UPDATE_RED_DOT);
//            }
        } else if (messageContent instanceof GroupNotificationMessage) {
            GroupNotificationMessage groupNotificationMessage = (GroupNotificationMessage) messageContent;
            NLog.e("" + groupNotificationMessage.getMessage());
            if (groupNotificationMessage.getOperation().equals("Kicked")) {
            } else if (groupNotificationMessage.getOperation().equals("Add")) {
            } else if (groupNotificationMessage.getOperation().equals("Quit")) {
            } else if (groupNotificationMessage.getOperation().equals("Rename")) {
            }

        } else if (messageContent instanceof ImageMessage) {
            ImageMessage imageMessage = (ImageMessage) messageContent;
            Log.e("imageMessage", imageMessage.getRemoteUri().toString());
        }
        return false;
    }*/

    /*@Override
    public UserInfo getUserInfo(String s) {
        return UserInfoEngine.getInstance(mContext).startEngine(s);
    }

    @Override
    public Group getGroupInfo(String s) {
        return GroupInfoEngine.getInstance(mContext).startEngine(s);
    }*/
}
