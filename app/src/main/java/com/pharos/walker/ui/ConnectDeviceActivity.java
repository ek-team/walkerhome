package com.pharos.walker.ui;

import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.exoplayer2.ui.PlayerView;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.adapter.BleDevAdapter;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.VideoPlayUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/13
 * Describe:
 */
public class ConnectDeviceActivity extends BaseActivity {
    @BindView(R.id.player_view)
    PlayerView playerView;
    @BindView(R.id.rv_bt)
    RecyclerView rvBt;
    @BindView(R.id.tv_cancel)
    TextView tvCancel;
    @BindView(R.id.tv_next)
    TextView tvNext;
    @BindView(R.id.btn_ble_init)
    TextView btnBleInit;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    private BtTemperatureReceiver broadcastReceiver;
    private BleDevAdapter bleDevAdapter;
    private HashMap<String, String> bleMap = new HashMap<>();
    private int selectActivity;
    private volatile boolean isRealConnected = false;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        selectActivity = getIntent().getIntExtra(AppKeyManager.EXTRA_CONNECT_MODE, 0);
    }

    private void initView() {
        if (selectActivity == Global.ConnectSetMode) {
            tvCancel.setText("完成");
            tvNext.setVisibility(View.GONE);
        }
        btnBleInit.setText(MessageFormat.format("{0}设置", getResources().getString(R.string.ble_shoes_setting)));
        initBtState();
        bleDevAdapter = new BleDevAdapter(new ArrayList<BleBean>());
        rvBt.setLayoutManager(new LinearLayoutManager(this));
        rvBt.setAdapter(bleDevAdapter);
        bleDevAdapter.setOnItemClickListener(new BleDevAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BleBean bleBean) {
                if (bleBean.getConnectState() == 0 || Global.ConnectStatus.equals("connecting")) {
                    Toast.makeText(ConnectDeviceActivity.this, getString(R.string.ble_connecting), Toast.LENGTH_SHORT).show();
                    return;
                }
                if (Global.isConnected && bleBean.getAddress().equalsIgnoreCase(Global.ConnectedAddress)) {
                    Toast.makeText(ConnectDeviceActivity.this, getString(R.string.ble_haven_connected), Toast.LENGTH_SHORT).show();
                    return;
                }
                //值不为null说明存在已经连接了的蓝牙设备，需要将其先断开
                if (Global.ConnectedAddress != null) {
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                }
                Global.ConnectedAddress = bleBean.getAddress();
                Global.ConnectedName = bleBean.getName();
                Global.ConnectStatus = "connecting";
                bleDevAdapter.setSelect(bleBean);
                BluetoothController.getInstance().connect(bleBean.getAddress());
            }
        });
        if (Global.isConnected && Global.ConnectedName != null && Global.ConnectedAddress != null) {
            BleBean bean = new BleBean(Global.ConnectedName, Global.ConnectedAddress, 1, -70);
            bleDevAdapter.addData(bean);
            bleMap.put(Global.ConnectedAddress, Global.ConnectedAddress);
            isRealConnected = false;
            if (Global.isSendHeart && Global.isConnected) {
                int a = 0x1A;
                int b = 0x04 | 0x30;
                int c = 0x00;
                int d = 0xFF - (a + b + c) + 1;
                String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
                BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress, message.getBytes(StandardCharsets.UTF_8));
                timerSend();
            }
        }
        btnBleInit.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                goEvaluateDialog();
                return false;
            }
        });
    }

    /**
     * 初始化蓝牙状态广播监听
     */
    private void initBtState() {
        broadcastReceiver = new BtTemperatureReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver, intentFilter);
//        BluetoothController.getInstance().startScanBle();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.BLE_SCAN_RESULT_HOME:
                bleBean = (BleBean) event.getData();
                String name = bleBean.getName();
                String address = bleBean.getAddress();
                int rssi = bleBean.getRssi();
                if (!bleMap.containsKey(address)) {
                    bleMap.put(address, address);
                    BleBean bean = new BleBean(name, address, -1, rssi);
                    bleDevAdapter.addData(bean);
                } else if (DateFormatUtil.setTimeInterval(1000)) {
                    bleDevAdapter.changeData(bleBean);
                }
                break;
            case MessageEvent.ACTION_GATT_CONNECTED_HOME:
                bleBean = (BleBean) event.getData();
                SPHelper.saveBleAddress(bleBean.getAddress());
                for (BleBean bean : bleDevAdapter.getData()) {
                    if (bleBean.getAddress().equalsIgnoreCase(bean.getAddress()) && bean.getConnectState() != 1) {
                        bean.setConnectState(1);
                        Global.ConnectStatus = "connected";
                        bleDevAdapter.notifyDataSetChanged();
                        break;
                    }
                }
                setPoint(true);
