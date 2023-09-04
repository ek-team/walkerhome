package com.pharos.walker.fragment;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.charts.CombinedChart;
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
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.pharos.walker.R;
import com.pharos.walker.adapter.EvaluateRecordAdapter;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.customview.ChartTodayMarkerView;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.ui.PrintContentActivity;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SPHelper;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class EvaluateRecordFragment extends Fragment {
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.tv_gender)
    TextView tvGender;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.rv_sheet)
    RecyclerView rvSheet;
    @BindView(R.id.chart)
    CombinedChart chart;
    private List<EvaluateEntity> evaluateEntityList;
    private EvaluateRecordAdapter evaluateRecordAdapter;
    public static int catRecord = 0;
    public static int generateRecord = 1;
    public static int printRecord = 2;
    private int axisFontSize = 22;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_evaluate_record, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initData();
        initView();
        return contentView;
    }

    private void initData() {
        evaluateEntityList = EvaluateManager.getInstance().loadAll(SPHelper.getUserId());
    }

    private void initView() {
        tvName.setText(MessageFormat.format("姓名：{0}", SPHelper.getUserName()));
        tvAge.setText(MessageFormat.format("年龄：{0}", SPHelper.getUser().getAge()));
        if (System.currentTimeMillis() > DateFormatUtil.getString2Date(SPHelper.getUser().getDate())) {
            tvDate.setText(MessageFormat.format("手术时间：{0}", SPHelper.getUser().getDate().substring(0, SPHelper.getUser().getDate().indexOf(" "))));
        } else {
            tvDate.setText("术前");
        }
//        if (SPHelper.getUser().getSex() == 0){
//            tvGender.setText("性别：女");
//        }else {
//            tvGender.setText("性别：男");
//        }
        rvSheet.setLayoutManager(new LinearLayoutManager(getContext()));
        //设置recyclerView每个item间的分割线
        DividerItemDecoration decoration = new DividerItemDecoration(Objects.requireNonNull(getContext()), DividerItemDecoration.VERTICAL);
        Drawable drawable = getResources().getDrawable(R.drawable.recycleview_divider_shape);
        decoration.setDrawable(drawable);
        rvSheet.addItemDecoration(decoration);
        evaluateRecordAdapter = new EvaluateRecordAdapter(evaluateEntityList);
        rvSheet.setAdapter(evaluateRecordAdapter);
        evaluateRecordAdapter.setOnItemClickListener((view, position, viewType, lastSelected) -> {
            if (viewType == catRecord) {
                TextView textView = (TextView) view;
                textView.setTextColor(getResources().getColor(R.color.white));
                //初始化上次点击的状态
                if (lastSelected != -1 && lastSelected != position){
                    LinearLayout linearLayout = (LinearLayout) rvSheet.getLayoutManager().findViewByPosition(lastSelected);
                    TextView lastTextView = (TextView) linearLayout.findViewById(R.id.tv_sheetRow4);
                    lastTextView.setTextColor(getResources().getColor(R.color.colorPrimary));
                }
                Intent intent = new Intent();
                intent.putExtra("EvaluateEntity",evaluateEntityList.get(position));
                intent.putExtra("class_type",1);
                intent.setClass(getContext(), PrintContentActivity.class);
                startActivity(intent);
            }
        });
        setChart();
        setYAxis();
        initLegend();
        List<Float> valueList = new ArrayList<>();
        for (EvaluateEntity entity: evaluateEntityList){
            valueList.add((float) entity.getEvaluateResult());
        }
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
                CombinedChart.DrawOrder.LINE
        });

        // 点击数据点弹出框
        ChartTodayMarkerView mv = new ChartTodayMarkerView(getActivity(), R.layout.chart_today_marker_view);
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
        leftAxis.setTextColor(getResources().getColor(R.color.white));
        //设置轴线颜色
        leftAxis.setAxisLineColor(getResources().getColor(R.color.white));
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
        rightAxis.setTextColor(getResources().getColor(R.color.white));
        //设置轴线颜色
        rightAxis.setAxisLineColor(getResources().getColor(R.color.white));
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
        l.setTextColor(getResources().getColor(R.color.white));
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
        xAxis.setTextColor(getResources().getColor(R.color.white));
        // 设置轴的显示个数
        xAxis.setLabelCount(5);
        // 图表将避免第一个和最后一个标签条目被减掉在图表或屏幕的边缘
        xAxis.setAvoidFirstLastClipping(false);
        xAxis.setGranularity(1f);
        xAxis.setCenterAxisLabels(true);//设置标签居中
        //设置轴线颜色
        xAxis.setAxisLineColor(getResources().getColor(R.color.white));
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
        data.setData(generateLineData(lists));
        data.setDrawValues(true);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(18);
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
    private LineData generateLineData(List<Float> lists) {
        LineData d = new LineData();
        ArrayList<Entry> entries = new ArrayList<>();
        for (int index = 0; index < lists.size(); index++) {
            entries.add(new Entry(index + 0.5f, lists.get(index), lists.get(index)));
        }
        LineDataSet set = new LineDataSet(entries, "评估值");
        set.setColor(Color.rgb(7, 190, 170));
        set.setLineWidth(2f);
        set.setDrawCircles(true);
        set.setDrawValues(true);
        set.setValueTextColor(getActivity().getResources().getColor(R.color.white));
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        d.addDataSet(set);
        return d;
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
}
