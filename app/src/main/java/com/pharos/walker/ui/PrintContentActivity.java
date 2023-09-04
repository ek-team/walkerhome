package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pharos.walker.BuildConfig;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.PrinterCommand;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.utils.AppUtils;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DeviceConnFactoryManager;
import com.pharos.walker.utils.PrintContent;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SaveViewUtil;
import com.pharos.walker.utils.ThreadPool;
import com.pharos.walker.utils.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

import static com.pharos.walker.utils.DeviceConnFactoryManager.ACTION_QUERY_PRINTER_STATE;
import static com.pharos.walker.utils.DeviceConnFactoryManager.CONN_STATE_FAILED;

public class PrintContentActivity extends BaseActivity {
    private static final int BLUETOOTH_REQUEST_CODE = 1001;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_logo)
    ImageView ivLogo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.edit_name)
    EditText editName;
    @BindView(R.id.edit_weight)
    EditText editWeight;
    @BindView(R.id.edit_age)
    EditText editAge;
    @BindView(R.id.edit_remark)
    EditText editRemark;
    @BindView(R.id.tv_evaluate_value)
    TextView tvEvaluateValue;
    @BindView(R.id.tv_evaluate_date)
    TextView tvEvaluateDate;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.chart)
    CombinedChart chart;
    @BindView(R.id.btn_printer)
    TextView btnPrinter;
    @BindView(R.id.tv_operation_name)
    TextView tvOperationName;
    @BindView(R.id.ll_print_content)
    ScrollView llPrintContent;
    @BindView(R.id.tv_hospital_title)
    TextView tvHospitalTitle;
    @BindView(R.id.tv_title)
    TextView tvTitle;
    @BindView(R.id.edit_department)
    EditText editDepartment;
    @BindView(R.id.btn_start_train)
    TextView btnStartTrain;
    private String picPath = "";
    /**
     * 判断打印机所使用指令是否是ESC指令
     */
    private int id = 0;
    private ThreadPool threadPool;
    /**
     * 连接状态断开
     */
    private static final int CONN_STATE_DISCONN = 0x007;
    /**
     * 使用打印机指令错误
     */
    private static final int PRINTER_COMMAND_ERROR = 0x008;
    private static final int CONN_PRINTER = 0x12;

    /**
     * ESC查询打印机实时状态指令
     */
    private byte[] esc = {0x10, 0x04, 0x02};
    private int axisFontSize = 18;
    private float firstValue = 0;
    private float secondValue = 0;
    private float thirdValue = 0;
    private int result = 0;
    private int vasValue = 1;
    private EvaluateEntity evaluateEntity;
    private int classType;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        Window window;
        window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        window.setAttributes(params);
        ButterKnife.bind(this);
        initData();
        initView();
        initBroadcast();
    }

    private void initData() {
        classType = getIntent().getIntExtra("class_type",0);
        evaluateEntity = getIntent().getParcelableExtra("EvaluateEntity");
        if (classType == 1){
            btnStartTrain.setVisibility(View.GONE);
            btnPrinter.setVisibility(View.GONE);
        }
        if (evaluateEntity != null){
            vasValue = evaluateEntity.getVas();
            result = evaluateEntity.getEvaluateResult();
            firstValue = evaluateEntity.getFirstValue();
            secondValue = evaluateEntity.getSecondValue();
            thirdValue = evaluateEntity.getThirdValue();
        }
    }

    /**
     * 注册广播
     * Registration broadcast
     */
    private void initBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_QUERY_PRINTER_STATE);//查询打印机缓冲区状态广播，用于一票一控
        filter.addAction(DeviceConnFactoryManager.ACTION_CONN_STATE);//与打印机连接状态
        registerReceiver(receiver, filter);
    }

    private void initView() {
        ivBack.setImageResource(R.drawable.ic_back_black);
        ivLogo.setVisibility(View.GONE);
        tvTime.setText("返回");
        tvTime.setTextColor(getResources().getColor(R.color.black));
        if (System.currentTimeMillis() > DateFormatUtil.getString2Date(SPHelper.getUser().getDate())) {
            tvDate.setText(SPHelper.getUser().getDate().substring(0,SPHelper.getUser().getDate().indexOf(" ")));
        } else {
            tvDate.setText("术前");
        }
//        String titleHospital = SPHelper.getSystemSettingHospitalName() + SPHelper.getSystemSettingDepartment();
//        tvHospitalTitle.setText(titleHospital);
        tvTitle.setText("负重评估报告单");
        tvEvaluateDate.setText(DateFormatUtil.getDate2String(evaluateEntity.getCreateDate(),null));
        editAge.setText(String.valueOf(SPHelper.getUser().getAge()));
        editName.setText(SPHelper.getUserName());
        editWeight.setText(MessageFormat.format("{0}", SPHelper.getUser().getWeight()));
        tvEvaluateValue.setText(MessageFormat.format("{0}kg ( VAS={1})", result, vasValue));
        tvOperationName.setText(SPHelper.getUser().getDiagnosis());
//        editDepartment.setText(SPHelper.getSystemSettingDepartment());
        setChart();
        setYAxis();
        initLegend();
        List<Float> valueList = new ArrayList<>();
        valueList.add(firstValue);
        valueList.add(secondValue);
        valueList.add(thirdValue);
        initChart(valueList);
    }

    private void setChart() {
        // 取消描述文字
        chart.getDescription().setEnabled(false);
        chart.setDoubleTapToZoomEnabled(false);//禁止双击放大
        // 透明背景
        chart.setBackgroundColor(Color.TRANSPARENT);
        // 没有数据时显示的文字
        chart.setNoDataText("暂无记录");
        // 没有数据时显示文字的颜色
        chart.setNoDataTextColor(Color.BLACK);
        // 绘图区后面的背景矩形将绘制
        chart.setDrawGridBackground(false);
        // 是否禁止绘制图表边框的线
        chart.setDrawBarShadow(false);
//        // 设置chart边框线的颜色。
//        chart.setBorderColor(Color.WHITE);
//        //设置 chart 边界线的宽度，单位 dp。
//        chart.setBorderWidth(1f);
        // 能否点击
        chart.setTouchEnabled(false);
        // 能否拖拽
        chart.setDragEnabled(false);
        // 能否缩放
        chart.setScaleXEnabled(false);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setScaleYEnabled(false);
        // 绘制动画 从下到上
        chart.animateY(1000);
        chart.setHighlightFullBarEnabled(false);
        chart.setExtraBottomOffset(10);//解决x轴底部显示被裁剪的问题
        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR
        });

        // 点击数据点弹出框
        ChartTodayMarkerView mv = new ChartTodayMarkerView(this, R.layout.chart_today_marker_view);
        mv.setChartView(chart); // For bounds control
    }

    private void setYAxis() {
        // 获取左y轴线
        YAxis leftAxis = chart.getAxisLeft();
        // 是否绘制轴线
        leftAxis.setDrawAxisLine(true);
        // 设置轴上每个点对应的线
        leftAxis.setDrawGridLines(false);
        // 绘制标签 指x轴上的对应数值
        leftAxis.setDrawLabels(true);
        // 设置文字大小
        leftAxis.setTextSize(axisFontSize);
//        leftAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        // 设置文字颜色
        leftAxis.setTextColor(getResources().getColor(R.color.black));
        //设置轴线颜色
        leftAxis.setAxisLineColor(getResources().getColor(R.color.black));
        //设置轴线宽度
        leftAxis.setAxisLineWidth(1f);
        leftAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "kg";
            }
        });
        leftAxis.setAxisMinimum(0f);

        // 获取左y轴线
        YAxis rightAxis = chart.getAxisRight();
        // 是否绘制轴线
        rightAxis.setDrawAxisLine(true);
        // 设置轴上每个点对应的线
        rightAxis.setDrawGridLines(false);
        // 绘制标签 指x轴上的对应数值
        rightAxis.setDrawLabels(true);
        // 设置文字大小
        rightAxis.setTextSize(axisFontSize);
