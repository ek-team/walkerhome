package com.pharos.walker.utils;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.GrammarListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechEvent;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.VoiceWakeuper;
import com.iflytek.cloud.WakeuperListener;
import com.iflytek.cloud.WakeuperResult;
import com.iflytek.cloud.util.ResourceUtil;
import com.pharos.walker.beans.ScBean;
import com.pharos.walker.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;

/**
 * Created by zhanglun on 2021/4/23
 * Describe:
 */
public class OneShotUtil {
    private static String TAG = OneShotUtil.class.getSimpleName();
    // 语音唤醒对象
    private VoiceWakeuper mIvw;
    // 语音识别对象
    private SpeechRecognizer mAsr;
    private Context context;
    private int curThresh = 1450;
    // 云端语法文件
    private String mCloudGrammar = null;
    // 云端语法id
    private String mCloudGrammarID;
    // 本地语法id
    private String mLocalGrammarID;
    // 本地语法文件
    private String mLocalGrammar = null;
    // 本地语法构建路径
    private String grmPath = Environment.getExternalStorageDirectory().getAbsolutePath()
            + "/msc/test";
    // 引擎类型
    private String mEngineType = SpeechConstant.TYPE_LOCAL;
    private static volatile OneShotUtil instance = null;
    public static OneShotUtil getInstance(Context context) {
        if (instance == null) {
            synchronized (OneShotUtil.class) {
                if (instance == null) {
                    instance = new OneShotUtil(context.getApplicationContext());
                }
            }
        }
        return instance;
    }

