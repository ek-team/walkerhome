package com.pharos.walker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pharos.walker.R;
import com.pharos.walker.beans.PlanEntity;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglun on 2021/4/26
 * Describe:
 */
public class TrainPlanAdapter extends RecyclerView.Adapter<TrainPlanAdapter.ViewHolder> {
    private List<PlanEntity> planEntityList;

    public TrainPlanAdapter(List<PlanEntity> planEntityList) {
        this.planEntityList = planEntityList;
    }

    @NonNull
    @Override
    public TrainPlanAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_train_plan_item, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull TrainPlanAdapter.ViewHolder holder, int position) {
        holder.tvLevel.setText(MessageFormat.format("第{0}阶段", planEntityList.get(position).getClassId()));
        holder.tvPlanLevelWeight.setText(MessageFormat.format("{0}kg", planEntityList.get(position).getLoad()));
        if (planEntityList.get(position).getPlanStatus() == 0){
            holder.tvStatus.setText("未开始");
            holder.ivStatus.setImageResource(R.drawable.round_red_point);
        }else if (planEntityList.get(position).getPlanStatus() == 1){
            holder.tvStatus.setText("进行中");
            holder.ivStatus.setImageResource(R.drawable.round_green_point);
        }else if (planEntityList.get(position).getPlanStatus() == 2){
            holder.tvStatus.setText("已完成");
            holder.ivStatus.setImageResource(R.drawable.round_green_point);
        }else {
            holder.tvStatus.setText("未完成");
            holder.ivStatus.setImageResource(R.drawable.round_red_point);
        }
        String startDate = planEntityList.get(position).getStartDate();
        startDate = startDate.substring(0,startDate.indexOf(" "));
        String endDate = planEntityList.get(position).getEndDate();
        endDate = endDate.substring(0,endDate.indexOf(" "));
        holder.tvStartEndDay.setText(MessageFormat.format("{0}至{1}",startDate , endDate));
        holder.tvRating.setText(String.valueOf(planEntityList.get(position).getTimeOfDay()));
        holder.tvNum.setText(MessageFormat.format("{0}", planEntityList.get(position).getTrainTime()));
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getLayoutPosition());
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            if (mOnItemLongClickListener != null){
                mOnItemLongClickListener.onItemLongClick(holder.getLayoutPosition());
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return planEntityList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.tv_level)
        TextView tvLevel;
        @BindView(R.id.iv_status)
        ImageView ivStatus;
        @BindView(R.id.tv_status)
        TextView tvStatus;
        @BindView(R.id.tv_start_end_day)
        TextView tvStartEndDay;
        @BindView(R.id.tv_rating)
        TextView tvRating;
        @BindView(R.id.tv_num)
        TextView tvNum;
        @BindView(R.id.tv_plan_level_weight)
        TextView tvPlanLevelWeight;

        ViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }
    }
    OnItemClickListener onItemClickListener;
    OnItemLongClickListener mOnItemLongClickListener;
    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(int position);
    }
    public interface OnItemClickListener {

        void onItemClick(int position);
    }
}