//        rightAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        // 设置文字颜色
        rightAxis.setTextColor(getResources().getColor(R.color.black));
        //设置轴线颜色
        rightAxis.setAxisLineColor(getResources().getColor(R.color.black));
        //设置轴线宽度
        rightAxis.setAxisLineWidth(1f);
        rightAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "kg";
            }
        });
        rightAxis.setAxisMinimum(0f);
    }

    private void initLegend() {
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(22);
        l.setTextColor(getResources().getColor(R.color.black));
        l.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        l.setDrawInside(false);
        l.setEnabled(false);
    }

    private void initChart(List<Float> lists) {
        if (lists == null)
            return;
        // 获取x轴线
        XAxis xAxis = chart.getXAxis();
        // 是否绘制轴线
        xAxis.setDrawAxisLine(true);
        // 设置轴上每个点对应的线
        xAxis.setDrawGridLines(false);
        // 绘制标签 指x轴上的对应数值
        xAxis.setDrawLabels(true);
        // 设置x轴的显示位置
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        // 设置文字大小
        xAxis.setTextSize(axisFontSize);
//        xAxis.setTypeface(Typeface.DEFAULT_BOLD);//字体加粗
        // 设置文字颜色
        xAxis.setTextColor(getResources().getColor(R.color.black));
        // 设置轴的显示个数
        xAxis.setLabelCount(3);
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.black));
        //设置轴线宽度
        xAxis.setAxisLineWidth(1f);
