package com.pharos.walker.ui;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatSpinner;

import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogDiagnosisSelect;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxImageDialog;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.joda.time.DateTime;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.Province;
import okhttp3.Request;

public class HomeRegisterActivity extends BaseActivity {
    @BindView(R.id.img_back)
    ImageView imgBack;
    @BindView(R.id.tv_back)
    TextView tvBack;
    @BindView(R.id.et_name)
    EditText etName;
    @BindView(R.id.et_age)
    EditText etAge;
    @BindView(R.id.et_weight)
    EditText etWeight;
    @BindView(R.id.rb_male)
    RadioButton rbMale;
    @BindView(R.id.rb_female)
    RadioButton rbFemale;
    @BindView(R.id.rg_sex)
    RadioGroup rgSex;
    @BindView(R.id.tv_location)
    TextView tvLocation;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.sp_select_diagnostic_result)
    AppCompatSpinner spSelectDiagnosticResult;
    @BindView(R.id.layout_step_2)
    LinearLayout layoutStep2;
    private String provinceName;
    private String cityName;
    private String countyName;
    private int sex = 1; //性别  男-1  女-0
    private String name;
    private String age;
    private String weight;
    private List<Province> provincesList;
    private AlertDialog provinceDialog;
    private UserBean userBean;
    private String hospitalAddress;
    private String hospitalName;
    private int selectPosition;
    private DatePickerDialog mDatePickerDialog;
    private Context context;
    private String dateOfSurgery;
    private long userId;
    private String otherDiagnosis;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = this;
        if (userBean == null){
            userBean = new UserBean();
        }
        initView();
    }

    private void initView() {
        rgSex.setOnCheckedChangeListener((group, checkedId) -> {
            switch (checkedId) {
                case R.id.rb_male:
                    sex = 1;
                    break;
                case R.id.rb_female:
                    sex = 0;
                    break;
            }
        });
        spSelectDiagnosticResult.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                TextView textView = null;
                if (view instanceof TextView) {
                    textView = ((TextView) view);
                    textView.setTextColor(getResources().getColor(R.color.white_88));
                }
                if (selectPosition == parent.getCount() -1){
                    if (textView != null) {
                        textView.setText("");
                    }
                    RxDialogDiagnosisSelect diagnosisSelect = new RxDialogDiagnosisSelect(HomeRegisterActivity.this);
                    TextView finalTextView = textView;
                    diagnosisSelect.setSureListener(v -> {
                        String editValue = diagnosisSelect.editText.getText().toString();
                        if (TextUtils.isEmpty(editValue)){
                            ToastUtils.showShort("请填写详细信息");
                            return;
                        }
                        otherDiagnosis = editValue + "(" + diagnosisSelect.selectValue + ")";
                        if (finalTextView != null){
                            finalTextView.setText(otherDiagnosis);
                        }
                        diagnosisSelect.dismiss();
                    });
                    diagnosisSelect.show();
                }else {
                    otherDiagnosis = "";
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initDataTime();
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_home_register;
    }


    @OnClick({R.id.img_back, R.id.tv_back,R.id.btn_commit,R.id.tv_date})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_back:
            case R.id.tv_back:
                finish();
                break;
            case R.id.tv_date:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
            case R.id.btn_commit:
                if (verifyData() && DateFormatUtil.avoidFastClick(2000)){
                    commitData();
                }
                break;
        }
    }
    private void commitData(){
        userBean.setCaseHistoryNo(String.valueOf(SnowflakeIdUtil.getUniqueId()));
        userBean.setWeight(weight);
        userBean.setAge(Integer.parseInt(age));
        if (TextUtils.isEmpty(hospitalAddress)) {
            hospitalAddress = SPHelper.getHospitalAddress();
        }
        userBean.setHospitalAddress(hospitalAddress);
        if (TextUtils.isEmpty(hospitalName)) {
            hospitalName = SPHelper.getHospitalName();
        }
        userBean.setHospitalName(hospitalName);
        userBean.setName(name);
        userBean.setSex(sex);
        Resources res = getResources();
        String[] array = res.getStringArray(R.array.diagnostic_result_list);
        if (selectPosition == array.length -1 && !TextUtils.isEmpty(otherDiagnosis)){
            userBean.setDiagnosis(otherDiagnosis);
        }else {
            userBean.setDiagnosis(array[selectPosition]);
        }
        userId = SnowflakeIdUtil.getUniqueId();
        userBean.setUserId(userId);
        Global.USER_MODE = true;
        UserManager.getInstance().insert(userBean, 0);
        if (NetworkUtils.isConnected()) {
            showWaiting("提示", "正在注册...");
            syncUser();
        } else {
            ToastUtils.showShort("网络不可用");
            goEvaluateDialog();
        }
    }
    private boolean bleConnectedStatus(){
        return Global.isConnected && !TextUtils.isEmpty(Global.ConnectedAddress);
    }
    private void syncUser() {
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = new Gson().toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                ToastUtils.showShort("服务器访问错误");
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                goEvaluateDialog();
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT, code));
                if (code == 0) {
                    UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    goEvaluateWithQrCodeDialog();

                } else if (code == 401) {
                    getToken();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("数据访问错误");
                    }
                    goEvaluateDialog();
                }
            }
        });

    }

    private void getToken() {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    syncUser();
                }
            }
        });

    }

    private void goEvaluateWithQrCodeDialog() {
        Resources res = getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        String content = Api.qrUrl + userId;
        Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, 400, 400, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, logoBitmap, 0.15F);
        RxImageDialog dialog = new RxImageDialog(this);
        dialog.setImage(qrBitmap);
        dialog.setContent("扫码同步用户到微信");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            dialog.dismiss();
            if (!bleConnectedStatus()){
                Bundle bundle = new Bundle();
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle, HomeConnectActivity.class, false);
            }else {
                Bundle bundle = new Bundle();
                bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
                startTargetActivity(bundle,StartActivity.class,false);
            }
