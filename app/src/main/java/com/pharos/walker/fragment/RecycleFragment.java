package com.pharos.walker.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.pharos.walker.R;
import com.pharos.walker.adapter.TrainPlanAdapter;
import com.pharos.walker.beans.PlanEntity;
import com.pharos.walker.database.TrainPlanManager;
import com.pharos.walker.ui.ConnectDeviceActivity;
import com.pharos.walker.utils.MyUtil;
import com.pharos.walker.utils.SPHelper;

import java.text.MessageFormat;
import java.util.List;
import java.util.Objects;

import butterknife.ButterKnife;

public class RecycleFragment extends Fragment {
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View contentView = inflater.inflate(R.layout.fragment_recycle, null);
        ButterKnife.bind(this, contentView);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        contentView.setLayoutParams(lp);
        initView();
        return contentView;
    }

    private void initView() {
    }
//
}