//                BluetoothController.getInstance().enableTXNotification(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED_HOME:
                bleBean = (BleBean) event.getData();
                for (BleBean bean : bleDevAdapter.getData()) {
                    if (TextUtils.equals(bean.getAddress(), bleBean.getAddress())) {
                        bleDevAdapter.getData().remove(bean);
                        break;
                    }
                }
                bleDevAdapter.notifyDataSetChanged();
                if (bleMap.containsKey(bleBean.getAddress())) {
                    bleMap.remove(bleBean.getAddress());
                }
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN_HOME:
                if ((int) event.getData() == 0) {
                    ToastUtils.showShort("鞋子连接成功");
                } else {
                    ToastUtils.showShort("蓝牙通信失败，请重新连接");
                }
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                isRealConnected = true;
                break;
            case MessageEvent.ACTION_ClEAR_CONNECTED_DEVICE:
                BluetoothController.getInstance().close();
                Global.isConnected = false;
                Global.ConnectStatus = "unconnected";
                bleDevAdapter.clear();
                bleMap.clear();
                BluetoothController.getInstance().startScanBle();
                break;
            default:
                break;
        }
    }


    @Override
    protected void onResume() {
        String fileName = "file:///android_asset/video_connect_1.mp4";
        VideoPlayUtil.getInstance().setVideoPlayer(this, fileName, playerView);
        VideoPlayUtil.getInstance().startPlayer();
        Global.OpenConnectView = true;
        super.onResume();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_connect_device;
    }

    @OnClick({R.id.tv_cancel, R.id.tv_next, R.id.tv_refresh, R.id.img_refresh,R.id.img_back, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                Global.OpenConnectView = false;
                if (selectActivity == Global.ConnectSetMode) {
                    finish();
                } else {
                    startTargetActivity(MainActivity.class, true);
                }
                break;
            case R.id.tv_next:
                if (DateFormatUtil.avoidFastClick2(2000) && Global.isConnected) {
                    if (selectActivity == Global.ConnectMainMode) {
                        startTargetActivity(TrainParamActivity.class, true);
                    } else if (selectActivity == Global.ConnectEvaluateMode || selectActivity == Global.ConnectUserMode) {
                        Bundle bundle = new Bundle();
                        bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                        startTargetActivity(bundle, StartActivity.class, true);
                    } else {
                        startTargetActivity(TrainParamActivity.class, true);
                    }
                }
                break;
            case R.id.tv_refresh:
            case R.id.img_refresh:
                if (DateFormatUtil.avoidFastClick(2000)) {
                    BluetoothController.getInstance().close();
                    Global.isConnected = false;
                    Global.ConnectStatus = "unconnected";
                    bleDevAdapter.clear();
                    bleMap.clear();
                    BluetoothController.getInstance().startScanBle();
                }
                break;
            case R.id.img_back:
            case R.id.tv_back:
                startTargetActivity(MainActivity.class,true);
                Global.OpenConnectView = false;
                break;
        }
    }

    private Timer mTimer;
    private TimerTask mTimerTask;
    private int count = 0;

    private void timerSend() {
        if (mTimer == null && mTimerTask == null) {
            mTimer = new Timer();
            mTimerTask = new TimerTask() {
                @Override
                public void run() {
                    count++;
                    if (!isRealConnected && count >= 5) {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_ClEAR_CONNECTED_DEVICE));
                        cancelSend();
                    }
                    if (count > 5) {
                        cancelSend();
                    }
                }
            };
            mTimer.schedule(mTimerTask, 0, 100);
        }
    }

    private void cancelSend() {
        if (mTimer != null) {
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
    }

    @Override
    protected void onPause() {
        VideoPlayUtil.getInstance().stopPlayer();
        VideoPlayUtil.getInstance().destroyPlayer();
        Global.OpenConnectView = false;
        super.onPause();
    }

    @Override
    protected void onStop() {
//        VideoPlayUtil.getInstance().destroyPlayer();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        BluetoothController.getInstance().stopScanBle();
        unregisterReceiver(broadcastReceiver);//注销广播接收器
        EventBus.getDefault().unregister(this);
    }

    private void goEvaluateDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否重置蓝牙");
        dialog.setCancel("取消");
        dialog.setSure("重置");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            BluetoothController.getInstance().close();
            Global.isConnected = false;
            Global.ConnectStatus = "unconnected";
            bleDevAdapter.clear();
            bleMap.clear();
            BluetoothController.getInstance().initBle();
            Toast.makeText(this, "重置成功", Toast.LENGTH_SHORT).show();
            dialog.dismiss();

        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
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
                        Toast.makeText(ConnectDeviceActivity.this, "蓝牙正在开启……，请稍等", Toast.LENGTH_SHORT).show();
                        break;
                    case BluetoothAdapter.STATE_ON:
                        BluetoothController.getInstance().startScanBle();
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
