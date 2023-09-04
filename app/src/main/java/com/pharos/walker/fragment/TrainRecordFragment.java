package com.pharos.walker.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

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
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.ScatterData;
import com.github.mikephil.charting.data.ScatterDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.pharos.walker.R;
import com.pharos.walker.beans.ChartRecordBean;
import com.pharos.walker.customview.ChartMarkerView;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.ui.ThatDayRecordActivity;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SqlToExcleUtil;
import com.pharos.walker.utils.ToastUtils;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/5/10
 * Describe:
 */
public class TrainRecordFragment extends Fragment {
    @BindView(R.id.chart)
    CombinedChart chart;
    @BindView(R.id.btn_save_record)
    TextView btnSaveRecord;
    private List<ChartRecordBean> chartRecordBeanList;
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_train_record, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initData();
        initView();
        return contentView;
    }
    private void initData() {
        chartRecordBeanList = UserTrainRecordManager.getInstance().getChartData(SPHelper.getUserId());
    }

    private void initView() {
        setChart();
        setYAxis();
        initLegend();
        if (chartRecordBeanList != null && chartRecordBeanList.size() > 0){
            initChart(chartRecordBeanList);
        }

    }

    private void initLegend(){
        Legend l = chart.getLegend();
        l.setWordWrapEnabled(true);
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setTextSize(16);
        l.setTextColor(getResources().getColor(R.color.white_88));
        l.setDrawInside(false);
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
        chart.setExtraBottomOffset(10f);

        // draw bars behind lines
        chart.setDrawOrder(new CombinedChart.DrawOrder[]{
                CombinedChart.DrawOrder.BAR, CombinedChart.DrawOrder.LINE, CombinedChart.DrawOrder.SCATTER
        });

        // 点击数据点弹出框
        ChartMarkerView mv = new ChartMarkerView(getActivity(), R.layout.chart_marker_view);
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv);
        chart.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            private ChartRecordBean chartRecordBean;
            @Override
            public void onValueSelected(Entry e, Highlight h) {
                chartRecordBean = (ChartRecordBean)e.getData();
            }

            @Override
            public void onNothingSelected() {
                Intent intent = new Intent();
                intent.setClass(getActivity(), ThatDayRecordActivity.class);
                intent.putExtra("Chart_Record_Bean",chartRecordBean);
                startActivity(intent);

            }
        });
    }
    private void setYAxis(){
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
                return (int)value + "";
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
                return (int)value + "kg";
            }
        });
        rightAxis.setAxisMinimum(0f);
    }

    private void initChart(List<ChartRecordBean> lists){
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
                int index = (int)value;
                if (index >= lists.size() || index < 0){
                    return "";
                }else {
                    String date = lists.get(index).getDate();
                    String[] dates = TextUtils.split(date, "-");
                    return dates[1] + "-" + dates[2];
                }
            }
        });

        CombinedData data = new CombinedData();
        data.setData(generateLineData(lists));
        data.setData(generateBarData(lists));
        data.setData(generateBubbleData(lists));
//        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMaximum(data.getXMax() + 0.25f);
        xAxis.setAxisMinimum(0);
//        xAxis.setCenterAxisLabels(true);
        chart.setData(data);
        chart.invalidate();
    }
    private LineData generateLineData(List<ChartRecordBean> lists) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries.add(new Entry(index + 0.3f, lists.get(index).getPainLevel(), lists.get(index)));
        }
        LineDataSet set = new LineDataSet(entries, "VAS反馈");
        set.setColor(Color.rgb(7, 190, 170));
        set.setLineWidth(3f);
        set.setDrawCircles(false);
        set.setDrawValues(true);
        set.setValueTextColor(getActivity().getResources().getColor(R.color.white));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.addDataSet(set);
        return d;
    }
    private BarData generateBarData(List<ChartRecordBean> lists) {
        ArrayList<BarEntry> entries1 = new ArrayList<>();
        ArrayList<BarEntry> entries2 = new ArrayList<>();

        for (int index = 0; index < lists.size(); index++) {
            entries1.add(new BarEntry(index + 0.3f ,lists.get(index).getTargetWeight(), lists.get(index)));
            entries2.add(new BarEntry(index + 0.6f,lists.get(index).getAverageWeight(), lists.get(index)));
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
//        float groupSpace = 0.2f;
//        float barSpace = 0.05f;
//        d.groupBars(0.2f, groupSpace, barSpace);
        return d;
    }
    private ScatterData generateBubbleData(List<ChartRecordBean> lists) {
        ScatterData bd = new ScatterData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            int errorFeedback = lists.get(index).getPainFeedback();
            if (errorFeedback > 0) {
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
//    @Override
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
