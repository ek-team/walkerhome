package com.pharos.walker.utils;


import com.pharos.walker.application.MyApplication;

public class DimensUtil {

    public static int dp2px(float dpValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从px(像素) 的单位 转成为dp
     */
    public static int px2dp(float pxValue) {
        final float scale = MyApplication.getInstance().getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }
}
