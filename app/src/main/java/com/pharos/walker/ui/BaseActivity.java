package com.pharos.walker.ui;

import android.app.ProgressDialog;
import android.app.Service;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.ListPopupWindow;

import com.pharos.walker.R;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.popupdialog.PopupSheet;
import com.pharos.walker.customview.popupdialog.PopupSheetCallback;
import com.pharos.walker.utils.DimensUtil;
import com.pharos.walker.utils.SPHelper;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.List;

/**
 * Created by zhanglun on 2021/1/26
 * Describe:
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static final String TAG = "BaseActivity";
    private ImageView ivRed;
    private ImageView ivPoint;
    private TextView tvBattery;
    private ImageView ivPower;
    private ImageView ivVoice;
    private ImageView ivHeader;
    private ImageView ivNotification;
    private TextView tvHeader;
    private TextView tvBleBattery;
    private ImageView imgBattery;
    private AudioManager audioManager = null; // Audio管理器，
    private List<String> musicDatas;
    private  AnimationDrawable animationDrawable;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.initConfig(savedInstanceState);
        ivRed = findView(R.id.iv_red);
        ivPoint = findView(R.id.iv_point);
        tvBattery = findView(R.id.tv_battery);
        ivNotification = findView(R.id.iv_notification);
        ivPower = findView(R.id.iv_power);
        ivVoice = findView(R.id.iv_voice);
        tvHeader = findView(R.id.tv_header);
        ivHeader = findView(R.id.iv_header);
        imgBattery = findView(R.id.img_battery);
        tvBleBattery = findView(R.id.tv_ble_battery);
        if (ivVoice != null){
            ivVoice.setOnClickListener(v -> {
                audioManager = (AudioManager)getSystemService(Service.AUDIO_SERVICE);
                audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
            });
        }
//        if (tvHeader != null){
//            tvHeader.setOnClickListener(v -> startTargetActivity(UserActivity.class,false));
//        }
//        if (ivHeader != null){
//            ivHeader.setOnClickListener(v -> startTargetActivity(UserActivity.class,false));
//        }
        if (ivNotification != null){
            String[] musics = getResources().getStringArray(R.array.music_list);
            musicDatas = Arrays.asList(musics);
            ivNotification.setOnClickListener(v -> trainMusicPop());
        }


    }

    private void initConfig(Bundle savedInstanceState) {
        if (this.getLayoutResId() > 0) {
            this.setContentView(this.getLayoutResId());
        }
        this.initialize(savedInstanceState);
    }

    protected abstract void initialize(Bundle savedInstanceState);
    protected abstract int getLayoutResId();
    public void startTargetActivity(Class<?> targetActivity,boolean isEndActivity){
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        if (isEndActivity){
            finish();
        }
    }



    @Override
    protected void onResume() {
        super.onResume();
        setPoint(Global.isConnected);
        if (Global.USER_MODE){
            setHeader();
        }
        setTvBleBattery(Global.BLE_BATTERY);

    }
    private void trainMusicPop(){
        PopupSheet popupSheet = new PopupSheet(this, ivNotification, musicDatas, new PopupSheetCallback() {
            @Override
            public View setupItemView(int position) {
                View itemV = LayoutInflater.from(BaseActivity.this).inflate(R.layout.item_music_dropdown, null);
                TextView titleTV = itemV.findViewById(R.id.tv_music);
                titleTV.setText(MessageFormat.format("{0}", musicDatas.get(position)));

                return itemV;
            }

            @Override
            public void itemClicked(ListPopupWindow popupWindow, int position) {
                popupWindow.dismiss();
                String musicName = musicDatas.get(position);
                SPHelper.saveMusicPosition(position);
                onMusicSelectedChanged(position);
            }
        }, DimensUtil.dp2px(260));
        popupSheet.show();
    }
    public void startTargetActivity(Bundle bundle, Class<?> targetActivity, boolean isEndActivity){
        Intent intent = new Intent(this, targetActivity);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isEndActivity){
            finish();
        }
    }
    protected final <T extends View> T findView(int id) {
        return (T) super.findViewById(id);
    }
    public void setPoint(boolean isConnected) {
        if (ivPoint != null) {
            if (isConnected) {
                ivPoint.setImageResource(R.drawable.round_green_point);
            } else {
                ivPoint.setImageResource(R.drawable.round_red_point);
            }
        }
    }

    public void setBattery(int battery,int batteryStatus) {
        if (tvBattery != null) {
            tvBattery.setText(MessageFormat.format("{0}%", battery));
        }
        if (imgBattery != null){
            if (battery >= 80){
                imgBattery.setImageResource(R.mipmap.icon_battery_full);
            }else if (battery >= 60){
                imgBattery.setImageResource(R.mipmap.icon_battery_heigh);
            }else if (battery >= 40){
                imgBattery.setImageResource(R.mipmap.icon_battery_middle);
            }else if (battery >= 20){
                imgBattery.setImageResource(R.mipmap.icon_battery_low);
            }else {
                imgBattery.setImageResource(R.mipmap.icon_battery_empty);
            }
        }
        if (ivPower != null){
            if (batteryStatus == BatteryManager.BATTERY_STATUS_CHARGING){
                ivPower.setVisibility(View.VISIBLE);
                ivPower.setImageResource(R.drawable.battery_charging);
                animationDrawable = (AnimationDrawable) ivPower.getDrawable();
                animationDrawable.start();
            }else {
                ivPower.setVisibility(View.INVISIBLE);
                ivPower.setImageResource(R.drawable.ic_power);
                if (animationDrawable != null){
                    animationDrawable.stop();
                    animationDrawable = null;
                }
            }
        }
    }

    public void setHeader(){
        if (tvHeader != null){
            tvHeader.setText(SPHelper.getUserName().equals("快速模式")? "":SPHelper.getUserName());
        }
    }
    public void setRed() {
        if (ivRed != null) {
            ivRed.setImageResource(R.drawable.round_red_point);
        }
    }
    public void setTvBleBattery(int value) {
        if (tvBleBattery != null) {
            tvBleBattery.setText(MessageFormat.format("{0}%", value));
        }
    }
    @Override
    public void onBackPressed() {
//        super.onBackPressed();
    }
    protected void onMusicSelectedChanged(int position){

    }
    public ProgressDialog progressDialog;
    /**
     * 圆圈加载进度的 dialog
     */
    public void showWaiting(String title,String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();
    }

}
