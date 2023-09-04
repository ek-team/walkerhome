package com.pharos.walker.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.beans.OtherFeedbackBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.TokenBean;
import com.pharos.walker.beans.TrainDataEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.beans.UserTrainRecordEntity;
import com.pharos.walker.constants.Api;
import com.pharos.walker.constants.AppKeyManager;
import com.pharos.walker.constants.Global;
import com.pharos.walker.constants.MessageEvent;
import com.pharos.walker.customview.bubble.BubbleSeekBar;
import com.pharos.walker.customview.rxdialog.RxDialogSureCancel;
import com.pharos.walker.database.ActivationCodeManager;
import com.pharos.walker.database.EvaluateManager;
import com.pharos.walker.database.OtherFeedbackManager;
import com.pharos.walker.database.SubPlanManager;
import com.pharos.walker.database.TrainDataManager;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.database.UserManager;
import com.pharos.walker.database.UserTrainRecordManager;
import com.pharos.walker.error.CrashHandler;
import com.pharos.walker.utils.DateFormatUtil;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.NetworkUtils;
import com.pharos.walker.utils.OkHttpUtils;
import com.pharos.walker.utils.SPHelper;
import com.pharos.walker.utils.SnowflakeIdUtil;
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

public class FeedbackOtherActivity extends Activity {
    @BindView(R.id.tv_complete_rate)
    TextView tvCompleteRate;
    @BindView(R.id.ratingbar_complete)
    RatingBar ratingbarCompleteScore;
    @BindView(R.id.tv_right_rate)
    TextView tvRightRate;
    @BindView(R.id.ratingbar_score)
    RatingBar ratingbarScore;
    @BindView(R.id.ll_step1)
    LinearLayout llStep1;
    @BindView(R.id.seek_bar)
    BubbleSeekBar seekBar;
    @BindView(R.id.img_low)
    ImageView imgLow;
    @BindView(R.id.tv_low)
    TextView tvLow;
    @BindView(R.id.img_middle)
    ImageView imgMiddle;
    @BindView(R.id.tv_middle)
    TextView tvMiddle;
    @BindView(R.id.img_high)
    ImageView imgHigh;
    @BindView(R.id.tv_high)
    TextView tvHigh;
    @BindView(R.id.ll_step2)
    LinearLayout llStep2;
    @BindView(R.id.chk_adverse_reactions1)
    CheckBox chkAdverseReactions1;
    @BindView(R.id.chk_adverse_reactions2)
    CheckBox chkAdverseReactions2;
    @BindView(R.id.chk_adverse_reactions3)
    CheckBox chkAdverseReactions3;
    @BindView(R.id.chk_adverse_reactions4)
    CheckBox chkAdverseReactions4;
    @BindView(R.id.tv_warning)
    TextView tvWarning;
    @BindView(R.id.ll_step3)
    LinearLayout llStep3;
    @BindView(R.id.tv_commit_save)
    TextView tvCommitSave;
    @BindView(R.id.tv_trained_day)
    TextView tvTrainedDay;
    @BindView(R.id.chk_adverse_reactions5)
    CheckBox chkAdverseReactions5;
    @BindView(R.id.ed_other_detail)
    EditText edOtherDetail;
    @BindView(R.id.img_value_select)
    ImageView imgValueSelect;
    @BindView(R.id.ll_select_value)
    LinearLayout llSelectValue;
    // 疼痛反馈（0到10,0到3为轻度疼痛，4到7为中度疼痛，8到10为重度疼痛）
    private int painFeedbackLevel = 0;
    private float trainScore;
    private float completeScore;
    private int trainTime;
    private int effectiveTime;
    private int warningTime;
    private int loadWeight;
    private String adverseReactions = "";
    private String adverseReactions1 = "";
    private String adverseReactions2 = "";
    private String adverseReactions3 = "";
    private UserBean userBean;
    private boolean isSave = false;
    private boolean isGenerateRecord = false;
    private ArrayList<TrainDataEntity> trainDataEntityList;
    private static final int TRAIN_REQ = 1;
    private int USER_REQ = 0;
    private int PLAN_REQ = 1;
    private int TRAIN_DATA_REQ = 2;
    private int TRAIN_DATA_TO_LOCAL_REQ = 3;
    private int EVALUATE_DATA_REQ = 5;
    private int EVALUATE_DATA_TO_LOCAL_REQ = 6;
    float completeRate = 0;
    float rightRate = 0;
    private Gson gson = new Gson();
    private boolean isSelectedFeedback = false;
    private LinkedHashSet<String> linkedHashSet = new LinkedHashSet<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        View dialogView = LayoutInflater.from(this).inflate(R.layout.activity_feedback_other, null);
        setContentView(dialogView);
        Window window;
        window = getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
//        params.systemUiVisibility = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
        params.height = 500;
        params.width = 1220;
        window.setAttributes(params);
        ButterKnife.bind(this);
        EventBus.getDefault().register(this);
        initData();
        initView();
    }

    private void initData() {
        if (getIntent().getExtras() != null) {
            Bundle bundle = getIntent().getExtras();
            trainScore = bundle.getFloat(AppKeyManager.EXTRA_SCORE, 0);
            completeScore = bundle.getFloat(AppKeyManager.EXTRA_COMPLETE_RATE, 0);
            rightRate = bundle.getFloat(AppKeyManager.EXTRA_RIGHT_RATE, 0);
            completeRate = bundle.getFloat(AppKeyManager.EXTRA_COMPLETE_SOURCE, 0);
            ratingbarScore.setRating(trainScore);
            ratingbarCompleteScore.setRating(completeScore);
            trainTime = bundle.getInt(AppKeyManager.EXTRA_TRAIN_TIME, 0);
            loadWeight = bundle.getInt(AppKeyManager.EXTRA_WEIGHT, 0);
            effectiveTime = bundle.getInt(AppKeyManager.EXTRA_EFFECTIVE_TIME, 0);
            warningTime = bundle.getInt(AppKeyManager.EXTRA_NOTE_ERRORNUMBER, 0);
            trainDataEntityList = bundle.getParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY);
        }
        userBean = SPHelper.getUser();
        List<OtherFeedbackBean> list = OtherFeedbackManager.getInstance().loadAll();
        for (OtherFeedbackBean otherFeedbackBean : list){
            linkedHashSet.add(otherFeedbackBean.getValue());
        }
    }

    private void initView() {
        //取控件当前的布局参数
//        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)ratingbarCompleteScore.getLayoutParams();
//        params.width = ViewGroup.LayoutParams.WRAP_CONTENT;
//        params.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        ratingbarCompleteScore.setLayoutParams(params);
//        LinearLayout.LayoutParams params1 = (LinearLayout.LayoutParams)ratingbarScore.getLayoutParams();
//        params1.width = 300;
//        params1.height = ViewGroup.LayoutParams.WRAP_CONTENT;
//        ratingbarScore.setLayoutParams(params1);
        llStep1.setVisibility(View.VISIBLE);
        int day = UserTrainRecordManager.getInstance().getTrainDay(SPHelper.getUserId());
        if (day == 0 || day == 1) {
            tvTrainedDay.setText("今天是第一天训练，请继续加油哦！");
        } else {
            tvTrainedDay.setText(MessageFormat.format("您已训练{0}天了，请继续保持!", day));
        }
        llStep2.setVisibility(View.GONE);
        llStep3.setVisibility(View.GONE);
        tvCommitSave.setText("下一步");
        tvCompleteRate.setText(getString(R.string.complete_rate) + "：" + (int) (completeRate * 100) + "%");
        tvRightRate.setText(getString(R.string.accuracy_rate) + "：" + (int) (rightRate * 100) + "%");
        chkAdverseReactions1.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions1 = chkAdverseReactions1.getText().toString() + ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            } else {
                adverseReactions1 = "";
            }
        });

        chkAdverseReactions2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions2 = chkAdverseReactions2.getText().toString() + ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            } else {
                adverseReactions2 = "";
            }
        });

        chkAdverseReactions3.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                adverseReactions3 = chkAdverseReactions3.getText().toString() + ",";
                tvWarning.setText(getString(R.string.text_feedback_tips));
                chkAdverseReactions4.setChecked(false);
            } else {
                adverseReactions3 = "";
            }
        });

        chkAdverseReactions4.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chkAdverseReactions1.setChecked(false);
                chkAdverseReactions2.setChecked(false);
                chkAdverseReactions3.setChecked(false);
                chkAdverseReactions5.setChecked(false);
                tvWarning.setText(getString(R.string.feedback_tip_1));
            }
        });
        chkAdverseReactions5.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                chkAdverseReactions4.setChecked(false);
                tvWarning.setText(getString(R.string.feedback_tip_1));
                edOtherDetail.setVisibility(View.VISIBLE);
                llSelectValue.setVisibility(View.VISIBLE);
            } else {
                edOtherDetail.setVisibility(View.GONE);
                llSelectValue.setVisibility(View.GONE);
                edOtherDetail.setText("");
            }
        });
        seekBar.setThumbColor(Color.GREEN);
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                isSelectedFeedback = true;
                if (progress <= 3) {
                    seekBar.setThumbColor(Color.GREEN);
//                    seekBar.setSecondTrackColor(Color.GREEN);
//                    seekBar.setThumbColor(Color.GREEN);
//                    seekBar.setBubbleColor(Color.GREEN);
                } else if (progress <= 6) {
                    seekBar.setThumbColor(Color.YELLOW);
//                    seekBar.setSecondTrackColor(Color.YELLOW);
//                    seekBar.setThumbColor(Color.YELLOW);
//                    seekBar.setBubbleColor(Color.YELLOW);
                } else {
                    seekBar.setThumbColor(Color.RED);
//                    seekBar.setSecondTrackColor(Color.RED);
//                    seekBar.setThumbColor(Color.RED);
//                    seekBar.setBubbleColor(Color.RED);
                }
            }

            @Override
            public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
                isSelectedFeedback = true;
                if (progress <= 3) {
                    seekBar.setThumbColor(Color.GREEN);
//                    seekBar.setSecondTrackColor(Color.GREEN);
//                    seekBar.setThumbColor(Color.GREEN);
//                    seekBar.setBubbleColor(Color.GREEN);
                } else if (progress <= 6) {
                    seekBar.setThumbColor(Color.YELLOW);
//                    seekBar.setSecondTrackColor(Color.YELLOW);
//                    seekBar.setThumbColor(Color.YELLOW);
//                    seekBar.setBubbleColor(Color.YELLOW);
                } else {
                    seekBar.setThumbColor(Color.RED);
//                    seekBar.setSecondTrackColor(Color.RED);
//                    seekBar.setThumbColor(Color.RED);
//                    seekBar.setBubbleColor(Color.RED);
                }
            }

            @Override
            public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {
                painFeedbackLevel = progress;
//                if (progress <= 3){
//                    seekBar.setSecondTrackColor(Color.GREEN);
//                    seekBar.setThumbColor(Color.GREEN);
//                    seekBar.setBubbleColor(Color.GREEN);
//                }else if (progress <= 6){
//                    seekBar.setSecondTrackColor(Color.YELLOW);
//                    seekBar.setThumbColor(Color.YELLOW);
//                    seekBar.setBubbleColor(Color.YELLOW);
//                }else{
//                    seekBar.setSecondTrackColor(Color.RED);
//                    seekBar.setThumbColor(Color.RED);
//                    seekBar.setBubbleColor(Color.RED);
//                }
            }
        });
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        switch (event.getAction()) {
            case MessageEvent.ACTION_REQ_FAIL_SINGLE:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("提交数据请求失败");
                UserBean userBean = UserManager.getInstance().loadByUserId(SPHelper.getUserId());
                userBean.setIsRecordUpdate(0);
                UserManager.getInstance().update(userBean);
                startTargetActivity(MainActivity.class, true);
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT_SINGLE:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("提交成功");
                finishDialog(null);
                break;
            case MessageEvent.ACTION_TOKEN_REQ_FAIL:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("Token获取失败");
                finishDialog(null);
                break;
            case MessageEvent.ACTION_SYNC_USER_RESULT:
                int codeUser = (int) event.getData();
                if (codeUser == 401) {
                    getToken(USER_REQ);
                } else if (codeUser == 0) {
                    syncPlan();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:
                int codePlan = (int) event.getData();
                if (codePlan == 401) {
                    getToken(PLAN_REQ);
                } else if (codePlan == 0) {
                    getTrainRecord(1);
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT:
                int codeTrainData = (int) event.getData();
                if (codeTrainData == 401) {
                    getToken(TRAIN_DATA_REQ);
                } else if (codeTrainData == 0) {
                    getEvaluateRecord(1);

                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT:
                int codeTrainData1 = (int) event.getData();
                if (codeTrainData1 == 401) {
                    getToken(TRAIN_DATA_TO_LOCAL_REQ);
                } else if (codeTrainData1 == 0) {
                    syncTrainRecord();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_REQ_FAIL:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("接口请求失败");
                finishDialog(null);
                break;
            case MessageEvent.ACTION_SYNC_EVALUATE_RECORD_RESULT:
                int codeEvaluateData = (int) event.getData();
                if (codeEvaluateData == 401) {
                    getToken(EVALUATE_DATA_REQ);
                } else if (codeEvaluateData == 0) {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                        ToastUtils.showShort("保存成功");
                        Bundle bundle = new Bundle();
                        bundle.putInt("ResultTips", 1);
                        finishDialog(bundle);
                    }

                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                }
                break;
            case MessageEvent.ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT:
                int codeEvaluateData1 = (int) event.getData();
                if (codeEvaluateData1 == 401) {
                    getToken(EVALUATE_DATA_TO_LOCAL_REQ);
                } else if (codeEvaluateData1 == 0) {
                    syncEvaluateRecord();
                } else {
                    if (progressDialog != null && progressDialog.isShowing()) {
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                }
                break;
            default:
                break;
        }
    }

    private void finishDialog(Bundle bundle) {
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("训练数据已保存，可在微信公众号查看");
        dialog.setCancel("返回主页");
        dialog.setSure("查看记录");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle1 = new Bundle();
            bundle1.putInt("feedback", 1);
            bundle1.putParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY, trainDataEntityList);
            startTargetActivity(bundle1, OnceRecordActivity.class, true);
        });
        dialog.setCancelListener(v -> {
            if (bundle != null) {
                bundle.putInt(AppKeyManager.EXTRA_BACK_HOME_SETTING, Global.HomeUser);
                startTargetActivity(bundle, MainActivity.class, true);
            } else {
                Bundle bundle1 = new Bundle();
                bundle1.putInt(AppKeyManager.EXTRA_BACK_HOME_SETTING, Global.HomeUser);
                startTargetActivity(bundle1, MainActivity.class, true);
            }
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }

    private void saveData() {
        UserTrainRecordEntity trainRecordEntity = new UserTrainRecordEntity();
        trainRecordEntity.setScore((int) trainScore);
        trainRecordEntity.setWarningTime(warningTime);
        trainRecordEntity.setDiagnostic(userBean.getDiagnosis());
        trainRecordEntity.setTargetLoad(loadWeight);
        trainRecordEntity.setSuccessTime(effectiveTime);
        trainRecordEntity.setPainLevel(painFeedbackLevel);
        adverseReactions = adverseReactions1 + adverseReactions2 + adverseReactions3;
        if (!TextUtils.isEmpty(edOtherDetail.getText().toString())) {
            adverseReactions = adverseReactions + "其他（" + edOtherDetail.getText().toString() + "）";
        } else if (chkAdverseReactions5.isChecked()) {
            adverseReactions = adverseReactions + "其他";
        }
        trainRecordEntity.setAdverseReactions(adverseReactions);
        trainRecordEntity.setTrainTime(trainTime);
        trainRecordEntity.setUserId(SPHelper.getUserId());
        UserTrainRecordEntity recordEntity = UserTrainRecordManager.getInstance().insert(trainRecordEntity);
        if (trainDataEntityList != null) {
            TrainDataManager.getInstance().insertMany(trainDataEntityList);
            recordEntity.setTrainDataList(trainDataEntityList);
        }
        if (!TextUtils.isEmpty(edOtherDetail.getText().toString())){
            String otherValue  = edOtherDetail.getText().toString();
            OtherFeedbackBean otherFeedbackBean = new OtherFeedbackBean();
            otherFeedbackBean.setValue(otherValue);
            otherFeedbackBean.setKeyId(SnowflakeIdUtil.getUniqueId());
            otherFeedbackBean.setCreateDate(System.currentTimeMillis());
            if (ActivationCodeManager.getInstance().getCodeBean() != null){
                otherFeedbackBean.setMacAddress(ActivationCodeManager.getInstance().getCodeBean().getMacAddress());
            }
            OtherFeedbackManager.getInstance().insert(otherFeedbackBean);
        }
        isSave = true;
    }

    private void uploadTrainDataSingle(List<UserTrainRecordEntity> recordEntityList) {
        OkHttpUtils.postJsonAsync(Api.uploadTrainRecord, new Gson().toJson(recordEntityList), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL_SINGLE));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("SingleFeedback Activity", "--->训练数据上传结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,code));
                if (code == 0) {
                    try {
                        UserTrainRecordManager.getInstance().updateMasterTrainUploadStatus(recordEntityList);
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort(e.getMessage());
                        CrashHandler.getInstance().saveThrowableMessage("更新训练记录error：" + e.getMessage());
                    }
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, code));
                } else if (code == 401) {
                    getToken(TRAIN_REQ, recordEntityList);
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL_SINGLE));
                }

            }
        });
    }

    private void syncUser() {
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = gson.toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Feedback Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT, code));
                if (code == 0) {
                    UserManager.getInstance().updateUserUploadStatus(userBeans, Global.UploadNetStatus);
                }
            }
        });

    }

    private void syncPlan() {
        OkHttpUtils.getAsync(Api.getPlan + SPHelper.getUserId(), true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Feedback Activity", "--->计划获取结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401) {
                    getToken(PLAN_REQ);
                    return;
                }
                if (code == 0) {
                    ServerPlanEntity serverPlanEntity = gson.fromJson(result, ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus) {
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, gson.toJson(localPlan), new OkHttpUtils.DataCallBack() {
                            @Override
                            public void requestFailure(Request request, IOException e) {
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                            }

                            @Override
                            public void requestSuccess(String result) throws Exception {
                                Log.e("Feedback Activity", "--->计划同步结果" + result);
                                JSONObject toJsonObj = new JSONObject(result);
                                int code = toJsonObj.getInt("code");
                                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));

                            }
                        });
                    } else if (TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadStatus) {
                        boolean isLoadLocalUpdate = TrainPlanManager.getInstance().isLoadLocalUpdate(planEntityList.get(0).getPlanId()
                                , planEntityList.get(0).getUserId(), planEntityList.get(0).getLoad());//判断是否刷新本地子计划的标识
                        try {
                            if (isLoadLocalUpdate) {//如果网络端起始负重修改了，需要本地更新计划 不用插入网络端的数据
                                MyUtil.insertTemplate(planEntityList.get(0).getLoad());
                            }
                            for (PlanEntity planEntity : planEntityList) {
                                TrainPlanManager.getInstance().insert(planEntity);
                                if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0 && !isLoadLocalUpdate) {
                                    SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                                }
                            }

                        } catch (Exception e) {
                            if (progressDialog != null && progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            ToastUtils.showShort(e.getMessage());
                            CrashHandler.getInstance().saveThrowableMessage("更新训练计划error：" + e.getMessage());
                        }
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                    } else {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT, code));
                    }

                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }

    private void syncTrainRecord() {
        List<UserTrainRecordEntity> recordEntityList = UserTrainRecordManager.getInstance().loadMasterUpdate(SPHelper.getUserId());
        List<List<UserTrainRecordEntity>> listList = splitList(recordEntityList, pageSize);
        int pageNum = 0;
        uploadTrainData(pageNum, listList);

    }

    private void uploadTrainData(int pageNum, List<List<UserTrainRecordEntity>> listList) {
        if (listList.size() <= 0) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, 0));
            return;
        }
        OkHttpUtils.postJsonAsync(Api.uploadTrainRecord, gson.toJson(listList.get(pageNum)), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Feedback Activity", "--->训练数据上传结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401) {
                    getToken(TRAIN_DATA_REQ);
                    return;
                }
                boolean isDuplicate = false;
                if (code == 1) {
                    String msg = toJsonObj.getString("msg");
                    isDuplicate = msg.contains("Duplicate entry");
                }
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,code));
                if (code == 0 || isDuplicate) {
                    try {
                        UserTrainRecordManager.getInstance().updateMasterTrainUploadStatus(listList.get(pageNum));
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort(e.getMessage());
                        CrashHandler.getInstance().saveThrowableMessage("更新训练记录error：" + e.getMessage());
                    }
//                    for (int i = pageNum + 1;i < listList.size(); i++ ){
//                        uploadTrainData(i,listList);
//                    }
                    if (pageNum + 1 >= listList.size()) {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, isDuplicate ? 0 : code));
                    } else {
                        uploadTrainData(pageNum + 1, listList);
                    }
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT, code));
                }

            }
        });
    }

    private List<List<UserTrainRecordEntity>> splitList(List<UserTrainRecordEntity> list, int groupSize) {
        return Lists.partition(list, groupSize);
    }

    private int totalSize = 0;
    private final int pageSize = 4;
    private int page = 0;

    private void getTrainRecord(int pageNum) {
        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT, 0));
