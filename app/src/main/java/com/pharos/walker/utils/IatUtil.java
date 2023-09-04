package com.pharos.walker.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.os.SystemClock;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.pharos.walker.constants.MessageEvent;

import org.greenrobot.eventbus.EventBus;

import static com.pharos.walker.constants.MessageEvent.ACTION_IAT;


/**
 * Created by zhanglun on 2020/9/4
 * Describe:
 */
public class IatUtil {
    private static String TAG = IatUtil.class.getSimpleName();
    // 语音听写对象
    private SpeechRecognizer mIat;
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_CLOUD;
    private Context context;
    private static boolean INITSUCCESS = false;
    private static volatile IatUtil instance = null;
    public static IatUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (IatUtil.class) {
                if (instance == null) {
                    instance = new IatUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public IatUtil(Context context) {
        this.context = context;
        initConfig();
    }

    int ret = 0;
    public void startListen(){
        if (mIat != null && INITSUCCESS){
            ret = mIat.startListening(mRecognizerListener);
        }else {
            SystemClock.sleep(100);
            ret = mIat.startListening(mRecognizerListener);
        }
        if (ret != ErrorCode.SUCCESS) {
            Log.e(TAG, "听写失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
        } else {
            SpeechUtil.getInstance(context).speak("请吩咐");
        }
    }
    private void initConfig() {
        mIat = SpeechRecognizer.createRecognizer(context, mInitListener);
        setParam();
    }
    /**
     * 参数设置
     * @return
     */
    private void setParam(){
        // 清空参数
        mIat.setParameter(SpeechConstant.PARAMS, null);
        // 设置引擎
        mIat.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置返回结果格式
        mIat.setParameter(SpeechConstant.RESULT_TYPE, "json");
        // 在线听写支持多种小语种，若想了解请下载在线听写能力，参看其speechDemo
        // 设置语言
        mIat.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mIat.setParameter(SpeechConstant.ACCENT,"mandarin");

        // 设置语音前端点:静音超时时间，即用户多长时间不说话则当做超时处理
        mIat.setParameter(SpeechConstant.VAD_BOS, "4000");

        // 设置语音后端点:后端点静音检测时间，即用户停止说话多长时间内即认为不再输入， 自动停止录音
        mIat.setParameter(SpeechConstant.VAD_EOS, "1000");

        // 设置标点符号,设置为"0"返回结果无标点,设置为"1"返回结果有标点
        mIat.setParameter(SpeechConstant.ASR_PTT, "0");

        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
        mIat.setParameter(SpeechConstant.AUDIO_FORMAT,"wav");
        mIat.setParameter(SpeechConstant.ASR_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/iat.wav");
    }
    /**
     * 初始化监听器。
     */
    private InitListener mInitListener = new InitListener() {

        @Override
        public void onInit(int code) {
            Log.d(TAG, "SpeechRecognizer init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.e(TAG,"初始化失败，错误码：" + code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }else {
                INITSUCCESS = true;
            }
        }
    };
    /**
     * 听写监听器。
     */
    private RecognizerListener mRecognizerListener = new RecognizerListener() {

        @Override
        public void onBeginOfSpeech() {
            // 此回调表示：sdk内部录音机已经准备好了，用户可以开始语音输入
            ToastUtils.showShort("开始说话");
        }

        @Override
        public void onError(SpeechError error) {
            // Tips：
            // 错误码：10118(您没有说话)，可能是录音机权限被禁，需要提示用户打开应用的录音权限。
        }

        @Override
        public void onEndOfSpeech() {
            // 此回调表示：检测到了语音的尾端点，已经进入识别过程，不再接受语音输入
        }

        StringBuffer sb = new StringBuffer();
        @Override
        public void onResult(RecognizerResult results, boolean isLast) {
            String text = JsonParser.parseIatResult(results.getResultString());
            sb.append(text);
            if(isLast) {
                EventBus.getDefault().post(new MessageEvent<>(ACTION_IAT, sb.toString()));
                sb.setLength(0);
            }
        }

        @Override
        public void onVolumeChanged(int volume, byte[] data) {
            Log.d(TAG, "返回音频数据："+data.length);
        }

        @Override
        public void onEvent(int eventType, int arg1, int arg2, Bundle obj) {
            // 以下代码用于获取与云端的会话id，当业务出错时将会话id提供给技术支持人员，可用于查询会话日志，定位出错原因
            // 若使用本地能力，会话id为null
            if (SpeechEvent.EVENT_SESSION_ID == eventType) {
                String sid = obj.getString(SpeechEvent.KEY_EVENT_AUDIO_URL);
                Log.d(TAG, "session id =" + sid);
            }
        }
    };
}
