package com.pharos.walker.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pharos.walker.R;
import com.pharos.walker.beans.EvaluateEntity;
import com.pharos.walker.fragment.EvaluateRecordFragment;
import com.pharos.walker.utils.DateFormatUtil;

import java.text.MessageFormat;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by zhanglun on 2021/3/17
 * Describe:
 */
public class EvaluateRecordAdapter extends RecyclerView.Adapter {
    private List<EvaluateEntity> list;
    private int selected = -1;

    public EvaluateRecordAdapter(List<EvaluateEntity> list) {
        this.list = list;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_evaluate_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ViewHolder mHolder = (ViewHolder) holder;
        mHolder.tvSheetRow1.setText(String.valueOf(position + 1));
        mHolder.tvSheetRow2.setText(DateFormatUtil.getDate2String(list.get(position).getCreateDate(),"yyyy-MM-dd HH:mm"));
        mHolder.tvSheetRow3.setText(MessageFormat.format("{0}", list.get(position).getEvaluateResult()));
        mHolder.tvSheetRow4.setText("查看");
//        mHolder.tvSheetRow5.setText("打印");
        if (mOnItemClickListener != null){
            mHolder.tvSheetRow4.setOnClickListener(v -> {
                mOnItemClickListener.onItemClick(v,position, EvaluateRecordFragment.catRecord, selected);
                selected = position;
            });
            mHolder.tvSheetRow5.setOnClickListener(v -> mOnItemClickListener.onItemClick(v,position, EvaluateRecordFragment.printRecord,selected));
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    static
    class ViewHolder extends RecyclerView.ViewHolder{
        @BindView(R.id.tv_sheetRow1)
        TextView tvSheetRow1;
        @BindView(R.id.tv_sheetRow2)
        TextView tvSheetRow2;
        @BindView(R.id.tv_sheetRow3)
        TextView tvSheetRow3;
        @BindView(R.id.tv_sheetRow4)
        TextView tvSheetRow4;
        @BindView(R.id.tv_sheetRow5)
        TextView tvSheetRow5;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
    public interface OnItemClickListener{
        void onItemClick(View view, int position, int viewType, int lastSelected);
    }

    public interface OnItemLongClickListener{
        void onItemLongClick(View view, int position);
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
