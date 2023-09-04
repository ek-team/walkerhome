package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxDialogThreeSelect extends RxDialog  {
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvSelect1;
    private TextView mTvSelect2;
    private TextView mTvSelect3;
    private View mViewLine;
    private View mViewLine1;
    public RxDialogThreeSelect(Context context) {
        super(context);
        initView();
    }

    public RxDialogThreeSelect(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();

    }

    public void setTvSelect1Listener(View.OnClickListener listener) {
        mTvSelect1.setOnClickListener(listener);
    }


    public void setTvSelect2Listener(View.OnClickListener listener) {
        mTvSelect2.setOnClickListener(listener);
    }

    public void setTvSelect3Listener(View.OnClickListener listener) {
        mTvSelect3.setOnClickListener(listener);
    }
    public void setTvSelect1(String content) {
        mTvSelect1.setText(content);
    }
    public void setTvSelect2(String content) {
        mTvSelect2.setText(content);
    }
    public void setTvSelect3(String content) {
        mTvSelect3.setText(content);
    }
    public void setContent(String str) {
        mTvContent.setText(str);
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_three_select, null);
        mTvSelect1 = dialogView.findViewById(R.id.tv_select_1);
        mTvSelect2 = dialogView.findViewById(R.id.tv_select_2);
        mTvSelect3 = dialogView.findViewById(R.id.tv_select_3);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        mTvTitle = dialogView.findViewById(R.id.tv_title);
        mViewLine = dialogView.findViewById(R.id.view_line);
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        setContentView(dialogView);
    }
}
