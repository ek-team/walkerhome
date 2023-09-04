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
public class ChartMarkerView extends MarkerView {

    private Activity mContext;
    private TextView tv_level;
    private TextView tv_weight;
    private TextView tv_avg_weight;
    private TextView tv_feedback;

    public ChartMarkerView(Activity context, int layoutResource) {
        super(context, layoutResource);
        mContext = context;
        tv_level = findViewById(R.id.tv_level);
        tv_weight = findViewById(R.id.tv_weight);
        tv_avg_weight = findViewById(R.id.tv_avg_weight);
        tv_feedback = findViewById(R.id.tv_feedback);
    }

    @Override
    public void refreshContent(Entry e, Highlight highlight) {
        ChartRecordBean bean = (ChartRecordBean) e.getData();
        tv_level.setText(bean.getClassId()+"");
        tv_weight.setText(MessageFormat.format("{0} kg", bean.getTargetWeight()));
        tv_avg_weight.setText(MessageFormat.format("{0} kg", bean.getAverageWeight()));
        int painfeed = bean.getPainLevel();
        String feedback = "";
        if (painfeed == 0) {
            feedback = "无";
        } else if (painfeed >=1 && painfeed<=4) {
            feedback = "轻度";
        } else if (painfeed >=5 && painfeed<=7) {
            feedback = "中度";
        } else if (painfeed >=8 && painfeed<=10) {
            feedback = "重度";
        }
        tv_feedback.setText(feedback);
        super.refreshContent(e, highlight);
    }

    @Override
    public MPPointF getOffset() {
        return new MPPointF(-(getWidth() / 2), -getHeight());
    }
}
