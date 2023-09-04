package com.pharos.walker.customview.rxdialog;

import android.app.DatePickerDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pharos.walker.R;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.utils.DateFormatUtil;

import org.joda.time.DateTime;

import java.text.MessageFormat;
import java.util.Calendar;

/**
 * Created by zhanglun on 2021/5/10
 * Describe:
 */
public class RxTrainDataEditDialog extends RxDialog {
    private TextView mTvCancel;
    private TextView mTvSure;
    private TextView tvTitle;
    private TextView tvStartTime;
    private TextView tvTimeOfDay;
    private TextView tvTrainTime;
    private TextView tvTrainLoad;
    private PlanEntity planEntity;
    private DatePickerDialog mDatePickerDialog;
    private Context context;
    private String startTime;
    public RxTrainDataEditDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public RxTrainDataEditDialog(Context context, PlanEntity planEntity) {
        super(context);
        this.planEntity = planEntity;
        this.context = context;
        initView();
        initDataTime();
    }

    public RxTrainDataEditDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    public PlanEntity getData(){
        planEntity.setLoad(Integer.parseInt(tvTrainLoad.getText().toString()));
        planEntity.setTimeOfDay(Integer.parseInt(tvTimeOfDay.getText().toString()));
        planEntity.setTrainTime(Integer.parseInt(tvTrainTime.getText().toString()));
        planEntity.setUpdateDate(DateFormatUtil.getNowDate());
//        planEntity.setStartDate(startTime);
        return planEntity;
    }
    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_train_data_edit, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        tvTitle = dialogView.findViewById(R.id.tv_title);
        tvStartTime = dialogView.findViewById(R.id.txtStartDate);
        tvTimeOfDay = dialogView.findViewById(R.id.tv_time_of_day);
        tvTrainTime = dialogView.findViewById(R.id.tv_train_time);
        tvTrainLoad = dialogView.findViewById(R.id.tv_train_load);
        tvTitle.setText(MessageFormat.format("第{0}阶段", planEntity.getClassId()));
        tvStartTime.setText(planEntity.getStartDate());
        tvTrainLoad.setText(String.valueOf(planEntity.getLoad()));
        tvTrainTime.setText(String.valueOf(planEntity.getTrainTime()));
        tvTimeOfDay.setText(String.valueOf(planEntity.getTimeOfDay()));
        mTvCancel.setOnClickListener(v -> dismiss());
//        tvStartTime.setOnClickListener(v -> {
//            if (mDatePickerDialog != null) {
//                mDatePickerDialog.show();
//            }
//        });
        setContentView(dialogView);
    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
                    DateTime curDate = DateTime.now();
                    if (a.isAfter(curDate)) {
                        year = curDate.getYear();
                        month = curDate.getMonthOfYear();
                        dayOfMonth = curDate.getDayOfMonth();
                    }
                    startTime = new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss");
                    tvStartTime.setText(year + "-" + month + "-" + dayOfMonth);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}
