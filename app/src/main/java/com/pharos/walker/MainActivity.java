package com.pharos.walker;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Process;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.app.ActivityCompat;

import com.android.dx.rop.cst.CstArray;
import com.app.hubert.guide.NewbieGuide;
import com.app.hubert.guide.model.GuidePage;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerActivationCodeBean;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxImageDialog;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.services.ForegroundWorkService;
import com.pharos.walker.ui.ActivationCodeActivity;
import com.pharos.walker.ui.BaseActivity;
import com.pharos.walker.ui.DoctorInfoActivity;
import com.pharos.walker.ui.EngineerActivity;
import com.pharos.walker.ui.HomeConnectActivity;
import com.pharos.walker.ui.HomeRegisterActivity;
import com.pharos.walker.ui.NewsActivity;
import com.pharos.walker.ui.PlanActivity;
import com.pharos.walker.ui.SettingActivity;
import com.pharos.walker.ui.StartActivity;
import com.pharos.walker.ui.TrainParamActivity;
import com.pharos.walker.ui.UserInfoActivity;
import com.pharos.walker.ui.VideoPlayerActivity;
import com.pharos.walker.ui.WelcomeTipsActivity;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DesUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SimInfoUtils;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/4/13
 * Describe:
 */
public class MainActivity extends BaseActivity implements View.OnTouchListener {
    @BindView(R.id.iv_power)
    ImageView ivPower;
    @BindView(R.id.tv_battery)
    TextView tvBattery;
    @BindView(R.id.iv_notification)
    ImageView ivNotification;
    @BindView(R.id.iv_red)
    ImageView ivRed;
    @BindView(R.id.iv_voice)
    ImageView ivVoice;
    @BindView(R.id.iv_point)
    ImageView ivPoint;
    @BindView(R.id.iv_bt)
    ImageView ivBt;
    @BindView(R.id.layout_bluetooth)
    LinearLayout layoutBluetooth;
    @BindView(R.id.iv_header)
    ImageView ivHeader;
    @BindView(R.id.layout_header)
    RelativeLayout layoutHeader;
    @BindView(R.id.tv_header)
    TextView tvHeader;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_am_pm)
    TextView tvAmPm;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.iv_video)
    ImageView ivVideo;
    @BindView(R.id.tv_help)
    TextView tvHelp;
    @BindView(R.id.layout_help)
    RelativeLayout layoutHelp;
    @BindView(R.id.ll_layout_help)
    LinearLayout llLayoutHelp;
    @BindView(R.id.iv_training)
    ImageView ivTraining;
    @BindView(R.id.iv_plan)
    TextView ivPlan;
    @BindView(R.id.iv_doctor)
    TextView ivDoctor;
    @BindView(R.id.iv_news)
    TextView ivNews;
    @BindView(R.id.iv_user_center)
    TextView ivUserCenter;
    @BindView(R.id.iv_setting)
    TextView ivSetting;
    @BindView(R.id.iv_training_1)
    ImageView ivTraining1;
    @BindView(R.id.iv_training_2)
    ImageView ivTraining2;
    @BindView(R.id.tv_more_operation)
    TextView tvMoreOperation;
    @BindView(R.id.rl_sample)
    RelativeLayout rlSample;
    @BindView(R.id.rl_complete)
    RelativeLayout rlComplete;
    @BindView(R.id.re_root)
    RelativeLayout reRoot;
    @BindView(R.id.tv_ble_battery)
    TextView tvBleBattery;
    @BindView(R.id.tv_time_1)
    TextView tvTime1;
    @BindView(R.id.tv_am_pm_1)
    TextView tvAmPm1;
    @BindView(R.id.tv_date_1)
    TextView tvDate1;
    @BindView(R.id.iv_video_1)
    ImageView ivVideo1;
    @BindView(R.id.img_logo)
    ImageView imgLogo;
    @BindView(R.id.tv_help_1)
    TextView tvHelp1;
    @BindView(R.id.layout_help_1)
    RelativeLayout layoutHelp1;
    @BindView(R.id.ll_more_function)
    LinearLayout llMoreFunction;
    private boolean touchFlag;
    private int TOKEN_REQ = 0;
    private int TIMESTAMP_REQ = 1;
    private int ACTIVATION_CODE_REQ = 2;
    private int PLAN_REQ = 3;
    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
    private AlphaAnimation alphaAniShow, alphaAniHide;
    private TranslateAnimation translateAniShow, translateAniHide;
    private boolean isAutoConnectBle = false;
    private int AUTO_GET = 0;
    private int HAND_GET = 1;
    private int CLICK_GET = 2;
    private int SELECT_MODE = 0;//0训练，1评估
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    private int GetUser = 4;
    private ActivationCodeBean localCodeBean;
    private Uri uri;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0){
                startTargetActivity(WelcomeTipsActivity.class,false);
            }
        }
    };
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        disableButton();
//        initData();
        initActivationCode();
        initView();
        if (TextUtils.isEmpty(SPHelper.getToken()) && NetworkUtils.isConnected()) {
            getToken(TOKEN_REQ);
        }
        if (NetworkUtils.isConnected()) {
            uploadVersionInfo();
        }
        // Android 动态请求权限 Android 10 需要单独处理
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,
                        Manifest.permission.RECORD_AUDIO, Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION",
                        Manifest.permission.READ_PHONE_STATE};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }else {
                mHandler.sendEmptyMessageDelayed(0,500);
            }
        }
        localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (!Global.FactoryCheck){
            checkScanUser(AUTO_GET);
        }
        startGuide();

