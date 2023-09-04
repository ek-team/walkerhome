package com.pharos.walker.ui;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.pharos.walker.R;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.VersionInfoBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.Utils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class EngineerActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_version_build_name)
    TextView tvVersionBuildName;
    @BindView(R.id.rg_version_select)
    RadioGroup rgVersionSelect;
    @BindView(R.id.rg_1)
    RadioButton rgSingle;
    @BindView(R.id.rg_2)
    RadioButton rgClient;
    @BindView(R.id.btn_start_calibration)
    TextView btnStartCalibration;
    @BindView(R.id.tv_value)
    TextView tvValue;
    @BindView(R.id.btn_clear_zero)
    TextView btnClearZero;
    @BindView(R.id.ed_remote_calibration)
    EditText edRemoteCalibration;
    @BindView(R.id.btn_calibration)
    TextView btnCalibration;
    @BindView(R.id.tv_ble_name)
    TextView tvBleName;
    @BindView(R.id.ll_calibration)
    LinearLayout llCalibration;
    private Gson gson = new Gson();
    private int CHECK_VERSION_REQ = 4;
    private int RESET_SCAN_USER = 5;
    private boolean isClearZero = false;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        tvVersionBuildName.setText(AppUtils.getAppVersionName());
        initView();
    }

    private void initView() {
//        if (SPHelper.getServiceStatus() && hotState){
//            llConnectDevice.setVisibility(View.GONE);
//            startService(new Intent(this,SocketServices.class));
//            cbSocketServer.setChecked(true);
//        }else {
//            cbSocketServer.setChecked(false);
//            llConnectDevice.setVisibility(View.VISIBLE);
//        }
        if (SPHelper.getReleaseVersion() == Global.SingleVersion) {
            rgSingle.setChecked(true);
        } else if (SPHelper.getReleaseVersion() == Global.ClientVersion) {
            rgClient.setChecked(true);
        }
        rgVersionSelect.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rg_1) {
                    Global.ReleaseVersion = Global.SingleVersion;
                } else if (checkedId == R.id.rg_2) {
                    Global.ReleaseVersion = Global.ClientVersion;
                } else if (checkedId == R.id.rg_3) {
                    Global.ReleaseVersion = Global.HomeVersion;
                }
                SPHelper.saveReleaseVersion(Global.ReleaseVersion);
            }
        });
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_engineer;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_DOWNLOAD_PROGRESS:
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.setProgress((int) event.getData());
                }
                break;
            case MessageEvent.ACTION_REQ_FAIL:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("接口请求失败");
                break;
            case MessageEvent.ACTION_CHECK_VERSION:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("已是最新版本了");
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                DecimalFormat df = new DecimalFormat("0.0");
                tvValue.setText(df.format(Float.valueOf(value)));
                break;
        }
    }

    @OnClick({R.id.iv_back, R.id.tv_version_build_name, R.id.btn_update_version,
            R.id.btn_system_default, R.id.btn_check,R.id.btn_start_calibration, R.id.btn_clear_zero, R.id.btn_calibration,R.id.btn_start_setting})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_version_build_name:
                break;
            case R.id.btn_update_version:
                if (NetworkUtils.isConnected() && DateFormatUtil.avoidFastClick(2000)) {
                    checkVersion();
                } else if (!NetworkUtils.isConnected()) {
                    ToastUtils.showShort("网络不可用");
                }
                break;
            case R.id.btn_system_default:
                selectModeDialog();
                break;
            case R.id.btn_check:
                generateUserDialog();
                break;
            case R.id.btn_start_calibration:
                if (!Global.isConnected){
                    Toast.makeText(this, "蓝牙鞋未连接", Toast.LENGTH_SHORT).show();
                }else {
                    if (llCalibration.getVisibility() == View.GONE){
                        llCalibration.setVisibility(View.VISIBLE);
                        tvBleName.setText(MessageFormat.format("蓝牙鞋：{0}", Global.ConnectedName));
                        startTask();
                    }
                }
                break;
            case R.id.btn_clear_zero:
                if (Global.isConnected){
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,"set0".getBytes(StandardCharsets.UTF_8));
                    isClearZero = true;
                    Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_calibration:
                String value = edRemoteCalibration.getText().toString();
                if (Float.parseFloat(value) <= 0){
                    Toast.makeText(this, "输入值不能小于0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Global.isConnected){
                    if (isClearZero){
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,("set" + value).getBytes(StandardCharsets.UTF_8));
                        Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(this, "请先进行清零操作", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "设备未连接", Toast.LENGTH_SHORT).show();
                }

                break;
            case R.id.btn_start_setting:
                Intent intent = new Intent();
                intent.setAction(Settings.ACTION_HOME_SETTINGS);
                startActivity(intent);
                break;
        }
    }
    private Timer mTimer;
    private TimerTask mTimerTask;
    private void startTask() {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int a = 0x1A;
                    int b = 0x04 | 0x10;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    Log.e("发送数据", "run: " + message);
                    Global.isStartReadData = true;
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    private void clearTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }
    private void selectModeDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否恢复出厂设置");
        dialog.setCancel("取消");
        dialog.setSure("恢复出厂设置");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
            showWaiting("恢复出厂操作", "正在恢复出厂设置…");
            if (NetworkUtils.isConnected()) {
                resetScanUser();
            } else {
                ToastUtils.showShort("网络不可用");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }
