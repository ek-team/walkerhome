package com.pharos.walker.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pharos.walker.beans.BleBean;
import com.pharos.walker.R;
import com.pharos.walker.utils.RssiUtils;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglun on 2021/4/9
 * Describe:
 */
public class BleDevAdapter extends RecyclerView.Adapter<BleDevAdapter.ViewHolder> {
    private ArrayList<BleBean> bleBeans;
    public BleDevAdapter(ArrayList<BleBean> bleBeans) {
        this.bleBeans = bleBeans;
    }
    public void addData(BleBean bleBean) {
        bleBeans.add(bleBean);
        notifyDataSetChanged();
    }
    public void changeData(BleBean ble){
        for(BleBean bleBean : bleBeans){
            if (bleBean.getAddress().equals(ble.getAddress())){
                bleBean.setRssi(ble.getRssi());
            }
        }
        notifyDataSetChanged();
    }
    public void setSelect(BleBean bleBean) {
        for (BleBean bean : bleBeans) {
            if (TextUtils.equals(bean.getAddress(), bleBean.getAddress())) {
                bean.setConnectState(0);
            } else {
                bean.setConnectState(-1);
            }
        }
        notifyDataSetChanged();
    }
    public ArrayList<BleBean> getData() {
        return bleBeans;
    }
    public void clear() {
        if (bleBeans != null && bleBeans.size() > 0) {
            bleBeans.clear();
            notifyDataSetChanged();
        }
    }
    @NonNull
    @Override
    public BleDevAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_bluetooth_glove, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        BleBean item = bleBeans.get(position);
        //添加连接成功动画
        AlphaAnimation alphaAnimation = new AlphaAnimation(0, 1);
        alphaAnimation.setDuration(500);
        alphaAnimation.setInterpolator(new AccelerateDecelerateInterpolator());
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        alphaAnimation.setRepeatCount(0);
        alphaAnimation.setFillAfter(true);

        switch (item.getConnectState()) {
            case 0: // 连接中
                holder.pbLoading.setVisibility(View.VISIBLE);
                holder.ivStatus.setVisibility(View.GONE);
                holder.tvUnConnect.setVisibility(View.GONE);
                break;
            case 1:  // 已连接
                holder.ivStatus.setImageResource(R.mipmap.connected);
                holder.pbLoading.setVisibility(View.GONE);
                holder.tvUnConnect.setVisibility(View.GONE);
                holder.ivStatus.setVisibility(View.VISIBLE);
//                holder.ivStatus.startAnimation(alphaAnimation);
                break;
            case -1:  // 未连接
            default:
                holder.pbLoading.setVisibility(View.GONE);
                holder.ivStatus.setVisibility(View.GONE);
                holder.tvUnConnect.setVisibility(View.VISIBLE);
                break;
        }

        switch (RssiUtils.getLeLevel(bleBeans.get(position).getRssi())){
            case 1:
                holder.imgSignal.setImageResource(R.mipmap.icon_signal_1);
                break;
            case 2:
                holder.imgSignal.setImageResource(R.mipmap.icon_signal_2);
                break;
            case 3:
                holder.imgSignal.setImageResource(R.mipmap.icon_signal_3);
                break;
            case 4:
                holder.imgSignal.setImageResource(R.mipmap.icon_signal_4);
                break;
        }
        holder.tvName.setText(String.format("%s", item.getName()));
    }

    @Override
    public int getItemCount() {
        return bleBeans.size();
    }
    class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.img_signal)
        ImageView imgSignal;
        @BindView(R.id.iv_success)
        ImageView ivStatus;
        @BindView(R.id.tv_un_connect)
        TextView tvUnConnect;
        @BindView(R.id.pb_loading)
        ProgressBar pbLoading;

        ViewHolder(View view) {
            super(view);
            view.setOnClickListener(this);
            ButterKnife.bind(this, view);
        }

        @Override
        public void onClick(View v) {
            int pos = getAdapterPosition();
            if (pos >= 0 && pos < bleBeans.size())
                mOnItemClickListener.onItemClick(bleBeans.get(pos));
        }
    }
    public interface OnItemClickListener{
        void onItemClick(BleBean bleBean);
    }
    public interface OnItemLongClickListener{
        void onItemLongClick(View view,int position);
    }
    private OnItemClickListener mOnItemClickListener;
    private OnItemLongClickListener mOnItemLongClickListener;

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener){
        this.mOnItemClickListener = mOnItemClickListener;
    }

    public void setOnItemLongClickListener(OnItemLongClickListener mOnItemLongClickListener) {
        this.mOnItemLongClickListener = mOnItemLongClickListener;
    }
}
