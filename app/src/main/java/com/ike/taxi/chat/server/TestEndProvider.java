package com.ike.taxi.chat.server;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
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
import com.ike.taxi.R;
import com.ike.taxi.chat.activity.IatSettings;
import com.ike.taxi.chat.utils.FucUtil;
import com.ike.taxi.chat.widget.audio.AudioRecordFunc;
import com.ike.taxi.utils.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;
import java.util.LinkedHashMap;

import io.rong.imkit.RongContext;
import io.rong.imkit.RongIM;
import io.rong.imkit.widget.provider.InputProvider;
import io.rong.imlib.RongIMClient;
import io.rong.message.TextMessage;
import io.rong.message.VoiceMessage;

/**
 * Created by Min on 2016/10/26.
 */

public class TestEndProvider extends InputProvider.ExtendProvider {
//    private Context context;

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    //
    private SpeechRecognizer mIat;  //语音听写对象
    private HashMap<String,String> mIatResults=new LinkedHashMap<>();

    private String mEngineType;
    private Toast mToast;

    private String message;
    private AudioRecordFunc audioRecordFunc;
    private SharedPreferences mSharedPreferences;
    private boolean flag=false;

    public TestEndProvider(RongContext context) {
        super(context);
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
        return context.getString(R.string.end_yun);
    }

    /**
     * 点击事件
     * @param view
     */
    int mResult=-1;
    @Override
    public void onPluginClick(final View view) {
        ToastUtil.show(view.getContext(),"结束说话");
        audioRecordFunc=AudioRecordFunc.getInstance();
        audioRecordFunc.stopRecordAndFile();
        initSpeech(view);
    }

    int ret=0;
    private void initSpeech(View view) {
        SpeechUtility.createUtility(view.getContext(),SpeechConstant.APPID+"=57e89480");
        mIat = SpeechRecognizer.createRecognizer(view.getContext(), mInitListener);
        setParam();
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            ToastUtil.show(view.getContext(),"识别失败,错误码：" + ret);
        } else {
            byte[] audioData= FucUtil.readAudio(Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
            if (null != audioData) {
                mIat.writeAudio(audioData, 0, audioData.length);
                mIat.stopListening();
            } else {
                mIat.cancel();
                ToastUtil.show(view.getContext(),"读取音频流失败");
            }
        }
    }

    private RecognizerListener recognizerListener=new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {

        }

        @Override
        public void onBeginOfSpeech() {
            Log.e("========onBeginOfSpeech","onBeginOfSpeech");
//            ToastUtil.show(context,"录音开始");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e("========onEndOfSpeech","onEndOfSpeech");
//            ToastUtil.show(context,"录音结束");
//            mIat.stopListening();

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.e("========onResult","onResult "+b);
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("========onError","onError:"+speechError);
//            mIat.stopListening();
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
//            Log.e("========onEvent","onEvent");
        }
    };

    private void printResult(RecognizerResult results) {
        String text = JsonParser.parseIatResult(results.getResultString());
        String sn = null;
        // 读取json结果中的sn字段
        try {
            JSONObject resultJson = new JSONObject(results.getResultString());
            sn = resultJson.optString("sn");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        mIatResults.put(sn, text);

        StringBuffer resultBuffer = new StringBuffer();
        for (String key : mIatResults.keySet()) {
            resultBuffer.append(mIatResults.get(key));
        }
        message=resultBuffer.toString();
        if(flag) {
            message = resultBuffer.toString();
            initText();
            initVoice();
            flag=false;
            return;
        }
        flag=true;
    }

    private void initText() {
        final TextMessage textMessage=TextMessage.obtain(message);
        if(RongIM.getInstance().getRongIMClient()!=null)
            RongIM.getInstance().getRongIMClient().sendMessage(getCurrentConversation().getConversationType(),
                    getCurrentConversation().getTargetId(), textMessage, null, null, new RongIMClient.SendMessageCallback() {
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

    private void initVoice() {
        File voiceFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), "FinalAudio.wav");
        final VoiceMessage voice= VoiceMessage.obtain(Uri.fromFile(voiceFile),3);

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
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d("===", "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
//                showTip("初始化失败误码：" + code);
                ToastUtil.show(getContext(),"初始化失败，错误码：" + code);
            }
        }
    };

    /**
     * 参数设置
     *
     * @param
     * @return
     */
    public void setParam() {
        mSharedPreferences =getContext().getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
//		mIat.setParameter(SpeechConstant.DOMAIN,"iat");
//		mIat.setParameter(SpeechConstant.LANGUAGE,"zh_ch");
//		mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
//		mIat.setParameter(SpeechConstant.ASR_PTT,"0");

        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        // 注：AUDIO_FORMAT参数语记需要更新版本才能生效
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
//		mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
    }
}
// 清空参数
        /*mIat.setParameter(SpeechConstant.PARAMS, null);

        // 设置听写引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 设置语言
        *//*mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT, "mandarin");
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");
        mIat.setParameter(SpeechConstant.ASR_PTT, "1");*//*
        String lag = mSharedPreferences.getString("iat_language_preference",
                "mandarin");
        if (lag.equals("en_us")) {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "en_us");
        } else {
            // 设置语言
            mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            // 设置语言区域
            mIat.setParameter(SpeechConstant.ACCENT, lag);
        }

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));

        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
        // 设置音频来源为外部文件
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
        ret = mIat.startListening(recognizerListener);
        if (ret != ErrorCode.SUCCESS) {
            ToastUtil.show(getContext(),"识别失败,错误码：" + ret);
        } else {
            byte[] audioData=FucUtil.readAudio(Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
            if (null != audioData) {
                mIat.writeAudio(audioData, 0, audioData.length);
                mIat.stopListening();
            } else {
                mIat.cancel();
                ToastUtil.show(getContext(),"读取音频流失败");
            }
        }*/