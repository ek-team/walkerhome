package com.pharos.walker.ui;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class LoginActivity extends BaseActivity {
    @BindView(R.id.tv_device_id)
    TextView tvDeviceId;
    @BindView(R.id.et_mobile)
    EditText etMobile;
    @BindView(R.id.tv_get_code)
    TextView tvGetCode;
    @BindView(R.id.et_verification_code)
    EditText etVerificationCode;
    @BindView(R.id.btn_login)
    TextView btnLogin;
    @BindView(R.id.btn_account_login)
    TextView btnAccountLogin;
    @BindView(R.id.iv_weixin)
    ImageView ivWeixin;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_see)
    ImageView ivSee;
    @BindView(R.id.tv_forget)
    TextView tvForget;
    @BindView(R.id.btn_acc_login)
    TextView btnAccLogin;
    @BindView(R.id.btn_code_login)
    TextView btnCodeLogin;
    @BindView(R.id.iv_weixin_2)
    ImageView ivWeixin2;
    @BindView(R.id.layout_account)
    LinearLayout layoutAccount;
    @BindView(R.id.tv_membership_agreement)
    TextView tvMembershipAgreement;
    @BindView(R.id.tv_privacy_policy)
    TextView tvPrivacyPolicy;
    private UserBean userBean;
    private int PLAN_REQ = 1;
    private boolean isHidenPwd = true;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null){
            userBean = bundle.getParcelable("userInfo");
        }
        syncPlan();
    }

    private void initView() {
        btnCodeLogin.setText("返回");
//        btnCodeLogin.setVisibility(View.GONE);
        ivWeixin2.setVisibility(View.GONE);
        if(userBean != null){
            etAccount.setText(userBean.getAccount());
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_login;
    }


    @OnClick({R.id.tv_get_code, R.id.btn_login, R.id.btn_account_login, R.id.iv_weixin, R.id.iv_see, R.id.tv_forget, R.id.btn_acc_login, R.id.btn_code_login, R.id.tv_membership_agreement, R.id.tv_privacy_policy})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_get_code:
                break;
            case R.id.btn_login:
                break;
            case R.id.btn_account_login:
                break;
            case R.id.iv_weixin:
                break;
            case R.id.iv_see:
                if (isHidenPwd) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivSee.setImageResource(R.drawable.ic_pwd_enable);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenPwd = !isHidenPwd;
                break;
            case R.id.tv_forget:
                break;
            case R.id.btn_acc_login:
                if (!TextUtils.isEmpty(userBean.getPassword())){
                    if (!etAccount.getText().toString().equals(userBean.getAccount())){
                        ToastUtils.showShort("用户名不对");
                        return;
                    }
                    if(!etPassword.getText().toString().equals(userBean.getPassword())){
                        ToastUtils.showShort("密码不对");
                        return;
                    }
                }
                Global.USER_MODE = true;
                SPHelper.saveUser(userBean);
                startTargetActivity(MainActivity.class,false);
                break;
            case R.id.btn_code_login:
                finish();
                break;
            case R.id.tv_membership_agreement:
                Bundle bundle = new Bundle();
                bundle.putInt("select_value",0);
                startTargetActivity(bundle,PrivacyPolicyActivity.class,false);
                break;
            case R.id.tv_privacy_policy:
                Bundle bundle1 = new Bundle();
                bundle1.putInt("select_value",1);
                startTargetActivity(bundle1,PrivacyPolicyActivity.class,false);
                break;
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_REQ_FAIL:
                ToastUtils.showShort("同步计划请求失败");
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:

                break;
        }
    }
    private void syncPlan(){
        OkHttpUtils.getAsync(Api.getPlan + userBean.getUserId(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->计划获取结果" + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401){
                    getToken(PLAN_REQ);
                    return;
                }
                if (code == 0){
                    ServerPlanEntity serverPlanEntity = new Gson().fromJson(result,ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus ){
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, new Gson().toJson(localPlan), new OkHttpUtils.DataCallBack() {
                            @Override
                            public void requestFailure(Request request, IOException e) {
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                            }
                            @Override
                            public void requestSuccess(String result) throws Exception {
                                Log.e("Setting Activity", "--->计划同步结果" + result);
                                JSONObject toJsonObj= new JSONObject(result);
                                int code = toJsonObj.getInt("code");
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT,code));

                            }
                        });
                    }else if (TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadStatus){
                        for (PlanEntity planEntity : planEntityList){
                            try {
                                TrainPlanManager.getInstance().insert(planEntity);
                                if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0){
                                    SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                                }
                            }catch (Exception e){
                                if (progressDialog != null && progressDialog.isShowing()){
                                    progressDialog.dismiss();
                                }
                                ToastUtils.showShort(e.getMessage());
                                CrashHandler.getInstance().saveThrowableMessage("更新训练计划error：" + e.getMessage());
                            }
                        }
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT,code));
                }else{
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }
    private void getToken(int flag){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    if (flag == PLAN_REQ){
                       syncPlan();
                    }

                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }
}
