package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pharos.walker.R;

/**
 * Created by zhanglun on 2021/4/27
 * Describe:
 */
public class RxRadioButtonDialog extends RxDialog {
    private TextView mTvContent;
    private TextView mTvCancel;
    private TextView mTvSure;
    public  int selectValue = 1;
    public RxRadioButtonDialog(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public RxRadioButtonDialog(Context context) {
        super(context);
        initView();
    }

    public RxRadioButtonDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_radio_button_select, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        mTvCancel.setOnClickListener(v -> dismiss());
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        RadioGroup rg = dialogView.findViewById(R.id.rg);
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.rb_1:
                    selectValue = 1;
                    break;
                case R.id.rb_2:
                    selectValue = 2;
                    break;
                case R.id.rb_3:
                    selectValue = 3;
                    break;
                case R.id.rb_4:
                    selectValue = 4;
                    break;
            }
        });
        setContentView(dialogView);
    }
}
