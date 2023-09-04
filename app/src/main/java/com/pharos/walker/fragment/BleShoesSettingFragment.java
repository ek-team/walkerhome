package com.pharos.walker.fragment;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.pharos.walker.R;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.rxdialog.RxClearZeroDialog;
import com.pharos.walker.ui.ConnectDeviceActivity;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class BleShoesSettingFragment extends Fragment {
    @BindView(R.id.tv_device_name)
    TextView tvDeviceName;
    @BindView(R.id.btn_select)
    TextView btnSelect;
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
    public RxClearZeroDialog dialog;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_ble_shoes_setting, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
//        initView();
        return contentView;
    }

    private void initView() {
        refreshBleInfo(Global.isConnected,Global.BLE_BATTERY);
    }
    public void refreshBleInfo(boolean isConnected,int bleBattery){
        if (tvCurrentBattery == null)
            return;
        if (bleBattery == 0) {
            tvCurrentBattery.setText("未获取");
        }else if (bleBattery <= 20 && bleBattery > 0){
            tvCurrentBattery.setText(MessageFormat.format("{0}%（请及时充电）", Global.BLE_BATTERY));
        }else {
            tvCurrentBattery.setText(MessageFormat.format("{0}%", Global.BLE_BATTERY));
        }
        if (isConnected && tvConnectStatus != null){
            tvConnectStatus.setText("已连接");
            tvDeviceName.setText(Global.ConnectedName);
            tvDeviceMac.setText(Global.ConnectedAddress);
        }else if (tvConnectStatus != null){
            tvConnectStatus.setText("未连接");
            tvDeviceName.setText("未获取");
            tvDeviceMac.setText("未获取");
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        getBLeDevicesInfo();
        initView();
        Log.e("second fragment", "显示了");//显示
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
    private void getBLeDevicesInfo(){
        int a = 0x1A;
        int b = 0x04 | 0x30;
        int c = 0x00;
        int d = 0xFF - (a + b + c) + 1;
        String message = ":1A" + DataTransformUtil.toHexString((byte) b) + "00" + DataTransformUtil.toHexString((byte) d);
        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
    }
    //        @Override
//    public void setUserVisibleHint(boolean isVisibleToUser) {
//        super.setUserVisibleHint(isVisibleToUser);
//        if (!isVisibleToUser) {
//            Log.e("second fragment", "隐藏了"); //隐藏
//        } else{
//            Log.e("second fragment", "显示了");//显示
//            refreshBleInfo(Global.isConnected);
//        }
//    }
    @OnClick({R.id.btn_select, R.id.btn_clear})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_select:
                if (DateFormatUtil.avoidFastClick(1000)){
                    Intent intent = new Intent();
                    intent.setClass(Objects.requireNonNull(getActivity()),ConnectDeviceActivity.class);
                    intent.putExtra(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectSetMode);
                    startActivity(intent);
                    Global.OpenConnectView = true;
                }
                break;
            case R.id.btn_clear:
                if (!Global.isConnected){
                    Toast.makeText(getContext(), "蓝牙鞋未连接", Toast.LENGTH_SHORT).show();
                    return;
                }
                startTask();
                if (dialog == null){
                    dialog = new RxClearZeroDialog(getContext());
                }
                dialog.setSureListener(v -> {
                    if (Global.isConnected){
                        String message = "set0";
                        BluetoothController.getInstance().writeRXCharacteristic(Global.ConnectedAddress,message.getBytes(StandardCharsets.UTF_8));
                        Toast.makeText(getContext(), "命令已发送", Toast.LENGTH_SHORT).show();
                    }else {
                        Toast.makeText(getContext(), "设备未连接", Toast.LENGTH_SHORT).show();
                    }
                    dialog.dismiss();
                    clearTimerTask();
                });
                dialog.setOnDismissListener(dialog -> clearTimerTask());
                dialog.show();
                break;
        }
    }

    @Override
    public void onDestroy() {
        clearTimerTask();
        super.onDestroy();
    }
}
