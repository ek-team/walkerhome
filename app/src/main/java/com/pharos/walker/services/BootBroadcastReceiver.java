package com.pharos.walker.services;

/**
 * Created by samael on 2017/6/15.
 * app自启动用广播
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.pharos.walker.ui.WelcomeActivity;

/**
 * 该类派生自BroadcastReceiver，覆载方法onReceive中
 * 检测接收到的Intent是否符合BOOT_COMPLETED，如果符合，则启动Activity。
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";
    static final String ACTION_SCREEN_OFF = "android.intent.action.SCREEN_OFF";
    static final String ACTION_SCREEN_ON = "android.intent.action.SCREEN_ON";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, WelcomeActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
        if (intent.getAction().equals(ACTION_SCREEN_OFF)){
            Log.e("BootBroadcastReceiver", "onReceive: 熄屏了" );
        }
        if (intent.getAction().equals(ACTION_SCREEN_ON)){
            Log.e("BootBroadcastReceiver", "onReceive: 屏幕亮了" );
        }

    }
}
