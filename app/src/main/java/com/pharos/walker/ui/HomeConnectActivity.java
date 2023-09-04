package com.pharos.walker.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flyco.tablayout.SegmentTabLayout;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.MessageFormat;
import java.util.Timer;
import java.util.TimerTask;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HomeConnectActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.tab_home)
    SegmentTabLayout tabHome;
    @BindView(R.id.img)
    ImageView img;
    @BindView(R.id.btn_next)
    TextView btnNext;
    @BindView(R.id.tv_tips)
    TextView tvTips;
    @BindView(R.id.tv_ble_name)
    TextView tvBleName;
    private int selectActivity;
    private TimerTask timerTask = null;
    private Timer timer = null;
    @SuppressLint("NewApi")
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        btnNext.setBackground(getDrawable(R.drawable.btn_gray_bg));
        btnNext.setClickable(false);
        SpeechUtil.getInstance(this).speak(tvTips.getText().toString());

        if (Global.isConnected){
            tvBleName.setText(MessageFormat.format("蓝牙鞋已连接{0}", Global.ConnectedName));
            SpeechUtil.getInstance(this).speak("康复鞋连接成功");
            btnNext.setBackground(getDrawable(R.drawable.btn_orange_bg));
            btnNext.setTextColor(getResources().getColor(R.color.white,null));
            btnNext.setClickable(true);
            finishTimer();
        }else {
            tvBleName.setText("正在连接蓝牙鞋·");
            startAnimTimerTask();
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home_connect;
    }
    private void initData() {
        selectActivity = getIntent().getIntExtra(AppKeyManager.EXTRA_CONNECT_MODE,0);
    }
    @SuppressLint("NewApi")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                bleBean = (BleBean) event.getData();
                tvBleName.setText(MessageFormat.format("蓝牙鞋已连接{0}", bleBean.getName()));
                finishTimer();
//                ToastUtils.showShort("鞋子连接成功");
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                btnNext.setBackground(getDrawable(R.drawable.btn_gray_bg));
                btnNext.setClickable(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.ACTION_GATT_CONNECTING_HOME:

                break;
            case MessageEvent.GATT_TRANSPORT_OPEN:
                if ((int)event.getData() == 0){
//                    ToastUtils.showShort("鞋子连接成功");
                     SpeechUtil.getInstance(this).speak("康复鞋连接成功");
                    btnNext.setBackground(getDrawable(R.drawable.btn_orange_bg));
                    btnNext.setTextColor(getResources().getColor(R.color.white,null));
                    btnNext.setClickable(true);
                    finishTimer();
                }else {
                    ToastUtils.showShort("蓝牙通信失败，请重启蓝牙鞋");
                }
                break;
        }
    }

    @OnClick({R.id.img_back, R.id.tv_back, R.id.btn_next})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.tv_back:
                startTargetActivity(MainActivity.class,true);
                break;
            case R.id.btn_next:
                if (DateFormatUtil.avoidFastClick2(2000) && Global.isConnected){
                    if (selectActivity == Global.ConnectMainMode){
                        startTargetActivity(TrainParamActivity.class,true);
                    }else if (selectActivity == Global.ConnectEvaluateMode || selectActivity == Global.ConnectUserMode){
                        Bundle bundle = new Bundle();
                        bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectEvaluateMode);
                        startTargetActivity(bundle,StartActivity.class,true);
                    }else {
                        startTargetActivity(TrainParamActivity.class,true);
                    }
                }
                break;
        }
    }
    private void startAnimTimerTask(){
        if (timer == null && timerTask == null){
            timer = new Timer();
            timerTask = new TimerTask() {
                @Override
                public void run() {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            if (tvBleName.getText().equals("正在连接蓝牙鞋·")){
                                tvBleName.setText("正在连接蓝牙鞋··");
                            }else if (tvBleName.getText().equals("正在连接蓝牙鞋··")){
                                tvBleName.setText("正在连接蓝牙鞋···");
                            }else if (tvBleName.getText().equals("正在连接蓝牙鞋···")){
                                tvBleName.setText("正在连接蓝牙鞋····");
                            }else if (tvBleName.getText().equals("正在连接蓝牙鞋····")){
                                tvBleName.setText("正在连接蓝牙鞋·····");
                            }else if (tvBleName.getText().equals("正在连接蓝牙鞋·····")){
                                tvBleName.setText("正在连接蓝牙鞋······");
                            }else {
                                tvBleName.setText("正在连接蓝牙鞋·");
                            }

                        }
                    });
                }
            };
            timer.schedule(timerTask, 1, 600);
        }
    }
    private void finishTimer(){
        if (timerTask != null){
            timerTask.cancel();
            timerTask = null;
        }
        if (timer != null){
            timer.cancel();
            timer = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
        if (timer != null && timerTask != null){
            timerTask.cancel();
            timerTask = null;
            timer.cancel();
            timer = null;
        }
    }
}
