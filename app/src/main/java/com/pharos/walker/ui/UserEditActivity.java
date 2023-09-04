package com.pharos.walker.ui;

import android.app.DatePickerDialog;
import android.content.res.Resources;
import android.database.sqlite.SQLiteConstraintException;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.RequiresApi;

import com.pharos.walker.MainActivity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.R;
import com.pharos.walker.adapter.AgeArrayAdapter;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.RegexUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.ToastUtils;

import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zhanglun on 2021/4/25
 * Describe:
 */
public class UserEditActivity extends BaseActivity {
    @BindView(R.id.radioButton)
    RadioButton txtMan;
    @BindView(R.id.radioButton2)
    RadioButton txtWoman;
    @BindView(R.id.txtSex)
    RadioGroup txtSex;
    @BindView(R.id.txtUserName)
    EditText txtUserName;
    @BindView(R.id.txtDiag)
    EditText txtDiag;
    @BindView(R.id.txtPhone)
    EditText txtPhone;
    @BindView(R.id.textView27)
    TextView textView27;
    @BindView(R.id.tv_doctor)
    EditText tvDoctor;
    @BindView(R.id.cmbAge)
    Spinner cmbAge;
    @BindView(R.id.txtCaseHistoryNO)
    EditText txtCaseHistoryNO;
    @BindView(R.id.tv_weight)
    EditText tvWeight;
    @BindView(R.id.txtUserDate)
    TextView txtUserDate;
    @BindView(R.id.sp_select_diagnostic_result)
    Spinner spinner;
    @BindView(R.id.btnUserEditReturn)
    Button btnUserEditReturn;
    @BindView(R.id.btnUserEditSave)
    Button btnUserEditSave;
    private DatePickerDialog mDatePickerDialog;
    private List<Integer> ageList = null;
    private int mode = 0;//0 新增用户 1更新 2查看
    private UserBean userBean;
    private UserManager mUserManager;
    private String sickType;
    private int selectPosition = -1;
    @Override
    protected void initialize(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        initData();
        initView();
        initDataTime();
        initAgeAdapter();

    }

    private void initView() {
        if (mode == 1 || mode == 2) {
            txtUserName.setText(userBean.getName());
            txtDiag.setText(userBean.getDiagnosis());
            txtPhone.setText(userBean.getTelephone());
            if (userBean.getSex() == 1) {
                txtMan.setChecked(true);
                txtWoman.setChecked(false);
            } else {
                txtMan.setChecked(false);
                txtWoman.setChecked(true);
            }
            tvDoctor.setText(userBean.getDoctor());
            txtCaseHistoryNO.setText(userBean.getCaseHistoryNo());
            txtUserDate.setText(DateFormatUtil.getString(userBean.getDate()));
            tvWeight.setText(userBean.getWeight());
        }
        if (mode == 2) {
            txtUserName.setFocusable(false);
            txtCaseHistoryNO.setFocusable(false);
            cmbAge.setEnabled(false);
            txtUserDate.setEnabled(false);
            txtSex.setFocusable(false);
            txtMan.setClickable(false);
            txtWoman.setClickable(false);
            tvDoctor.setFocusable(false);
            txtPhone.setFocusable(false);
            txtDiag.setFocusable(false);
            spinner.setFocusable(false);
            btnUserEditSave.setVisibility(View.GONE);
        }
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectPosition = position;
                if (view instanceof TextView && position == 0) {
                    ((TextView) view).setTextColor(getColor(R.color.white_60));
                }else if (view instanceof TextView){
                    ((TextView) view).setTextColor(getColor(R.color.white));
                }
                if (selectPosition == 15){
                    txtDiag.setVisibility(View.VISIBLE);
                }else {
                    txtDiag.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initData() {
        mode = getIntent().getIntExtra("Mode", 0);
        mUserManager = UserManager.getInstance();

        if (mode == 0) {
            userBean = new UserBean();
        } else {
            userBean = SPHelper.getUser();
            sickType = userBean.getDiagnosis();
            selectPosition = MyUtil.getDiagnosticNum(sickType);
            spinner.setSelection(selectPosition);
        }
        if (ageList == null) {
            ageList = new ArrayList<>();
            for (int i = 10; i < 100; i++) {
                ageList.add(i);
            }
        }
    }

    private void initAgeAdapter() {
        ArrayAdapter<Integer> ageAdapter = new AgeArrayAdapter(UserEditActivity.this, ageList);
        ageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        cmbAge.setAdapter(ageAdapter);

        cmbAge.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) view).setTextColor(getColor(R.color.white));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if (mode == 1 || mode == 2) {
            cmbAge.setSelection(userBean.getAge() - 10);
        } else {
            cmbAge.setSelection(40);
        }
    }
    private void initDataTime() {
        Calendar c = Calendar.getInstance();
        mDatePickerDialog = new DatePickerDialog(UserEditActivity.this,
                // 绑定监听器
                (view, year, monthOfYear, dayOfMonth) -> {
                    int month = monthOfYear + 1;
                    DateTime a = new DateTime(year, month, dayOfMonth, 0, 0);
                    DateTime curDate = DateTime.now();
                    if (a.isAfter(curDate)) {
                        year = curDate.getYear();
                        month = curDate.getMonthOfYear();
                        dayOfMonth = curDate.getDayOfMonth();
                    }
                    userBean.setDate(new DateTime(year, month, dayOfMonth, DateTime.now().getHourOfDay(), DateTime.now().getMinuteOfHour()).toString("yyyy-MM-dd HH:mm:ss"));
                    txtUserDate.setText(year + "-" + month + "-" + dayOfMonth);
                }
                // 设置初始日期
                , c.get(Calendar.YEAR), c.get(Calendar.MONTH),
                c.get(Calendar.DAY_OF_MONTH));
    }



