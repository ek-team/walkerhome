package com.pharos.walker.ui;

import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import com.pharos.walker.MainActivity;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.R;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SpeechUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/14
 * Describe:
 */
public class StartActivity extends BaseActivity {
    @BindView(R.id.tv_warning_left)
    TextView tvWarningLeft;
    @BindView(R.id.tv_warning_right)
    TextView tvWarningRight;
    @BindView(R.id.btn_start)
    TextView btnStart;
    private TimerTask timerTask = null;
    private Timer timer = null;
    private int clo;
    private int count;
    private Timer mTimer;
    private TimerTask mTimerTask;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initView();
        startAnimTimerTask();
    }

    private void initView() {
        Animation mAnimation = AnimationUtils.loadAnimation(this, R.anim.start_anim);
        btnStart.setAnimation(mAnimation);
        mAnimation.start();
    }
    private void startTask(){
        if (mTimer == null && mTimerTask == null){
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    int a = 0x1A;
                    int b = 0x04 | 0x10;
                    int c = 0x00;
                    int d = 0xFF - (a + b + c) + 1;
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.UPDATE_TOP_TIME:
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_start;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
    }

    @Override
    protected void onPause() {
        super.onPause();
        clearTimerTask();
    }

    private void startAnimTimerTask(){
        if (timer == null && timerTask == null){
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (clo == 0) {
                                clo = 1;
                                tvWarningLeft.setVisibility(View.VISIBLE);
                                tvWarningRight.setVisibility(View.VISIBLE);
                            } else if (clo == 1) {
                                clo = 0;
                                tvWarningLeft.setVisibility(View.INVISIBLE);
                                tvWarningRight.setVisibility(View.INVISIBLE);
                                count++;
                                if (count >= 10) {
                                    if (timer != null) {
                                        timer.cancel();
                                        timer = null;
                                        tvWarningLeft.setVisibility(View.VISIBLE);
                                        tvWarningRight.setVisibility(View.VISIBLE);
                                    }
                                }
                            }
                        }
                    });
                }
            };
            timer = new Timer();
            timer.schedule(timerTask, 1, 600);
        }
    }
    @OnClick({R.id.tv_battery, R.id.iv_notification, R.id.iv_voice, R.id.iv_back, R.id.btn_start,R.id.img_back,R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_battery:
                break;
            case R.id.iv_notification:
                break;
            case R.id.iv_voice:
                break;
            case R.id.iv_back:
            case R.id.img_back:
            case R.id.tv_back:
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.btn_start:
                if (DateFormatUtil.avoidFastClick(2000)){
                    BluetoothController.getInstance().sendReadDeviceInfoCmd();
                    Bundle bundle = getIntent().getExtras();
                    if (bundle != null && bundle.getInt(AppKeyManager.EXTRA_CONNECT_MODE,0)  == Global.ConnectEvaluateMode){
                        startTargetActivity(bundle,EvaluateActivity.class,true);
                    }else if (bundle != null && bundle.getInt(AppKeyManager.EXTRA_CONNECT_MODE,0)  == Global.ConnectMainMode){
                        startTargetActivity(bundle,TrainingActivity.class,true);
                        SpeechUtil.getInstance(this).speak(getString(R.string.start_train));
                    }
                }
                break;
        }
    }

    private void clearTimerTask(){
        if (mTimer != null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (timerTask != null && timer != null){
            timer.cancel();
            timerTask.cancel();
            timerTask = null;
            timer = null;
        }
    }
}
