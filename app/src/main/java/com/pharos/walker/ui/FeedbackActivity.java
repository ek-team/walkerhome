package com.pharos.walker.ui;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.AppCompatRatingBar;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.pharos.walker.MainActivity;
import com.pharos.walker.R;
import com.pharos.walker.beans.Battery;
import com.pharos.walker.beans.BleBean;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.ServerPlanEntity;
import com.pharos.walker.beans.ServerTrainDataBean;
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
import com.pharos.walker.utils.SpeechUtil;
import com.pharos.walker.utils.SqlToExcleUtil;
import com.pharos.walker.utils.ToastUtils;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by zhanglun on 2021/4/21
 * Describe:
 */
public class FeedbackActivity extends BaseActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.ratingbar_score)
    AppCompatRatingBar ratingbarScore;
    @BindView(R.id.ratingbar_complete)
    AppCompatRatingBar ratingbarCompleteScore;
    @BindView(R.id.seek_bar)
    BubbleSeekBar seekBar;
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
    @BindView(R.id.tv_commit_save)
    TextView tvCommitSave;
    @BindView(R.id.tv_complete_rate)
    TextView tvCompleteRate;
    @BindView(R.id.tv_right_rate)
    TextView tvRightRate;
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
    float completeRate = 0;
    float rightRate = 0;
    private Gson gson = new Gson();
    @Override
    protected void initialize(Bundle savedInstanceState) {
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
    }

    private void initView() {
        tvCompleteRate.setText(getString(R.string.complete_rate) + "：" + (int)(completeRate * 100) + "%");
        tvRightRate.setText(getString(R.string.accuracy_rate) + "：" + (int)(rightRate * 100) + "%");
        tvCommitSave.setVisibility(View.VISIBLE);
        SpeechUtil.getInstance(this).speak("请点选疼痛程度，和不良反应");
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
                tvWarning.setText(getString(R.string.feedback_tip_1));
            }
        });
        seekBar.setThumbColor(Color.GREEN);
        seekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat, boolean fromUser) {

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
        BleBean bleBean;
        switch (event.getAction()) {
            case MessageEvent.ACTION_GATT_CONNECTED:
                setPoint(true);
                break;
            case MessageEvent.ACTION_GATT_DISCONNECTED:
                bleBean = (BleBean) event.getData();
                setPoint(false);
                Toast.makeText(this, getString(R.string.ble_disconnect), Toast.LENGTH_SHORT).show();
                break;
            case MessageEvent.BATTERY_REFRESH:
                Battery battery = (Battery) event.getData();
                setBattery(battery.getBatteryVolume(), battery.getBatteryStatus());
                break;
            case MessageEvent.ACTION_SAVE_TIP:
//                Toast.makeText(this, "保存成功", Toast.LENGTH_SHORT).show();
//                progressDialog.dismiss();
                if (!Global.USER_MODE) {//访客模式下不记录数据
                    startTargetActivity(MainActivity.class, true);
                    return;
                }
                if (!isSave) {
                    saveData();
                }
//                startTargetActivity(MainActivity.class, true);
                break;
            case MessageEvent.ACTION_REQ_FAIL_SINGLE:
                if (progressDialog != null && progressDialog.isShowing()) {
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("提交数据请求失败");
                UserBean userBean = UserManager.getInstance().loadByUserId(SPHelper.getUserId());
                userBean.setIsRecordUpdate(0);
                UserManager.getInstance().update(userBean);
                finishDialog(null);
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
                int codeUser  = (int) event.getData();
                if (codeUser == 401){
                    getToken(USER_REQ);
                }else if (codeUser == 0){
                    syncPlan();
                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_PLAN_RESULT:
                int codePlan  = (int) event.getData();
                if (codePlan == 401){
                    getToken(PLAN_REQ);
                }else if (codePlan == 0){
                    getTrainRecord(0);
                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT:
                int codeTrainData  = (int) event.getData();
                if (codeTrainData == 401){
                    getToken(TRAIN_DATA_REQ);
                }else if (codeTrainData == 0){
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                        ToastUtils.showShort("保存成功");
                        Bundle bundle = new Bundle();
                        bundle.putInt("ResultTips",1);
                        finishDialog(bundle);
                    }

                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT:
                int codeTrainData1  = (int) event.getData();
                if (codeTrainData1 == 401){
                    getToken(TRAIN_DATA_TO_LOCAL_REQ);
                }else if (codeTrainData1 == 0){
                    syncTrainRecord();
                }else {
                    if (progressDialog != null && progressDialog.isShowing()){
                        progressDialog.dismiss();
                    }
                    ToastUtils.showShort("接口请求失败");
                    finishDialog(null);
                }
                break;
            case MessageEvent.ACTION_REQ_FAIL:
                if (progressDialog != null && progressDialog.isShowing()){
                    progressDialog.dismiss();
                }
                ToastUtils.showShort("接口请求失败");
                finishDialog(null);
                break;
            default:
                break;
        }
    }

    @Override
    protected int getLayoutResId() {
        return R.layout.activity_feedback;
    }


    @OnClick({R.id.iv_back, R.id.img_low, R.id.tv_low, R.id.img_middle, R.id.tv_middle, R.id.img_high, R.id.tv_high,
            R.id.tv_commit_save,R.id.img_back,R.id.tv_back})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_back:
            case R.id.img_back:
            case R.id.tv_back:
                startTargetActivity(MainActivity.class, true);
                break;
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
                if (!Global.USER_MODE) {//访客模式下不记录数据
                    startTargetActivity(MainActivity.class, true);
                    return;
                }else {
                    if (!isSave) {
                        saveData();
//                        String rootPath = Environment.getExternalStorageDirectory() + File.separator + "FLSReport" +
//                                File.separator;
//                        String userPath =SPHelper.getUserName() + "_" + SPHelper.getUserId() +
//                                File.separator;
////                        String path = Environment.getExternalStorageDirectory() + File.separator + "FLSReport" +
////                                File.separator +  SPHelper.getUserName() + "_" + SPHelper.getUserId() +
////                                File.separator;
//                        new Thread() {
//                            @Override
//                            public void run() {
//                                new SqlToExcleUtil().onUserTrainRecord(rootPath,userPath,DateFormatUtil.getNowDate()+ "_训练记录.xls");
//                            }
//                        }.start();
                    }
                    if(NetworkUtils.isConnected() && DateFormatUtil.avoidFastClick(2000) && !Global.FactoryCheck){
                        showWaiting("操作提示","正在保存...");
                        syncUser();
                    }else {
//                        ToastUtils.showShort("网络不可用");
                        Bundle bundle = new Bundle();
                        bundle.putInt("ResultTips",1);
                        finishDialog(bundle);
                    }
                }

//                startTargetActivity(CommitResultActivity.class,false);
                break;
        }
    }

    private void saveData() {
        UserTrainRecordEntity trainRecordEntity = new UserTrainRecordEntity();
        trainRecordEntity.setScore((int) trainScore);
        trainRecordEntity.setWarningTime(warningTime);
        trainRecordEntity.setDiagnostic(userBean.getDiagnosis());
        trainRecordEntity.setTargetLoad(loadWeight);
        trainRecordEntity.setSuccessTime(effectiveTime);
        trainRecordEntity.setPainLevel(painFeedbackLevel);
//                if(painFeedbackLevel >= 0 && painFeedbackLevel <= 3){
//                    trainRecordEntity.setPainLevel("轻度疼痛");
//                }else if(painFeedbackLevel >= 4 && painFeedbackLevel <= 7){
//                    trainRecordEntity.setPainLevel("中度疼痛");
//                }else if(painFeedbackLevel >= 8 && painFeedbackLevel <= 10){
//                    trainRecordEntity.setPainLevel("重度疼痛");
//                }else {
//                    trainRecordEntity.setPainLevel("无反馈");
//                }
        adverseReactions = adverseReactions1 + adverseReactions2 + adverseReactions3;
        trainRecordEntity.setAdverseReactions(adverseReactions);
        trainRecordEntity.setTrainTime(trainTime);
        trainRecordEntity.setUserId(SPHelper.getUserId());
        UserTrainRecordEntity recordEntity = UserTrainRecordManager.getInstance().insert(trainRecordEntity);
        if (trainDataEntityList != null) {
            TrainDataManager.getInstance().insertMany(trainDataEntityList);
            recordEntity.setTrainDataList(trainDataEntityList);
//            List<TrainDataEntity> tempList = new ArrayList<>();
//
//            for (int i = 0; i < 99; i++){
//                TrainDataEntity trainData = new TrainDataEntity();
//                trainData.setKeyId(SnowflakeIdUtil.getUniqueId());
//                trainData.setIsUpload(trainDataEntityList.get(0).getIsUpload());
//                trainData.setClassId(trainDataEntityList.get(0).getClassId());
//                trainData.setCreateDate(trainDataEntityList.get(0).getCreateDate());
//                trainData.setDateStr(trainDataEntityList.get(0).getDateStr());
//                trainData.setFrequency(trainDataEntityList.get(0).getFrequency());
//                trainData.setPlanId(trainDataEntityList.get(0).getPlanId());
//                trainData.setRealLoad(trainDataEntityList.get(0).getRealLoad());
//                trainData.setTargetLoad(trainDataEntityList.get(0).getTargetLoad());
//                trainData.setUserId(trainDataEntityList.get(0).getUserId());
//                tempList.add(trainData);
//            }
//            TrainDataManager.getInstance().insertMany(tempList);
        }
        if (Global.ReleaseVersion == Global.HomeVersion){
            return;
        }
        if (NetworkUtils.isConnected() && !Global.FactoryCheck) {
            if (!isGenerateRecord) {
                showWaiting("操作提示", "正在提交…");
            }
            List<UserTrainRecordEntity> recordEntityList = new ArrayList<>();
            recordEntityList.add(recordEntity);
            uploadTrainDataSingle(recordEntityList);
        } else {
            if (progressDialog != null && progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
            finishDialog(null);
//            startTargetActivity(MainActivity.class, true);
//            ToastUtils.showShort("网络不可用！");
        }
        isSave = true;
    }
    private void finishDialog(Bundle bundle){
        RxDialogSureCancel dialog = new RxDialogSureCancel(this);
        dialog.setContent("训练数据已保存，可在微信公众号查看");
        dialog.setCancel("返回主页");
        dialog.setSure("查看记录");
        dialog.setSureListener(v -> {
            dialog.dismiss();
            Bundle bundle1 = new Bundle();
            bundle1.putInt("feedback",1);
            bundle1.putParcelableArrayList(AppKeyManager.EXTRA_TRAIN_DATA_ARRAY,trainDataEntityList);
            startTargetActivity(bundle1,OnceRecordActivity.class,true);
        });
        dialog.setCancelListener(v -> {
            if (bundle != null){
                startTargetActivity(bundle,MainActivity.class, true);
            }else {
                startTargetActivity(MainActivity.class, true);
            }
            dialog.dismiss();
        });
        dialog.setCanceledOnTouchOutside(false);
        dialog.setCancelable(false);
        dialog.show();
    }
    private void uploadTrainDataSingle(List<UserTrainRecordEntity> recordEntityList) {
        OkHttpUtils.postJsonAsync(Api.uploadTrainRecord, new Gson().toJson(recordEntityList), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL_SINGLE));

            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->训练数据上传结果" + result);
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
    private void syncUser(){
        List<UserBean> userBeans = UserManager.getInstance().loadNoNetUploadUser();
        String data = gson.toJson(userBeans);
        OkHttpUtils.postJsonAsync(Api.uploadUser, data, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->用户同步结果" + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_USER_RESULT,code));
                if (code == 0){
                    UserManager.getInstance().updateUserUploadStatus(userBeans,Global.UploadNetStatus);
                }
            }
        });

    }
    private void syncPlan(){
        OkHttpUtils.getAsync(Api.getPlan + SPHelper.getUserId(), true, new OkHttpUtils.DataCallBack() {
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
                    ServerPlanEntity serverPlanEntity = gson.fromJson(result,ServerPlanEntity.class);
//                Type type = new TypeToken<List<PlanEntity>>(){}.getType();
//                List<PlanEntity> planEntityList = gson.fromJson(serverPlanEntity.getData(),type);
                    List<PlanEntity> planEntityList = serverPlanEntity.getData();
                    if (planEntityList.size() == 0 || TrainPlanManager.getInstance().comparePlanUpdateDate(DateFormatUtil.getString2Date(planEntityList.get(0).getUpdateDate()),
                            planEntityList.get(0).getUserId()) == Global.UploadLocalStatus ){
                        List<PlanEntity> localPlan = TrainPlanManager.getInstance().getMasterPlanListByUserId(SPHelper.getUserId());
                        OkHttpUtils.postJsonAsync(Api.uploadPlan, gson.toJson(localPlan), new OkHttpUtils.DataCallBack() {
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
                        try {
                            boolean isLoadLocalUpdate =  TrainPlanManager.getInstance().isLoadLocalUpdate(planEntityList.get(0).getPlanId()
                                ,planEntityList.get(0).getUserId(),planEntityList.get(0).getLoad());//判断是否刷新本地子计划的标识

                            if (isLoadLocalUpdate){//如果网络端起始负重修改了，需要本地更新计划 不用插入网络端的数据
                                MyUtil.insertTemplate(planEntityList.get(0).getLoad());
                            }
                            for (PlanEntity planEntity : planEntityList){
                                TrainPlanManager.getInstance().insert(planEntity);
                                if (planEntity.getSubPlanEntityList() != null && planEntity.getSubPlanEntityList().size() > 0 && !isLoadLocalUpdate){
                                    SubPlanManager.getInstance().insertMany(planEntity.getSubPlanEntityList());
                                }
                            }

                        }catch (Exception e){
                            if (progressDialog != null && progressDialog.isShowing()){
                                progressDialog.dismiss();
                            }
                            ToastUtils.showShort(e.getMessage());
                            CrashHandler.getInstance().saveThrowableMessage("更新训练计划error：" + e.getMessage());
                        }
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT,code));
                    }else{
                        EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_PLAN_RESULT,code));
                    }

                }else{
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
                }
            }
        });
    }

    private void syncTrainRecord(){
        List<UserTrainRecordEntity> recordEntityList = UserTrainRecordManager.getInstance().loadMasterUpdate(SPHelper.getUserId());
        List<List<UserTrainRecordEntity>> listList = splitList(recordEntityList,pageSize);
        int pageNum = 0;
        uploadTrainData(pageNum,listList);

    }
    private void uploadTrainData( int pageNum,List<List<UserTrainRecordEntity>> listList){
        if (listList.size() <= 0){
            EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,0));
            return;
        }
        OkHttpUtils.postJsonAsync(Api.uploadTrainRecord, gson.toJson(listList.get(pageNum)), new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->训练数据上传结果" + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 401){
                    getToken(TRAIN_DATA_REQ);
                    return;
                }
                boolean isDuplicate = false;
                if (code == 1){
                    String msg = toJsonObj.getString("msg");
                    isDuplicate = msg.contains("Duplicate entry");
                }