    @Override
    protected int getLayoutResId() {
        return R.layout.activity_user_edit;
    }

    @OnClick({R.id.btnUserEditReturn, R.id.btnUserEditSave, R.id.txtUserDate})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.btnUserEditReturn:
                finish();
                break;
            case R.id.btnUserEditSave:
                saveUser();
                break;
            case  R.id.txtUserDate:
                if (mDatePickerDialog != null) {
                    mDatePickerDialog.show();
                }
                break;
        }
    }
    private void saveUser() {
        boolean valid = ValidData();
        if (valid) {
            String caseHistoryNum = txtCaseHistoryNO.getText().toString();
            userBean.setName(txtUserName.getText().toString());
            userBean.setCaseHistoryNo(caseHistoryNum);
            userBean.setAge(Integer.parseInt(cmbAge.getSelectedItem().toString()));
            String diag = txtDiag.getText().toString();
            userBean.setWeight(tvWeight.getText().toString());
//            userBean.setRemark(txtDiag.getText().toString());
            if (txtSex.getCheckedRadioButtonId() == txtWoman.getId()) {
                userBean.setSex(0);
            } else {
                userBean.setSex(1);
            }
            userBean.setDoctor(tvDoctor.getText().toString());
            userBean.setTelephone(txtPhone.getText().toString());
            Resources res = getResources();
            String[] array = res.getStringArray(R.array.diagnostic_result_list);
            userBean.setDiagnosis(array[selectPosition]);
            if (mUserManager.isUniqueValue(caseHistoryNum,mode)){
                try {
                    mUserManager.insert(userBean,mode);
                }catch (SQLiteConstraintException e){
                    if (e.getMessage().contains("USER.CASE_HISTORY_NO")){
                        ToastUtils.showShort("病历号重复了，请修改");
                    }
                    return;
                }
            }else {
                ToastUtils.showShort("病历号重复了，请修改");
                return;
            }
            if(mode == 0){
                goEvaluateDialog();
            }else {
                finish();
            }

        }
    }
    private void goEvaluateDialog(){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("是否开始评估");
        dialog.setCancel("取消");
        dialog.setSure("开始评估");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle = new Bundle();
            bundle.putInt(AppKeyManager.EXTRA_CONNECT_MODE,Global.ConnectUserMode);
            startTargetActivity(bundle,ConnectDeviceActivity.class,true);
        });
        dialog.setCancelListener(v -> {
            dialog.dismiss();
            startTargetActivity(MainActivity.class,true);
        });
        dialog.show();
    }
    private boolean ValidData() {
        if (txtUserName.getText().toString().trim().isEmpty()) {
            Toast.makeText(UserEditActivity.this, "请填写姓名!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!RegexUtils.isName(txtUserName.getText().toString())) {
            Toast.makeText(UserEditActivity.this, "姓名不正确!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(txtCaseHistoryNO.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "请填写病历号!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (!(txtPhone.getText().toString().startsWith("1") && txtPhone.getText().toString().length() == 11) && !TextUtils.isEmpty(txtPhone.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "手机号不正确!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(tvWeight.getText())) {
            Toast.makeText(UserEditActivity.this, "请填写体重!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (txtSex.getCheckedRadioButtonId() != txtMan.getId() && txtSex.getCheckedRadioButtonId() != txtWoman.getId()) {
            Toast.makeText(UserEditActivity.this, "请选择性别!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (selectPosition <= 0) {
            Toast.makeText(UserEditActivity.this, "请填写诊断结果!", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (TextUtils.isEmpty(txtUserDate.getText().toString().trim())) {
            Toast.makeText(UserEditActivity.this, "请填写手术日期!", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
