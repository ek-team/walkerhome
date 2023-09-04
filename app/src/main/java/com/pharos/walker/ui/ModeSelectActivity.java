package com.pharos.walker.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.core.app.ActivityCompat;

import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.constants.Global;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.SPHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/30
 * Describe:
 */
public class ModeSelectActivity extends BaseActivity {
    @BindView(R.id.ll_user_mode)
    LinearLayout llUserMode;
    @BindView(R.id.ll_fast_mode)
    LinearLayout llFastMode;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        // Android 动态请求权限 Android 10 需要单独处理
        if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                String[] strings ={Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.RECORD_AUDIO,Manifest.permission.WRITE_EXTERNAL_STORAGE };
                ActivityCompat.requestPermissions(this, strings, 1);
            }
        } else {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    || ActivityCompat.checkSelfPermission(this,
                    "android.permission.ACCESS_BACKGROUND_LOCATION") != PackageManager.PERMISSION_GRANTED) {
                String[] strings = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION,
                        "android.permission.ACCESS_BACKGROUND_LOCATION"};
                ActivityCompat.requestPermissions(this, strings, 2);
            }
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_mode_select;
    }

    @OnClick({R.id.ll_user_mode, R.id.ll_fast_mode})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_user_mode:
                Global.USER_MODE = true;
                startTargetActivity(UserActivity.class,true);
                break;
            case R.id.ll_fast_mode:
                Global.USER_MODE = false;
                SPHelper.saveUser(UserManager.getInstance().initUser(0L));//创建初始用户，并保存到本地
                startTargetActivity(MainActivity.class,true);
                break;
        }
    }
}
