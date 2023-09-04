package com.pharos.walker.ui;

import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;

import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.bluetooth.BluetoothController;
import com.pharos.walker.constants.Global;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.services.ForegroundWorkService;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.OneShotUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SqlToExcleUtil;


/**
 * Created by zhanglun on 2020/6/4
 * Describe:
 */
public class WelcomeActivity extends BaseActivity {

    private static final long DELAY_TIME = 1000;
    private Handler mHandler = new Handler();
    @Override
    protected void initialize(Bundle savedInstanceState) {
        BluetoothController.getInstance().initBle();
        ForegroundWorkService.launch();
        UserManager mUserManager = UserManager.getInstance();
        if (SPHelper.getUser().getId() <= 0){
            SPHelper.saveUser(mUserManager.initUser(0L));//创建初始用户，并保存到本地
            Global.USER_MODE = false;
        }
        ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
        if (localCodeBean == null || TextUtils.isEmpty(localCodeBean.getMacAddress())){
            ActivationCodeBean codeBean = new ActivationCodeBean();
            codeBean.setId(0L);
            codeBean.setCreateDate(System.currentTimeMillis());
            codeBean.setPublicKey("productStock");
            codeBean.setRecordDate(System.currentTimeMillis());
            codeBean.setMacAddress( MyUtil.getMac());
            ActivationCodeManager.getInstance().insertCodeBean(codeBean);
        }else if (TextUtils.isEmpty(localCodeBean.getMacAddress())){
            localCodeBean.setMacAddress(MyUtil.getMac());
            ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
//            ActivationCodeBean codeBean =  ActivationCodeManager.getInstance().getCodeBean();
//            codeBean.setActivationCode("1adbf93d94d7bd37b817a56e37d0fa47");
//            ActivationCodeManager.getInstance().insertCodeBean(codeBean);
        }
        mHandler.postDelayed(() -> startTargetActivity(MainActivity.class,true), DELAY_TIME);
//        mHandler.postDelayed(() -> startTargetActivity(FeedbackActivity.class,true), DELAY_TIME);
//        OneShotUtil.getInstance(this);
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_welcome;
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(null);
    }
}
