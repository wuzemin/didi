package com.ike.taxi.chat.server;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
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
import com.ike.taxi.chat.widget.audio.AudioRecordFunc;
import com.ike.taxi.utils.ToastUtil;

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
 * Created by Min on 2016/10/26.
 */

public class TestEndProvider extends InputProvider.ExtendProvider {
    private Context context;

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    //
    private SpeechRecognizer mIat;  //语音听写对象
    private HashMap<String,String> mIatResults=new LinkedHashMap<>();

    private String mEngineType;
    private Toast mToast;

    private String message;

    Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case 1:
                    ToastUtil.show(context,"说话结束");
                    AudioRecordFunc recordFunc=AudioRecordFunc.getInstance();
                    recordFunc.stopRecordAndFile();
                    break;
            }
        }
    };


    public TestEndProvider(RongContext context) {
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
        AudioRecordFunc audioRecordFunc=AudioRecordFunc.getInstance();
        audioRecordFunc.stopRecordAndFile();
        initSpeech(view);
    }


    private void initSpeech(View view) {
        mEngineType= SpeechConstant.TYPE_CLOUD;
        SpeechUtility.createUtility(view.getContext(),SpeechConstant.APPID+"=57e89480");
        mIat = SpeechRecognizer.createRecognizer(view.getContext(), mInitListener);
        mIat.setParameter(SpeechConstant.DOMAIN,"iat");
        mIat.setParameter(SpeechConstant.LANGUAGE,"zh_ch");
        mIat.setParameter(SpeechConstant.ACCENT,"mandarin");
        mIat.setParameter(SpeechConstant.ASR_PTT,"0");

        //音频文件
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.AUDIO_SOURCE,"-2");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
                Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"FinalAudio.wav");
//                Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+"FinalAudio.amr");
        mIat.startListening(recognizerListener);
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
            mIat.stopListening();

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.e("========onResult","onResult");
            printResult(recognizerResult);
            initVoice();
            initText();
            mIat.stopListening();
        }

        @Override
        public void onError(SpeechError speechError) {
            Log.e("========onError","onError:"+speechError);
            mIat.stopListening();
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
        try {
            // 读取音频文件。
            InputStream is=context.getAssets().open("FinalAudio.wav");
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
                ToastUtil.show(context,"初始化失败，错误码：" + code);
            }
        }
    };
}