//        xAxis.setLabelRotationAngle(2);//设置x轴字体显示角度
//        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= lists.size() || index < 0) {
                    return "";
                } else {
                    index = index + 1;
                    return "第" + index + "次";
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData(lists));
        data.setDrawValues(true);
        data.setValueTextColor(Color.BLACK);
        data.setValueTextSize(axisFontSize);
//        data.setValueTypeface(Typeface.DEFAULT_BOLD);
        data.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                return (int) value + "kg";
            }
        });
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }

    private BarData generateBarData(List<Float> lists) {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries1.add(new BarEntry(index + 0.5f, lists.get(index), lists.get(index)));
        }
        BarDataSet set1 = new BarDataSet(entries1, "评估负重");
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setColors(Color.rgb(220, 187, 94));

        BarData d = new BarData(set1);
        d.setBarWidth(0.3f);
        return d;
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_print_content;
    }

    @OnClick({R.id.iv_back, R.id.btn_printer, R.id.tv_time, R.id.btn_start_train})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.tv_time:
                if (classType == 1){
                    finish();
                }else {
                    startTargetActivity(MainActivity.class, true);
                }
                break;
            case R.id.btn_printer:
                if (!TextUtils.isEmpty(evaluateEntity.getRecordPath())){
                    picPath = evaluateEntity.getRecordPath();
                    startSharePrinter();
                }else {
                    showWaiting("打印提示","准备打印中…");
                    new Thread() {
                        @Override
                        public void run() {
                            saveRecordAsPdf();
                            mHandler.sendEmptyMessage(1001);

                        }
                    }.start();
                }