//                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,code));
                if (code == 0 || isDuplicate){
                    try {
                        UserTrainRecordManager.getInstance().updateMasterTrainUploadStatus(listList.get(pageNum));
                    }catch (Exception e){
                        if (progressDialog != null && progressDialog.isShowing()){
                            progressDialog.dismiss();
                        }
                        ToastUtils.showShort(e.getMessage());
                        CrashHandler.getInstance().saveThrowableMessage("更新训练记录error：" + e.getMessage());
                    }
//                    for (int i = pageNum + 1;i < listList.size(); i++ ){
//                        uploadTrainData(i,listList);
//                    }
                }
                if (pageNum+1 >= listList.size()){
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_RESULT,isDuplicate? 0 : code));
                }else {
                    uploadTrainData(pageNum+1,listList);
                }
            }
        });
    }
    private List<List<UserTrainRecordEntity>> splitList(List<UserTrainRecordEntity> list , int groupSize){
        return  Lists.partition(list, groupSize);
    }
    private int totalSize = 0;
    private final int pageSize = 4;
    private int page = 0;
    private void getTrainRecord(int pageNum){
        OkHttpUtils.getAsync(Api.getRecord + SPHelper.getUserId() + "?pageSize=" + pageSize + "&pageNum=" + pageNum, true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }
            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("Setting Activity", "--->训练数据获取结果" + result);
                JSONObject toJsonObj= new JSONObject(result);
                int code = toJsonObj.getInt("code");
                if (code == 0){
                    ServerTrainDataBean serverTrainDataBean = gson.fromJson(result,ServerTrainDataBean.class);
                    totalSize = serverTrainDataBean.getData().getTotal();
                    page = totalSize/pageSize + (totalSize%pageSize == 0?0:1);//获取总页数
                    for (UserTrainRecordEntity trainRecordEntity : serverTrainDataBean.getData().getRecords()){
                        trainRecordEntity.setIsUpload(Global.UploadNetStatus);
                        UserTrainRecordManager.getInstance().insertServerData(trainRecordEntity);
                        List<TrainDataEntity> trainDataEntityList = trainRecordEntity.getTrainDataList();
                        TrainDataManager.getInstance().insertMany(trainDataEntityList);
                    }
                }

                if (pageNum +1 >= page){
                    EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_SYNC_TRAIN_DATA_TO_LOCAL_RESULT,code));
                }else {
                    getTrainRecord(pageNum + 1);
                }

            }
        });
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
    private void getToken(int flag){
        OkHttpUtils.getAsyncToken(Api.tokenUrl + "?grant_type=client_credentials" , true, new OkHttpUtils.DataCallBack() {
            @Override
            public void requestFailure(Request request, IOException e) {
                e.printStackTrace();
                EventBus.getDefault().post(new MessageEvent<>(MessageEvent.ACTION_REQ_FAIL));
            }

            @Override
            public void requestSuccess(String result) throws Exception {
                Log.e("MainActivity", "requestSuccess: " + result);
                TokenBean tokenBean = new Gson().fromJson(result,TokenBean.class);
                if (tokenBean.getCode() == 0){
                    SPHelper.saveToken(tokenBean.getData().getAccess_token());
                    if (flag == PLAN_REQ){
                        syncPlan();
                    }else if (flag == USER_REQ){
                        syncUser();
                    }else if (flag == TRAIN_DATA_REQ){
                        syncTrainRecord();
                    }else if (flag == TRAIN_DATA_TO_LOCAL_REQ){
                        getTrainRecord(0);
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
