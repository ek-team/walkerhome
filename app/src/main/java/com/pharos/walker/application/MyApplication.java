package com.pharos.walker.application;

import android.app.Application;
import android.content.res.Configuration;
import android.content.res.Resources;

import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUtility;
import com.pharos.walker.R;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.GreenDaoHelper;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class MyApplication extends Application {
    public static MyApplication instance;

    public static MyApplication getInstance() {
        return instance;
    }
    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        CrashHandler.getInstance().init(instance);
        String param = "appid=" + getString(R.string.app_id) +
                "," +
                SpeechConstant.ENGINE_MODE + "=" + SpeechConstant.MODE_MSC;
        SpeechUtility.createUtility(MyApplication.this, param);
        GreenDaoHelper.initDatabase();
    }
    @Override
    public Resources getResources() {
        Resources res = super.getResources();
        float fontSize = res.getConfiguration().fontScale;
        if (fontSize != 1) {//非默认值
            Configuration newConfig = new Configuration();
            newConfig.setToDefaults();//设置默认
            res.updateConfiguration(newConfig, res.getDisplayMetrics());
        }
        return res;
    }
    //设置字体为默认大小，不随系统字体大小改而改变
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        if (newConfig.fontScale != 1)//非默认值
            getResources();
        super.onConfigurationChanged(newConfig);
    }
}