//        WakeUtil.getInstance(this).startWake();
//        OneShotUtil.getInstance(this).startOneShot();
    }
    private void startGuide(){
        NewbieGuide.with(MainActivity.this)
                .setLabel("train")
                .alwaysShow(false)
                .setShowCounts(1)
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(llLayoutHelp)
                        .setLayoutRes(R.layout.view_guide_video))
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(ivTraining2)
                        .setLayoutRes(R.layout.view_guide_train))
                .addGuidePage(GuidePage.newInstance()
                        .addHighLight(ivTraining1)
                        .setLayoutRes(R.layout.view_guide_evaluate))
                .addGuidePage(GuidePage.newInstance().addHighLight(llMoreFunction).setLayoutRes(R.layout.view_guide_mine)).show();
    }


    private void getToken(int status) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);

                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (status == TIMESTAMP_REQ) {
                        getServerTimestamp();
                    } else if (status == ACTIVATION_CODE_REQ) {
                        ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
                        getActivationCode(localCodeBean.getMacAddress());
                    } else if (status == PLAN_REQ) {
                        syncPlan();
                    }else if (status == GetUser){
                        getScanUser(SELECT_MODE);
                    }
                } else {
                    ToastUtils.showShort("Token 获取失败");
                }

            }
        });

    }

    private void getActivationCode(String macAddress) {
        OkHttpUtils.getAsync(Api.getActivationCode + macAddress, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                netErrorDialog(macAddress);
                enableButton();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "code request success: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
                if (code == 0) {
                    try {
                        getQrCodeLink();//激活码获取成功后  获取设备二维码
                        ServerActivationCodeBean codeBean = new Gson().fromJson(result, ServerActivationCodeBean.class);
                        localCodeBean.setActivationCode(codeBean.getData().getActivationCode());
                        localCodeBean.setRecordDate(System.currentTimeMillis());
                        ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
                        if (verifyActivationCode(localCodeBean.getPublicKey(), codeBean.getData().getActivationCode(), localCodeBean.getMacAddress())) {
                            enableButton();
                        } else {
                            activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                        }
                    } catch (Exception e) {
                        activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                    }
                } else if (code == 401) {
                    getToken(ACTIVATION_CODE_REQ);
                } else {
                    netErrorDialog(macAddress);
                    enableButton();
                }

            }
        });

    }

    private void getServerTimestamp() {
        OkHttpUtils.getAsync(Api.getServerTimestamp, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "code request success: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    long timestamp = toJsonObj.getInt("data");
                    ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
                    if (timestamp / 1000 >= System.currentTimeMillis() / 1000) {
                        codeBean.setRecordDate(timestamp);
                        ActivationCodeManager.getInstance().insertCodeBean(codeBean);
                    } else {
                        codeBean.setRecordDate(timestamp);
                        ActivationCodeManager.getInstance().insertCodeBean(codeBean);
                    }
                } else if (code == 401) {
                    getToken(TIMESTAMP_REQ);
                }

            }
        });
    }

    private void initActivationCode() {
        ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (NetworkUtils.isConnected()) {
            getServerTimestamp();
        } else {
            if (codeBean != null && codeBean.getRecordDate() > System.currentTimeMillis()) {
                RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
                rxDialog.setContent("请把设备联网或者手动同步时间到最新，重启设备");
                rxDialog.setCancel("");
                rxDialog.setSure("退出系统");
                rxDialog.setCancelable(false);
                rxDialog.setCanceledOnTouchOutside(false);
                rxDialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Process.killProcess(Process.myPid());
                        System.exit(1);
                    }
                });
                rxDialog.show();
                return;
            } else if (codeBean != null) {
                codeBean.setRecordDate(System.currentTimeMillis());
                ActivationCodeManager.getInstance().insertCodeBean(codeBean);
            }
        }
        if (!TextUtils.isEmpty(codeBean.getMacAddress())) {
            if (NetworkUtils.isConnected()) {
                getActivationCode(codeBean.getMacAddress());
            } else if (codeBean.getActivationCode() != null) {
                boolean isAuthPass = verifyActivationCode(codeBean.getPublicKey(), codeBean.getActivationCode(), codeBean.getMacAddress());
                if (isAuthPass) {
                    enableButton();
                } else {
                    activationCodeDialog("软件需要更新，请联系厂家维修", "退出系统", false);
                }
            } else {
                activationCodeDialog("网络未连接请设置网络，重启软件", "退出系统", false);
            }

        } else {
            activationCodeDialog("未获取到mac地址,请打开WiFi然后重启软件", "退出系统", false);
        }
    }

    private boolean verifyActivationCode(String publicKey, String activationCode, String mac) {
        String decryptCode = null;
        try {
            decryptCode = new DesUtil(publicKey).decrypt(activationCode);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        String[] strings;
        if (decryptCode != null) {
            strings = decryptCode.split("-");
        } else {
            return false;
        }
        String macAddress = strings[0];
        long endDate = DateFormatUtil.getSpecialString2Date(strings[1]);
        if (!mac.replaceAll(":", "").endsWith(macAddress)) {
            return false;
        }
        return System.currentTimeMillis() <= endDate;
    }

    private void activationCodeDialog(String content, String sureText, boolean isGoActivate) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent(content);
        rxDialog.setCancel("");
        rxDialog.setSure(sureText);
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            if (isGoActivate) {
                startTargetActivity(ActivationCodeActivity.class, false);

            } else {
                Process.killProcess(Process.myPid());
                System.exit(1);
            }
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    private void netErrorDialog(String mac) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent("数据获取失败，请尝试重试");
        rxDialog.setCancel("");
        rxDialog.setSure("重试");
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
            showWaiting("获取数据", "正在请求……");
            getActivationCode(mac);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initView() {
        layoutBluetooth.setVisibility(View.GONE);
        ivTraining.setOnTouchListener(this);
        ivTraining1.setOnTouchListener(this);
        ivTraining2.setOnTouchListener(this);
        tvMoreOperation.setOnTouchListener(this);
        ivPlan.setOnTouchListener(this);
        ivNews.setOnTouchListener(this);
        ivDoctor.setOnTouchListener(this);
        ivUserCenter.setOnTouchListener(this);
        ivSetting.setOnTouchListener(this);
        alphaAnimation();
        translateAnimation();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_main;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
//            case MessageEvent.ACTION_GATT_CONNECTED:
//                setPoint(true);
//                BleBean bleBean = (BleBean) event.getData();
//                Global.ConnectedAddress = bleBean.getAddress();
//                Global.ConnectedName = bleBean.getName();
//                break;
            case MessageEvent.UPDATE_TOP_TIME:
                updateTime();
                break;
//            case MessageEvent.ACTION_GATT_DISCONNECTED:
//                setPoint(false);
//                if (progressDialog != null && progressDialog.isShowing())
//                    progressDialog.dismiss();
//                if (isAutoConnectBle){
//                    SpeechUtil.getInstance(this).speak("蓝牙鞋连接失败，请检查蓝牙鞋是否打开");
//                    handConnectBle();
//                }
//                isAutoConnectBle = false;
//                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_REQ_FAIL:
                ToastUtils.showShort("同步计划请求失败");
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:
                if (progressDialog != null && progressDialog.isShowing())
                    progressDialog.dismiss();
                break;
            default:
                break;
        }
    }

    @OnClick({R.id.iv_notification, R.id.iv_red, R.id.iv_voice, R.id.iv_video, R.id.tv_help, R.id.layout_help, R.id.iv_training, R.id.iv_plan, R.id.iv_doctor, R.id.iv_news,
            R.id.iv_user_center, R.id.iv_setting, R.id.tv_header, R.id.iv_training_1, R.id.tv_more_operation, R.id.img_more_function,R.id.iv_training_2,R.id.img_logo,
            R.id.ll_layout_help,R.id.ll_more_function})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_notification:
                break;
            case R.id.iv_red:
                break;
            case R.id.iv_voice:
                break;
            case R.id.iv_training:
            case R.id.iv_training_2:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    if (!Global.USER_MODE){
                        checkScanUser(HAND_GET);
                    } else if ((TrainPlanManager.getInstance().isPlanEmpty(SPHelper.getUserId()) && !MyUtil.isNoPlanUser()) || isNeedEvaluate()){
                        evaluate();
                    }else {
                        if (!bleConnectedStatus()){
                            Bundle bundle = new Bundle();
                            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectMainMode);
                            startTargetActivity(bundle, HomeConnectActivity.class, false);
                        }else {
                            startTargetActivity(TrainParamActivity.class,false);
                        }
                    }
                }
                break;
            case R.id.iv_training_1:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    evaluate();
                }
                break;
            case R.id.iv_doctor:
                if (DateFormatUtil.avoidFastClick(1000)) {
                    startTargetActivity(DoctorInfoActivity.class, false);
//                    Bundle bundle = new Bundle();
//                    bundle.putInt("ConnectMode",1);
//                    startTargetActivity(bundle,ConnectDeviceActivity.class,false);
                }
                break;
            case R.id.iv_video:
            case R.id.tv_help:
            case R.id.layout_help:
            case R.id.iv_video_1:
            case R.id.tv_help_1:
            case R.id.layout_help_1:
            case R.id.ll_layout_help:
                Intent intent = new Intent(this, VideoPlayerActivity.class);
                intent.putExtra(AppKeyManager.EXTRA_VIDEO_FILE, "asset:///video_user_help.mp4");
                startActivity(intent);
                break;
            case R.id.iv_plan:
                if (DateFormatUtil.avoidFastClick(1500)) {
                    startTargetActivity(PlanActivity.class, false);
                }
                break;
            case R.id.iv_news:
                if (DateFormatUtil.avoidFastClick(1500)) {
                    startTargetActivity(NewsActivity.class, false);
                }
                break;
            case R.id.iv_user_center:
                startTargetActivity(UserInfoActivity.class, false);
                break;
            case R.id.iv_setting:
                startTargetActivity(SettingActivity.class, false);
                break;
            case R.id.tv_more_operation:
            case R.id.img_more_function:
            case R.id.ll_more_function:
                if (Global.ReleaseVersion == Global.HomeVersion && DateFormatUtil.avoidFastClick(1500)) {
                    if (Global.USER_MODE){
                        startTargetActivity(PlanActivity.class, false);
                    }else {
                        checkScanUser(HAND_GET);
                    }
                }
                break;
            case R.id.img_logo:
                //设置在五秒内点击七次版本号会进入工程师模式
                ClickCount ++;
                if (ClickCount == 1){
                    ClickTime = System.currentTimeMillis();
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)){
                    startTargetActivity(EngineerActivity.class,false);
                    ClickTime = 0;
                    ClickCount = 0;
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)){
                    ClickTime = 0;
                    ClickCount = 0;
                }
                break;
        }
    }
    private void evaluate(){
        if (!Global.USER_MODE){
            checkScanUser(HAND_GET);
        }else {
            if (!bleConnectedStatus()){
                Bundle bundle = new Bundle();
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle, HomeConnectActivity.class, false);
            }else {
                Bundle bundle = new Bundle();
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle,StartActivity.class,false);
            }
        }
    }
    private boolean bleConnectedStatus(){
        return Global.isConnected && !TextUtils.isEmpty(Global.ConnectedAddress);
    }
    private boolean isNeedEvaluate(){
        long lastEvaluateDate = SPHelper.getEvaluateDate();
        if (lastEvaluateDate <= 0){
            return true;
        }else return (System.currentTimeMillis() - lastEvaluateDate) >= (3 * 24 * 60 * 60 * 1000);
    }
    private void checkScanUser(int getType){
        SELECT_MODE = getType;
        if (NetworkUtils.isConnected()){
            getScanUser(getType);
        }else {
            ToastUtils.showShort("网络不可用");
            if (getType != AUTO_GET){
                startTargetActivity(HomeRegisterActivity.class,false);
            }
        }
    }
    private void getScanUser(int getType){
        OkHttpUtils.getAsync(Api.getPlatformQrScanUser + "?macAddress=" + localCodeBean.getMacAddress() , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("数据请求失败");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "获取扫码用户返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    Type type = new TypeToken<List<UserBean>>(){}.getType();
                    List<UserBean> userBeans = new Gson().fromJson(toJsonObj.getString("data"),type);
                    if (userBeans.size() > 0){
                        if (UserManager.getInstance().isUniqueUserId(userBeans.get(0).getUserId())){
                            UserManager.getInstance().insert(userBeans.get(0),4);
                            syncPlan();
                        }
                        checkUser(userBeans.get(0).getUserId());
//                        UserManager.getInstance().insert(userBeans.get(userBeans.size() -1),4);
//                        setHeader();
                        if (getType == CLICK_GET){
                            ToastUtils.showShort("导入成功");
                        }
                    }else if (getType == CLICK_GET){
                        ToastUtils.showShort("未获取到设备同步的用户信息");
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                    }else if (getType == HAND_GET){
                        selectModeDialog();
                    }
                    if (userBeans.size() > 0){
                        resetScanUser();
                        deleteScanUser();
                    }
                }else if (code == 401){
                    getToken(GetUser);
                }else if (code == 1){
                    resetScanUser();
                    deleteScanUser();
                    MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
                    factoryDefaultSetting();
                }else {
                    ToastUtils.showShort("数据请求失败");
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }
    private void checkUser(long userId){
        OkHttpUtils.getAsync(Api.checkUser  + "?macAdd=" +  localCodeBean.getMacAddress() +"&userId=" + userId ,true,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "检查用户结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    setHeader();
                }else if (code == 1){
                    MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
                    factoryDefaultSetting();
                }

            }
        });
    }
    private void resetScanUser(){
        OkHttpUtils.getAsync(Api.clearPlatformQrScanUser  + "?macAdd=" +  localCodeBean.getMacAddress() ,true,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){

                }
            }
        });
    }
    private void deleteScanUser(){
        OkHttpUtils.deleteAsyncToken(Api.deletePlatformQrScanUser +  ActivationCodeManager.getInstance().getCodeBean().getMacAddress() ,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("访问服务器出错");
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }
    private void factoryDefaultSetting() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("当前账号和设备订单的账号不一致，请重启机器");
        dialog.setCancel("");
        dialog.setSure("");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
//            exitSystemDialog("恢复出厂完成，请手动重启机器","确认");
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        dialog.show();
    }
    private void disableButton() {
        tvHelp.setClickable(false);
        ivVideo.setClickable(false);
        layoutHelp.setClickable(false);
        ivDoctor.setClickable(false);
        ivUserCenter.setClickable(false);
        ivSetting.setClickable(false);
        ivNews.setClickable(false);
        ivPlan.setClickable(false);
        ivTraining.setClickable(false);
        ivHeader.setClickable(false);
        tvHeader.setClickable(false);
    }
    private void enableButton() {
        tvHelp.setClickable(true);
        ivVideo.setClickable(true);
        layoutHelp.setClickable(true);
        ivDoctor.setClickable(true);
        ivUserCenter.setClickable(true);
        ivSetting.setClickable(true);
        ivNews.setClickable(true);
        ivPlan.setClickable(true);
        ivTraining.setClickable(true);
        ivHeader.setClickable(true);
        tvHeader.setClickable(true);
    }

    private void uploadVersionInfo() {
        ActivationCodeBean activationCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        StringBuilder sb = new StringBuilder();
        sb.append("{");
        sb.append("\"versionStr\"");
        sb.append(":");
        sb.append(AppUtils.getAppVersionCode());
        sb.append(",");
        sb.append("\"macAddress\"");
        sb.append(":");
        sb.append("\"");
        sb.append(activationCodeBean.getMacAddress());
        sb.append("\"");
        sb.append(",");
        sb.append("\"iccId\"");
        sb.append(":");
        sb.append("\"");
        sb.append(new SimInfoUtils(this).getIccid());
        sb.append("\"");
        sb.append("}");
        OkHttpUtils.putJsonAsync(Api.uploadVersionInfo, sb.toString(), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->上传版本信息返回结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {

                }

            }
        });
    }

    private void syncPlan() {
        OkHttpUtils.getAsync(Api.getPlan + SPHelper.getUserId(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->计划获取结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401) {
                    getToken(PLAN_REQ);
                    return;
                }
                if (code == 0) {
                    ServerPlanEntity serverPlanEntity = new Gson().fromJson(result, ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus) {
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, new Gson().toJson(localPlan), new OkHttpUtils.DataCallBack() {
                            @Override
                            public void requestFailure(Request request, IOException e) {
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                            }

                            @Override
                            public void requestSuccess(String result) throws Exception {
                                Log.e("Setting Activity", "--->计划同步结果" + result);
                                JSONObject toJsonObj = new JSONObject(result);
                                int code = toJsonObj.getInt("code");
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));

                            }
                        });
                    } else if (TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadStatus) {
                        for (PlanEntity planEntity : planEntityList) {
                            try {
                                TrainPlanManager.getInstance().insert(planEntity);
                                if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0) {
                                    SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                                }
                            } catch (Exception e) {
                                if (progressDialog != null && progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                                ToastUtils.showShort(e.getMessage());
                                CrashHandler.getInstance().saveThrowableMessage("更新训练计划error：" + e.getMessage());
                            }
                        }
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }

    private void updateTime() {
        Calendar cal = Calendar.getInstance();
        String date = DateFormatUtil.getDate2String(System.currentTimeMillis(), "yyyy/MM/dd");
        String time = DateFormatUtil.getDate2String(System.currentTimeMillis(), "HH : mm");
        if (Global.ReleaseVersion == Global.HomeVersion){
            tvDate1.setText(date);
            tvTime1.setText(time);
            if (cal.get(Calendar.AM_PM) == Calendar.AM){
                tvAmPm1.setText("AM");
            } else {
                tvAmPm1.setText("PM");
            }
        }else {
            tvDate.setText(date);
            tvTime.setText(time);
            if (cal.get(Calendar.AM_PM) == Calendar.AM) {
                tvAmPm.setText("AM");
            } else {
                tvAmPm.setText("PM");
            }
        }

    }
    private void getQrCodeLink(){
        OkHttpUtils.getAsync(Api.getQrCodeLink  + "?macAdd=" +  localCodeBean.getMacAddress() ,true,new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("数据请求失败");
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "获取二维码链接返回结果: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (code == 0){
                    String qrcodeLink = toJsonObj.getString("data");
                    localCodeBean.setQrcodeLink(qrcodeLink);
                    ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
                }
            }
        });
    }

    private void getPlatformQr(){
        File filesDir = getFilesDir();
        String absolutePath = filesDir.getAbsolutePath();
        if (NetworkUtils.isConnected()){
            OkHttpUtils.downloadAsync(Api.getPlatformQr + localCodeBean.getMacAddress(),absolutePath, true, new OkHttpUtils.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("数据请求失败");
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    if (result != null){
                        uri = Uri.fromFile(new File(result));
                    }else {
                        ToastUtils.showShort("图片获取失败");
                    }
                }
            });
        }else {
            File[] files = filesDir.listFiles();
            if (files == null){
                Log.e("error","空目录");
                return;
            }
            for (File file : files) {
                if (file.getName().startsWith(localCodeBean.getMacAddress())) {
                    uri = Uri.fromFile(new File(file.getAbsolutePath()));
                }
            }

        }

    }
    private void selectModeDialog() {
        RxImageDialog dialog = new RxImageDialog(this);
        Resources res = getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(res, R.mipmap.icon_logo);
        if (!TextUtils.isEmpty(localCodeBean.getQrcodeLink())){
            Bitmap qrBitmap = QrUtil.createQRCodeBitmap(localCodeBean.getQrcodeLink(), 400, 400, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, logoBitmap, 0.15F);
            dialog.setImage(qrBitmap);
        }
        dialog.setContent("如果微信已经同步账号，请扫描下面设备二维码后，点击导入账号可以免注册");
        dialog.setCancel("导入账号");
        dialog.setSure("立即注册");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            startTargetActivity(HomeRegisterActivity.class,false);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            showWaiting("获取数据", "正在导入…");
            getScanUser(CLICK_GET);

        });
        dialog.show();
    }
    @Override
    protected void onResume() {
        super.onResume();
//        initBtState();
//        Global.ReleaseVersion = SPHelper.getReleaseVersion();
        Global.ReleaseVersion = Global.HomeVersion;
        if (Global.ReleaseVersion == Global.HomeVersion) {
            rlSample.setVisibility(View.VISIBLE);
            reRoot.setVisibility(View.GONE);
            ivNotification.setVisibility(View.GONE);
            ivVoice.setVisibility(View.GONE);
        } else {
            rlSample.setVisibility(View.GONE);
            reRoot.setVisibility(View.VISIBLE);
            ivNotification.setVisibility(View.GONE);
        }
        TrainPlanManager.getInstance().refreshPlanStatus(SPHelper.getUserId());

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Bundle bundle = intent.getExtras();
        int selectUser = 0;
        int ResultTips = 0;
        if (bundle != null) {
            selectUser = bundle.getInt("SelectUser", 0);
            ResultTips = bundle.getInt("ResultTips", 0);
        }
//        if (selectUser == 1 && NetworkUtils.isConnected()) {
//            syncPlan();
//        }
//        if (ResultTips == 1) {
//            startTargetActivity(CommitResultActivity.class, false);
//
//        }
        super.onNewIntent(intent);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN://收缩到0.8(正常值是1)，速度200
                v.animate().scaleX(0.8f).scaleY(0.8f).setDuration(200).start();
                touchFlag = false;
                break;
            case MotionEvent.ACTION_UP:
                v.animate().scaleX(1).scaleY(1).setDuration(200).start();
                if (touchFlag) return true;
                break;
        }
        return false;
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    //透明度动画
    private void alphaAnimation() {
        //显示
        alphaAniShow = new AlphaAnimation(0, 1);//百分比透明度，从0%到100%显示
        alphaAniShow.setDuration(1000);//一秒
        //隐藏
        alphaAniHide = new AlphaAnimation(1, 0);
        alphaAniHide.setDuration(1000);
    }

    //位移动画
    private void translateAnimation() {
        //向上位移显示动画  从自身位置的最下端向上滑动了自身的高度
        translateAniShow = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                1,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                0);//fromXValue表示结束的Y轴位置
        translateAniShow.setRepeatMode(Animation.REVERSE);
        translateAniShow.setDuration(1000);
        //向下位移隐藏动画  从自身位置的最上端向下滑动了自身的高度
        translateAniHide = new TranslateAnimation(
                Animation.RELATIVE_TO_SELF,//RELATIVE_TO_SELF表示操作自身
                0,//fromXValue表示开始的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示结束的X轴位置
                Animation.RELATIVE_TO_SELF,
                0,//fromXValue表示开始的Y轴位置
                Animation.RELATIVE_TO_SELF,
                1);//fromXValue表示结束的Y轴位置
        translateAniHide.setRepeatMode(Animation.REVERSE);
        translateAniHide.setDuration(1000);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
            case 2:
                boolean isAllGranted = true;
                // 判断是否所有的权限都已经授予了
                for (int grant : grantResults) {
                    if (grant != PackageManager.PERMISSION_GRANTED) {
                        isAllGranted = false;
                        break;
                    }
                }
                if (isAllGranted) {
                    ForegroundWorkService.launch(Global.initBle);
                    if (NetworkUtils.isConnected()) {
                        uploadVersionInfo();
                    }
                } else {
                    activationCodeDialog("必要的权限没有允许，请退出系统重新打开软件，允许权限", "退出系统", false);
                }
                return;
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                mHandler.sendEmptyMessageDelayed(0,500);
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings granted");
            } else {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings not granted");
            }
        }
    }
}
