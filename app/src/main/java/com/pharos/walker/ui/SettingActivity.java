package com.pharos.walker.ui;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.net.DhcpInfo;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.Formatter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.ListPopupWindow;
import androidx.appcompat.widget.SwitchCompat;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.DataSendBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.ServerTrainDataBean;
import com.pharos.walker.beans.SubPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.beans.VersionInfoBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.constants.SocketCmd;
import com.pharos.walker.customview.popupdialog.PopupSheet;
import com.pharos.walker.customview.popupdialog.PopupSheetCallback;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DimensUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SPUtils;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;
import com.xuhao.didi.core.iocore.interfaces.ISendable;
import com.xuhao.didi.core.pojo.OriginalData;
import com.xuhao.didi.socket.client.impl.client.action.ActionDispatcher;
import com.xuhao.didi.socket.client.sdk.OkSocket;
import com.xuhao.didi.socket.client.sdk.client.ConnectionInfo;
import com.xuhao.didi.socket.client.sdk.client.OkSocketOptions;
import com.xuhao.didi.socket.client.sdk.client.action.SocketActionAdapter;
import com.xuhao.didi.socket.client.sdk.client.connection.IConnectionManager;
import com.xuhao.didi.socket.client.sdk.client.connection.NoneReconnect;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

import static android.widget.Toast.LENGTH_SHORT;

/**
 * Created by zhanglun on 2021/5/12
 * Describe:
 */
