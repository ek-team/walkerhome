package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SynthesizerListener;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.DoubleSlideSeekBar;
import com.pharos.walker.customview.WaveLoadingView;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxDialogThreeSelect;
import com.pharos.walker.customview.rxdialog.RxRadioButtonDialog;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.PrintContent;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.text.DecimalFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/27
 * Describe:
 */
public class EvaluateActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.double_slide)
    DoubleSlideSeekBar doubleSlide;
    @BindView(R.id.waveLoadingView)
    WaveLoadingView waveLoadingView;
    @BindView(R.id.speedview)
    SpeedView speedview;
    @BindView(R.id.tv_vas)
    TextView tvVas;
    @BindView(R.id.tv_study_setting)
    TextView tvStudySetting;
    @BindView(R.id.img_reduce)
    ImageView imgReduce;
    @BindView(R.id.tv_result)
    TextView tvResult;
    @BindView(R.id.img_increase)
    ImageView imgIncrease;
    @BindView(R.id.btn_clear)
    TextView btnClear;
    @BindView(R.id.btn_commit)
    TextView btnCommit;
    @BindView(R.id.algorithm_spinner)
    Spinner algorithmSpinner;
    @BindView(R.id.ed_remote_calibration)
    EditText edRemoteCalibration;
    @BindView(R.id.tv_remote_calibration)
    TextView tvRemoteCalibration;
    @BindView(R.id.tv_first_value)
    TextView tvFirstValue;
    @BindView(R.id.tv_second_value)
    TextView tvSecondValue;
    @BindView(R.id.tv_third_value)
    TextView tvThirdValue;
    @BindView(R.id.tv_evaluate_tips1)
    TextView tvEvaluateTips1;
    private float weightValue = 0;
    private float saveResult = 0;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private int selectPosition = 1;
    private int vasValue = 1;
    private int mode = 0;
    private static int ClickCount = 0;
    private static long ClickTime = 0;
    private boolean firstEvaluateFinish = false;
    private boolean secondEvaluateFinish = false;
    private boolean thirdEvaluateFinish = false;
    private boolean evaluateFinish = false;
    List<Float> floatList = new ArrayList<>();
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                int[] list = (int[]) msg.obj;
                updateSpeedView(list[0], list[1]);
            }
        }
    };

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
        Global.isReconnectBle = true;
        clearView();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        btnCommit.setClickable(false);
        if (bundle != null) {
            mode = bundle.getInt("TrainParam", 0);
        }
    }

    private void initView() {
        btnCommit.setClickable(false);
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(100);
        speedview.setMinSpeed(0);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        doubleSlide.setOnRangeListener((low, big) -> {
            int[] list = {0, 0};
            Message msg = new Message();
            msg.what = 0;
            list[0] = Math.round(low);
            list[1] = Math.round(big);
            msg.obj = list;
            mHandler.sendMessage(msg);
        });
        tvStudySetting.setOnLongClickListener(v -> {
            RxRadioButtonDialog dialog = new RxRadioButtonDialog(EvaluateActivity.this);
            dialog.setSureListener(v1 -> {
                tvVas.setText(MessageFormat.format("VAS {0}分", dialog.selectValue));
                vasValue = dialog.selectValue;
                dialog.dismiss();
            });
            dialog.show();
            return false;
        });
        algorithmSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    selectPosition = 1;
                    clearView();
                } else {
                    selectPosition = position;
                    clearView();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void updateSpeedView(float min, float max) {
        speedview.setIndicator(Indicator.Indicators.KiteIndicator);
        speedview.setIndicatorColor(Color.WHITE);
        speedview.setWithTremble(false);
        speedview.setMaxSpeed(max);
        speedview.setMinSpeed(min);
        speedview.setTickNumber(11);
        speedview.setTickPadding(36);
        speedview.speedTo(min, 10);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                clearTimerTask();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                SpeechUtil.getInstance(this).speak("蓝牙鞋已断开请重新连接");
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                synchronized (this){
                    Global.ReadCount = 0;
                }
                String value = bleBean.getData();
                if (selectPosition == 2) {
                    refreshView(Float.valueOf(value));
                } else {
                    calculMinValue(Float.valueOf(value));
                }
//                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.READ_DATA_HEART_DISCONNECT:
                Log.e("Evaluate", "onMessageEvent: " + "心跳停了");
                if (Global.ConnectedAddress != null) {
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                    BluetoothController.getInstance().connect(Global.ConnectedAddress);
                    BluetoothController.getInstance().clearConnectedStatus();
                    BluetoothController.getInstance().startScanBle();
                }
                break;
            default:
                break;
        }
    }

    private void refreshView(Float valueOf) {
        speedview.speedTo(valueOf, 150);
//        if (valueOf < weightValue) {
//            return;
//        }
        if(evaluateCount >= 3)
            return;
        if (tempValue < valueOf){
            tempValue = valueOf;
        }
        floatList.add(valueOf);
        weightValue = Collections.max(floatList).intValue();
//        tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
        if (evaluateCount == 0){
            tvFirstValue.setText(MessageFormat.format("第一次：{0}kg",weightValue));
        }
        if (evaluateCount == 1){
            tvSecondValue.setText(MessageFormat.format("第二次：{0}kg",weightValue));
            result = 0;
        }
        if (evaluateCount == 2){
            tvThirdValue.setText(MessageFormat.format("第三次：{0}kg",weightValue));
            result = 0;
        }

        if (tempValue > valueOf && valueOf < 2 && weightValue > 1){
            evaluateCount ++;
            tempValue = valueOf;
            if (evaluateCount == 1){
                firstValue = weightValue;
                tvFirstValue.setText(MessageFormat.format("第一次：{0}kg",firstValue));
                tempValue = 0;
                floatList.clear();
                tvResult.setText(MessageFormat.format("{0}kg", firstValue));
            }
            if (evaluateCount == 2){
                secondValue = weightValue;
                tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", secondValue));
                tempValue = 0;
                floatList.clear();
                tvResult.setText(MessageFormat.format("{0}kg", (firstValue + secondValue)/2));
            }
            if (evaluateCount == 3){
                thirdValue = weightValue;
                tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", thirdValue));
                result = (firstValue + secondValue + thirdValue)/3;
                tvResult.setText(MessageFormat.format("{0}kg", (int)result));
                tempValue = 0;
                floatList.clear();
            }
        }
    }

    private List<Float> valueList = new ArrayList<>();
    private List<Float> minValueList = new ArrayList<>();
    private int indexMax = 5;

    private float tempValue = 0;
    private int evaluateCount = 0;
    private float firstValue = 0;
    private float secondValue = 0;
    private float thirdValue = 0;
    private float result = 0;
    private void calculMinValue(float value) {
        if (tempValue < value){
            tempValue = value;
        }
        speedview.speedTo(value, 150);
        if (valueList.size() < indexMax) {
            valueList.add(value);
        } else if (valueList.size() == indexMax) {
            minValueList.add(Collections.min(valueList));
            valueList.clear();
        }
        if (minValueList.size() > 0) {
            weightValue = Collections.max(minValueList).intValue();
//            tvResult.setText(MessageFormat.format("{0}kg", weightValue));
            if (evaluateCount == 0){
                tvFirstValue.setText(MessageFormat.format("第一次：{0}kg",weightValue));
            }
            if (evaluateCount == 1){
                tvSecondValue.setText(MessageFormat.format("第二次：{0}kg",weightValue));
                result = 0;
            }
            if (evaluateCount == 2){
                tvThirdValue.setText(MessageFormat.format("第三次：{0}kg",weightValue));
                result = 0;
            }



            if (tempValue > value && value < 1.8 && weightValue > 1){
                evaluateCount ++;
                tempValue = value;
                if (evaluateCount == 1){
                    firstValue = weightValue;
                    tvFirstValue.setText(MessageFormat.format("第一次：{0}kg",firstValue));
                    tempValue = 0;
                    minValueList.clear();
                    tvResult.setText(MessageFormat.format("{0}kg", firstValue));
                    SpeechUtil.getInstance(this).speak("请开始第二次踩踏");
                    tvEvaluateTips1.setText("请开始第二次踩踏");
                }
                if (evaluateCount == 2){
                    secondValue = weightValue;
                    tvSecondValue.setText(MessageFormat.format("第二次：{0}kg", secondValue));
                    tempValue = 0;
                    minValueList.clear();
                    tvResult.setText(MessageFormat.format("{0}kg", (firstValue + secondValue)/2));
                    SpeechUtil.getInstance(this).speak("请开始第三次踩踏");
                    tvEvaluateTips1.setText("请开始第三次踩踏");
                }
                if (evaluateCount == 3){
                    thirdValue = weightValue;
                    tvThirdValue.setText(MessageFormat.format("第三次：{0}kg", thirdValue));
                    result = (firstValue + secondValue + thirdValue)/3;
                    tvResult.setText(MessageFormat.format("{0}kg", (int)result));
                    tempValue = 0;
                    minValueList.clear();
                    SpeechUtil.getInstance(this).speak("你已评估完成，可以开始训练了");
                    tvEvaluateTips1.setText("你已评估完成，可以开始训练了");
                    evaluateFinish = true;
                    btnCommit.setBackground(getResources().getDrawable(R.drawable.btn_orange_bg));
                    btnCommit.setClickable(true);
                }
            }
        }

    }


    private void deleteMax(float value) {
        for (int i = 0; i < minValueList.size(); i++) {
            if (minValueList.get(i) >= value) {
                minValueList.set(i, value);
            }
        }
    }
    private void clearView(){
        tvFirstValue.setText("");
        tvSecondValue.setText("");
        tvThirdValue.setText("");
        tvResult.setText("0kg");
        tempValue = 0;
        evaluateCount = 0;
        firstValue = 0;
        secondValue = 0;
        thirdValue = 0;
        result = 0;
        floatList.clear();
        minValueList.clear();
        SpeechUtil.getInstance(this).speak(getString(R.string.text_evaluate_tips), new SynthesizerListener() {
            @Override
            public void onSpeakBegin() {

            }

            @Override
            public void onBufferProgress(int i, int i1, int i2, String s) {

            }

            @Override
            public void onSpeakPaused() {

            }

            @Override
            public void onSpeakResumed() {

            }

            @Override
            public void onSpeakProgress(int i, int i1, int i2) {

            }

            @Override
            public void onCompleted(SpeechError speechError) {
                SpeechUtil.getInstance(EvaluateActivity.this).speak("请开始第一次踩踏");
                tvEvaluateTips1.setText("请开始第一次踩踏");

            }

            @Override
            public void onEvent(int i, int i1, int i2, Bundle bundle) {

            }
        });

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_evaluate;
    }

    @Override
    protected void onResume() {
        startTask();
        super.onResume();
    }

    @OnClick({R.id.iv_back, R.id.tv_study_setting, R.id.img_reduce, R.id.img_increase, R.id.btn_clear, R.id.btn_commit,R.id.tv_remote_calibration,R.id.img_back,R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.img_back:
            case R.id.tv_back:
                floatList.clear();
                valueList.clear();
                minValueList.clear();
                weightValue = 0;
                if (evaluateFinish){
                    saveData();
                }
                startTargetActivity(MainActivity.class, true);
                break;
            case R.id.tv_study_setting:
                //设置在五秒内点击七次版本号会显示标定功能
                ClickCount ++;
                if (ClickCount == 1){
                    ClickTime = System.currentTimeMillis();
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime < 5000)){
                    tvRemoteCalibration.setVisibility(View.VISIBLE);
                    edRemoteCalibration.setVisibility(View.VISIBLE);
                    ClickTime = 0;
                    ClickCount = 0;
                }else if (ClickCount >= 7 && (System.currentTimeMillis() - ClickTime > 5000)){
                    ClickTime = 0;
                    ClickCount = 0;
                }
                break;
            case R.id.img_reduce:
                if (evaluateCount >= 3){
                    if (result > 0) {
                        result = result - 1;
                        deleteMax(result);
//                    DecimalFormat decimalFormat = new DecimalFormat("0.0");
//                    tvResult.setText(MessageFormat.format("{0}kg", decimalFormat.format(weightValue)));
                        tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                    }
                }
                break;
            case R.id.img_increase:
                if (evaluateCount >= 3){
                    if (result < 100) {
                        result = result + 1;
                        if (selectPosition == 1) {
                            minValueList.add(result);
                        }
                        tvResult.setText(MessageFormat.format("{0}kg", (int) result));
                    }
                }
                break;
            case R.id.btn_clear:
                weightValue = 0;
                result = 0;
                floatList.clear();
                valueList.clear();
                minValueList.clear();
                clearView();
                evaluateFinish = false;
                tvResult.setText(MessageFormat.format("{0}kg", (int) weightValue));
                break;
            case R.id.btn_commit:
                saveResult = result;
                if (saveResult < 1){
                    Toast.makeText(this, "评估值不能为0", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (DateFormatUtil.avoidFastClick(2000)){
                    saveData();
                    if (Global.isConnected) {
                        startTargetActivity(TrainParamActivity.class, true);
                    } else {
                        startTargetActivity(HomeConnectActivity.class, true);
                    }
                }

//                if (Global.USER_MODE)
//                    EvaluateManager.getInstance().insert((int) saveResult, vasValue);
//
//                if (Global.USER_MODE){
//                    RxDialogThreeSelect dialog = new RxDialogThreeSelect(this);
//                    dialog.setContent("评估完成");
//                    dialog.setTvSelect1("回到主界面");
//                    dialog.setTvSelect2("打印报告");
//                    dialog.setTvSelect3("康复训练");
//                    dialog.setTvSelect1Listener(v -> {
//                        floatList.clear();
//                        valueList.clear();
//                        minValueList.clear();
//                        weightValue = 0;
//                        dialog.dismiss();
//                        startTargetActivity(MainActivity.class, true);
//                    });
//                    dialog.setTvSelect2Listener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            Bundle bundle = new Bundle();
//                            bundle.putFloat("result",result);
//                            bundle.putFloat("vas_value",vasValue);
//                            bundle.putFloat("first_value",firstValue);
//                            bundle.putFloat("second_value",secondValue);
//                            bundle.putFloat("third_value",thirdValue);
//                            startTargetActivity(bundle, PrintContentActivity.class,true);
//                        }
//                    });
//                    dialog.setTvSelect3Listener(v -> {
//                        dialog.dismiss();
//                        if (Global.isConnected) {
//                            startTargetActivity(TrainParamActivity.class, true);
//                        } else {
//                            startTargetActivity(ConnectDeviceActivity.class, true);
//                        }
//                    });
//                    dialog.show();
//                }else {
//                    RxDialogSureCancel dialog = new RxDialogSureCancel(this);
//                    dialog.setContent("是否进入训练");
//                    dialog.setCancel("回到主界面");
//                    dialog.setSure("进入训练");
//                    dialog.setSureListener(v -> {
//                        dialog.dismiss();
//                        if (Global.isConnected) {
//                            startTargetActivity(TrainParamActivity.class, true);
//                        } else {
//                            startTargetActivity(ConnectDeviceActivity.class, true);
//                        }
//                    });
//                    dialog.setCancelListener(v -> {
//                        floatList.clear();
//                        valueList.clear();
//                        minValueList.clear();
//                        weightValue = 0;
//                        dialog.dismiss();
//                        startTargetActivity(MainActivity.class, true);
//                    });
//                    dialog.show();
//
//                }

                break;
            case R.id.tv_remote_calibration:
                String message;
                if (TextUtils.isEmpty(edRemoteCalibration.getText().toString())){
                    ToastUtils.showShort("请输入标定值");
                    return;
                }else {
                    message = "set" + edRemoteCalibration.getText().toString();
                }
                BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                Toast.makeText(this, "命令已发送", Toast.LENGTH_SHORT).show();
                tvRemoteCalibration.setVisibility(View.GONE);
                edRemoteCalibration.setVisibility(View.GONE);
                break;
        }
    }

    private void saveData(){
        UserBean userBean = SPHelper.getUser();
        userBean.setEvaluateWeight(saveResult);
        SPHelper.saveUser(userBean);
        UserManager.getInstance().insert(userBean, 1);
        MyUtil.insertTemplate((int) saveResult);
        if (evaluateFinish){
            EvaluateManager.getInstance().insert((int) result, vasValue,firstValue,secondValue,thirdValue);
            SPHelper.saveEvaluateDate(System.currentTimeMillis());
        }
    }

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
                    Global.isStartReadData = true;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
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

    @Override
    protected void onDestroy() {
        Global.isReconnectBle = false;
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        Global.isStartReadData = false;
        Global.ReadCount = 0;
        super.onDestroy();
    }

}
