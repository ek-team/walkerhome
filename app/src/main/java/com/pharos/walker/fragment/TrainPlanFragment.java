package com.pharos.walker.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.pharos.walker.R;
import com.pharos.walker.adapter.TrainPlanAdapter;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.customview.rxdialog.RxTrainDataEditDialog;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.ui.ConnectDeviceActivity;
import com.pharos.walker.ui.TrainParamActivity;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.SPHelper;

import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class TrainPlanFragment extends Fragment {
    @BindView(R.id.tv_info)
    TextView tvInfo;
    @BindView(R.id.tv_time)
    TextView tvTime;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_train_time_day)
    TextView tvTrainTimeDay;
    @BindView(R.id.tv_train_count_time)
    TextView tvTrainCountTime;
    private TrainPlanAdapter adapter;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_train_plan, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initView();
        return contentView;
    }

    private void initView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        List<PlanEntity> planEntityList = TrainPlanManager.getInstance().getPlanListByUserId(SPHelper.getUserId());
        adapter = new TrainPlanAdapter(planEntityList);
        recyclerView.setAdapter(adapter);
        adapter.setOnItemClickListener(position -> {
            Intent intent = new Intent();
            intent.setClass(Objects.requireNonNull(getActivity()), TrainParamActivity.class);
            startActivity(intent);
        });
//        adapter.setOnItemLongClickListener(position -> {
//            RxTrainDataEditDialog dialog = new RxTrainDataEditDialog(getContext(),planEntityList.get(position));
//            dialog.setSureListener(v -> {
//                PlanEntity planEntity = dialog.getData();
//                planEntityList.set(position,planEntity);
//                adapter.notifyDataSetChanged();
//                dialog.dismiss();
//            });
//            dialog.show();
//        });
        tvInfo.setText(MessageFormat.format("总体规划：{0}{1}。手术时间{2}",SPHelper.getUser().getDiagnosis(), MyUtil.getPlanSummar(),SPHelper.getUser().getDate()));
    }
//
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
