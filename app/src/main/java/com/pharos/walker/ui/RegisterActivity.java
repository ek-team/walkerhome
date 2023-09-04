package com.pharos.walker.ui;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.DoctorBean;
import com.pharos.walker.beans.HospitalBean;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.customview.rxdialog.RxImageDialog;
import com.pharos.walker.database.UserManager;
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
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.util.ConvertUtils;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/6/1
 * Describe:
 */
public class RegisterActivity extends BaseActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tv_step_1)
    TextView tvStep1;
    @BindView(R.id.tv_step_2)
    TextView tvStep2;
    @BindView(R.id.tv_step_3)
    TextView tvStep3;
    @BindView(R.id.et_account)
    EditText etAccount;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.iv_pwd_see)
    ImageView ivPwdSee;
    @BindView(R.id.et_confirm_password)
    EditText etConfirmPassword;
    @BindView(R.id.iv_confirm_pwd_see)
    ImageView ivConfirmPwdSee;
    @BindView(R.id.tv_step_1_ok)
    TextView tvStep1Ok;
    @BindView(R.id.layout_sub_step_1)
    LinearLayout layoutSubStep1;
    @BindView(R.id.layout_step_1)
    RelativeLayout layoutStep1;
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
    @BindView(R.id.tv_step_2_previous)
    TextView tvStep2Previous;
    @BindView(R.id.tv_step_2_ok)
    TextView tvStep2Ok;
    @BindView(R.id.layout_step_2)
    LinearLayout layoutStep2;
    @BindView(R.id.tv_hospital_address)
    TextView tvHospitalAddress;
    @BindView(R.id.tv_hospital_name)
    TextView tvHospitalName;
    @BindView(R.id.tv_doctor)
    TextView tvDoctor;
    @BindView(R.id.tv_step_3_previous)
    TextView tvStep3Previous;
    @BindView(R.id.tv_step_3_finish)
    TextView tvStep3Finish;
    @BindView(R.id.layout_step_3)
    LinearLayout layoutStep3;
    @BindView(R.id.tv_date)
    TextView tvDate;
    @BindView(R.id.et_CaseHistoryNum)
    EditText etCaseHistoryNum;
    @BindView(R.id.sp_select_diagnostic_result)
    Spinner spinner;
    @BindView(R.id.cb_password_manager)
    CheckBox cbPasswordManager;
    @BindView(R.id.tv_step_skip)
    TextView tvStepSkip;
    private String provinceName;
    private String cityName;
    private String countyName;
    private int sex = 1; //性别  男-1  女-0
    private String account;
    private String password;
    private String name;
    private String age;
    private String weight;
    private String city;
    private String doctor;
    private String mobile;
    private String openid;
    private List<Province> provincesList;
    private AlertDialog provinceDialog;
    private Context context;
    private boolean isHidenPwd = true;
    private boolean isHidenConfirmPwd = true;
    private DatePickerDialog mDatePickerDialog;
    private UserBean userBean;
    private String hospitalAddress;
    private String hospitalName;
    private int selectPosition;
    private String dateOfSurgery;
    private int SELECT_PERSON_ADDRESS = 0;
    private int SELECT_HOSPITAL_ADDRESS = 1;
    private int HOSPITAL_REQ = 1;
    private int DOCTOR_REQ = 2;
    private int hospitalId = 0;
    private long userId = 0;
    private List<String> doctorList = new ArrayList<>();

    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        context = this;
        initView();
        initData();
    }


    @TargetApi(Build.VERSION_CODES.M)
    private void initView() {
        etPassword.setFocusableInTouchMode(false);
        etConfirmPassword.setFocusableInTouchMode(false);
        etPassword.setHintTextColor(getColor(R.color.white_30));
        etConfirmPassword.setHintTextColor(getColor(R.color.white_30));
        cbPasswordManager.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    etPassword.setFocusableInTouchMode(true);
                    etConfirmPassword.setFocusableInTouchMode(true);
                    etConfirmPassword.requestFocus();
                    etPassword.requestFocus();
                    etPassword.setHintTextColor(getColor(R.color.white_88));
                    etConfirmPassword.setHintTextColor(getColor(R.color.white_88));
                }else {
                    etPassword.setFocusableInTouchMode(false);
                    etConfirmPassword.setFocusableInTouchMode(false);
                    etPassword.clearFocus();
                    etConfirmPassword.clearFocus();
                    etPassword.setHintTextColor(getColor(R.color.white_30));
                    etConfirmPassword.setHintTextColor(getColor(R.color.white_30));
                }
            }
        });
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
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                if (view instanceof TextView) {
                    ((TextView) view).setTextColor(getResources().getColor(R.color.white_88));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        initDataTime();
    }

    private void initData() {
        if (userBean == null) {
            userBean = new UserBean();
        }

    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_register;
    }

    @OnClick({R.id.iv_back, R.id.iv_pwd_see, R.id.tv_step_1_ok, R.id.tv_location, R.id.tv_hospital_address, R.id.tv_hospital_name, R.id.tv_doctor,
            R.id.tv_step_2_previous, R.id.tv_step_3_previous, R.id.tv_step_3_finish, R.id.tv_step_2_ok, R.id.iv_confirm_pwd_see, R.id.tv_date,R.id.tv_step_skip})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_pwd_see:
                if (isHidenPwd) {
                    etPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                } else {
                    etPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenPwd = !isHidenPwd;
                break;
            case R.id.tv_step_1_ok:
                firstCommit();
                break;
            case R.id.tv_step_skip:
                etAccount.setText("");
                etConfirmPassword.setText("");
                etPassword.setText("");
                setDisplayView(layoutStep2, tvStep2);
                break;
            case R.id.tv_hospital_address:
                addressSelect(SELECT_HOSPITAL_ADDRESS);
                break;
            case R.id.tv_location:
                addressSelect(SELECT_PERSON_ADDRESS);
                break;
            case R.id.tv_hospital_name:
                if (NetworkUtils.isConnected()) {
                    getHospitalByAddress();
                } else {
                    ToastUtils.showShort("网络未连接");
                }
                break;
            case R.id.tv_step_3_previous:
                setDisplayView(layoutStep2, tvStep2);
                break;
            case R.id.tv_step_3_finish:
                finishCommit();
                break;
            case R.id.tv_step_2_previous:
                setDisplayView(layoutStep1, tvStep1);
                break;
            case R.id.tv_step_2_ok:
                secondCommit();
                break;
            case R.id.iv_confirm_pwd_see:
                if (isHidenConfirmPwd) {
                    etConfirmPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());//显示密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_enable);
                } else {
                    etConfirmPassword.setTransformationMethod(PasswordTransformationMethod.getInstance());//隐藏密码
                    ivConfirmPwdSee.setImageResource(R.drawable.ic_pwd_unable);
                }
                isHidenConfirmPwd = !isHidenConfirmPwd;
                break;
            case R.id.tv_date:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
                break;
            case R.id.tv_doctor:
                selectDoctor();
                break;
        }
    }


    private void firstCommit() {
        account = etAccount.getText().toString().trim();
//        if (TextUtils.isEmpty(account)) {
//            ToastUtils.showShort("请输入用户名");
//            return;
//        }
//        if (account.length() < 2 || account.length() > 25) {
//            ToastUtils.showShort("用户名为2-25位字符");
//            return;
//        }

        password = etPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(password)) {
//            ToastUtils.showShort("请输入密码");
//            return;
//        }
        if (!TextUtils.isEmpty(password) && password.length() < 4 || password.length() > 20) {
            ToastUtils.showShort("密码为4-20位字符");
            return;
        }

        String confirm_password = etConfirmPassword.getText().toString().trim();
//        if (TextUtils.isEmpty(confirm_password)) {
//            ToastUtils.showShort("请确认密码");
//            return;
//        }
        if (!password.equals(confirm_password)) {
            ToastUtils.showShort("两次密码输入不一致");
            return;
        }
        setDisplayView(layoutStep2, tvStep2);
    }

    private void secondCommit() {
        name = etName.getText().toString().trim();
        if (TextUtils.isEmpty(name)) {
            ToastUtils.showShort("请输入姓名");
            return;
        }
        age = etAge.getText().toString().trim();
        if (TextUtils.isEmpty(age)) {
            ToastUtils.showShort("请输入年龄");
            return;
        }
        if (Integer.parseInt(age) < 1 || Integer.parseInt(age) > 150) {
            ToastUtils.showShort("年龄不符合范围");
            return;
        }
        weight = etWeight.getText().toString().trim();
        if (TextUtils.isEmpty(weight)) {
            ToastUtils.showShort("请输入体重");
            return;
        }
        if (Integer.parseInt(weight) < 35 || Integer.parseInt(weight) > 100) {
            ToastUtils.showShort("体重必须在35kg到100kg之间");
            return;
        }
//        if (TextUtils.isEmpty(city)) {
//            ToastUtils.showShort("请选择个人所在地");
//            return;
//        }
        if (TextUtils.isEmpty(dateOfSurgery)) {
            ToastUtils.showShort("请选择手术时间");
            return;
        }
        if (selectPosition <= 0) {
            ToastUtils.showShort("请选择手术名称");
            return;
        }
        setDisplayView(layoutStep3, tvStep3);
    }

    private void finishCommit() {
        if (TextUtils.isEmpty(etCaseHistoryNum.getText().toString())) {
            ToastUtils.showShort("请输入病历号");
            return;
        }
        userBean.setCaseHistoryNo(etCaseHistoryNum.getText().toString());
        userBean.setWeight(weight);
        userBean.setAccount(account);
        userBean.setPassword(password);
        userBean.setAddress(city);
        userBean.setAge(Integer.parseInt(age));
        if (TextUtils.isEmpty(hospitalAddress)) {
            hospitalAddress = SPHelper.getHospitalAddress();
        }
        userBean.setHospitalAddress(hospitalAddress);
        if (TextUtils.isEmpty(hospitalName)) {
            hospitalName = SPHelper.getHospitalName();
        }
        userBean.setHospitalName(hospitalName);
        userBean.setDoctor(doctor);
        userBean.setName(name);
        userBean.setSex(sex);
        Resources res = getResources();
        String[] array = res.getStringArray(R.array.diagnostic_result_list);
        userBean.setDiagnosis(array[selectPosition]);
        userBean.setTelephone(mobile);
        userId = SnowflakeIdUtil.getUniqueId();
        userBean.setUserId(userId);
        if (UserManager.getInstance().isUniqueValue(etCaseHistoryNum.getText().toString(), 0)) {
            try {
                UserManager.getInstance().insert(userBean, 0);
            } catch (SQLiteConstraintException e) {
                if (e.getMessage().contains("USER.CASE_HISTORY_NO")) {
                    ToastUtils.showShort("病历号重复了，请修改");
                }
                return;
            }
        } else {
            ToastUtils.showShort("病历号重复了，请修改");
            return;
        }
        Global.USER_MODE = true;
        if (NetworkUtils.isConnected()) {
            showWaiting("提示", "正在注册...");
            syncUser();
        } else {
//            ToastUtils.showShort("网络不可用");
            goEvaluateDialog();
        }
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
        dialog.setContent("微信扫描同步设备");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, true);
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
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否开始评估");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        Global.USER_MODE = true;
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE, Global.ConnectUserMode);
            startTargetActivity(bundle, ConnectDeviceActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class, true);
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void setDisplayView(LinearLayout layout, TextView textView) {
        if (!TextUtils.isEmpty(etAccount.getText().toString())){
            etName.setText(etAccount.getText());
        }
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }

    private void setDisplayView(RelativeLayout layout, TextView textView) {
        layoutStep1.setVisibility(View.GONE);
        layoutStep2.setVisibility(View.GONE);
        layoutStep3.setVisibility(View.GONE);
        tvStep1.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep2.setBackgroundResource(R.drawable.round_empty_bg);
        tvStep3.setBackgroundResource(R.drawable.round_empty_bg);
        layout.setVisibility(View.VISIBLE);
        textView.setBackgroundResource(R.drawable.round_orange_bg);

    }

    private void addressSelect(int selectStatus) {
        provincesList = new ArrayList<>();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        try {
            String json = ConvertUtils.toString(getAssets().open("city.json"));
            provincesList.addAll(new Gson().fromJson(json, new TypeToken<List<Province>>() {
            }.getType()));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (provincesList.size() != 0) {
            builder.setTitle("选择省份");
            List<String> provinces = new ArrayList<>();
            for (Province province : provincesList) {
                provinces.add(province.getAreaName());
            }
            builder.setItems(provinces.toArray(new String[provinces.size()]), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Province selectProvince = provincesList.get(which);
                    provinceName = selectProvince.getAreaName();

                    List<City> citiesList = selectProvince.getCities();
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择城市");
                    ArrayList<String> cities = new ArrayList<>();
                    for (City city : citiesList) {
                        cities.add(city.getAreaName());
                    }
                    builder.setItems(cities.toArray(new String[cities.size()]), (dialog12, which12) -> {
                        City selectCity = citiesList.get(which12);
                        cityName = citiesList.get(which12).getAreaName();

                        List<County> countiesList = selectCity.getThirds();
                        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
                        builder1.setTitle("选择区县");
                        ArrayList<String> counties = new ArrayList<>();
                        for (County county : countiesList) {
                            counties.add(county.getAreaName());
                        }
                        builder1.setItems(counties.toArray(new String[counties.size()]), (dialog1, which1) -> {
                            countyName = countiesList.get(which1).getAreaName();
                            city = provinceName + "/" + cityName + "/" + countyName;
                            hospitalAddress = provinceName + "/" + cityName + "/" + countyName;
                            if (selectStatus == SELECT_PERSON_ADDRESS) {
                                tvLocation.setText(city);
                            } else {
                                tvHospitalAddress.setText(hospitalAddress);
                            }
                        });
                        builder1.show();
                    });
                    builder.show();
                }
            });
            if (provinceDialog == null) {
                provinceDialog = builder.show();
            } else {
                if (!provinceDialog.isShowing()) {
                    provinceDialog = builder.show();
                }
            }
        }
    }

    private void getHospitalByAddress() {
        OkHttpUtils.getAsync(Api.getHospitalByAddress + "?province=" + provinceName + "&city=" + cityName + "&area=" + countyName, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Register Activity", "request result: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    HospitalBean hospitalBean = new Gson().fromJson(result, HospitalBean.class);
                    AlertDialog.Builder builder = new AlertDialog.Builder(context);
                    builder.setTitle("选择医院");
                    ArrayList<String> hospitals = new ArrayList<>();
                    ArrayList<Integer> hospitalIds = new ArrayList<>();
                    for (HospitalBean.DataBean dataBean : hospitalBean.getData()) {
                        hospitals.add(dataBean.getName());
                        hospitalIds.add(dataBean.getId());
                    }
                    builder.setItems(hospitals.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            hospitalName = hospitals.get(which);
                            hospitalId = hospitalIds.get(which);
                            tvHospitalName.setText(hospitalName);
                            if (NetworkUtils.isConnected()) {
                                getDoctorByHospital(hospitalId);
                            } else {
                                ToastUtils.showShort("网络未连接");
                            }

                        }
                    });
                    builder.show();
                } else if (code == 401) {
                    getToken(HOSPITAL_REQ);
                }

            }
        });
    }

    private void getDoctorByHospital(int hospitalId) {
        OkHttpUtils.getAsync(Api.getDoctorByHospital + "?id=" + hospitalId, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Register Activity", "doctor request result: " + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0) {
                    DoctorBean doctorBean = new Gson().fromJson(result, DoctorBean.class);
                    if (doctorList.size() > 0)
                        doctorList.clear();
                    for (DoctorBean.DataBean dataBean : doctorBean.getData()) {
                        doctorList.add(dataBean.getNickname());
                    }
                    tvDoctor.setText("请选择");
                } else if (code == 401) {
                    getToken(DOCTOR_REQ);
                }

            }
        });
    }

    private void getToken(int status) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                ToastUtils.showShort(e.getMessage());

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);

                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (status == HOSPITAL_REQ) {
                        getHospitalByAddress();
                    } else if (status == DOCTOR_REQ) {
                        getDoctorByHospital(hospitalId);
                    }
                } else {
                    ToastUtils.showShort("Token 获取失败");
                }

            }
        });

    }

    private void selectDoctor() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("选择医生");
        builder.setItems(doctorList.toArray(new String[0]), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                doctor = doctorList.get(which);
                tvDoctor.setText(doctor);
            }
        });
        builder.show();
    }

    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(context,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
//                    DateTime curDate = DateTime.now();
//                    if (a.isAfter(curDate)) {
//                        year = curDate.getYear();
//                        month = curDate.getMonthOfYear();
//                        dayOfMonth = curDate.getDayOfMonth();
//                    }
                    userBean.setDate(new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss"));
                    dateOfSurgery = year + "-" + month + "-" + dayOfMonth;
                    tvDate.setText(dateOfSurgery);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }
}
