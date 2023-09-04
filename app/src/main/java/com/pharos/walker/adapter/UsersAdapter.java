package com.pharos.walker.adapter;

import android.app.Activity;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.pharos.walker.beans.UserBean;
import com.pharos.walker.R;
import com.pharos.walker.utils.SPHelper;

import java.util.List;


public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.ViewHolder> {

    private List<UserBean> listData;
    private Activity mActivity;

    private int position;

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public UsersAdapter(Activity activity) {
        this.mActivity = activity;
    }

    public void setData(List<UserBean> beans) {
        if ( listData != null && listData.size() > 0){
            listData.clear();
        }
        listData = beans;
        notifyDataSetChanged();
    }

    public void addData(List<UserBean> beans) {
        listData.addAll(beans);
        notifyDataSetChanged();
    }

    public List<UserBean> getData() {
        return listData;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user_list, parent, false));
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        UserBean userBean = listData.get(position);
        if (userBean.getCaseHistoryNo().equals("123456")){
            holder.tvIdTitle.setVisibility(View.GONE);
            holder.tvNo.setVisibility(View.GONE);
            holder.tvSex.setVisibility(View.GONE);
            holder.tvAge.setVisibility(View.GONE);
            holder.tvName.setText(userBean.getName());
        }else {
            holder.tvName.setText(userBean.getName());
            holder.tvNo.setText(userBean.getCaseHistoryNo());
//        holder.ivPhoto.setImageBitmap();
            String sex = userBean.getSex() == 1 ? "男" : "女";
            holder.tvSex.setText(String.format("性别：%s", sex));
            holder.tvAge.setText(String.format("年龄：%d", userBean.getAge()));
            if (SPHelper.getUserId() == userBean.getUserId()){
                holder.imgSelect.setVisibility(View.VISIBLE);
            }else {
                holder.imgSelect.setVisibility(View.GONE);
            }
        }
        holder.itemView.setOnClickListener(v -> {
            if (onItemClickListener != null) {
                onItemClickListener.onItemClick(holder.getLayoutPosition());
            }
        });
        holder.itemView.setOnLongClickListener(v -> {
            setPosition(holder.getLayoutPosition());
            return false;
        });
    }

    @Override
    public void onViewRecycled(ViewHolder holder) {
        holder.itemView.setOnLongClickListener(null);
        super.onViewRecycled(holder);
    }

    @Override
    public int getItemCount() {
        return listData == null ? 0 : listData.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnCreateContextMenuListener {
        private TextView tvName;
        private TextView tvNo;
        private TextView tvIdTitle;
        private ImageView ivPhoto;
        private ImageView imgSelect;
        private TextView tvSex;
        private TextView tvAge;
        private TextView tvDate;

        public ViewHolder(View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_name);
            tvNo =  itemView.findViewById(R.id.tv_id);
            tvIdTitle =  itemView.findViewById(R.id.tv_id_title);
            ivPhoto =  itemView.findViewById(R.id.photo);
            imgSelect = itemView.findViewById(R.id.img_select);
            tvSex = itemView.findViewById(R.id.tv_sex);
            tvAge = itemView.findViewById(R.id.tv_age);
            tvDate = itemView.findViewById(R.id.tv_date);
            itemView.setOnCreateContextMenuListener(this);
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
            mActivity.getMenuInflater().inflate(R.menu.menu_user, menu);
        }
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public interface OnItemClickListener {

        void onItemClick(int position);
    }

}