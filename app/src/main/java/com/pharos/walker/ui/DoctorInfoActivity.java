package com.pharos.walker.ui;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.flyco.tablayout.SegmentTabLayout;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.SPHelper;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/6/3
 * Describe:
 */
public class DoctorInfoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.img_wx)
    ImageView imgWx;
    @BindView(R.id.ll_wx)
    LinearLayout llWx;
    @BindView(R.id.ll_doctor_info)
    LinearLayout llDoctorInfo;
    @BindView(R.id.tv_doctor_name)
    TextView tvDoctorName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.tv_level)
    TextView tvLevel;
    @BindView(R.id.tv_work_years)
    TextView tvWorkYears;
    @BindView(R.id.tv_hospital_name)
    TextView tvHospitalName;
    @BindView(R.id.tv_hospital_tel)
    TextView tvHospitalTel;
    private UserBean userBean;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        userBean = SPHelper.getUser();
    }

    private void initView() {
        if (!Global.USER_MODE){
            llWx.setVisibility(View.GONE);
            llDoctorInfo.setVisibility(View.GONE);
            return;

        }
        if (!TextUtils.isEmpty(userBean.getDoctor())){
            tvDoctorName.setText(userBean.getDoctor());
            tvHospitalName.setText(userBean.getHospitalName());
            llDoctorInfo.setVisibility(View.VISIBLE);
        }else {
            llWx.setVisibility(View.VISIBLE);
            llDoctorInfo.setVisibility(View.GONE);
//            imgWx.setImageResource(R.mipmap.ic_wx_app);
            Resources res = getResources();
            Bitmap logoBitmap= BitmapFactory.decodeResource(res,R.mipmap.ic_launcher);
            String content = Api.qrUrl + SPHelper.getUserId();
            Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, 400, 400,"UTF-8","H", "1", Color.BLACK, Color.WHITE,logoBitmap,0.15F);
            imgWx.setImageBitmap(qrBitmap);
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                setPoint(false);
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(),battery.getBatteryStatus());
                break;
            default:
                break;
        }
    }
    @Override
    protected int getLayoutResId() {
        return R.layout.activity_doctor_info;
    }


    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
