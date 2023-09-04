package com.pharos.walker.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechSynthesizer;
import com.iflytek.cloud.SynthesizerListener;
import com.iflytek.cloud.util.ResourceUtil;

/**
 * Created by zhanglun on 2020/6/23
 * Describe:
 */
public class SpeechUtil {
    private static String TAG = SpeechUtil.class.getSimpleName();
    // 语音合成对象
    private SpeechSynthesizer mTts;
    // 默认云端发音人
    private static String voicerCloud="xiaoyan";
    // 默认本地发音人
    private static String voicerLocal="xiaoyan";
    //增强语音发音人
    private static String voicerXtts="xiaoyan";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_XTTS;
    private Context context;
    private static boolean INITSUCCESS = false;
    private static volatile SpeechUtil instance = null;
    public static SpeechUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (SpeechUtil.class) {
                if (instance == null) {
                    instance = new SpeechUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }
    private SpeechUtil(Context context) {
        this.context = context;
        initSpeechConfig();
    }
    private void initSpeechConfig(){
        mTts = SpeechSynthesizer.createSynthesizer(context, mTtsInitListener);
        if (mTts != null){
            setParam();
        }
    }
    public void speak(String text){
        if (SPHelper.getVoiceState()) {
            if (mTts != null && INITSUCCESS){
                mTts.startSpeaking(text, mTtsListener);
            }else if (mTts != null){
                SystemClock.sleep(100);
                mTts.startSpeaking(text, mTtsListener);
            }
        }
    }
    public void speak(String text,SynthesizerListener mTtsListener){
        if (SPHelper.getVoiceState()) {
            if (mTts != null && INITSUCCESS){
                mTts.startSpeaking(text, mTtsListener);
            }else if (mTts != null){
                SystemClock.sleep(100);
                mTts.startSpeaking(text, mTtsListener);
            }
        }
    }
    public void closeSpeak(){
        if( null != mTts ){
            mTts.stopSpeaking();
            // 退出时释放连接
            mTts.destroy();
        }
    }
    /**
     * 参数设置
     */
    private void setParam(){
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null);
        //设置合成
        if(mEngineType.equals(SpeechConstant.TYPE_CLOUD))
        {
            //设置使用云端引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_CLOUD);
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerCloud);

        }else if(mEngineType.equals(SpeechConstant.TYPE_LOCAL)){
            //设置使用本地引擎
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerLocal);
        }else{
            mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_XTTS);
            //设置发音人资源路径
            mTts.setParameter(ResourceUtil.TTS_RES_PATH,getResourcePath());
            //设置发音人
            mTts.setParameter(SpeechConstant.VOICE_NAME,voicerXtts);
        }
        //mTts.setParameter(SpeechConstant.TTS_DATA_NOTIFY,"1");//支持实时音频流抛出，仅在synthesizeToUri条件下支持
        //设置合成语速
        mTts.setParameter(SpeechConstant.SPEED, "50");
        //设置合成音调
        mTts.setParameter(SpeechConstant.PITCH, "50");
        //设置合成音量
        mTts.setParameter(SpeechConstant.VOLUME, "50");
        //设置播放器音频流类型
        mTts.setParameter(SpeechConstant.STREAM_TYPE, "3");
        //	mTts.setParameter(SpeechConstant.STREAM_TYPE, AudioManager.STREAM_MUSIC+"");

        // 设置播放合成音频打断音乐播放，默认为true
        mTts.setParameter(SpeechConstant.KEY_REQUEST_FOCUS, "true");
        // 设置音频保存路径，保存音频格式支持pcm、wav，设置路径为sd卡请注意WRITE_EXTERNAL_STORAGE权限
//        mTts.setParameter(SpeechConstant.AUDIO_FORMAT, "wav");
//        mTts.setParameter(SpeechConstant.TTS_AUDIO_PATH, Environment.getExternalStorageDirectory()+"/msc/tts.wav");


    }

    //获取发音人资源路径
    private String getResourcePath(){
        StringBuilder tempBuffer = new StringBuilder();
        String type= "tts";
        if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
            type="xtts";
        }
        //合成通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, type+"/common.jet"));
        tempBuffer.append(";");
        //发音人资源
        if(mEngineType.equals(SpeechConstant.TYPE_XTTS)){
            tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, type+"/"+voicerXtts+".jet"));
        }else {
            tempBuffer.append(ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, type + "/" + voicerLocal + ".jet"));
        }

        return tempBuffer.toString();
    }
    /**
     * 初始化监听。
     */
    private InitListener mTtsInitListener = new InitListener() {
        @Override
        public void onInit(int code) {

            if (code == ErrorCode.SUCCESS) {
                //初始化成功
                INITSUCCESS = true;
                Log.d(TAG, "语音初始化成功InitListener init() code = " + code);
            }else {
                Log.e(TAG, "初始化失败,错误码："+code+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };
    /**
     * 合成回调监听。
     */
    private SynthesizerListener mTtsListener = new SynthesizerListener() {

        @Override
        public void onSpeakBegin() {
            //showTip("开始播放");
        }

        @Override
        public void onSpeakPaused() {
        }

        @Override
        public void onSpeakResumed() {
        }

        @Override
        public void onBufferProgress(int percent, int beginPos, int endPos,
                                     String info) {
            // 合成进度
        }

        @Override
        public void onSpeakProgress(int percent, int beginPos, int endPos) {
            // 播放进度
        }

        @Override
        public void onCompleted(SpeechError error) {
        }

        @Override
        public void onEvent(int i, int i1, int i2, Bundle bundle) {

        }
    };
}
