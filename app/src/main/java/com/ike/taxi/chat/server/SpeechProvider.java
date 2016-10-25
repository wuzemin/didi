package com.ike.taxi.chat.server;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

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

    private String message;


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
        mDialog.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mDialog.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath()+"test.wav");
        Log.e("========",Environment.getExternalStorageDirectory().getAbsolutePath());
        mDialog.setListener(mRecognizerDialogListener);
//        mDialog.setListener(mRecoListener);
        mDialog.show();
    }

    private RecognizerListener mRecoListener=new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {

        }

        @Override
        public void onEndOfSpeech() {

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {

        }

        @Override
        public void onError(SpeechError speechError) {

        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };

    /**
     * 听写UI监听器-----UI
     */
    private RecognizerDialogListener mRecognizerDialogListener = new RecognizerDialogListener() {
        public void onResult(RecognizerResult results, boolean isLast) {
            printResult(results);
            /**
             *
             */
            File voiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "FinalAudio.wav");

            try {
                // 读取音频文件。
//                InputStream is = getAssets().open("BlackBerry.amr");
                InputStream is=context.getAssets().open("FinalAudio,wav");
                OutputStream os = new FileOutputStream(voiceFile);

                byte[] buffer = new byte[1024];

                int bytesRead;

                // 写入缓存文件。
                while((bytesRead = is.read(buffer)) !=-1){
                    os.write(buffer, 0, bytesRead);
                }

                is.close();
                os.flush();
                os.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            final TextMessage content = TextMessage.obtain(message);
            final VoiceMessage voice= VoiceMessage.obtain(Uri.fromFile(voiceFile),10);

            if (RongIM.getInstance().getRongIMClient() != null)
                RongIM.getInstance().getRongIMClient().sendMessage(getCurrentConversation().getConversationType(),
                        getCurrentConversation().getTargetId(), voice, null, null, new RongIMClient.SendMessageCallback() {
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
        mIatResult.put(sn,text);
        StringBuffer sb=new StringBuffer();
        for(String key:mIatResult.keySet()){
            sb.append(mIatResult.get(key));
        }
        String str=sb.toString();
        message=str;
        Log.e("==========1",str);
        Log.e("==========1",message);
        /*for(int i=0;i<str.length();i++){

        }*/
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