//            exitSystemDialog("恢复出厂完成，请手动重启机器","确认");
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    private void generateUserDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否生成检验账号");
        dialog.setCancel("取消");
        dialog.setSure("生成");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Global.FactoryCheck = true;
            Global.USER_MODE = true;
            SPHelper.saveUser(UserManager.getInstance().initUser(0L));//创建检验用户，并保存到本地
            ToastUtils.showShort("账号生成成功");
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }

    private void resetScanUser() {
        OkHttpUtils.getAsync(Api.clearPlatformQrScanUser + "?macAdd=" + ActivationCodeManager.getInstance().getCodeBean().getMacAddress(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("访问服务器出错");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    deleteScanUser();
//                    exitSystemDialog("恢复出厂完成，请手动重启机器","确认");
                } else if (code == 401) {
                    getToken(RESET_SCAN_USER);
                } else {
                    ToastUtils.showShort("访问服务器出错");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }

    private void deleteScanUser() {
        OkHttpUtils.deleteAsyncToken(Api.deletePlatformQrScanUser + ActivationCodeManager.getInstance().getCodeBean().getMacAddress(), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort("访问服务器出错");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("User Activity", "重置扫码用户返回结果: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    exitSystemDialog("恢复出厂完成，请手动重启机器", "确认");
                } else if (code == 401) {
                    getToken(RESET_SCAN_USER);
                } else {
                    ToastUtils.showShort("访问服务器出错");
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                }

            }
        });
    }

    private void exitSystemDialog(String content, String sureText) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(this);
        rxDialog.setContent(content);
        rxDialog.setCancel("");
        rxDialog.setSure(sureText);
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
//            Process.killProcess(Process.myPid());
//            System.exit(0);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }

    private void checkVersion() {
        OkHttpUtils.getAsync(Api.getNewVersion + "?type=" + Global.HOME, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "获取最新版本号返回结果: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    VersionInfoBean bean = gson.fromJson(result, VersionInfoBean.class);
                    if (!TextUtils.isEmpty(bean.getData().getVersion()) && Float.parseFloat(bean.getData().getVersion()) > AppUtils.getAppVersionCode()) {
                        RxDialogSureCancel rxDialog = new RxDialogSureCancel(EngineerActivity.this);
                        rxDialog.setContent("新版本可更新");
                        rxDialog.setCancel("取消");
                        rxDialog.setSure("更新");
                        rxDialog.setCancelable(false);
                        rxDialog.setCanceledOnTouchOutside(false);
                        rxDialog.setSureListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                updateApk(bean.getData().getUrl());
                                rxDialog.dismiss();
                            }
                        });
                        rxDialog.show();
                        rxDialog.setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                rxDialog.dismiss();
                            }
                        });
                    } else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_CHECK_VERSION));
                    }
                } else if (code == 401) {
                    getToken(CHECK_VERSION_REQ);
                }

            }
        });
    }

    private void updateApk(String url) {
        String desDir = Environment.getExternalStorageDirectory() + File.separator;
        if (waitDialog == null) {
            progressDisplay();
            OkHttpUtils.downloadAsync(url, desDir, false, new OkHttpUtils.DataCallBack() {
                @Override
                public void requestFailure(Request request, IOException e) {
                    e.printStackTrace();
                    waitDialog.dismiss();
                    waitDialog = null;
                    ToastUtils.showShort("下载失败");
                }

                @Override
                public void requestSuccess(String result) throws Exception {
                    waitDialog.dismiss();
                    waitDialog = null;
                    AppUtils.installApp(desDir + url.substring(url.lastIndexOf("/")));
                }
            });
        } else if (!waitDialog.isShowing()) {
            waitDialog.show();
        }

    }

    private void getToken(int flag) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == CHECK_VERSION_REQ) {
                        checkVersion();
                    } else if (flag == RESET_SCAN_USER) {
                        resetScanUser();
                    }
                }
            }
        });

    }

    private ProgressDialog waitDialog;

    private void progressDisplay() {
        if (waitDialog == null) {
            waitDialog = new ProgressDialog(this);
            waitDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            waitDialog.setCancelable(false);
            waitDialog.setCanceledOnTouchOutside(false);
            waitDialog.setMessage("下载中，请稍等。。。");

            waitDialog.setProgress(0);
        }
        waitDialog.show();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        clearTimerTask();
    }

}

