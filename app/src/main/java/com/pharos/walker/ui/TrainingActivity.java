package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.net.DhcpInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.text.format.Formatter;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.github.anastr.speedviewlib.SpeedView;
import com.github.anastr.speedviewlib.components.Indicators.Indicator;
import com.github.anastr.speedviewlib.components.note.Note;
import com.github.anastr.speedviewlib.components.note.TextNote;
import com.google.android.exoplayer2.ui.PlayerView;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.GameEntity;
import com.pharos.walker.beans.MessageBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.VerticalProgressBar;
import com.pharos.walker.customview.WaveLoadingView;
import com.pharos.walker.customview.electime.ElecTimeNumView;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.VideoPlayUtil;
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

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/14
 * Describe:
 */
public class TrainingActivity extends BaseActivity {
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
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.tv_effective_time)
    TextView tvEffectiveTime;
    @BindView(R.id.tv_total_time)
    TextView tvTotalTime;
    @BindView(R.id.layout_time)
    LinearLayout layoutTime;
    @BindView(R.id.tv_warning_time)
    TextView tvWarningTime;
    @BindView(R.id.layout_warning_time)
    LinearLayout layoutWarningTime;
    @BindView(R.id.tv_train_time)
    TextView tvTrainTime;
    @BindView(R.id.waveLoadingView_v2)
    WaveLoadingView waveLoadingViewV2;
    @BindView(R.id.speedview_v2)
    SpeedView speedviewV2;
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.tv_show_tips)
    TextView tvShowTips;
    @BindView(R.id.tv_stop_v2)
    TextView tvStopV2;
    @BindView(R.id.rl_root_v2)
    RelativeLayout rlRootV2;
    @BindView(R.id.elect_time_minute_1)
    ElecTimeNumView elecTimeMinute1;
    @BindView(R.id.elect_time_minute_2)
    ElecTimeNumView elecTimeMinute2;
    @BindView(R.id.elect_time_second_1)
    ElecTimeNumView elecTimeSecond1;
    @BindView(R.id.elect_time_second_2)
    ElecTimeNumView elecTimesecond2;
    private Timer mTimer;
    private TimerTask mTimerTask;
    private MediaPlayer mediaPlayer;
    private int count = 0;
    private int warningcount = 0;
    private int notcount = 0;
    private int totalTrainingTime = 0;
    private int totalCount = 0;
    private int trainingTime = 0;
    private float weight = 0;
    private float minWeight;
    private float maxWeight;
    private int planid = 0;
    private int level = 1;
    private int planTrainNum = 10;
    private int musicPosition;
    private ArrayList<TrainDataEntity> trainDataEntityList;
    private static final String TAG = "TrainingActivity";
    private static final int delayTime = 4000;
    private List<String> listMusic;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        rlRootV2.setVisibility(View.VISIBLE);
        initView();
        SPHelper.saveRebootTime(0);

    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        listMusic = MyUtil.getFilesAllName(Environment.getExternalStorageDirectory() + File.separator + "android" + File.separator + "fls" + File.separator + "background_data");
        if (bundle != null) {
            trainingTime = bundle.getInt(AppKeyManager.EXTRA_TIMENUM, 0);
            totalTrainingTime = trainingTime * 60;
            totalCount = totalTrainingTime;
            weight = bundle.getInt(AppKeyManager.EXTRA_WEIGHT, 0);
            musicPosition = bundle.getInt(AppKeyManager.EXTRA_MUSIC_FILE, 0);
            planTrainNum = bundle.getInt(AppKeyManager.EXTRA_PLAN_TRAIN_NUM, 0);
        }
        mediaPlayer = new MediaPlayer();
        try {
//            AssetFileDescriptor fd = getAssets().openFd(musicPosition + ".mp3");
//            mediaPlayer.setDataSource(fd.getFileDescriptor(), fd.getStartOffset(), fd.getLength());
            if (listMusic != null && musicPosition < 6){
                mediaPlayer.setDataSource(listMusic.get(musicPosition));
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else if (listMusic == null){
                ToastUtils.showShort("未获取到背景音乐");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        Global.isReconnectBle = true;
    }

    @SuppressLint("NewApi")
    private void initView() {
        minWeight = weight * 0.8f;
        maxWeight = weight * 1.2f;
        speedviewV2.setIndicator(Indicator.Indicators.KiteIndicator);
        speedviewV2.setIndicatorColor(Color.WHITE);
        if (!Global.isChangSha) {
            speedviewV2.setLowSpeedColor(getColor(R.color.speed_yellow));
            speedviewV2.setMediumSpeedColor(getColor(R.color.speed_green));
        }
        speedviewV2.setLowSpeedPercent(40);
        speedviewV2.setMediumSpeedPercent(60);
        speedviewV2.setWithTremble(false);
        speedviewV2.setMaxSpeed(weight * 2);
        speedviewV2.setTickNumber(11);
        speedviewV2.setTickPadding(36);
        tvTotalTime.setText(MessageFormat.format("/{0}次", planTrainNum));
//        planid = b.getInt(AppKeyManager.EXTRA_PLANID, 0);
//        trainingTime = b.getInt(AppKeyManager.EXTRA_TREAD_NUM, 0);
//        tvTotalTime.setText(MessageFormat.format("/{0}次", totalTrainingTime));
    }

    @Override
    protected void onMusicSelectedChanged(int position) {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        mediaPlayer = new MediaPlayer();
        try {
            if (listMusic != null && position < 6){
                mediaPlayer.setDataSource(listMusic.get(position));
                mediaPlayer.setLooping(true);
                mediaPlayer.prepare();
                mediaPlayer.start();
            }else if (listMusic == null){
                ToastUtils.showShort("未获取到背景音乐");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        super.onMusicSelectedChanged(position);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_training;
    }

    @Override
    protected void onResume() {
        super.onResume();
        startTask();
        countDownThread();
//        OneShotUtil.getInstance(this).startOneShot();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                countDownThread();
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                clearTimerTask();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                SpeechUtil.getInstance(this).speak("蓝牙鞋已断开请重新连接");
                BluetoothController.getInstance().startScanBle();
                break;
            case MessageEvent.ACTION_READ_DATA:
                synchronized (this){
                    Global.ReadCount = 0;
                }
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                refreshView(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                BluetoothController.getInstance().stopScanBle();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            case MessageEvent.ACTION_COUNTDOWN:
                int countTime = (int) event.getData();
                if (Global.ReleaseVersion == Global.HomeVersion){
                    tvTrainTime.setText(DateFormatUtil.getMinuteTime(countTime));
//                    tvTrainTimeV2.setText(DateFormatUtil.getMinuteTime(countTime));
//                    int[] times = DateFormatUtil.getMinuteTimes(countTime);
//                    elecTimeMinute1.setCurNum(times[0]);
//                    elecTimeMinute2.setCurNum(times[1]);
//                    elecTimeSecond1.setCurNum(times[2]);
//                    elecTimesecond2.setCurNum(times[3]);
                }else {
                    tvTrainTime.setText(DateFormatUtil.getMinuteTime(countTime));
                }
                break;
            case MessageEvent.ACTION_TRAIN_TIPS:
                SpeechUtil.getInstance(this).speak("太用力了");
                noteCenterIndicator("太用力了!!");
                break;
            case MessageEvent.ACTION_GAME_DOG:
                try {
                    Thread.sleep(delayTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            case MessageEvent.READ_DATA_HEART_DISCONNECT:
                Log.e(TAG, "onMessageEvent: " + "心跳停了");
                if (Global.ConnectedAddress != null){
                    Log.e(TAG, "onMessageEvent: " + "关闭上次连接，开始新的连接");
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

    float currentMaxweight;
    boolean isEffect = false;
    boolean isWarning = false;
    boolean isDidNotMakeIt = false;//判断最大值是否在黄色范围，便于记录未达标的数据

    private void refreshView(Float value) {
        if (value < 0.5f) {
            value = 0.0f;
            speedviewV2.speedTo(value, 150);
        } else {
            speedviewV2.speedTo(value, 150);
        }
        if (value >= maxWeight && DateFormatUtil.avoidFastClick(1000)) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_TRAIN_TIPS));
        }
        // 计算压力最高值并保存
        // 计算压力最高值并保存
        if (currentMaxweight < value) {
            currentMaxweight = value;
        }
        if (currentMaxweight >= minWeight && currentMaxweight < maxWeight) {
            if (!isWarning) {
                isEffect = true;
                isDidNotMakeIt = false;
            }
        } else if (currentMaxweight >= maxWeight) {
            isWarning = true;
            isEffect = false;
            isDidNotMakeIt = false;
        } else if (currentMaxweight < minWeight && currentMaxweight > weight * 0.3) {
            if (!isWarning && !isEffect) {
                isDidNotMakeIt = true;
            }
        }

        if (isDidNotMakeIt && !isWarning && !isEffect) {
            noteCenterIndicator("加油");
        }

        if (value <= weight * 0.2 && (isDidNotMakeIt || isEffect || isWarning)) {
            insertData(currentMaxweight, weight);
            currentMaxweight = 0;
            if (isWarning) {
                warningcount++;
                tvWarningTime.setText(MessageFormat.format("{0}", warningcount));
            }
            if (isEffect) {
                SpeechUtil.getInstance(this).speak("太棒了，完成一次");
                count++;
                tvEffectiveTime.setText(MessageFormat.format("{0}", count));
                if (count == planTrainNum){
                    startFeedback();
                }
            }
            if (isDidNotMakeIt){
                notcount++;
            }
            isEffect = false;
            isWarning = false;
            isDidNotMakeIt = false;
        }


    }

    private void insertData(float realLoad, float targetLoad) {
        if (!Global.USER_MODE)//访客模式下不记录数据
            return;
        if (realLoad > 200)//过滤异常数据
            return;
        if (trainDataEntityList == null) {
            trainDataEntityList = new ArrayList<>();
        }
        TrainDataEntity entity = new TrainDataEntity();
        entity.setRealLoad((int) realLoad);
        entity.setTargetLoad((int) targetLoad);
        long userId = SPHelper.getUserId();
        long date = System.currentTimeMillis();
        entity.setCreateDate(date);
        entity.setDateStr(DateFormatUtil.getDate2String(date, AppKeyManager.DATE_YMD));
        entity.setPlanId(TrainPlanManager.getInstance().getCurrentPlanId(userId));
        entity.setClassId(TrainPlanManager.getInstance().getCurrentClassId(userId));
        entity.setIsUpload(0);
        entity.setUserId(userId);
        entity.setFrequency(UserTrainRecordManager.getInstance().getLastTimeFrequency(userId));
        entity.setKeyId(SnowflakeIdUtil.getUniqueId());
        trainDataEntityList.add(entity);
//        TrainDataManager.getInstance().insert(entity);
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
                    String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                    Log.e("发送数据", "run: " + message);
                    Global.isStartReadData = true;
                    BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                }
            };
            mTimer.schedule(mTimerTask, 0, 1000);
        }
    }

    private Timer mTimer1;
    private TimerTask mTimerTask1;

    private void countDownThread() {
        if (mTimer1 == null && mTimerTask1 == null) {
            mTimer1 = new Timer();
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    totalTrainingTime--;
                    if (totalTrainingTime <= 0) {
                        mTimer1.cancel();
                        mTimerTask1.cancel();
                        startFeedback();
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_COUNTDOWN, totalTrainingTime));
                }
            };
            mTimer1.schedule(mTimerTask1, 0, 1000);
        }
    }

    private void clearTimerTask() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimerTask1.cancel();
            mTimer1 = null;
            mTimerTask1 = null;
        }
    }

    @OnClick({R.id.iv_back,R.id.tv_stop_v2})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_stop_v2:
                RxDialogSureCancel dialog = new RxDialogSureCancel(this);
                dialog.setContent("是否退出训练？");
                dialog.setSureListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startFeedback();
                        dialog.dismiss();
                    }
                });
                dialog.show();
                break;
        }
    }

    private void startFeedback() {
        Bundle b = new Bundle();
        float completeRate = 0;
        float rightRate = 0;
        if (count + warningcount + notcount == 0){
            b.putFloat(AppKeyManager.EXTRA_SCORE, 0);
        }else {
            rightRate = (float)count/(count + warningcount + notcount);
            b.putFloat(AppKeyManager.EXTRA_SCORE, getStarByRate(rightRate));
        }
//        completeRate = (totalCount - totalTrainingTime)/(float)totalCount;
        completeRate = (float)count/planTrainNum;
        b.putFloat(AppKeyManager.EXTRA_COMPLETE_RATE, getStarByRate(completeRate));
        b.putFloat(AppKeyManager.EXTRA_COMPLETE_SOURCE, completeRate);
        b.putFloat(AppKeyManager.EXTRA_RIGHT_RATE, rightRate);
        b.putInt(AppKeyManager.EXTRA_LEVEL, level);
        b.putInt(AppKeyManager.EXTRA_TRAIN_TIME, trainingTime);
        b.putInt(AppKeyManager.EXTRA_EFFECTIVE_TIME, count);
        b.putInt(AppKeyManager.EXTRA_NOTE_ERRORNUMBER, warningcount);
        b.putInt(AppKeyManager.EXTRA_WEIGHT, (int) weight);
        if (trainDataEntityList != null) {
            b.putParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY, trainDataEntityList);
        }
        startTargetActivity(b, FeedbackOtherActivity.class, true);

    }
    private Timer mTimer2;
    private TimerTask mTimerTask2;

    private void timerSend() {
        if (mTimer2 == null && mTimerTask2 == null) {
            mTimer2 = new Timer();
            mTimerTask2 = new TimerTask() {
                @Override
                public void run() {
                }
            };
            mTimer2.schedule(mTimerTask2, 0, 200);
        }
    }

    private void cancelSend() {
        if (mTimer2 != null) {
            mTimer2.cancel();
            mTimerTask2.cancel();
            mTimer2 = null;
            mTimerTask2 = null;
        }
    }

    @Override
    protected void onStop() {
        Global.isReconnectBle = false;
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        }
        Global.isStartReadData = false;
        Global.ReadCount = 0;
