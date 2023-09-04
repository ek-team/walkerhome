package com.pharos.walker.ui;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.ScatterChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.customview.MyScatterDataSet;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;

public class CommitResultActivity extends Activity {
    @BindView(R.id.tv_train_result)
    TextView tvTrainResult;
    @BindView(R.id.tv_countdown)
    TextView tvCountdown;
    @BindView(R.id.chart)
    CombinedChart chart;
    private int axisFontSize = 15;
    private int totalTime = 5;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
//        Window window;
//        window = getWindow();
//        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
//        window.setAttributes(params);
        setContentView(R.layout.activity_home_train_data_commit_result);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        setChart();
        setYAxis();
        initLegend();
        List<Float> valueList = new ArrayList<>();
        valueList.add(3f);
        valueList.add(2f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        valueList.add(3f);
        initChart(valueList);
        countDownThread();
    }
    private void setChart(){
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
        chart.animateX(2000);
        chart.setExtraBottomOffset(10);//解决x轴底部显示被裁剪的问题
        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.SCATTER
        });

        // 点击数据点弹出框
        ChartTodayMarkerView mv = new ChartTodayMarkerView(this, R.layout.chart_today_marker_view);
        mv.setChartView(chart); // For bounds control
    }
    private void setYAxis(){
        // 获取左y轴线
        YAxis leftAxis = chart.getAxisLeft();
        // 是否绘制轴线
        leftAxis.setDrawAxisLine(false);
        // 设置轴上每个点对应的线
        leftAxis.setDrawGridLines(false);
        // 绘制标签 指x轴上的对应数值
        leftAxis.setDrawLabels(false);
//        // 设置文字大小
//        leftAxis.setTextSize(axisFontSize);
//        // 设置文字颜色
//        leftAxis.setTextColor(getResources().getColor(R.color.white));
//        //设置轴线颜色
//        leftAxis.setAxisLineColor(getResources().getColor(R.color.white));
//        //设置轴线宽度
//        leftAxis.setAxisLineWidth(1f);
//        leftAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return (int)value + "kg";
//            }
//        });
        leftAxis.setAxisMinimum(0f);

        // 获取左y轴线
        YAxis rightAxis = chart.getAxisRight();
        // 是否绘制轴线
        rightAxis.setDrawAxisLine(false);
        // 设置轴上每个点对应的线
        rightAxis.setDrawGridLines(false);
        // 绘制标签 指x轴上的对应数值
//        rightAxis.setDrawLabels(true);
//        // 设置文字大小
//        rightAxis.setTextSize(axisFontSize);
//        // 设置文字颜色
//        rightAxis.setTextColor(getResources().getColor(R.color.white));
//        //设置轴线颜色
//        rightAxis.setAxisLineColor(getResources().getColor(R.color.white));
//        //设置轴线宽度
//        rightAxis.setAxisLineWidth(1f);
//        rightAxis.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return (int)value + "kg";
//            }
//        });
        rightAxis.setAxisMinimum(0f);
    }
    private void initLegend(){
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(22);
        l.setTextColor(getResources().getColor(R.color.black));
        l.setDrawInside(false);
        l.setEnabled(false);
    }
    private void initChart(List<Float> lists){
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
        // 设置文字颜色
        xAxis.setTextColor(getResources().getColor(R.color.white));
        // 设置轴的显示个数
        xAxis.setLabelCount(lists.size());
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
        //设置轴线宽度
        xAxis.setAxisLineWidth(1f);
        xAxis.setLabelRotationAngle(90);//设置x轴字体显示角度
//        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int)value;
                if (index >= lists.size() || index < 0){
                    return "";
                }else {
                    index = index +1;
                    return "第" + index + "天";
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData(lists));
//        data.setDrawValues(true);
//        data.setValueTextColor(Color.WHITE);
//        data.setValueTextSize(axisFontSize);
//        data.setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                return (int)value + "kg";
//            }
//        });
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }
    private ScatterData generateBarData(List<Float> lists) {
        ArrayList<Entry> entries1 = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries1.add(new Entry(index + 0.5f , lists.get(index), lists.get(index)));
        }
        MyScatterDataSet scatterDataSet = new MyScatterDataSet(entries1, "评估负重");
        scatterDataSet.setDrawValues(false);
        scatterDataSet.setAxisDependency(YAxis.AxisDependency.RIGHT);
        List<Integer> colors = new ArrayList<>();
        colors.add(Color.GREEN);
        colors.add(Color.RED);
//        scatterDataSet.setColors(Color.rgb(220, 187, 94));
        scatterDataSet.setColors(colors);
        scatterDataSet.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        scatterDataSet.setScatterShapeSize(10);
        ScatterData set1 = new ScatterData(scatterDataSet);
        return set1;
    }
    private Timer mTimer1;
    private TimerTask mTimerTask1;

    private void countDownThread() {
        if (mTimer1 == null && mTimerTask1 == null) {
            mTimer1 = new Timer();
            mTimerTask1 = new TimerTask() {
                @Override
                public void run() {
                    totalTime--;
                    if (totalTime <= 0) {
                        mTimer1.cancel();
                        mTimerTask1.cancel();
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_COUNTDOWN, totalTime));
                }
            };
            mTimer1.schedule(mTimerTask1, 0, 1000);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:

                break;
            case MessageEvent.ACTION_COUNTDOWN:
                int totalTime = (int) event.getData();
                tvCountdown.setText(MessageFormat.format("倒计时：{0}", totalTime));
                if (totalTime <= 0){
                    finish();
                }
                break;
            default:
                break;
        }
    }
    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        if (mTimer1 != null) {
            mTimer1.cancel();
            mTimerTask1.cancel();
            mTimer1 = null;
            mTimerTask1 = null;
        }
        super.onDestroy();
    }
}
