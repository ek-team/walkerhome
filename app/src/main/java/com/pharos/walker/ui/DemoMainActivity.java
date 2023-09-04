package com.pharos.walker.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.R;
import com.pharos.walker.adapter.BleDevAdapter;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.utils.DataTransformUtil;
import com.pharos.walker.utils.DateFormatUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class DemoMainActivity extends AppCompatActivity {

    @BindView(R.id.btn_reScan)
    Button btnReScan;
    @BindView(R.id.rv_bt)
    RecyclerView rvBt;
    @BindView(R.id.tv_ble_data)
    TextView tvBleData;
    @BindView(R.id.chart)
    LineChart chart;
    private BtTemperatureReceiver broadcastReceiver;
    private BleDevAdapter bleDevAdapter;
    private HashMap<String, String> bleMap = new HashMap<>();
    private Timer mTimer;
    private TimerTask mTimerTask;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo_main);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        // Android 动态请求权限 Android 10 需要单独处理
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                String[] strings =
                        {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
        initView();
        initData();
    }

    private void initData() {
        List<Entry> dataList1 = new ArrayList<>();
        ArrayList<ILineDataSet> dataSets = new ArrayList<>();   //线条数据集合
        dataList1.add(new Entry(0,0));
        LineDataSet dataSet1 = new LineDataSet(dataList1, "实时数据");
        dataSet1.setLineWidth(2f);
        dataSet1.setColor(Color.parseColor("#FF0000"));
        dataSet1.setDrawCircles(false);
        dataSet1.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);
        dataSet1.setDrawValues(false);
        dataSets.add(dataSet1);
        LineData data = new LineData(dataSets);
        chart.setData(data);
        chart.invalidate();
    }

    private void initView() {
        initBtState();
        bleDevAdapter = new BleDevAdapter(new ArrayList<BleBean>());
        rvBt.setLayoutManager(new LinearLayoutManager(this));
        rvBt.setAdapter(bleDevAdapter);
        bleDevAdapter.setOnItemClickListener(new BleDevAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BleBean bleBean) {
                if (bleBean.getConnectState() == 0 || Global.ConnectStatus.equals("connecting")) {
                    Toast.makeText(DemoMainActivity.this, getString(R.string.ble_connecting), Toast.LENGTH_SHORT).show();
                    return;
                }
                //如果G.Left_hand和G.Right_hand的值不为null说明存在已经连接了的蓝牙设备，需要将其先断开
                if (Global.ConnectedAddress != null) {
                    clearTimerTask();
                    BluetoothController.getInstance().closeGatt(Global.ConnectedAddress);
                }
                Global.ConnectedAddress = bleBean.getAddress();
                Global.ConnectedName = bleBean.getName();
                Global.ConnectStatus = "connecting";
                bleDevAdapter.setSelect(bleBean);
                BluetoothController.getInstance().connect(bleBean.getAddress());
            }
        });
        setChartStyle();
    }
    private void setChartStyle(){
        //不显示描述内容
        chart.getDescription().setEnabled(false);
        //设置样式
        YAxis rightAxis = chart.getAxisRight();
        //设置图表右边的y轴禁用
        rightAxis.setEnabled(false);
//        rightAxis.setDrawZeroLine(true);
        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTextSize(21f);
        leftAxis.setAxisMinimum(0f);
        //设置x轴
        XAxis xAxis = chart.getXAxis();
        xAxis.setTextColor(Color.parseColor("#333333"));
        chart.getAxisLeft().setDrawGridLines(false);
        chart.getAxisRight().setDrawGridLines(false);
        xAxis.setTextSize(21f);
        xAxis.setAxisMinimum(1f);
        xAxis.setDrawAxisLine(true);//是否绘制轴线
        xAxis.setDrawGridLines(false);//设置x轴上每个点对应的线
        xAxis.setDrawLabels(true);//绘制标签  指x轴上的对应数值
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);//设置x轴的显示位置
        xAxis.setGranularity(1f);//禁止放大后x轴标签重绘
    }
    private void addEntry(float yValue){
        LineData data = chart.getData();
        //第0条折线
        LineDataSet dataSet1 = (LineDataSet) data.getDataSetByIndex(0);
        data.addEntry(new Entry(dataSet1.getEntryCount(), yValue), 0);
        chart.setVisibleXRangeMaximum(100);
        chart.notifyDataSetChanged();
        chart.moveViewToX(data.getEntryCount() - 5);
    }
    @OnClick(R.id.btn_reScan)
    public void onViewClicked() {
        BluetoothController.getInstance().close();
        bleDevAdapter.clear();
        bleMap.clear();
        if (mTimer != null){
            mTimer.cancel();
            mTimerTask.cancel();
            mTimer = null;
            mTimerTask = null;
        }
        BluetoothController.getInstance().startScanBle();

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
    /**
     * 初始化蓝牙状态广播监听
     */
    private void initBtState(){
        broadcastReceiver = new BtTemperatureReceiver();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        registerReceiver(broadcastReceiver,intentFilter);
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.BLE_SCAN_RESULT:
                bleBean = (BleBean) event.getData();
                String name = bleBean.getName();
                String address = bleBean.getAddress();
                int rssi = bleBean.getRssi();
                if (!bleMap.containsKey(address)) {
                    bleMap.put(address, address);
                    BleBean bean = new BleBean(name, address, -1,rssi);
                    bleDevAdapter.addData(bean);
                }else if (DateFormatUtil.setTimeInterval(1000)){
                    bleDevAdapter.changeData(bleBean);
                }
                break;
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                for (BleBean bean : bleDevAdapter.getData()) {
                    if (bleBean.getAddress().equalsIgnoreCase(bean.getAddress()) && bean.getConnectState() != 1) {
                        bean.setConnectState(1);
                        Global.ConnectStatus = "connected";
                        bleDevAdapter.notifyDataSetChanged();
                        break;
                    }
                }
//                BluetoothController.getInstance().enableTXNotification(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
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
                clearTimerTask();
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.ACTION_READ_DATA:
                bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                tvBleData.setText(MessageFormat.format("重量：{0}kg", value));
                addEntry(Float.valueOf(value));
                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                startTask();
                break;
            default:
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
        BluetoothController.getInstance().stopScanBle();
        unregisterReceiver(broadcastReceiver);//注销广播接收器
        EventBus.getDefault().unregister(this);
        super.onDestroy();
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
                        Toast.makeText(DemoMainActivity.this,"蓝牙正在开启……，请稍等",Toast.LENGTH_SHORT).show();
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