//                startSharePrinter();
//                if (TextUtils.isEmpty(picPath)){
//                    saveRecordAsPicture();
//                }
//                startActivityForResult(new Intent(this, PrinterBluetoothActivity.class), BLUETOOTH_REQUEST_CODE);
                break;
            case R.id.btn_start_train:
                if (Global.isConnected) {
                    startTargetActivity(TrainParamActivity.class, true);
                } else {
                    startTargetActivity(ConnectDeviceActivity.class, true);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == BLUETOOTH_REQUEST_CODE) {
            closeport();
            /*获取蓝牙mac地址*/
            String macAddress = null;
            if (data != null) {
                macAddress = data.getStringExtra(PrinterBluetoothActivity.EXTRA_DEVICE_ADDRESS);
            } else {
                return;
            }
            //初始化话DeviceConnFactoryManager
            new DeviceConnFactoryManager.Build()
                    .setId(id)
                    //设置连接方式
                    .setConnMethod(DeviceConnFactoryManager.CONN_METHOD.BLUETOOTH)
                    //设置连接的蓝牙mac地址
                    .setMacAddress(macAddress)
                    .build();
            //打开端口
            threadPool = ThreadPool.getInstantiation();
            threadPool.addSerialTask(new Runnable() {
                @Override
                public void run() {
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].openPort();
                }
            });
            showWaiting("打印提示", "打印中请稍等");

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 重新连接回收上次连接的对象，避免内存泄漏
     */
    private void closeport() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] != null && DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort != null) {
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].reader.cancel();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort.closePort();
            DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].mPort = null;
        }
    }

    /**
     * 打印票据
     */
    private void btnReceiptPrint() {
        if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id] == null || !DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getConnState()) {
            ToastUtils.showShort("请先连接打印机");
            return;
        }
        threadPool = ThreadPool.getInstantiation();
        threadPool.addSerialTask(new Runnable() {
            @Override
            public void run() {
                if (DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].getCurrentPrinterCommand() == PrinterCommand.ESC) {
                    ArrayList<String> stringList = new ArrayList<>();
                    stringList.add(SPHelper.getHospitalName());
                    stringList.add("下肢负重评估报告");
                    stringList.add("姓名：" + editName.getText().toString());
                    stringList.add("年龄：" + editAge.getText().toString());
                    stringList.add("体重：" + editWeight.getText().toString() + "kg");
                    stringList.add("评估结果：" + tvEvaluateValue.getText().toString());
                    stringList.add("手术时间：" + tvDate.getText().toString());
                    stringList.add("评估时间：" + tvEvaluateDate.getText().toString());
                    stringList.add("备注：" + editRemark.getText().toString());
                    stringList.add("手术名称：" + tvOperationName.getText().toString());
                    DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].sendDataImmediately(PrintContent.getReceipt(stringList, picPath));
                } else {
                    mHandler.obtainMessage(PRINTER_COMMAND_ERROR).sendToTarget();
                }
            }
        });
    }

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case CONN_STATE_DISCONN://断开连接
                    DeviceConnFactoryManager deviceConnFactoryManager = DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id];
                    if (deviceConnFactoryManager != null && deviceConnFactoryManager.getConnState()) {
                        DeviceConnFactoryManager.getDeviceConnFactoryManagers()[id].closePort(id);
                        ToastUtils.showShort("成功断开连接");
                    }
                    break;
                case PRINTER_COMMAND_ERROR://打印机指令错误
                    ToastUtils.showShort("请选择正确的打印机指令");
                    break;
                case CONN_PRINTER://未连接打印机
                    ToastUtils.showShort("请先连接打印机");
                    break;
                case 1001:
                    if (progressDialog!= null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    evaluateEntity.setRecordPath(picPath);
                    EvaluateManager.getInstance().insert(evaluateEntity);
                    startSharePrinter();
                    break;
            }
        }
    };
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            //连接状态
            if (DeviceConnFactoryManager.ACTION_CONN_STATE.equals(action)) {
                int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                int deviceId = intent.getIntExtra(DeviceConnFactoryManager.DEVICE_ID, -1);
                switch (state) {
                    case DeviceConnFactoryManager.CONN_STATE_DISCONNECT:

                        if (id == deviceId) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            ToastUtils.showShort("打印机未连接！");
                            Log.e("PrintContentActivity", "连接状态：未连接");
//                            tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                        }
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTING:
                        Log.e("PrintContentActivity", "连接状态：连接中");
//                        tvConnState.setText(getString(R.string.str_conn_state_connecting));
                        break;
                    case DeviceConnFactoryManager.CONN_STATE_CONNECTED:
                        Log.e("PrintContentActivity", "连接状态：已连接");
                        btnReceiptPrint();
//                        tvConnState.setText(getString(R.string.str_conn_state_connected) + "\n" + getConnDeviceInfo());
                        break;
                    case CONN_STATE_FAILED:
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort("打印机连接失败！");
                        Log.e("PrintContentActivity", "连接状态：连接失败");
                        //wificonn=false;
//                        tvConnState.setText(getString(R.string.str_conn_state_disconnect));
                        break;
                    default:
                        break;
                }
            } else if (ACTION_QUERY_PRINTER_STATE.equals(action)) {
                int state = intent.getIntExtra(DeviceConnFactoryManager.STATE, -1);
                Log.e("PrintContentActivity", "打印机状态：" + state);
                if (state == -1 && progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                    ToastUtils.showShort("打印完成");
                }
            }
        }
    };

    private void startSharePrinter() {
//        saveRecordAsPicture();
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_SEND);
        Uri uri = FileProvider.getUriForFile(this,
                BuildConfig.APPLICATION_ID + ".provider",
                new File(picPath));
