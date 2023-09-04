package com.pharos.walker.services;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.os.BatteryManager;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.SystemClock;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.pharos.walker.MainActivity;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.R;
import com.pharos.walker.application.MyApplication;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.ui.StartActivity;
import com.pharos.walker.ui.TrainParamActivity;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ShellUtils;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.SqlToExcleUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;


/**
 * @Description: 前台工作服务（设置为前台优先级最高，软件启动就开始了）
 * @Author: zf
 * @Time 2019/4/25
 */
public class ForegroundWorkService extends Service {

    public static final String CMD = "cmd";
    /**
     * id不可设置为0,否则不能设置为前台service
     */
    private static final int NOTIFICATION_DOWNLOAD_PROGRESS_ID = 0x0001;

    private boolean isTimeFlag = true;
    private TimeThread mTimeThread;
    private HeartThread mHeartThread;
    private BatteryRefreshThread batteryRefreshThread;
    private CheckHeartThread checkHeartThread;
    private IBinder mBinder = new LocalBinder();
    private BtTemperatureReceiver broadcastReceiver;
    private boolean defaultBLeNoConnected = false;
    private HashMap<String, String> bleMap = new HashMap<>();
    private ScreenReceiver screenReceiver;
    public class LocalBinder extends Binder {
        public ForegroundWorkService getService() {
            return ForegroundWorkService.this;
        }
    }


    public static void launch() {
        launch(null);
    }

    public static void launch(String cmd) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            MyApplication.getInstance().startForegroundService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }else {
            MyApplication.getInstance().startService(new Intent(MyApplication.getInstance(), ForegroundWorkService.class)
                    .putExtra(CMD, cmd));
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createNotification();
        EventBus.getDefault().register(this);
        mTimeThread = new TimeThread();
        mTimeThread.start();
        mHeartThread = new HeartThread();
        mHeartThread.start();
        batteryRefreshThread = new BatteryRefreshThread();
        batteryRefreshThread.start();
        checkHeartThread = new CheckHeartThread();
        checkHeartThread.start();
        initBtState();
        setScreenReceiver();
    }
    /**
     * 初始化蓝牙状态广播监听
     */
    private void initBtState(){
        broadcastReceiver = new BtTemperatureReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);
        BluetoothController.getInstance().startScanBle();
    }
    private void setScreenReceiver(){
        screenReceiver = new ScreenReceiver();
        IntentFilter filter = new IntentFilter();
        //添加要注册的action
        filter.addAction(Intent.ACTION_SCREEN_ON);
        filter.addAction(Intent.ACTION_SCREEN_OFF);
        //动态注册广播接收者
        registerReceiver(screenReceiver, filter);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                BleBean bleBean = (BleBean) event.getData();
                Global.ConnectedAddress = bleBean.getAddress();
                Global.ConnectedName = bleBean.getName();
                SPHelper.saveBleAddress(bleBean.getAddress());
                Global.ConnectStatus = "connecting";
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                BluetoothController.getInstance().startScanBle();
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                if ((int)event.getData() == 0){
                    Global.ConnectStatus = "connected";
                    Global.isConnected = true;
                    BluetoothController.getInstance().stopScanBle();
                }else {
                    ToastUtils.showShort("蓝牙鞋通信失败，重新连接");
                    Global.ConnectedAddress = null;
                    Global.ConnectedName = null;
                    Global.isConnected = false;
                }
                break;
            case MessageEvent.BLE_SCAN_RESULT:
                bleBean = (BleBean) event.getData();
                String name = bleBean.getName();
                String address = bleBean.getAddress();
                if (Global.isConnected && !TextUtils.isEmpty(Global.ConnectedAddress)){
//                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                }else if (!Global.ConnectStatus.equals("connecting") && !Global.isConnected && !Global.isReconnectBle){
                    if (TextUtils.isEmpty(SPHelper.getBleAddress())){
                        BluetoothController.getInstance().connect(address);
                        Global.ConnectStatus = "connecting";
                    }else if (defaultBLeNoConnected){
                        BluetoothController.getInstance().connect(address);
                        Global.ConnectStatus = "connecting";
                    }else {
                        connectDefaultBle(address);
                    }
//                    else if (isEnable){
//                        BluetoothController.getInstance().connect(address);
//                        Global.ConnectStatus = "connecting";
//                        isEnable = false;
//                    }
                }
//                int rssi = bleBean.getRssi();
//                if (!bleMap.containsKey(address)) {
//                    bleMap.put(address, address);
//                }
                break;
            case MessageEvent.BLE_CONNECT_TIME_OUT:
                if (!Global.isConnected){
                    defaultBLeNoConnected = true;
                    Global.ConnectStatus = "unconnected";
                    Log.e("work service", "onMessageEvent: " + "默认连接超时");
                }
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                synchronized (this){
                    Global.ReadCount = 0;
                }
                break;
            case MessageEvent.READ_DATA_HEART_DISCONNECT_SERVICES:
                Log.e("work service", "onMessageEvent: " + "心跳停了");
                if (Global.ConnectedAddress != null && !Global.isReconnectBle) {//非训练状态关闭连接
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                    BluetoothController.getInstance().clearConnectedStatus();
                }
                BluetoothController.getInstance().startScanBle();
                break;
        }
    }
    private Timer mTimer2;
    private TimerTask mTimerTask2;
    private void startBleConnectTimer(){
        if (mTimer2 == null && mTimerTask2 == null) {
            mTimer2 = new Timer();
            mTimerTask2 = new TimerTask() {
                @Override
                public void run() {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.BLE_CONNECT_TIME_OUT));
                    mTimer2.cancel();
                    mTimerTask2.cancel();
                    mTimer2 = null;
                    mTimerTask2 = null;

                }
            };
            mTimer2.schedule(mTimerTask2,5000);
        }
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null){
            String cmd = intent.getStringExtra(CMD);
            if (!TextUtils.isEmpty(cmd) && cmd.equals(Global.initBle)){//第一次获取权限重新扫描
                BluetoothController.getInstance().stopScanBle();
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                BluetoothController.getInstance().startScanBle();
            }
        }
