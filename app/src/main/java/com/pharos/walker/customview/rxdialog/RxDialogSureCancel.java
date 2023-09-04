package com.pharos.walker.customview.rxdialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.pharos.walker.R;

/**
 * 确认对话框
 */
public class RxDialogSureCancel extends RxDialog {

    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvCancel;
    private TextView mTvSure;
    private View mViewLine;
    private View line2;
    private LinearLayout llSelectButton;
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && mTvContent != null){
                mTvContent.setText(msg.obj.toString());
            }else if (msg.what == 1 && mTvTitle != null){
                if (mTvTitle.getVisibility() == View.GONE){
                    mTvTitle.setVisibility(View.VISIBLE);
                }
                mTvTitle.setText(msg.obj.toString());
            }
        }
    };
    public RxDialogSureCancel(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxDialogSureCancel(Context context, boolean cancelable, OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        initView();
    }

    public RxDialogSureCancel(Context context) {
        super(context);
        initView();
    }

    public RxDialogSureCancel(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }

    public TextView getSureView() {
        return mTvSure;
    }

    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }

    public TextView getCancelView() {
        return mTvCancel;
    }

    public void setCancelListener(View.OnClickListener listener) {
        mTvCancel.setOnClickListener(listener);
    }

    public TextView getContentView() {
        return mTvContent;
    }

    public void setSure(String content) {
        if (TextUtils.isEmpty(content)){
            line2.setVisibility(View.GONE);
            llSelectButton.setVisibility(View.GONE);
        }else {
            mTvSure.setText(content);
        }
    }

    public void setCancel(String content) {
        if (!TextUtils.isEmpty(content)){
            mTvCancel.setText(content);
        }else {
            mTvCancel.setVisibility(View.GONE);
            mViewLine.setVisibility(View.GONE);
        }
    }

    public void setContent(String str) {
        mTvContent.setText(str);
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_sure_cancel, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        mTvTitle = dialogView.findViewById(R.id.tv_title);
        mViewLine = dialogView.findViewById(R.id.view_line);
        line2 = dialogView.findViewById(R.id.line_2);
        llSelectButton = dialogView.findViewById(R.id.ll_select_button);
        mTvCancel.setOnClickListener(v -> dismiss());
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        setContentView(dialogView);
    }
}
