package com.pharos.walker.customview;

import android.app.Activity;
import android.widget.TextView;

import com.github.mikephil.charting.components.MarkerView;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.utils.MPPointF;
import com.pharos.walker.R;
import com.pharos.walker.beans.ChartRecordBean;

import java.text.MessageFormat;

/**
 * Custom implementation of the MarkerView.
 *
 * @author Philipp Jahoda
 */
public class ChartTodayMarkerView extends MarkerView {

    private Activity mContext;
    private TextView tv_weight;
    private TextView tv_avg_weight;
    private TextView tv_detail;

    public ChartTodayMarkerView(Activity context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tv_weight = findViewById(R.id.tv_weight);
        tv_avg_weight = findViewById(R.id.tv_avg_weight);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        ChartRecordBean.NumOfTimeBean bean = (ChartRecordBean.NumOfTimeBean) e.getData();
        tv_weight.setText(MessageFormat.format("{0} kg", bean.getTargetWeight()));
        tv_avg_weight.setText(MessageFormat.format("{0} kg", bean.getAverageWeight()));
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
