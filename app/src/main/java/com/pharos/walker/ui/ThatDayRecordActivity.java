package com.pharos.walker.ui;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dx.command.Main;
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
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.ChartRecordBean;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.ChartTodayMarkerView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/5/24
 * Describe:
 */
public class ThatDayRecordActivity extends BaseActivity {
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.chart)
    CombinedChart chart;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tv_return_last_page)
    TextView tvReturnLastPage;
    private List<ChartRecordBean.NumOfTimeBean> numOfTimeBeanList;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        ChartRecordBean chartRecordBean = null;
        if (bundle != null) {
            chartRecordBean = bundle.getParcelable("Chart_Record_Bean");
        }
        if (chartRecordBean != null) {
            tvDate.setText(chartRecordBean.getDate());
            numOfTimeBeanList = chartRecordBean.getNumOfTimeBeanList();
        }
    }

    private void initView() {
        setChart();
        setYAxis();
        initLegend();
        initChart(numOfTimeBeanList);
    }

    private void initLegend() {
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(16);
        l.setTextColor(getResources().getColor(R.color.white_88));
        l.setDrawInside(false);
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
        chart.setNoDataTextColor(Color.WHITE);
        // 绘图区后面的背景矩形将绘制
        chart.setDrawGridBackground(false);
        // 是否禁止绘制图表边框的线
        chart.setDrawBarShadow(false);
//        // 设置chart边框线的颜色。
//        chart.setBorderColor(Color.WHITE);
//        //设置 chart 边界线的宽度，单位 dp。
//        chart.setBorderWidth(1f);
        // 能否点击
        chart.setTouchEnabled(true);
        // 能否拖拽
        chart.setDragEnabled(true);
        // 能否缩放
        chart.setScaleXEnabled(true);
        chart.setAutoScaleMinMaxEnabled(true);
        chart.setScaleYEnabled(false);
        // 绘制动画 从下到上
        chart.animateY(1000);
        chart.setHighlightFullBarEnabled(false);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.SCATTER
        });

        // 点击数据点弹出框
        ChartTodayMarkerView mv = new ChartTodayMarkerView(this, R.layout.chart_today_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            private ChartRecordBean.NumOfTimeBean numOfTimeBean;

            @Override
            public void onValueSelected(Entry e, Highlight h) {
                numOfTimeBean = (ChartRecordBean.NumOfTimeBean) e.getData();
            }

            @Override
            public void onNothingSelected() {
                Intent intent = new Intent();
                intent.setClass(ThatDayRecordActivity.this, OnceRecordActivity.class);
                intent.putExtra("Num_Of_Time_Bean", numOfTimeBean);
                startActivity(intent);

            }
        });
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
        leftAxis.setTextSize(18);
        // 设置文字颜色
        leftAxis.setTextColor(getResources().getColor(R.color.white_66));
        //设置轴线颜色
        leftAxis.setAxisLineColor(getResources().getColor(R.color.axis_color));
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
        rightAxis.setTextSize(18);
        // 设置文字颜色
        rightAxis.setTextColor(getResources().getColor(R.color.white_66));
        //设置轴线颜色
        rightAxis.setAxisLineColor(getResources().getColor(R.color.axis_color));
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

    private void initChart(List<ChartRecordBean.NumOfTimeBean> lists) {
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
        xAxis.setTextSize(20);
        // 设置文字颜色
        xAxis.setTextColor(getResources().getColor(R.color.white));
        // 设置轴的显示个数
        xAxis.setLabelCount(10);
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.axis_color));
        //设置轴线宽度
        xAxis.setAxisLineWidth(1f);
//        xAxis.setCenterAxisLabels(true);
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= lists.size() || index < 0) {
                    return "";
                } else {
                    return "第" + lists.get(index).getFrequency() + "次";
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData(lists));
        data.setData(generateBubbleData(lists));
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }

    private BarData generateBarData(List<ChartRecordBean.NumOfTimeBean> lists) {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries1.add(new BarEntry(index + 0.3f, lists.get(index).getTargetWeight(), lists.get(index)));
            entries2.add(new BarEntry(index + 0.6f, lists.get(index).getAverageWeight(), lists.get(index)));
        }

        BarDataSet set1 = new BarDataSet(entries1, "目标负重");
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setColors(Color.rgb(220, 187, 94));

        BarDataSet set2 = new BarDataSet(entries2, "实际负重");
        set2.setDrawValues(false);
        set2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set2.setColors(Color.rgb(216, 98, 42));

        BarData d = new BarData(set1, set2);
        d.setBarWidth(0.2f);
        return d;
    }
    private ScatterData generateBubbleData(List<ChartRecordBean.NumOfTimeBean> lists) {
        ScatterData bd = new ScatterData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            boolean isPainError = lists.get(index).isPainError();
            if (isPainError) {
                entries.add(new Entry(index + 0.6f, (lists.get(index).getAverageWeight()) + 1f, lists.get(index)));
            }
        }

        ScatterDataSet set = new ScatterDataSet(entries, "异常反馈");
        set.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set.setScatterShape(ScatterChart.ScatterShape.CIRCLE);
        set.setColors(Color.RED);
        set.setDrawValues(false);
        set.setFormSize(2);
        bd.addDataSet(set);
        return bd;
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_that_day_record;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            default:
                break;
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }



    @OnClick({R.id.img_back, R.id.tv_back, R.id.tv_return_last_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.tv_back:
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.tv_return_last_page:
                finish();
                break;
        }
    }
}