//        connectDefaultBle();
        return super.onStartCommand(intent, flags, startId);
    }

    private void connectDefaultBle(String address){
        startBleConnectTimer();
        if (!Global.ConnectStatus.equals("connecting") && !Global.isConnected && address.equals(SPHelper.getBleAddress())){
            Global.ConnectStatus = "connecting";
            BluetoothController.getInstance().connect(SPHelper.getBleAddress());
        }
    }
    @Override
    public IBinder onBind(Intent intent) {
        Log.d("Service", "onBind()");
        return mBinder;
    }

    @Override
    public void onDestroy() {
        if (mTimeThread != null) {
            mTimeThread.interrupt();
            isTimeFlag = false;
            mTimeThread = null;
        }
        if (mHeartThread != null){
            mHeartThread.interrupt();
            Global.isSendHeart = false;
            mHeartThread = null;
        }
        if (batteryRefreshThread != null){
            batteryRefreshThread.interrupt();
            batteryRefreshThread = null;
        }
        if (checkHeartThread != null){
            checkHeartThread.interrupt();
            checkHeartThread = null;
        }
        if (mTimer2 != null){
            mTimer2.cancel();
            mTimerTask2.cancel();
            mTimer2 = null;
            mTimerTask2 = null;
        }

        stopForeground(true);
        BluetoothController.getInstance().stopScanBle();
        unregisterReceiver(broadcastReceiver);//注销广播接收器
        unregisterReceiver(screenReceiver);//注销广播接收器
        EventBus.getDefault().unregister(this);
        Log.e("ForegroundWorkService", "onDestroy: "+"死掉了" );
        super.onDestroy();
    }

    /**
     * Notification
     */
    public void createNotification() {
        String channel_id = "com.pharos.walker";
        String channelName = "MyWorkService";

        //使用兼容版本
        Notification.Builder builder = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel chan = new NotificationChannel(channel_id, channelName, NotificationManager.IMPORTANCE_NONE);
            chan.setLightColor(Color.BLUE);
            chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
            NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            assert manager != null;
            manager.createNotificationChannel(chan);
            builder = new Notification.Builder(this,channel_id);
        }else {
            builder = new Notification.Builder(this);
        }
        //设置状态栏的通知图标
        builder.setSmallIcon(R.mipmap.ic_launcher);
        //禁止用户点击删除按钮删除
        builder.setAutoCancel(true);
        //禁止滑动删除
        builder.setOngoing(true);
        //右上角的时间显示
        builder.setShowWhen(true);
        //设置通知栏的标题内容
        builder.setContentTitle("助行");
