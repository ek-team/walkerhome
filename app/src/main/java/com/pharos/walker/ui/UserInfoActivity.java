package com.pharos.walker.ui;

import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.flyco.tablayout.SegmentTabLayout;
import com.flyco.tablayout.listener.OnTabSelectListener;
import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/2
 * Describe:
 */
public class UserInfoActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tab)
    SegmentTabLayout tab;
    @BindView(R.id.tv_name)
    TextView tvName;
    @BindView(R.id.iv_edit_name)
    ImageView ivEditName;
    @BindView(R.id.tv_age)
    TextView tvAge;
    @BindView(R.id.iv_edit_age)
    ImageView ivEditAge;
    @BindView(R.id.tv_weight)
    TextView tvWeight;
    @BindView(R.id.iv_edit_weight)
    ImageView ivEditWeight;
    @BindView(R.id.tv_exit)
    TextView tvExit;
    @BindView(R.id.layout_personal_info)
    LinearLayout layoutPersonalInfo;
    @BindView(R.id.layout_about_us)
    LinearLayout layoutAboutUs;
    @BindView(R.id.ll_system_info)
    LinearLayout llSystemInfo;
    @BindView(R.id.tv_mac)
    TextView tvMac;
    @BindView(R.id.list_system_message)
    RecyclerView listSystemMessage;
    @BindView(R.id.img_qr)
    ImageView imgQr;
    private ActivationCodeBean localCodeBean;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        initView();
    }

    private void initView() {
        String[] mTitles = {getResources().getString(R.string.personal_info),
                getResources().getString(R.string.about_us),
                getResources().getString(R.string.system_message)};
        tab.setTabData(mTitles);
        tab.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelect(int position) {
                switch (position) {
                    case 0:
                        layoutPersonalInfo.setVisibility(View.VISIBLE);
                        layoutAboutUs.setVisibility(View.GONE);
                        llSystemInfo.setVisibility(View.GONE);
                        break;
                    case 1:
                        layoutPersonalInfo.setVisibility(View.GONE);
                        layoutAboutUs.setVisibility(View.VISIBLE);
                        llSystemInfo.setVisibility(View.GONE);
                        break;
                    case 2:
                        layoutPersonalInfo.setVisibility(View.GONE);
                        layoutAboutUs.setVisibility(View.GONE);
                        llSystemInfo.setVisibility(View.VISIBLE);
                        if (NetworkUtils.isConnected()){
                            showWaiting("获取二维码","正在获取");
                            getToken();
                        }else {
                            File filesDir = getFilesDir();
                            File[] files = filesDir.listFiles();
                            if (files == null){
                                Log.e("error","空目录");
                                return;
                            }
                            for (File file : files) {
                                if (file.getName().startsWith(localCodeBean.getMacAddress())) {
                                    imgQr.setImageURI(Uri.fromFile(new File(file.getAbsolutePath())));
                                }
                            }

                        }
                        break;
                }
            }

            @Override
            public void onTabReselect(int position) {

            }
        });
        tvName.setText(SPHelper.getUser().getName());
        tvWeight.setText(SPHelper.getUser().getTelephone());
        String mac = MyUtil.getMac();
        if (!TextUtils.isEmpty(mac) && TextUtils.isEmpty(localCodeBean.getMacAddress())){
            localCodeBean.setMacAddress(mac);
            ActivationCodeManager.getInstance().updateCodeBean(localCodeBean);
        }
        tvMac.setText(MessageFormat.format("{0}\n设备二维码地址：", MessageFormat.format("设备Mac地址：{0}", mac)));

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_info;
    }
    private void getPlatformQr(){
        File filesDir = getFilesDir();
        String absolutePath = filesDir.getAbsolutePath();
        OkHttpUtils.downloadAsync(Api.getPlatformQr + localCodeBean.getMacAddress(),absolutePath, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("数据请求失败");
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                if (result != null){
                    imgQr.setImageURI(Uri.fromFile(new File(result)));
                }else {
                    ToastUtils.showShort("图片获取失败");
                }

            }
        });
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
            case MessageEvent.ACTION_READ_DEVICE:
                int bleBattery  = (int) event.getData();
                setTvBleBattery(bleBattery);
                break;
            default:
                break;
        }
    }
    @OnClick({R.id.iv_back, R.id.tv_exit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.tv_exit:
                Global.USER_MODE = false;
                UserBean userBean = UserManager.getInstance().loadGuest();
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,true);
                break;
        }
    }
    private void getToken(){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (!TextUtils.isEmpty(localCodeBean.getMacAddress())){
                        getPlatformQr();
                    }else {
                        ToastUtils.showShort("Mac地址获取失败");
                    }
                }else {
                    ToastUtils.showShort("Token 获取失败");
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

            }
        });

    }
}
