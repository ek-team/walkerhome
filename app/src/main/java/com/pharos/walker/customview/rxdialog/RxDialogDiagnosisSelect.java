package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.pharos.walker.R;


public class RxDialogDiagnosisSelect extends RxDialog {
    private TextView mTvContent;
    private TextView mTvCancel;
    private TextView mTvSure;
    public  String selectValue;
    public EditText editText;
    private RadioButton rb1;
    private RadioButton rb2;
    private RadioButton rb3;
    private RadioButton rb4;
    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }
    public RxDialogDiagnosisSelect(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxDialogDiagnosisSelect(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public RxDialogDiagnosisSelect(Context context) {
        super(context);
        initView();
    }
    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_diagnosis_select, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        editText = dialogView.findViewById(R.id.txtDiag);
        mTvCancel.setOnClickListener(v -> dismiss());
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        RadioGroup rg = dialogView.findViewById(R.id.rg);
        rb1 = dialogView.findViewById(R.id.rb_1);
        rb2 = dialogView.findViewById(R.id.rb_2);
        rb3 = dialogView.findViewById(R.id.rb_3);
        rb4 = dialogView.findViewById(R.id.rb_4);
        selectValue = rb1.getText().toString();
        rg.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId){
                case R.id.rb_1:
                    selectValue = rb1.getText().toString();
                    break;
                case R.id.rb_2:
                    selectValue = rb2.getText().toString();
                    break;
                case R.id.rb_3:
                    selectValue = rb3.getText().toString();
                    break;
                case R.id.rb_4:
                    selectValue = rb4.getText().toString();
                    break;
            }
        });
        setContentView(dialogView);
    }
}