//        builder.setContentText("连接你我");
        //创建通知
        Notification notification = builder.build();
        //设置为前台服务
        startForeground(NOTIFICATION_DOWNLOAD_PROGRESS_ID, notification);
    }

    //动态更新时间的线程
    private class TimeThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                EventBus.getDefault().post(new MessageEvent(MessageEvent.UPDATE_TOP_TIME));
//                isBackground(MyApplication.getInstance());
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
    }
    //动态更新时间的线程
    private class BatteryRefreshThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                BatteryManager manager = (BatteryManager) getSystemService(BATTERY_SERVICE);
                int batteryVolume = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY);///当前电量百分比
                int batteryStatus = manager.getIntProperty(BatteryManager.BATTERY_PROPERTY_STATUS);
                if (batteryVolume >= 100){
                    batteryVolume = 100;
                }else if (batteryVolume <= 0){
                    batteryVolume = 30;
                }
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.BATTERY_REFRESH, new Battery(batteryStatus,batteryVolume)));
                SystemClock.sleep(1000);
            } while (isTimeFlag);
        }
    }
    //维持心跳线程 一分钟和下位机通信一次
    private class HeartThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isSendHeart && Global.isConnected){

//                HermesEventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_HEART_STATUS, heartStatus));
                    if (!Global.isReconnectBle){//非训练状态的心跳发送
                        int a = 0x1A;
                        int b = 0x04 | 0x30;
                        int c = 0x00;
                        int d = 0xFF - (a + b + c) + 1;
                        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                        SystemClock.sleep(5000);
                    }else {
//                        SystemClock.sleep(5 * 1000);
//                        SystemClock.sleep(60 * 1000);
                    }
                }
            }while (isTimeFlag);
        }
    }
    //检测心跳线程
    private class CheckHeartThread extends Thread {
        @Override
        public void run() {
            super.run();
            do {
                if (Global.isConnected && Global.isStartReadData){
                    if (Global.ReadCount == 1){
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.READ_DATA_HEART_DISCONNECT));
                    }else {
                        Global.ReadCount = 1;
                    }
                    SystemClock.sleep(1000);
                }else if (Global.isConnected && !Global.isStartReadData){//非训练状态的心跳检测
                    if (Global.ReadCount == 1){
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.READ_DATA_HEART_DISCONNECT_SERVICES));
                    }else {
                        Global.ReadCount = 1;
                    }
                    SystemClock.sleep(6000);
                }
            }while (isTimeFlag);
        }
    }
    public static boolean isBackground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        boolean isBackground = true;
        String processName = "empty";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(context.getPackageName())) {
                processName = appProcess.processName;
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    isBackground = true;
                } else if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    isBackground = false;
                } else {
                    isBackground = true;
                }
            }
        }
        if (isBackground) {
            Log.e("work service", "后台:" + processName);
        } else {
            Log.e("work service", "前台+" + processName);
        }
        return isBackground;
    }
    public class ScreenReceiver extends BroadcastReceiver {

        //当我们进行屏幕锁屏和解锁 这个方法执行
        @Override
        public void onReceive(Context context, Intent intent) {

            //获取当前广播的事件类型
            String action = intent.getAction();
            if ("android.intent.action.SCREEN_OFF".equals(action)) {
                Log.e("Work Service", "onReceive: 熄屏了" );
                ShellUtils.execCmd("reboot -p",true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShellUtils.execCmd("reboot -p",true);
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                ShellUtils.execCmd("reboot -p",true);
            } else if ("android.intent.action.SCREEN_ON".equals(action)) {
                Log.e("Work Service", "onReceive: 屏幕亮了" );
            }
        }
    }
    /**
     * 蓝牙状态广播回调
     */
    class BtTemperatureReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //注意!这里是先拿action 等于 BluetoothAdapter.ACTION_STATE_CHANGED 在解析intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0)
            if (BluetoothAdapter.ACTION_STATE_CHANGED.equals(Objects.requireNonNull(action))) {
                int blState = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, 0);
                switch (blState) {
                    case BluetoothAdapter.STATE_TURNING_ON:
//                        ToastUtils.showShort("蓝牙正在开启……，请稍等");
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothController.getInstance().startScanBle();
//                        connectDefaultBle();
                        break;
                    case BluetoothAdapter.STATE_TURNING_OFF:
                        break;
                    case BluetoothAdapter.STATE_OFF:
                        break;
                    case BluetoothAdapter.ERROR:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
