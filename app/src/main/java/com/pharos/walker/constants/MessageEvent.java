package com.pharos.walker.constants;

public class MessageEvent<T> {

    public final static String UPDATE_TOP_TIME = "0";                    // 更新标题栏的时间
    public final static String BATTERY_REFRESH = "1";                       // 电池电量刷新
    public final static String READ_DATA_HEART_DISCONNECT = "1001";                    // 读取数据心跳状态
    public final static String READ_DATA_HEART_DISCONNECT_SERVICES = "1002";                    // 读取数据心跳状态
    public final static String BLE_SCAN_RESULT = "3001";                    // 蓝牙扫描结果
    public final static String BLE_SCAN_RESULT_HOME = "3000";                    // 蓝牙扫描结果
    public final static String ACTION_GATT_CONNECTED = "3002";              // 蓝牙连接成功（更改界面手的显示状态）
    public final static String ACTION_GATT_DISCONNECTED = "3003";         // 蓝牙连接失败(连接中断)
    public final static String ACTION_GATT_CONNECTED_HOME = "3004";              // 蓝牙连接成功（更改界面手的显示状态）
    public final static String ACTION_GATT_DISCONNECTED_HOME = "3005";         // 蓝牙连接失败(连接中断)
    public final static String ACTION_GATT_CONNECTING_HOME = "3006";         // 蓝牙连接失败(连接中断)
    public final static String ACTION_READ_DATA = "2001";         // 读取重量
    public final static String ACTION_READ_DEVICE = "2002";         // 读取设备
    public final static String DISCONNECT_DEVICE = "2003";         // 断开设备
    public final static String GATT_TRANSPORT_OPEN = "2004";         // 传输通道打开
    public final static String GATT_TRANSPORT_OPEN_HOME = "2005";         // 传输通道打开
    public final static String BLE_CONNECT_TIME_OUT = "2006";         // 连接超时


    public final static String ACTION_IAT = "6001";         // 语音识别返回结果
    public final static String ACTION_COUNTDOWN = "7001";         // 更新倒计时时间
    public final static String ACTION_ClEAR_CONNECTED_DEVICE = "7002";         // 更新倒计时时间

    public final static String ACTION_SAVE_TIP = "8001";         // 保存文件提示
    public final static String ACTION_TRAIN_TIPS = "8002";         // 训练报警提示
    public final static String ACTION_DOWNLOAD_PROGRESS = "8003";         // 下载进度
    public final static String ACTION_GAME_DOG = "8004";         // 游戏发送
    public final static String ACTION_CHECK_VERSION = "8005";         // 检查版本
    public final static String ACTION_REQ_FAIL = "9000";         // 用户云端请求失败
    public final static String ACTION_SYNC_USER_RESULT = "9001";         // 用户云端同步结果
    public final static String ACTION_SYNC_PLAN_RESULT = "9002";         // 计划云端同步结果
    public final static String ACTION_SYNC_TRAIN_DATA_RESULT = "9003";         // 训练数据同步到云端结果
    public final static String ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT = "9004";         // 训练数据云端同步到本地结果
    public final static String ACTION_TOKEN_REQ_FAIL = "9005";         // token 请求失败
    public final static String ACTION_REQ_FAIL_SINGLE = "9006";         // 用户云端请求失败
    public final static String ACTION_SYNC_TRAIN_DATA_RESULT_SINGLE = "9007";         //
    public final static String ACTION_SYNC_EVALUATE_RECORD_RESULT = "9008";         // 评估记录同步到云端结果
    public final static String ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT = "9009";         // 评估记录云端同步到本地结果



    private String action;
    private T data;

    public MessageEvent(String action, T data) {
        this.action = action;
        this.data = data;
    }

    public MessageEvent(String message) {
        this.action = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