//        OkHttpUtils.getAsync(Api.getRecord + SPHelper.getUserId() + "?pageSize=" + pageSize + "&pageNum=" + pageNum, true, new OkHttpUtils.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
//            }
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                Log.e("Feedback Activity", "--->训练数据获取结果" + result);
//                JSONObject toJsonObj= new JSONObject(result);
//                int code = toJsonObj.getInt("code");
//                if (code == 0){
//                    ServerTrainDataBean serverTrainDataBean = gson.fromJson(result, ServerTrainDataBean.class);
//                    totalSize = serverTrainDataBean.getData().getTotal();
//                    page = totalSize/pageSize + (totalSize%pageSize == 0?0:1);//获取总页数
//                    for (UserTrainRecordEntity trainRecordEntity : serverTrainDataBean.getData().getRecords()){
//                        trainRecordEntity.setIsUpload(Global.UploadNetStatus);
//                        UserTrainRecordManager.getInstance().insertServerData(trainRecordEntity);
//                        List<TrainDataEntity> trainDataEntityList = trainRecordEntity.getTrainDataList();
//                        TrainDataManager.getInstance().insertMany(trainDataEntityList);
//                    }
//                    if (pageNum +1 > page){
//                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT,code));
//                    }else {
//                        getTrainRecord(pageNum + 1);
//                    }
//                }else {
//                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT,code));
//                }
////                for (int i = pageNum + 1; i < page; i++){
////                    getTrainRecord(i);
////                }
//
//
//            }
//        });
    }

    private int totalSize1 = 0;
    private final int pageSize1 = 4;
    private int page1 = 0;

    private void getEvaluateRecord(int pageNum) {
        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT, 0));