//            Bundle bundle = new Bundle();
//            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
//            startTargetActivity(bundle, ConnectDeviceActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void goEvaluateDialog() {
        if (!bleConnectedStatus()){
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectMainMode);
            startTargetActivity(bundle, HomeConnectActivity.class, false);
        }else {
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectEvaluateMode);
            startTargetActivity(bundle,StartActivity.class,false);
        }
//        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
//        dialog.setContent("是否开始评估");
//        dialog.setCancel("取消");
//        dialog.setSure("开始评估");
//        Global.USER_MODE = true;
//        dialog.setSureListener(v -> {
//            dialog.dismiss();
//            Bundle bundle = new Bundle();
//            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
//            startTargetActivity(bundle, ConnectDeviceActivity.class, true);
//        });
//        dialog.setCancelListener(v -> {
//            dialog.dismiss();
//            startTargetActivity(MainActivity.class, true);
//        });
//        dialog.setCanceledOnTouchOutside(false);
//        dialog.setCancelable(false);
//        dialog.show();
    }
    private boolean verifyData() {
        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请输入姓名");
            return false;
        }
        age = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            ToastUtils.showShort("请输入年龄");
            return false;
        }
        if (Integer.parseInt(age) < 1 || Integer.parseInt(age) > 150) {
            ToastUtils.showShort("年龄不符合范围");
            return false;
        }
        weight = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            ToastUtils.showShort("请输入体重");
            return false;
        }
        if (Integer.parseInt(weight) < 35 || Integer.parseInt(weight) > 100) {
            ToastUtils.showShort("体重必须在35kg到100kg之间");
            return false;
        }
//        if (TextUtils.isEmpty(city)) {
//            ToastUtils.showShort("请选择个人所在地");
//            return;
//        }
        if (TextUtils.isEmpty(dateOfSurgery)) {
            ToastUtils.showShort("请选择手术时间");
            return false;
        }
        Resources res = getResources();
        String[] array = res.getStringArray(R.array.diagnostic_result_list);
        if (selectPosition <= 0 || (selectPosition == array.length-1 && TextUtils.isEmpty(otherDiagnosis))) {
            ToastUtils.showShort("请选择手术名称");
            return false;
        }
        return true;
    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
//                    userBean.setDate(new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss"));
                    userBean.setDate(new DateTime(year, month, dayOfMonth, 0, 0).toString("yyyy-MM-dd HH:mm:ss"));
                    dateOfSurgery = year + "-" + month + "-" + dayOfMonth;
                    tvDate.setText(dateOfSurgery);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}
