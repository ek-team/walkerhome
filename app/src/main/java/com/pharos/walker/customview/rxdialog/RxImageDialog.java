package com.pharos.walker.customview.rxdialog;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.pharos.walker.R;

public class RxImageDialog extends RxDialog {
    private TextView mTvTitle;
    private TextView mTvContent;
    private TextView mTvCancel;
    private TextView mTvSure;
    private View mViewLine;
    private ImageView img;
    public RxImageDialog(Context context, int themeResId) {
        super(context, themeResId);
        initView();
    }

    public RxImageDialog(Context context) {
        super(context);
        initView();
    }

    public RxImageDialog(Context context, float alpha, int gravity) {
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
        mTvSure.setText(content);
    }
    public void setImage(Bitmap bitmap){
        img.setImageBitmap(bitmap);
    }
    public void setImageUri(Uri uri){
        img.setImageURI(uri);
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
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.dialog_image, null);
        mTvSure = dialogView.findViewById(R.id.tv_sure);
        mTvCancel = dialogView.findViewById(R.id.tv_cancel);
        mTvContent = dialogView.findViewById(R.id.tv_content);
        mTvTitle = dialogView.findViewById(R.id.tv_title);
        mViewLine = dialogView.findViewById(R.id.view_line);
        img = dialogView.findViewById(R.id.img);
        mTvCancel.setOnClickListener(v -> dismiss());
        mTvContent.setMovementMethod(ScrollingMovementMethod.getInstance());
        mTvContent.setTextIsSelectable(true);
        setContentView(dialogView);
    }
}
