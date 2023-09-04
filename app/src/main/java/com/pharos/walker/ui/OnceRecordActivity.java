package com.pharos.walker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.dx.command.Main;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.ChartRecordBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.ChartOnceMarkerView;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/5/24
 * Describe:
 */
public class OnceRecordActivity extends BaseActivity {
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
    private List<TrainDataEntity> trainDataEntityList;
    private String searchDate;
    private int frequency;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        ChartRecordBean.NumOfTimeBean numOfTimeBean = null;
        if (bundle != null) {
            numOfTimeBean = bundle.getParcelable("Num_Of_Time_Bean");
            trainDataEntityList = bundle.getParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY);
            tvReturnLastPage.setVisibility(View.GONE);
        }
        if (numOfTimeBean != null) {
            tvDate.setText(MessageFormat.format("第{0}次", numOfTimeBean.getFrequency()));
            searchDate = numOfTimeBean.getDateSte();
            frequency = numOfTimeBean.getFrequency() - 1;
            trainDataEntityList = TrainDataManager.getInstance().getTrainDataByDateAndFrequency(SPHelper.getUserId(), searchDate, frequency);
            tvReturnLastPage.setVisibility(View.VISIBLE);
        }
    }

    private void initView() {
        setChart();
        setYAxis();
        initLegend();
        if (trainDataEntityList != null && trainDataEntityList.size() > 0) {
            initChart(trainDataEntityList);
            setChartDatas(trainDataEntityList);
        }
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
                CombinedChart.DrawOrder.BAR
        });

        // 点击数据点弹出框
        ChartOnceMarkerView mv = new ChartOnceMarkerView(this, R.layout.chart_once_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);
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

    private void initChart(List<TrainDataEntity> lists) {
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
                    index = index + 1;
                    return "第" + index + "踩";
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateBarData(lists));
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }

    private void setChartDatas(List<TrainDataEntity> lists) {
        //---------基准线-------------
        if (lists != null && lists.size() > 0) {
            YAxis rightAxis = chart.getAxisRight();
            rightAxis.removeAllLimitLines();

            LimitLine ll1 = new LimitLine(lists.get(0).getTargetLoad() * 0.8f, "最小负重");
            ll1.setLineWidth(2f);
            ll1.enableDashedLine(10f, 10f, 0f);
            ll1.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            if (Global.isChangSha) {
                ll1.setLineColor(Color.GREEN);
                ll1.setTextColor(Color.GREEN);
            } else {
                ll1.setLineColor(Color.YELLOW);
                ll1.setTextColor(Color.YELLOW);
            }
            ll1.setTextSize(20f);
            rightAxis.addLimitLine(ll1);

            LimitLine ll2 = new LimitLine(lists.get(0).getTargetLoad(), "目标负重");
            ll2.setLineWidth(2f);
            ll2.enableDashedLine(10f, 10f, 0f);
            if (Global.isChangSha) {
                ll2.setLineColor(Color.YELLOW);
                ll2.setTextColor(Color.YELLOW);
            } else {
                ll2.setLineColor(Color.GREEN);
                ll2.setTextColor(Color.GREEN);
            }
            ll2.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll2.setTextSize(20f);
            rightAxis.addLimitLine(ll2);

            LimitLine ll3 = new LimitLine(lists.get(0).getTargetLoad() * 1.2f, "最大负重");
            ll3.setLineWidth(2f);
            ll3.enableDashedLine(10f, 10f, 0f);
            ll3.setLineColor(Color.RED);
            ll3.setLabelPosition(LimitLine.LimitLabelPosition.RIGHT_BOTTOM);
            ll3.setTextColor(Color.RED);
            ll3.setTextSize(20f);
            rightAxis.addLimitLine(ll3);
            initChart(lists);
        }
    }

    private BarData generateBarData(List<TrainDataEntity> lists) {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries1.add(new BarEntry(index + 0.6f, lists.get(index).getRealLoad(), lists.get(index)));
        }
        BarDataSet set1 = new BarDataSet(entries1, "实际负重");
        set1.setDrawValues(false);
        set1.setAxisDependency(YAxis.AxisDependency.RIGHT);
        set1.setColors(Color.rgb(216, 98, 42));

        BarData d = new BarData(set1);
        d.setBarWidth(0.2f);
        return d;
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
    protected int getLayoutResId() {
        return R.layout.activity_that_day_record;
    }

    @OnClick({R.id.tv_date,R.id.img_back, R.id.tv_back, R.id.tv_return_last_page})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_back:
            case R.id.img_back:
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.tv_return_last_page:
                finish();
                break;
        }
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

}