//        OkHttpUtils.getAsync(Api.getEvaluateRecord +"?userid=" +  SPHelper.getUserId() + "&pageSize=" + pageSize + "&pageNum=" + pageNum, true, new OkHttpUtils.DataCallBack() {
//            @Override
//            public void requestFailure(Request request, IOException e) {
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
//            }
//
//            @Override
//            public void requestSuccess(String result) throws Exception {
//                Log.e("Feedback Activity", "--->评估记录获取结果" + result);
//                JSONObject toJsonObj= new JSONObject(result);
//                int code = toJsonObj.getInt("code");
//                if (code == 0){
////                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT,code));
//                    ServerEvaluateEntity serverEvaluate = gson.fromJson(result,ServerEvaluateEntity.class);
//                    totalSize1 = serverEvaluate.getData().getTotal();
//                    page1 = totalSize1/pageSize1 + (totalSize1%pageSize1 == 0?0:1);//获取总页数
//                    if (serverEvaluate.getData() != null){
//                        for (EvaluateEntity evaluateEntity : serverEvaluate.getData().getRecords()){
//                            EvaluateManager.getInstance().insertServerEntity(evaluateEntity);
//                        }
//                    }
//                    if (pageNum +1 > page1){
//                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT,code));
//                    }else {
//                        getEvaluateRecord(pageNum + 1);
//                    }
//                }else {
//                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_TO_LOCAL_RESULT,code));
//                }
//
//            }
//        });
    }

    private void syncEvaluateRecord() {
        List<EvaluateEntity> recordEntityList = EvaluateManager.getInstance().loadNotUploadRecord(SPHelper.getUserId());
        List<List<EvaluateEntity>> listList = splitEvaluateList(recordEntityList, pageSize1);
        int pageNum = 0;
        uploadEvaluateData(pageNum, listList);
    }

    private void uploadEvaluateData(int pageNum, List<List<EvaluateEntity>> listList) {
        if (listList.size() <= 0) {
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_RESULT, 0));
            return;
        }
        OkHttpUtils.postJsonAsync(Api.uploadEvaluateRecord, gson.toJson(listList.get(pageNum)), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Feedback Activity", "--->评估记录上传结果" + result);
                JSONObject toJsonObj = new JSONObject(result);
                int code = toJsonObj.getInt("code");
                boolean isDuplicate = false;
                if (code == 1) {
                    String msg = toJsonObj.getString("msg");
                    isDuplicate = msg.contains("Duplicate entry");
                }
                if (code == 0 || isDuplicate) {
                    try {
                        EvaluateManager.getInstance().updateEvaluateRecordStatus(listList.get(pageNum));
                    } catch (Exception e) {
                        if (progressDialog != null && progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort(e.getMessage());
                        CrashHandler.getInstance().saveThrowableMessage("更新训练记录error：" + e.getMessage());
                    }
//                    for (int i = pageNum + 1;i < listList.size(); i++ ){
//                        uploadEvaluateData(i,listList);
//                    }
                    if (pageNum + 1 >= listList.size()) {
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_RESULT, isDuplicate ? 0 : code));
                    } else {
                        uploadEvaluateData(pageNum + 1, listList);
                    }
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_EVALUATE_RECORD_RESULT, code));
                }

            }
        });
    }

    private List<List<EvaluateEntity>> splitEvaluateList(List<EvaluateEntity> list, int groupSize) {
        return Lists.partition(list, groupSize);
    }

    private void getToken(int flag, List<UserTrainRecordEntity> recordEntityList) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_TOKEN_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == TRAIN_REQ) {
                        uploadTrainDataSingle(recordEntityList);
                    }
                } else {
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_TOKEN_REQ_FAIL));
                }
            }
        });
    }

    private void getToken(int flag) {
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials", true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result, TokenBean.class);
                if (tokenBean.getCode() == 0) {
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == PLAN_REQ) {
                        syncPlan();
                    } else if (flag == USER_REQ) {
                        syncUser();
                    } else if (flag == TRAIN_DATA_REQ) {
                        syncTrainRecord();
                    } else if (flag == TRAIN_DATA_TO_LOCAL_REQ) {
                        getTrainRecord(1);
                    } else if (flag == EVALUATE_DATA_REQ) {
                        getEvaluateRecord(1);
                    } else if (flag == EVALUATE_DATA_TO_LOCAL_REQ) {
                        syncEvaluateRecord();
                    }
                }
            }
        });

    }

    @OnClick({R.id.img_low, R.id.tv_low, R.id.img_middle, R.id.tv_middle, R.id.img_high, R.id.tv_high, R.id.tv_commit_save,R.id.ll_select_value,R.id.img_value_select})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_low:
                seekBar.setProgress(1.0f);
                break;
            case R.id.tv_low:
                seekBar.setProgress(2.0f);
                break;
            case R.id.img_middle:
                seekBar.setProgress(4.0f);
                break;
            case R.id.tv_middle:
                seekBar.setProgress(5.0f);
                break;
            case R.id.img_high:
                seekBar.setProgress(8.0f);
                break;
            case R.id.tv_high:
                seekBar.setProgress(9.0f);
                break;
            case R.id.tv_commit_save:
                if (llStep1.getVisibility() == View.VISIBLE) {
                    llStep1.setVisibility(View.GONE);
                    llStep2.setVisibility(View.VISIBLE);
                    llStep3.setVisibility(View.GONE);
                    tvCommitSave.setText("下一步");
                    SpeechUtil.getInstance(this).speak("请点选疼痛程度");
                } else if (llStep2.getVisibility() == View.VISIBLE) {
                    if (isSelectedFeedback) {
                        isSelectedFeedback = false;
                        llStep1.setVisibility(View.GONE);
                        llStep2.setVisibility(View.GONE);
                        llStep3.setVisibility(View.VISIBLE);
                        SpeechUtil.getInstance(this).speak("请点选不良反应");
                        tvCommitSave.setText("保存记录");
                    } else {
                        SpeechUtil.getInstance(this).speak("请点选疼痛程度");
                    }
                } else {
                    if (!chkAdverseReactions1.isChecked() && !chkAdverseReactions2.isChecked() && !chkAdverseReactions3.isChecked() && !chkAdverseReactions4.isChecked() && !chkAdverseReactions5.isChecked()) {
                        SpeechUtil.getInstance(this).speak("请点选不良反应");
                    } else if (!isSave) {
                        saveData();
                        if (NetworkUtils.isConnected() && DateFormatUtil.avoidFastClick(2000) && !Global.FactoryCheck) {
                            showWaiting("操作提示", "正在保存...");
                            syncUser();
                        } else {
//                        ToastUtils.showShort("网络不可用");
                            Bundle bundle = new Bundle();
                            bundle.putInt("ResultTips", 1);
                            finishDialog(bundle);
                        }
                    }
                }
                break;
            case R.id.ll_select_value:
            case R.id.img_value_select:
                selectOtherFeedback();
                break;
        }
    }
    private void selectOtherFeedback() {
        List<String> list = new ArrayList<>(linkedHashSet);
        AlertDialog.Builder builder = new AlertDialog.Builder(this,android.R.style.Theme_Material_Light_Dialog_Alert);
        builder.setTitle("选择不良反应");
        builder.setItems(list.toArray(new String[0]), (dialog, which) -> {
            String value = list.get(which);
            edOtherDetail.setText(value);
        });
        builder.show();
    }
    public void startTargetActivity(Bundle bundle, Class<?> targetActivity, boolean isEndActivity) {
        Intent intent = new Intent(this, targetActivity);
        intent.putExtras(bundle);
        startActivity(intent);
        if (isEndActivity) {
            finish();
        }
    }

    public void startTargetActivity(Class<?> targetActivity, boolean isEndActivity) {
        Intent intent = new Intent(this, targetActivity);
        startActivity(intent);
        if (isEndActivity) {
            finish();
        }
    }

    public ProgressDialog progressDialog;

    /**
     * 圆圈加载进度的 dialog
     */
    public void showWaiting(String title, String msg) {
        progressDialog = new ProgressDialog(this);
        progressDialog.setIcon(R.mipmap.ic_launcher);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.setIndeterminate(true);// 是否形成一个加载动画  true表示不明确加载进度形成转圈动画  false 表示明确加载进度
        progressDialog.setCancelable(false);//点击返回键或者dialog四周是否关闭dialog  true表示可以关闭 false表示不可关闭
        progressDialog.show();
    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

}
