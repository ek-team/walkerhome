package com.pharos.walker.ui;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharos.walker.R;
import com.pharos.walker.beans.ActivationCodeBean;
import com.pharos.walker.beans.ServerActivationCodeBean;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.DesUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class ActivationCodeActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_device_id)
    TextView tvDeviceId;
    @BindView(R.id.et_code)
    EditText etCode;
    @BindView(R.id.btn_ok)
    TextView btnOk;
    @BindView(R.id.et1)
    EditText et1;
    @BindView(R.id.et2)
    EditText et2;
    @BindView(R.id.et3)
    EditText et3;
    @BindView(R.id.et4)
    EditText et4;
    @BindView(R.id.et5)
    EditText et5;
    @BindView(R.id.et6)
    EditText et6;
    @BindView(R.id.et7)
    EditText et7;
    @BindView(R.id.et8)
    EditText et8;
    private List<EditText> editTexts = new ArrayList<>();

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        editTexts.add(et1);
        editTexts.add(et2);
        editTexts.add(et3);
        editTexts.add(et4);
        editTexts.add(et5);
        editTexts.add(et6);
        editTexts.add(et7);
        editTexts.add(et8);
        initView();
    }

    private void initView() {
        for (int i = 0; i < editTexts.size(); i++){
            editTexts.get(i).addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (count == 4){

                    }

                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_activation_code;
    }


    @OnClick({R.id.iv_back, R.id.btn_ok,R.id.btn_net_activation})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                break;
            case R.id.btn_ok:
                StringBuilder stringBuffer = new StringBuilder();
                for (EditText editText:editTexts){
                    stringBuffer.append(editText.getText().toString());
                }
                if (TextUtils.isEmpty(stringBuffer.toString())) {
                    ToastUtils.showShort("激活码不能为空");
                    return;
                }
                if (stringBuffer.toString().length() < 32) {
                    ToastUtils.showShort("激活码不全");
                    return;
                }
                ActivationCodeBean codeBean = ActivationCodeManager.getInstance().getCodeBean();
                codeBean.setActivationCode(stringBuffer.toString());
                codeBean.setRecordDate(System.currentTimeMillis());
                verifyActivationCode(codeBean.getPublicKey(),codeBean.getActivationCode(),codeBean.getMacAddress());
                ActivationCodeManager.getInstance().insertCodeBean(codeBean);
                break;
            case R.id.btn_net_activation:
                String macAddress = MyUtil.getMac();
                if (!TextUtils.isEmpty(macAddress) && NetworkUtils.isConnected()){
                    showWaiting("激活码激活提示","正在激活");
                    getActivationCode(macAddress);
                }else {
                    ToastUtils.showShort("网络未连接");
                }
                break;
        }
    }
    private void getActivationCode(String macAddress){
        OkHttpUtils.getAsync(Api.getActivationCode + macAddress, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("ActivationCodeActivity", "code request success: " + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    ServerActivationCodeBean codeBean = new Gson().fromJson(result,ServerActivationCodeBean.class);
                    ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
                    localCodeBean.setActivationCode(codeBean.getData().getActivationCode());
                    localCodeBean.setRecordDate(System.currentTimeMillis());
                    ActivationCodeManager.getInstance().insertCodeBean(localCodeBean);
                    verifyActivationCode(localCodeBean.getPublicKey(),codeBean.getData().getActivationCode(),localCodeBean.getMacAddress());
                }else if (code == 401){
                    getToken();
                }else {
                    ToastUtils.showShort("激活码获取失败");
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                }

            }
        });

    }
    private void verifyActivationCode(String publicKey,String activationCode,String mac){
        String decryptCode = null;
        try {
            decryptCode = new DesUtil(publicKey).decrypt(activationCode);
        } catch (Exception e) {
            e.printStackTrace();
            ToastUtils.showShort("激活码错误，请重试");
        }
        Log.e("ActivationCodeActivity", "decryptCode: " + decryptCode);
        String[] strings;
        if (decryptCode != null) {
            strings = decryptCode.split("-");
        }else {
            ToastUtils.showShort("激活码错误，请重试");
            return;
        }
        String macAddress = strings[0];
        long endDate = DateFormatUtil.getSpecialString2Date(strings[1]);
        if (!mac.replaceAll(":","").endsWith(macAddress)){
            ToastUtils.showShort("激活码错误，请重试");
        } else if (System.currentTimeMillis() > endDate){
            ToastUtils.showShort("激活码已过期，请联系业务员重新激活");
        }else {
            ToastUtils.showShort("激活成功");
            finish();
        }
        if (progressDialog != null && progressDialog.isShowing()){
            progressDialog.dismiss();
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
                Log.e("ActivationCodeActivity", "requestSuccess: " + result);

                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    ActivationCodeBean localCodeBean = ActivationCodeManager.getInstance().getCodeBean();
                    getActivationCode(localCodeBean.getMacAddress());
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
