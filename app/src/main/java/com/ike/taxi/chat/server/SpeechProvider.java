package com.ike.taxi.chat.server;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;
import com.iflytek.cloud.ui.RecognizerDialog;
import com.iflytek.cloud.ui.RecognizerDialogListener;
import com.iflytek.sunflower.FlowerCollector;
import com.ike.taxi.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.LinkedHashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.message.TextMessage;

/**
 * Created by Min on 2016/10/22.
 * 语音
 */

public class SpeechProvider extends InputProvider.ExtendProvider{
    private Context context;

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    private int REQUEST_CONTACT = 20;
    //
    private SpeechRecognizer mIat;  //语音听写对象
    private RecognizerDialog mIatDialog;  //语音听写UI
    private HashMap<String,String> mIatResult=new LinkedHashMap<>();

    private String mEngineType;
    private Toast mToast;


    public SpeechProvider(RongContext context) {
        super(context);
        this.context=context;
        mWorkThread = new HandlerThread("RongDemo");
        mWorkThread.start();
        mUploadHandler = new Handler(mWorkThread.getLooper());
    }

    /**
     * 图标
     * @param context
     * @return
     */
    @Override
    public Drawable obtainPluginDrawable(Context context) {
        return context.getResources().getDrawable(R.mipmap.taxi);
    }

    /**
     * title
     * @param context
     * @return
     */
    @Override
    public CharSequence obtainPluginTitle(Context context) {
        return context.getString(R.string.xfyun);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onPluginClick(View view) {
        mEngineType= SpeechConstant.TYPE_CLOUD;  //引擎类型---云端
        // 使用SpeechRecognizer对象，可根据回调消息自定义界面；
        SpeechUtility.createUtility(view.getContext(),SpeechConstant.APPID+"=57e89480");
        initRecognizerDialog(view);
        mIat=SpeechRecognizer.createRecognizer(view.getContext(),mInitListener);
        mIatDialog = new RecognizerDialog(view.getContext(), mInitListener);
        mToast = Toast.makeText(view.getContext(), "", Toast.LENGTH_SHORT);
        FlowerCollector.onEvent(view.getContext(), "iat_recognize");
        // 设置参数
        // 显示听写对话框
        mIatDialog.setListener(mRecognizerDialogListener);
        mIatDialog.show();
        showTip("请说话");


    }

    private void initRecognizerDialog(View view) {
        RecognizerDialog mDialog= new RecognizerDialog(view.getContext(),mInitListener);
        mDialog.setParameter(SpeechConstant.LANGUAGE,"zh_cn");
        mDialog.setParameter(SpeechConstant.ACCENT,"mandarin");
        mDialog.setListener(mRecognizerDialogListener);
        mDialog.show();
    }

    /**
     * 听写UI监听器
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            Log.e("=========",results.getResultString());
            printResult(results);
            String showMessage = results.getResultString().toString().trim();
            final TextMessage content = TextMessage.obtain(showMessage);

            if (RongIM.getInstance().getRongIMClient() != null)
                RongIM.getInstance().getRongIMClient().sendMessage(getCurrentConversation().getConversationType(), getCurrentConversation().getTargetId(), content, null, null, new RongIMClient.SendMessageCallback() {
                    @Override
                    public void onError(Integer integer, RongIMClient.ErrorCode errorCode) {
                        Log.d("ExtendProvider", "onError--" + errorCode);
                    }

                    @Override
                    public void onSuccess(Integer integer) {
                        Log.d("ExtendProvider", "onSuccess--" + integer);
                    }
                });
        }

        /**
         * 识别回调错误.
         */
        public void onError(SpeechError error) {
            showTip(error.getPlainDescription(true));
        }

    };

    /**
     * 听写监听器。
     */

    private void printResult(RecognizerResult results) {
        String text=JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("----------", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                showTip("初始化失败，错误码：" + code);
            }
        }
    };

    private void showTip(final String str) {
        mToast.setText(str);
        mToast.show();
    }
}