    public OneShotUtil(Context context) {
        this.context = context;
        initConfig();
    }
    private void initConfig() {
        // 初始化唤醒对象
        mIvw = VoiceWakeuper.createWakeuper(context, null);
        // 初始化识别对象---唤醒+识别,用来构建语法
        mAsr = SpeechRecognizer.createRecognizer(context, null);
        // 初始化语法文件
        mCloudGrammar = readFile(context, "wake_grammar_sample.abnf", "utf-8");
        mLocalGrammar = readFile(context, "wake.bnf", "utf-8");
        if (mAsr != null && mIvw != null){
            buildGrammar();
        }
    }
    public void startOneShot(){
        if (mIvw != null){
            mIvw.startListening(mWakeuperListener);
        }
    }
    private void setParam() {
        final String resPath = ResourceUtil.generateResourcePath(context, ResourceUtil.RESOURCE_TYPE.assets, "ivw/" + context.getString(R.string.app_id)+".jet");
        // 清空参数
        mIvw.setParameter(SpeechConstant.PARAMS, null);
        // 设置识别引擎
        mIvw.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
        // 设置唤醒资源路径
        mIvw.setParameter(ResourceUtil.IVW_RES_PATH, resPath);
        /**
         * 唤醒门限值，根据资源携带的唤醒词个数按照“id:门限;id:门限”的格式传入
         * 示例demo默认设置第一个唤醒词，建议开发者根据定制资源中唤醒词个数进行设置
         */
        mIvw.setParameter(SpeechConstant.IVW_THRESHOLD, "0:"
                + curThresh);
        // 设置唤醒+识别模式
        mIvw.setParameter(SpeechConstant.IVW_SST, "oneshot");
        // 设置返回结果格式
        mIvw.setParameter(SpeechConstant.RESULT_TYPE, "json");
//				mIvw.setParameter(SpeechConstant.IVW_SHOT_WORD, "0");
        // 设置唤醒录音保存路径，保存最近一分钟的音频
        mIvw.setParameter( SpeechConstant.IVW_AUDIO_PATH, Environment.getExternalStorageDirectory().getPath()+"/msc/ivw.wav" );
        mIvw.setParameter( SpeechConstant.AUDIO_FORMAT, "wav" );
        mAsr.setParameter(SpeechConstant.VAD_BOS, "4000");
        mAsr.setParameter(SpeechConstant.VAD_EOS, "4000");
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            if (!TextUtils.isEmpty(mCloudGrammarID)) {
                // 设置云端识别使用的语法id
                mIvw.setParameter(SpeechConstant.CLOUD_GRAMMAR,
                        mCloudGrammarID);
                mIvw.startListening(mWakeuperListener);
            } else {
                ToastUtils.showShort("请先构建语法");
            }
        } else {
            if (!TextUtils.isEmpty(mLocalGrammarID)) {
                // 设置本地识别资源
                mIvw.setParameter(ResourceUtil.ASR_RES_PATH,
                        getResourcePath());
                // 设置语法构建路径
                mIvw.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
                // 设置本地识别使用语法id
                mIvw.setParameter(SpeechConstant.LOCAL_GRAMMAR,
                        mLocalGrammarID);
//                mIvw.startListening(mWakeuperListener);
            } else {
                ToastUtils.showShort("请先构建语法");
            }
        }

    }
    //构建语法
    private void buildGrammar(){
        int ret = 0;
        if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
            // 设置参数
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            // 开始构建语法
            ret = mAsr.buildGrammar("abnf", mCloudGrammar, grammarListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.e(TAG,"语法构建失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        } else {
            mAsr.setParameter(SpeechConstant.PARAMS, null);
            mAsr.setParameter(SpeechConstant.TEXT_ENCODING, "utf-8");
            // 设置引擎类型
            mAsr.setParameter(SpeechConstant.ENGINE_TYPE, mEngineType);
            // 设置语法构建路径
            mAsr.setParameter(ResourceUtil.GRM_BUILD_PATH, grmPath);
            // 设置资源路径
            mAsr.setParameter(ResourceUtil.ASR_RES_PATH, getResourcePath());
            ret = mAsr.buildGrammar("bnf", mLocalGrammar, grammarListener);
            if (ret != ErrorCode.SUCCESS) {
                Log.e(TAG,"语法构建失败,错误码：" + ret+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }

    }
    private WakeuperListener mWakeuperListener = new WakeuperListener() {

        @Override
        public void onResult(WakeuperResult result) {
            String resultString;
            try {
                String text = result.getResultString();
                JSONObject object;
                object = new JSONObject(text);
                StringBuffer buffer = new StringBuffer();
                buffer.append("【RAW】 "+text);
                buffer.append("\n");
                buffer.append("【操作类型】"+ object.optString("sst"));
                buffer.append("\n");
                buffer.append("【唤醒词id】"+ object.optString("id"));
                buffer.append("\n");
                buffer.append("【得分】" + object.optString("score"));
                buffer.append("\n");
                buffer.append("【前端点】" + object.optString("bos"));
                buffer.append("\n");
                buffer.append("【尾端点】" + object.optString("eos"));
                resultString = buffer.toString();
            } catch (JSONException e) {
                resultString = "结果解析出错";
                e.printStackTrace();
            }
            Log.e(TAG,resultString);

        }

        @Override
        public void onError(SpeechError error) {
            ToastUtils.showShort(error.getPlainDescription(true));
        }

        @Override
        public void onBeginOfSpeech() {
            ToastUtils.showShort("开始说话");
        }

        @Override
        public void onEvent(int eventType, int isLast, int arg2, Bundle obj) {
            Log.d(TAG, "eventType:"+eventType+ "arg1:"+isLast + "arg2:" + arg2);
            // 识别结果
            if (SpeechEvent.EVENT_IVW_RESULT == eventType) {
                RecognizerResult reslut = ((RecognizerResult)obj.get(SpeechEvent.KEY_EVENT_IVW_RESULT));
                if (reslut != null){
                    String resultJson = reslut.getResultString();
                    ScBean scBean = new Gson().fromJson(resultJson,ScBean.class);
                    String recoString = JsonParser.parseGrammarResult(reslut.getResultString());
                    Log.e(TAG,recoString);
                }
            }
        }

        @Override
        public void onVolumeChanged(int volume) {
            // TODO Auto-generated method stub

        }

    };
    private GrammarListener grammarListener = new GrammarListener() {
        @Override
        public void onBuildFinish(String grammarId, SpeechError error) {
            if (error == null) {
                if (mEngineType.equals(SpeechConstant.TYPE_CLOUD)) {
                    mCloudGrammarID = grammarId;
                } else {
                    mLocalGrammarID = grammarId;
                }
                mIvw = VoiceWakeuper.getWakeuper();
                if (mIvw != null){
                    setParam();
                }
                ToastUtils.showShort("语法构建成功：" + grammarId);
            } else {
                Log.e(TAG,"语法构建失败,错误码：" + error.getErrorCode()+",请点击网址https://www.xfyun.cn/document/error-code查询解决方案");
            }
        }
    };
    public void destoryOneShot(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.destroy();
        }
        if (mAsr != null){
            mAsr.destroy();
        }
    }
    public void stopOneShot(){
        mIvw = VoiceWakeuper.getWakeuper();
        if (mIvw != null) {
            mIvw.stopListening();
        }
//        if (mAsr != null){
//            mAsr.stopListening();
//        }
    }
    /**
     * 读取asset目录下文件。
     *
     * @return content
     */
    public static String readFile(Context mContext, String file, String code) {
        int len = 0;
        byte[] buf = null;
        String result = "";
        try {
            InputStream in = mContext.getAssets().open(file);
            len = in.available();
            buf = new byte[len];
            in.read(buf, 0, len);

            result = new String(buf, code);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    // 获取识别资源路径
    private String getResourcePath() {
        StringBuffer tempBuffer = new StringBuffer();
        // 识别通用资源
        tempBuffer.append(ResourceUtil.generateResourcePath(context,
                ResourceUtil.RESOURCE_TYPE.assets, "asr/common.jet"));
        return tempBuffer.toString();
    }
}
