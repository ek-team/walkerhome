package com.pharos.walker.constants;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class Global {
    public static final String Header = ":";
    public static final String Header_S = "FFFF";
    public static volatile boolean isReconnectBle = false;
    public static volatile boolean isConnected = false;
    public static volatile boolean isStartReadData = false;
    public static volatile String ConnectStatus = "unconnected";
    public static volatile String ConnectedAddress = null;
    public static String ConnectedName = null;
    public static boolean isSendHeart = true;
    public static int VOICE_SWITCH = 0;          //语音开关	开0 关1
    public static volatile int BLE_BATTERY = 0;          //蓝牙鞋电量
    public static volatile boolean USER_MODE = true;
    public static volatile int ConnectMainMode = 0;          //训练模式
    public static volatile int ConnectEvaluateMode = 1;          //评估模式
    public static volatile int ConnectUserMode = 2;          //用户模式
    public static volatile int ConnectSetMode = 3;          //设置模式
    public static volatile int ReleaseVersion = 0;          //0单击版本,1客户端版，2服务端版，3家庭版
    public static final int SingleVersion = 0;
    public static final int ClientVersion = 1;
    public static final int ServerVersion = 2;
    public static final int HomeVersion = 3;

    public static final int UploadStatus = 0;
    public static final int UploadLocalStatus = 1;
    public static final int UploadNetStatus = 2;
    public static final String Delimiter = "[";
    public static final String Comma = ",";
    public static boolean isChangSha = false;
    public static final int ChangSha = 101;//长沙下肢
    public static final int AnHui = 102;//安徽下肢
    public static final int HOME = 301;//家庭版下肢
    public static final int TrainTime = 5;//默认训练时间
    public static volatile int ReadCount = 0;
    public static volatile boolean OpenConnectView = false;
    public static final String initBle = "init_ble";//初始化蓝牙
    public static volatile boolean FactoryCheck = false;//出厂检验标志
    public static volatile boolean enable = true;//是否记录电量
    public static volatile boolean isScanning = false;

    public static final int TrainCountMinute = 15;//每分钟训练步数
    public static final int MinTrainStep = 20;//最小训练步数
    public static final int MaxTrainStep = 200;//最大训练步数

    public static final int HomeMain = 0;
    public static final int HomeUser = 1;
}
