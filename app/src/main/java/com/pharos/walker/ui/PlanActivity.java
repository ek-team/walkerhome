package com.pharos.walker.ui;

import android.os.Bundle;
import android.os.Message;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.NoScrollViewPager;
import com.pharos.walker.fragment.BleShoesSettingFragment;
import com.pharos.walker.fragment.EvaluateRecordFragment;
import com.pharos.walker.fragment.InfoFragment;
import com.pharos.walker.fragment.NewsFragment;
import com.pharos.walker.fragment.RecycleFragment;
import com.pharos.walker.fragment.SystemFragment;
import com.pharos.walker.fragment.TrainPlanFragment;
import com.pharos.walker.fragment.TrainRecordFragment;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class PlanActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.home_left)
    RelativeLayout homeLeft;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.tab_home)
    SegmentTabLayout tabHome;
    @BindView(R.id.pager)
    NoScrollViewPager pager;
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    private TrainPlanFragment trainPlanFragment;
    private TrainRecordFragment recordFragment;
    private InfoFragment infoFragment;
    private RecycleFragment recycleFragment;
    private NewsFragment newsFragment;
    private SystemFragment systemFragment;
    private BleShoesSettingFragment bleShoesSettingFragment;
    private EvaluateRecordFragment evaluateRecordFragment;
    private List<Fragment> mFragment = new ArrayList<>(2);
    private int receiveStatus = 0;

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();

    }

    private void initView() {
        String[] mTitles;
        if (Global.ReleaseVersion == Global.HomeVersion) {
            mTitles = new String[]{getResources().getString(R.string.info),getResources().getString(R.string.plan),
                    getResources().getString(R.string.record),
                    getResources().getString(R.string.evaluate),
                    getResources().getString(R.string.news),
                    getResources().getString(R.string.ble_shoes_setting)
                    };
            tabHome.setVisibility(View.VISIBLE);
            tab = tabHome;
            tabHome.setTabData(mTitles);
//            recycleFragment = new RecycleFragment();
            infoFragment = new InfoFragment();
            trainPlanFragment = new TrainPlanFragment();
            recordFragment = new TrainRecordFragment();
            evaluateRecordFragment = new EvaluateRecordFragment();
            newsFragment = new NewsFragment();
            bleShoesSettingFragment = new BleShoesSettingFragment();
//            systemFragment = new SystemFragment();
            mFragment.add(infoFragment);
            mFragment.add(trainPlanFragment);
            mFragment.add(recordFragment);
            mFragment.add(evaluateRecordFragment);
            mFragment.add(newsFragment);
            mFragment.add(bleShoesSettingFragment);
//            mFragment.add(systemFragment);
        } else {
            mTitles = new String[]{getResources().getString(R.string.info),
                    getResources().getString(R.string.plan),
                    getResources().getString(R.string.record)};
            tab.setTabData(mTitles);
            infoFragment = new InfoFragment();
            trainPlanFragment = new TrainPlanFragment();
            recordFragment = new TrainRecordFragment();
            mFragment.add(infoFragment);
            mFragment.add(trainPlanFragment);
            mFragment.add(recordFragment);
        }

        FragmentPagerAdapter fragmentPagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return mFragment.get(position);
            }

            @Override
            public int getCount() {
                return mFragment.size();
            }
        };
        pager.setAdapter(fragmentPagerAdapter);
        pager.setOffscreenPageLimit(2);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                pager.setCurrentItem(position, false);
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        if (receiveStatus == 1){
            tab.setCurrentTab(2);
            pager.setCurrentItem(2);
        }
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            receiveStatus = bundle.getInt("feedback",0);
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
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery = (int) event.getData();
                bleShoesSettingFragment.refreshBleInfo(Global.isConnected,bleBattery);
                break;
            case MessageEvent.ACTION_READ_DATA:
                BleBean bleBean = (BleBean) event.getData();
                String value = bleBean.getData();
                if (bleShoesSettingFragment.dialog != null){
                    DecimalFormat df = new DecimalFormat("0.0");
                    Message msg = new Message();
                    msg.what = 0;
                    msg.obj = df.format(Float.valueOf(value)) + "kg";
                    bleShoesSettingFragment.dialog.mHandler.sendMessage(msg);
                }
                break;
            default:
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_plan;
    }

    @OnClick(R.id.iv_back)
    public void onViewClicked() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (EventBus.getDefault().isRegistered(this)) {
            EventBus.getDefault().unregister(this);
        }
    }


    @OnClick({R.id.img_back, R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.tv_back:
                finish();
                break;
        }
    }
}
