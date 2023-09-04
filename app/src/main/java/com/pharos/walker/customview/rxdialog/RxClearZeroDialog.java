package com.pharos.walker.customview.rxdialog;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxClearZeroDialog extends RxDialog {
    private TextView tvValue;
    private TextView mTvContent;
    private TextView mTvSure;
    private ImageView img;
    @SuppressLint("HandlerLeak")
    public Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0 && tvValue != null){
                tvValue.setText(msg.obj.toString());
            }
        }
    };
    public RxClearZeroDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxClearZeroDialog(Context context) {
        super(context);
        initView();
    }

    public RxClearZeroDialog(Context context, float alpha, int gravity) {
        super(context, alpha, gravity);
        initView();
    }
    public TextView getSureView() {
        return mTvSure;
    }

    public void setSureListener(View.OnClickListener listener) {
        mTvSure.setOnClickListener(listener);
    }


    public TextView getContentView() {
        return mTvContent;
    }

    public void setSure(String content) {
        mTvSure.setText(content);
    }
    public void setImage(Bitmap bitmap){
        img.setImageBitmap(bitmap);
    }


    public void setContent(String str) {
        mTvContent.setText(str);
    }

    private void initView() {
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_clear_zero, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        tvValue = dialogView.findViewById(R.id.tv_value);
        img = dialogView.findViewById(R.id.img);
        setContentView(dialogView);
    }
}