public class SettingActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.btn_activation_code)
    TextView btnActivationCode;
    @BindView(R.id.switch_voice)
    SwitchCompat switchVoice;
    @BindView(R.id.tv_music)
    TextView tvMusic;
    @BindView(R.id.layout_music)
    RelativeLayout layoutMusic;
    @BindView(R.id.seekbar_volume)
    SeekBar seekbarVolume;
    @BindView(R.id.seekbar_brightness)
    SeekBar seekbarBrightness;
    @BindView(R.id.tv_version_name)
    TextView tvVersionName;
    @BindView(R.id.tv_update)
    TextView tvUpdate;
    @BindView(R.id.layout_system_setting)
    LinearLayout layoutSystemSetting;
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.btn_clear)
    TextView btnClear;
    @BindView(R.id.tv_connect_status)
    TextView tvConnectStatus;
    @BindView(R.id.iv_disconnect)
    ImageView ivDisconnect;
    @BindView(R.id.tv_current_battery)
    TextView tvCurrentBattery;
    @BindView(R.id.tv_device_mac)
    TextView tvDeviceMac;
    @BindView(R.id.layout_rehabilitation_shoes)
    LinearLayout layoutRehabilitationShoes;
    @BindView(R.id.et_hospital)
    EditText etHospital;
    @BindView(R.id.iv_edit_name)
    ImageView ivEditName;
    @BindView(R.id.tv_save_hospital)
    TextView tvSaveHospital;
    @BindView(R.id.et_department)
    EditText etDepartment;
    @BindView(R.id.iv_edit_department)
    ImageView ivEditDepartment;
    @BindView(R.id.tv_save_department)
    TextView tvSaveDepartment;
    private List<String> musicDatas;
    private AudioManager mAudioManager;
    private static String TAG = "SettingActivity";
    private static final int REQUEST_CODE_WRITE_SETTINGS = 2;
    private int musicPosition = 0;
    private String selectMusic = "卡农";
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    private IConnectionManager mManager;
    private OkSocketOptions mOkOptions;
    private String ip;
    private int port = 9001;
    private int USER_REQ = 0;
    private int PLAN_REQ = 1;
    private int TRAIN_DATA_REQ = 2;
    private int TRAIN_DATA_TO_LOCAL_REQ = 3;
    private int CHECK_VERSION__REQ = 4;
    private Gson gson = new Gson();

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        mAudioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        initVolume();
        initLight();
        initView();
        initManager();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (!Settings.System.canWrite(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS,
                        Uri.parse("package:" + getPackageName()));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivityForResult(intent, REQUEST_CODE_WRITE_SETTINGS);
            }
        }

    }

    private void initView() {
        String[] musics = getResources().getStringArray(R.array.music_list);
        musicDatas = Arrays.asList(musics);
        tvMusic.setText(musicDatas.get(SPHelper.getMusicPosition()));
        if (Global.isChangSha) {
            tvVersionName.setText(MessageFormat.format("V{0}", "1.7.0.20191018"));
        } else {
            tvVersionName.setText(MessageFormat.format("V{0}", "1.0.0.1"));
        }
        String[] mTitles = {getResources().getString(R.string.rehabilitation_shoes), getResources().getString(R.string.system_setting)};
        tab.setTabData(mTitles);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 1:
                        layoutRehabilitationShoes.setVisibility(View.GONE);
                        layoutSystemSetting.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                        layoutRehabilitationShoes.setVisibility(View.VISIBLE);
                        layoutSystemSetting.setVisibility(View.GONE);
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        switchVoice.setChecked(SPHelper.getVoiceState());
        switchVoice.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SPHelper.saveVoiceState(isChecked);
            if (isChecked) {
                SpeechUtil.getInstance(SettingActivity.this).speak(getString(R.string.voice_text_function_on));
            } else {
                Toast.makeText(getApplicationContext(), getString(R.string.voice_text_function_off), LENGTH_SHORT).show();
                SpeechUtil.getInstance(SettingActivity.this).closeSpeak();
            }

        });
        seekbarVolume.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                // 设置音量
                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                SPUtils.getInstance().put("system_voice", progress);
            }
        });
        // 调节亮度
        seekbarBrightness.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                setBrightness(SettingActivity.this, progress);
            }
        });
        tvDate.setText("无");
    }

    private void initManager() {
        ip = getWifiRouteIPAddress(this);
        final Handler handler = new Handler();
        ConnectionInfo mInfo;
        mInfo = new ConnectionInfo(ip, port);
        mOkOptions = new OkSocketOptions.Builder()
                .setReconnectionManager(new NoneReconnect())
                .setConnectTimeoutSecond(10)
                .setCallbackThreadModeToken(new OkSocketOptions.ThreadModeToken() {
                    @Override
                    public void handleCallbackEvent(ActionDispatcher.ActionRunnable runnable) {
                        handler.post(runnable);
                    }
                })
                .build();
        mManager = OkSocket.open(mInfo).option(mOkOptions);
        mManager.registerReceiver(adapter);
    }

    private static String getWifiRouteIPAddress(Context context) {
        WifiManager wifi_service = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        DhcpInfo dhcpInfo = wifi_service.getDhcpInfo();
        String routeIp = Formatter.formatIpAddress(dhcpInfo.gateway);
        Log.i("route ip", "wifi route ip：" + routeIp);
        return routeIp;
    }

    private SocketActionAdapter adapter = new SocketActionAdapter() {
        @Override
        public void onSocketDisconnection(ConnectionInfo info, String action, Exception e) {
            Log.e(TAG, "onSocketDisconnection: 断开连接！！！ ");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onSocketConnectionSuccess(ConnectionInfo info, String action) {
            startSync();
            Log.e(TAG, "onSocketConnectionSuccess: 连接成功！！！");
        }

        @Override
        public void onSocketConnectionFailed(ConnectionInfo info, String action, Exception e) {
            Log.e(TAG, "onSocketConnectionFailed: 连接失败！！！");
            ToastUtils.showShort("连接失败。请检查网络是否连接");
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }

        @Override
        public void onSocketReadResponse(ConnectionInfo info, String action, OriginalData data) {
            String receiveResult = new String(data.getBodyBytes(), Charset.forName("utf-8"));
            if (TextUtils.isEmpty(receiveResult))
                return;
            handleReceiveData(receiveResult);
            Log.d(TAG, "onSocketReadResponse: 收到数据 " + receiveResult);
        }

        @Override
        public void onSocketWriteResponse(ConnectionInfo info, String action, ISendable data) {
            String str = new String(data.parse(), Charset.forName("utf-8"));
            Log.d(TAG, "onSocketWriteResponse: 发送数据：" + str);
        }
    };

    private void handleReceiveData(String receiveResult) {
        int endPosition = receiveResult.indexOf("[");
        if (endPosition <= 0) {
            endPosition = receiveResult.indexOf("{");
        }
        String dataStr = receiveResult.substring(endPosition);
        String head = receiveResult.substring(0, endPosition);
        String request;
        DataSendBean msgDataBean;
        switch (head) {
            case SocketCmd.SYNC_USER_ASK:
                if (dataStr.length() > 2) {
                    UserBean userBean = gson.fromJson(dataStr, UserBean.class);
                    UserManager.getInstance().insertSyncUser(userBean);
                }

                break;
            case SocketCmd.SYNC_USER_FINISH:
                request = SocketCmd.SYNC_PLAN + gson.toJson(UserManager.getInstance().loadAllUserIdPlanUpdate());
                msgDataBean = new DataSendBean(request);
                mManager.send(msgDataBean);
                break;
            case SocketCmd.SYNC_PLAN_ASK:
                Type type = new TypeToken<List<PlanEntity>>() {
                }.getType();
                List<PlanEntity> planEntityList = gson.fromJson(dataStr, type);
                TrainPlanManager.getInstance().insertMany(planEntityList);
                String userId = "";
                if (planEntityList.size() > 0) {
                    userId = planEntityList.get(0).getUserId() + "";
                }
                if (TextUtils.isEmpty(userId))
                    return;
                request = SocketCmd.SYNC_SUB_PLAN + Global.Delimiter + userId;
                msgDataBean = new DataSendBean(request);
                mManager.send(msgDataBean);
                break;
            case SocketCmd.SYNC_PLAN:
                String userIdStr = dataStr.replaceAll("\\[", "");
                if (!TextUtils.isEmpty(userIdStr)) {
                    List<PlanEntity> planList = TrainPlanManager.getInstance().getPlanListByUserId(Long.parseLong(userIdStr));
                    String result = SocketCmd.SYNC_PLAN_ASK + gson.toJson(planList);
                    msgDataBean = new DataSendBean(result);
                    mManager.send(msgDataBean);
                }
                break;
            case SocketCmd.SYNC_SUB_PLAN_ASK:
                Type type1 = new TypeToken<List<SubPlanEntity>>() {
                }.getType();
                List<SubPlanEntity> subPlanEntityList = gson.fromJson(dataStr, type1);
                SubPlanManager.getInstance().insertMany(subPlanEntityList);
                break;
            case SocketCmd.SYNC_TRAIN_RECORD:
                List<Long> ids = UserManager.getInstance().loadAllUserIdRecord();
                for (Long id : ids) {
                    List<UserTrainRecordEntity> listRecord = UserTrainRecordManager.getInstance().loadAllUpdate(id);
                    for (UserTrainRecordEntity entity : listRecord) {
                        String result = SocketCmd.SYNC_TRAIN_RECORD_ASK + Global.Delimiter + gson.toJson(entity);
                        DataSendBean dataSendBean = new DataSendBean(result);
                        mManager.send(dataSendBean);
                    }
                    UserBean userBean = UserManager.getInstance().loadByUserId(id);
                    userBean.setIsRecordUpdate(1);
                    UserManager.getInstance().update(userBean);
                    UserTrainRecordManager.getInstance().updateTrainUploadStatus(listRecord);
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                progressDialog.dismiss();
                ToastUtils.showShort("同步成功");
                break;
            case SocketCmd.SYNC_TRAIN_DATA:
                String userInfo = dataStr.replaceAll("\\[", "");
                String[] infoArray = userInfo.split(Global.Comma);
                if (!TextUtils.isEmpty(infoArray[0])) {
                    List<TrainDataEntity> trainDataEntityList = TrainDataManager.getInstance().
                            getTrainDataByDateAndFrequency(Long.parseLong(infoArray[0]), infoArray[1], Integer.parseInt(infoArray[2]) - 1);
                    String result = SocketCmd.SYNC_TRAIN_DATA_ASK + gson.toJson(trainDataEntityList);
                    DataSendBean dataSendBean = new DataSendBean(result);
                    mManager.send(dataSendBean);
                }
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_setting;
    }


    @OnClick({R.id.iv_back, R.id.tv_update, R.id.btn_clear, R.id.btn_select, R.id.layout_music, R.id.tv_version_name, R.id.btn_send, R.id.btn_cloud_send,
            R.id.iv_edit_name, R.id.tv_save_hospital, R.id.iv_edit_department, R.id.tv_save_department})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_update:
//                AppUtils.installApp(Environment.getExternalStorageDirectory()  + File.separator + "walknew.apk");
                if (NetworkUtils.isConnected() && DateFormatUtil.avoidFastClick(2000)) {
                    checkVersion();
//                    updateApk();
                } else if (!NetworkUtils.isConnected()) {
                    ToastUtils.showShort("网络不可用");
                }
                break;
            case R.id.btn_clear:
                if (Global.isConnected) {
                    String message = "set0";
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                    Toast.makeText(this, "命令已发送", LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "设备未连接", LENGTH_SHORT).show();
                }
                break;
            case R.id.btn_select:
                if (DateFormatUtil.avoidFastClick(1000)) {
                    Bundle bundle = new Bundle();
                    bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectSetMode);
                    startTargetActivity(bundle, ConnectDeviceActivity.class, false);
                }
                break;

            case R.id.layout_music:
                trainMusicPop();
                break;
            case R.id.btn_send:
                if (NetworkUtils.isWifiConnected()) {
                    connectServer();
                } else {
                    ToastUtils.showShort("wifi 没有连接");
                }
                break;
            case R.id.btn_cloud_send:
                if (NetworkUtils.isConnected() && DateFormatUtil.avoidFastClick(2000)) {
                    showWaiting("同步提示", "正在同步...");
                    syncUser();
                } else {
                    ToastUtils.showShort("网络不可用");
                }
                break;
            case R.id.tv_version_name:
                //设置在五秒内点击七次版本号会显示标定功能
                ClickCount++;
                if (ClickCount == 1) {
                    ClickTime = System.currentTimeMillis();
                } else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)) {
                    startTargetActivity(EngineerActivity.class, false);
                    ClickTime = 0;
                    ClickCount = 0;
                } else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)) {
                    ClickTime = 0;
                    ClickCount = 0;
                }
                break;
            case R.id.iv_edit_name:
                break;
            case R.id.tv_save_hospital:
                break;
            case R.id.iv_edit_department:
                break;
            case R.id.tv_save_department:
                break;

        }
    }

    private void syncUser() {
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = gson.toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT, code));
                if (code == 0) {
                    UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
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
                    ServerPlanEntity serverPlanEntity = gson.fromJson(result, ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus) {
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, gson.toJson(localPlan), new OkHttpUtils.DataCallBack() {
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
                        boolean isLoadLocalUpdate = TrainPlanManager.getInstance().isLoadLocalUpdate(planEntityList.get(0).getPlanId()
                                , planEntityList.get(0).getUserId(), planEntityList.get(0).getLoad());//判断是否刷新本地子计划的标识
                        try {
                            if (isLoadLocalUpdate) {//如果网络端起始负重修改了，需要本地更新计划 不用插入网络端的数据
                                MyUtil.insertTemplate(planEntityList.get(0).getLoad());
                            }
                            for (PlanEntity planEntity : planEntityList) {
                                TrainPlanManager.getInstance().insert(planEntity);
                                if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0 && !isLoadLocalUpdate) {
                                    SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                                }
                            }

                        } catch (Exception e) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            ToastUtils.showShort(e.getMessage());
                            CrashHandler.getInstance().saveThrowableMessage("更新训练计划error：" + e.getMessage());
                        }
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                    } else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                    }

                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }

    private void syncTrainRecord() {
        List<UserTrainRecordEntity> recordEntityList = UserTrainRecordManager.getInstance().loadMasterUpdate(SPHelper.getUserId());
        List<List<UserTrainRecordEntity>> listList = splitList(recordEntityList, pageSize);
        int pageNum = 0;
        uploadTrainData(pageNum, listList);

    }

    private void uploadTrainData(int pageNum, List<List<UserTrainRecordEntity>> listList) {
        if (listList.size() <= 0) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, 0));
            return;
        }
        OkHttpUtils.postJsonAsync(Api.uploadTrainRecord, gson.toJson(listList.get(pageNum)), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->训练数据上传结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                boolean isDuplicate = false;
                if (code == 1) {
                    String msg = toJsonObj.getString("msg");
                    isDuplicate = msg.contains("Duplicate entry");
                }
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,code));
                if (code == 0 || isDuplicate) {
                    try {
                        UserTrainRecordManager.getInstance().updateMasterTrainUploadStatus(listList.get(pageNum));
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort(e.getMessage());
                        CrashHandler.getInstance().saveThrowableMessage("更新训练记录error：" + e.getMessage());
                    }
                    for (int i = pageNum + 1; i < listList.size(); i++) {
                        uploadTrainData(i, listList);
                    }
                }
                if (pageNum + 1 >= listList.size()) {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, isDuplicate ? 0 : code));
                }
            }
        });
    }

    private List<List<UserTrainRecordEntity>> splitList(List<UserTrainRecordEntity> list, int groupSize) {
        return Lists.partition(list, groupSize);
    }

    private int totalSize = 0;
    private final int pageSize = 4;
    private int page = 0;

    private void getTrainRecord(int pageNum) {
        OkHttpUtils.getAsync(Api.getRecord + SPHelper.getUserId() + "?pageSize=" + pageSize + "&pageNum=" + pageNum, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->训练数据获取结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    ServerTrainDataBean serverTrainDataBean = gson.fromJson(result, ServerTrainDataBean.class);
                    totalSize = serverTrainDataBean.getData().getTotal();
                    page = totalSize / pageSize + (totalSize % pageSize == 0 ? 0 : 1);//获取总页数
                    for (UserTrainRecordEntity trainRecordEntity : serverTrainDataBean.getData().getRecords()) {
                        UserTrainRecordManager.getInstance().insertServerData(trainRecordEntity);
                        List<TrainDataEntity> trainDataEntityList = trainRecordEntity.getTrainDataList();
                        TrainDataManager.getInstance().insertMany(trainDataEntityList);
                    }
                }
                for (int i = pageNum + 1; i < page; i++) {
                    getTrainRecord(i);
                }
                if (pageNum + 1 >= page) {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT, code));
                }

            }
        });
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
                    if (flag == PLAN_REQ) {
                        syncPlan();
                    } else if (flag == USER_REQ) {
                        syncUser();
                    } else if (flag == TRAIN_DATA_REQ) {
                        syncTrainRecord();
                    } else if (flag == TRAIN_DATA_TO_LOCAL_REQ) {
                        getTrainRecord(0);
                    } else if (flag == CHECK_VERSION__REQ) {
                        checkVersion();
                    }
                }
            }
        });

    }

    private void connectServer() {
        if (mManager == null) {
            ToastUtils.showShort("对象创建失败");
            return;
        }
        if (!mManager.isConnect()) {
//            showWaiting("设备连接","正在连接设备...");
            initManager();
            mManager.connect();
        } else {
            startSync();
        }

    }

    private void startSync() {
        if (!mManager.isConnect()) {
            Toast.makeText(getApplicationContext(), "Unconnected", LENGTH_SHORT).show();
        } else {
            showWaiting("同步提示", "正在同步...");
            String request = SocketCmd.SYNC_USER + Global.Delimiter + TextUtils.join(",", UserManager.getInstance().loadAllUserId());
            DataSendBean msgDataBean = new DataSendBean(request);
            mManager.send(msgDataBean);
        }
    }

    private void checkVersion() {
        OkHttpUtils.getAsync(Api.getNewVersion + "?type=" + Global.ChangSha, true, new OkHttpUtils.DataCallBack() {
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
                    if (Float.parseFloat(bean.getData().getVersion()) > AppUtils.getAppVersionCode()) {
                        RxDialogSureCancel rxDialog = new RxDialogSureCancel(SettingActivity.this);
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
                    getToken(CHECK_VERSION__REQ);
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

    private void trainMusicPop() {
        PopupSheet popupSheet = new PopupSheet(this, layoutMusic, musicDatas, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(SettingActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", musicDatas.get(position)));
                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                String musicName = musicDatas.get(position);
                selectMusic = musicName;
                musicPosition = position;
                tvMusic.setText(MessageFormat.format("{0}", musicName));
                SPHelper.saveMusicPosition(position);
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().register(this);
        }
        if (Global.BLE_BATTERY == 0) {
            tvCurrentBattery.setText("未获取");
        } else if (Global.BLE_BATTERY <= 20 && Global.BLE_BATTERY > 0) {
            tvCurrentBattery.setText(MessageFormat.format("{0}%（请及时充电）", Global.BLE_BATTERY));
        } else {
            tvCurrentBattery.setText(MessageFormat.format("{0}%", Global.BLE_BATTERY));
        }
        refreshBleInfo(Global.isConnected);
    }

    private void refreshBleInfo(boolean isConnected) {
        if (isConnected) {
            tvConnectStatus.setText("已连接");
            tvDeviceName.setText(Global.ConnectedName);
            tvDeviceMac.setText(Global.ConnectedAddress);
        } else {
            tvConnectStatus.setText("未连接");
            tvDeviceName.setText("未获取");
            tvDeviceMac.setText("未获取");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                refreshBleInfo(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(false);
                refreshBleInfo(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), LENGTH_SHORT).show();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                if (bleBattery < 20) {
                    tvCurrentBattery.setText(MessageFormat.format("{0}%（请及时充电）", bleBattery));
                } else {
                    tvCurrentBattery.setText(MessageFormat.format("{0}%", bleBattery));
                }
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_DOWNLOAD_PROGRESS:
                if (waitDialog != null && waitDialog.isShowing()) {
                    waitDialog.setProgress((int) event.getData());
                }
                break;
            case MessageEvent.ACTION_SYNC_USER_RESULT:
                int codeUser = (int) event.getData();
                if (codeUser == 401) {
                    getToken(USER_REQ);
                } else if (codeUser == 0) {
                    syncPlan();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                }
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:
                int codePlan = (int) event.getData();
                if (codePlan == 401) {
                    getToken(PLAN_REQ);
                } else if (codePlan == 0) {
                    getTrainRecord(0);
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT:
                int codeTrainData = (int) event.getData();
                if (codeTrainData == 401) {
                    getToken(TRAIN_DATA_REQ);
                } else if (codeTrainData == 0) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("同步完成");
                    }

                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT:
                int codeTrainData1 = (int) event.getData();
                if (codeTrainData1 == 401) {
                    getToken(TRAIN_DATA_TO_LOCAL_REQ);
                } else if (codeTrainData1 == 0) {
                    syncTrainRecord();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
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

            default:
                break;
        }
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
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        if (mManager != null) {
            mManager.disconnect();
            mManager.unRegisterReceiver(adapter);
        }
        super.onStop();
    }

    private void initVolume() {
        // 获取系统最大音量
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        // 设置voice_seekbar的最大值
        seekbarVolume.setMax(maxVolume);
        // 获取到当前 设备的音量
        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        // 显示音量
        Log.e("当前音量百分比：", currentVolume * 100 / maxVolume + " %");
        seekbarVolume.setProgress(currentVolume);
    }

    private void initLight() {
        seekbarBrightness.setMax(255);
        float currentBright = 0.0f;
        try {
            // 系统亮度值范围：0～255，应用窗口亮度范围：0.0f～1.0f。
            currentBright = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS) * 100 / 255;
            currentBright = getScreenBrightness(this);
            Log.e("当前亮度：", currentBright + "");
        } catch (Exception e) {
            Log.e(TAG, "initLight: ", e);
        }
        seekbarBrightness.setProgress((int) currentBright);
    }

    /**
     * 获取屏幕的亮度
     */
    public static int getScreenBrightness(Activity activity) {
        if (isAutoBrightness(activity)) {
            return getAutoScreenBrightness(activity);
        } else {
            return getManualScreenBrightness(activity);
        }
    }

    /**
     * 判断是否开启了自动亮度调节
     */
    public static boolean isAutoBrightness(Activity activity) {
        boolean automicBrightness = false;
        try {
            automicBrightness = Settings.System.getInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE) == Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC;
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        return automicBrightness;
    }

    /**
     * 获取手动模式下的屏幕亮度
     */
    public static int getManualScreenBrightness(Activity activity) {
        int nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getInt(resolver, Settings.System.SCREEN_BRIGHTNESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return nowBrightnessValue;
    }

    /**
     * 设置亮度
     */
    public void setBrightness(Activity activity, int brightness) {
        try {
            WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
            lp.screenBrightness = (float) brightness * (1f / 255f);
            int k = 0;
            k = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
            Settings.System.putInt(this.getContentResolver(),
                    Settings.System.SCREEN_BRIGHTNESS, brightness);
            Uri uri = Settings.System.getUriFor("screen_brightness");
            this.getContentResolver().notifyChange(uri, null);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    /**
     * 获取自动模式下的屏幕亮度
     */
    public static int getAutoScreenBrightness(Activity activity) {
        float nowBrightnessValue = 0;
        ContentResolver resolver = activity.getContentResolver();
        try {
            nowBrightnessValue = Settings.System.getFloat(resolver, "screen_auto_brightness_adj"); //[-1,1],无法直接获取到Setting中的值，以字符串表示
            Log.d(TAG, "[ouyangyj] Original AutoBrightness Value:" + nowBrightnessValue);
        } catch (Exception e) {
            e.printStackTrace();
        }
        float tempBrightness = nowBrightnessValue + 1.0f; //[0,2]
        float fValue = (tempBrightness / 2.0f) * 255.0f;
        Log.d(TAG, "[ouyangyj] Converted Value: " + fValue);
        return (int) fValue;
    }

    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_WRITE_SETTINGS) {
            if (Settings.System.canWrite(this)) {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings granted");
            } else {
                Log.e("ERRRRRRRRRRRRR", "onActivityResult write settings not granted");
            }
        }
    }
}
