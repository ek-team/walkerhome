package com.pharos.walker.fragment;

import android.content.Context;
import android.os.Bundle;
import android.os.Process;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pharos.walker.R;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.ui.ActivationCodeActivity;
import com.pharos.walker.ui.ConnectDeviceActivity;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.ToastUtils;
import com.pharos.walker.utils.Utils;

import java.io.File;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SystemFragment extends Fragment {
    @BindView(R.id.btn_update_version)
    TextView btnUpdateVersion;
    @BindView(R.id.btn_system_default)
    TextView btnSystemDefault;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_system, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initView();
        return contentView;
    }

    private void initView() {

    }

    @OnClick({R.id.btn_update_version, R.id.btn_system_default})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btn_update_version:
                break;
            case R.id.btn_system_default:
                selectModeDialog();
                break;
        }
    }
    private void selectModeDialog() {
        RxDialogSureCancel dialog = new RxDialogSureCancel(getContext());
        dialog.setContent("是否恢复出厂设置");
        dialog.setCancel("取消");
        dialog.setSure("恢复出厂设置");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            MyUtil.deleteFile(new File("data/data/" + Utils.getApp().getPackageName()));
            exitSystemDialog("恢复出厂完成","请重启机器");
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
        });
        dialog.show();
    }
    private void exitSystemDialog(String content, String sureText) {
        RxDialogSureCancel rxDialog = new RxDialogSureCancel(getContext());
        rxDialog.setContent(content);
        rxDialog.setCancel("");
        rxDialog.setSure(sureText);
        rxDialog.setCancelable(false);
        rxDialog.setCanceledOnTouchOutside(false);
        rxDialog.setSureListener(v -> {
//            Process.killProcess(Process.myPid());
//            System.exit(0);
            rxDialog.dismiss();
        });
        rxDialog.show();
    }
}