//        VideoPlayUtil.getInstance().destroyPlayer();
//        OneShotUtil.getInstance(this).stopOneShot();
        super.onStop();
    }

    public void noteCenterIndicator(String text) {
        TextNote note = new TextNote(getApplicationContext(), text)
                .setPosition(Note.Position.CenterIndicator)
                .setTextTypeFace(Typeface.create(Typeface.DEFAULT, Typeface.BOLD))
                .setTextSize(speedviewV2.dpTOpx(25f));
        speedviewV2.addNote(note, 200);
    }
    private float getStarByRate(float rate){
        if (rate > 0 && rate <= 0.1f){
            return 0.5f;
        }
        if (rate > 0.1 && rate <= 0.2f){
            return 1f;
        }
        if (rate > 0.2f && rate <= 0.3f){
            return 1.5f;
        }
        if (rate > 0.3 && rate <= 0.4f){
            return 2f;
        }
        if (rate > 0.4 && rate <= 0.5f){
            return 2.5f;
        }
        if (rate > 0.5f && rate <= 0.6f){
            return 3f;
        }
        if (rate > 0.6 && rate <= 0.7f){
            return 3.5f;
        }
        if (rate > 0.7 && rate <= 0.8f){
            return 4f;
        }
        if (rate > 0.8 && rate <= 0.9f){
            return 4.5f;
        }
        if (rate > 0.9 && rate <= 1.0f){
            return 5f;
        }
        return 0;
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        clearTimerTask();
        cancelSend();
        super.onDestroy();
    }
}
