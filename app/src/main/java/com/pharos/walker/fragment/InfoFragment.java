package com.pharos.walker.fragment;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.pharos.walker.R;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.beans.UserBean;
import com.pharos.walker.constants.Api;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.QrUtil;
import com.pharos.walker.utils.SPHelper;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglun on 2021/6/3
 * Describe:
 */
public class InfoFragment extends Fragment {
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_info1)
    TextView tvInfo1;
    @BindView(R.id.tv_info2)
    TextView tvInfo2;
    @BindView(R.id.tv_info3)
    TextView tvInfo3;
    @BindView(R.id.tv_xray)
    TextView tvXray;
    @BindView(R.id.img_qr)
    ImageView imgQr;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_info, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initView();
        return contentView;
    }

    private void initView() {
        UserBean userBean = SPHelper.getUser();
        if (userBean.getId() == 0 && userBean.getCaseHistoryNo().equals("123456")){
            tvXray.setVisibility(View.GONE);
            return;
        }
        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
        int currentClass = 0;
        int totalFinishClass = 0;
        for (PlanEntity planEntity : planEntityList){
            if (planEntity.getPlanStatus() == 1){
                currentClass = planEntity.getClassId();
            }else if (planEntity.getPlanStatus() == 2){
                totalFinishClass ++;
            }
        }

        String sex;
        if (userBean.getSex() == 0){
            sex = "女";
        }else {
            sex = "男";
        }
        tvInfo.setText(MessageFormat.format("姓名：{0}        {1}岁        {2}        描述：{3}        所在地：{4}",
                userBean.getName(), userBean.getAge(), sex, TextUtils.isEmpty(userBean.getRemark())?"":userBean.getRemark(), TextUtils.isEmpty(userBean.getAddress())?"":userBean.getAddress()));

        tvInfo1.setText(MessageFormat.format("住 院 号：{0}\n\n手术时间：{1}\n\n就诊科室：无\n\n主治医生：{2}\n\n病情描述：{3}",
                userBean.getCaseHistoryNo(), userBean.getDate(), userBean.getDoctor(), userBean.getDiagnosis()));
//        tvInfo2.setText(MessageFormat.format("诊断结果：{0}\n\n手术时间：{1}\n\n手术医生：{2}\n\n手术名称：{3}\n\n手术描述：{4}\n\n出院时间：{5}\n\n出院医嘱：{6}",
//                userBean.getDiagnosis(), userBean.getDate(), userBean.getDoctor(), userBean.getDiagnosis(), userBean.getDiagnosis(),
//                TextUtils.isEmpty(userBean.getUpdateDate())?"":userBean.getUpdateDate(), TextUtils.isEmpty(userBean.getRemark())?"":userBean.getRemark()));

        tvInfo2.setText(MessageFormat.format("复诊时间：{0}\n\n康复总阶段：{1}个阶段\n\n已完成阶段：{2}个阶段\n当 前 阶 段：{3}阶段\n\n康 复 描 述：{4}",
                TextUtils.isEmpty(userBean.getCreateDate())?"":userBean.getCreateDate(), planEntityList.size(), totalFinishClass, currentClass, MyUtil.getPlanSummar()));
        tvXray.setVisibility(View.GONE);
        Resources res = getResources();
        Bitmap logoBitmap = BitmapFactory.decodeResource(res, R.mipmap.ic_launcher);
        String content = Api.qrUrl + userBean.getUserId();
        Bitmap qrBitmap = QrUtil.createQRCodeBitmap(content, 320, 320, "UTF-8", "H", "1", Color.BLACK, Color.WHITE, logoBitmap, 0.15F);
        imgQr.setImageBitmap(qrBitmap);
    }

//    @Override
//    public void onDetach() {
//        super.onDetach();
//        try {
//            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
//            childFragmentManager.setAccessible(true);
//            childFragmentManager.set(this, null);
//
//        } catch (NoSuchFieldException | IllegalAccessException e) {
//            throw new RuntimeException(e);
//        }
//    }
}