//        Uri uri = Uri.parse(MediaStore.Images.Media.insertImage(getContentResolver(), bitmap, "IMG" + Calendar.getInstance().getTime(), null));
//        intent.setType("image/*");
        intent.setType("application/pdf");
        if (AppUtils.isAppInstalled("com.dynamixsoftware.printershare")) {
            intent.setPackage("com.dynamixsoftware.printershare");//趣打印插件
        }
        intent.putExtra(Intent.EXTRA_STREAM, uri);
        startActivity(Intent.createChooser(intent, "请选择打印服务"));
    }

    private void saveRecordAsPicture() {
        Bitmap bmp = SaveViewUtil.shotScrollView(llPrintContent);
        String path = null;
        String rootPath = Environment.getExternalStorageDirectory() + File.separator + "FLSReport" +
                File.separator;
        String userPath = SPHelper.getUserName() + "_" + SPHelper.getUserId() +
                File.separator;
        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File file1 = new File(rootPath + userPath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        try {
            path = file1.getAbsolutePath() + File.separator + DateFormatUtil.getNowDate() + "_评估报告 " + ".jpg";
            File e = new File(path);
            FileOutputStream fos;
            if (e.exists()) {
                e.delete();
            }
            fos = new FileOutputStream(e);
            bmp.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        picPath = path;
        Log.i("path", path);
    }
    private void saveRecordAsPdf(){
        View root = llPrintContent.getChildAt(0);
        PdfDocument document = new PdfDocument();
        PdfDocument.PageInfo pageInfo = new PdfDocument.PageInfo.Builder(root.getWidth()+50, root.getHeight(), 1).create();
        PdfDocument.Page page = document.startPage(pageInfo);
        root.draw(page.getCanvas());//3
        document.finishPage(page);
        String path = null;
        String rootPath = Environment.getExternalStorageDirectory() + File.separator + "FLSReport" +
                File.separator;
        String userPath = SPHelper.getUserName() + "_" + SPHelper.getUserId() +
                File.separator;
        File file = new File(rootPath);
        if (!file.exists()) {
            file.mkdir();
        }
        File file1 = new File(rootPath + userPath);
        if (!file1.exists()) {
            file1.mkdir();
        }
        try {
            path = file1.getAbsolutePath() + File.separator + DateFormatUtil.getNowDate() + "_评估报告" + ".pdf";
            File e = new File(path);
            if (e.exists()) {
                e.delete();
            }
            document.writeTo(new FileOutputStream(e));
        } catch (IOException e) {
            e.printStackTrace();
        }
        document.close();
        picPath = path;
        Log.i("path", path);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
        DeviceConnFactoryManager.closeAllPort();
        if (threadPool != null) {
            threadPool.stopThreadPool();
        }
    }

}
