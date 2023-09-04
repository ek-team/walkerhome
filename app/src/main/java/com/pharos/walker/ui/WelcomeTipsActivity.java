package com.pharos.walker.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.pharos.walker.R;
import com.pharos.walker.utils.SpeechUtil;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class WelcomeTipsActivity extends Activity {
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.btn_start)
    TextView btnStart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_welcome_tips, null);
        setContentView(dialogView);
        ButterKnife.bind(this);
        Window window;
        window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        params.height = 600;
        params.width = 900;
        window.setAttributes(params);
        SpeechUtil.getInstance(this).speak(tvTips.getText().toString());
    }

    @OnClick(R.id.btn_start)
    public void onViewClicked() {
        finish();
    }
}
