package com.ike.taxi.chat.server;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.view.MotionEvent;
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
import com.ike.taxi.chat.utils.FucUtil;
import com.ike.taxi.chat.widget.audio.AudioRecordFunc;
import com.ike.taxi.utils.T;

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
 * Created by Min on 2016/10/25.
 */

public class TestProvider extends  InputProvider.ExtendProvider implements View.OnTouchListener,View.OnLongClickListener{
    private Context context;

    HandlerThread mWorkThread;
    Handler mUploadHandler;
    //
    private SpeechRecognizer mIat;  //语音听写对象
    private HashMap<String,String> mIatResults=new LinkedHashMap<>();

    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Toast mToast;

    private String message;
    private AudioRecordFunc audioRecordFunc;

    private boolean flag=false;
//    private SharedPreferences mSharedPreferences;

    public TestProvider(RongContext context) {
        super(context);
        this.context=context;
        /*mSharedPreferences = context.getSharedPreferences(IatSettings.PREFER_NAME,
                Activity.MODE_PRIVATE);*/
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
        return context.getString(R.string.start_yun);
    }

    /**
     * 点击事件
     * @param view
     */
    @Override
    public void onPluginClick(final View view) {
        T.showShort(view.getContext(),"开始说话");
        audioRecordFunc=AudioRecordFunc.getInstance();
        audioRecordFunc.startRecordAndFile();
//        audioRecordFunc.stopRecordAndFile();
//        initSpeech(view);
    }

    int ret=0;
   private void initSpeech(View view) {
       SpeechUtility.createUtility(view.getContext(),SpeechConstant.APPID+"=57e89480");
       mIat = SpeechRecognizer.createRecognizer(view.getContext(), mInitListener);
       // 清空参数
       mIat.setParameter(SpeechConstant.PARAMS, null);

       // 设置听写引擎
       mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
       // 设置返回结果格式
       mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");

       /*String lag = mSharedPreferences.getString("iat_language_preference",
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
       mIat.setParameter(SpeechConstant.VAD_BOS, mSharedPreferences.getString("iat_vadbos_preference", "4000"));
       mIat.setParameter(SpeechConstant.VAD_EOS, mSharedPreferences.getString("iat_vadeos_preference", "1000"));
       mIat.setParameter(SpeechConstant.ASR_PTT, mSharedPreferences.getString("iat_punc_preference", "1"));*/

       mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
       mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH,
               Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
       // 设置音频来源为外部文件
       mIat.setParameter(SpeechConstant.AUDIO_SOURCE, "-1");
//       mIat.setParameter(SpeechConstant.SAMPLE_RATE,"8000");
       ret = mIat.startListening(recognizerListener);
       if (ret != ErrorCode.SUCCESS) {
           T.showShort(context,"识别失败,错误码：" + ret);
       } else {
           byte[] audioData = FucUtil.readAudio(Environment.getExternalStorageDirectory()+"/FinalAudio.wav");
           if (null != audioData) {
               mIat.writeAudio(audioData, 0, audioData.length);
               mIat.stopListening();
           } else {
               mIat.cancel();
               T.showShort(context,"读取音频流失败");
           }
       }
    }

    private RecognizerListener recognizerListener=new RecognizerListener() {
        @Override
        public void onVolumeChanged(int i, byte[] bytes) {
//            Log.e("========onVolumeChanged",i+"onVolumeChanged"+bytes);
        }

        @Override
        public void onBeginOfSpeech() {
            Log.e("========onBeginOfSpeech","onBeginOfSpeech");
        }

        @Override
        public void onEndOfSpeech() {
            Log.e("========onEndOfSpeech","onEndOfSpeeh");

        }

        @Override
        public void onResult(RecognizerResult recognizerResult, boolean b) {
            Log.e("========onResult",b+"");
            printResult(recognizerResult);
        }

        @Override
        public void onError(SpeechError speechError) {
            T.showShort(getContext(),speechError+"");
            Log.e("========onError","onError:"+speechError);
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {
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
        Log.e("============",resultBuffer+"");
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
                T.showShort(context,"初始化失败，错误码：" + code);
            }
        }
    };

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent) {
        T.showShort(view.getContext(),"dsdafasdfa");
        return false;
    }

    @Override
    public boolean onLongClick(View view) {
        T.showShort(view.getContext(),"onlongClick");
        return false;
    }
}
