package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ListItemBinding;
import com.gameaholix.coinops.model.Item;

import java.util.List;

public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RepairAdapterViewHolder> {
    private List<Item> mRepairLogs;
    private final RepairAdapterOnClickHandler mClickHandler;

    public interface RepairAdapterOnClickHandler {
        void onClick(Item repairLog);
    }

    public RepairAdapter (RepairAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class RepairAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemBinding mBinding;

        RepairAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.getRoot().setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            Item repairLog = mRepairLogs.get(adapterPosition);
            mClickHandler.onClick(repairLog);
        }
    }

    @NonNull
    @Override
    public RepairAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new RepairAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RepairAdapterViewHolder holder, int position) {
        Item repairLog = mRepairLogs.get(position);

        holder.mBinding.tvName.setText(repairLog.getName());
        holder.mBinding.tvName.setMaxLines(1);
        holder.mBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getItemCount() {
        return mRepairLogs == null ? 0 : mRepairLogs.size();
    }

    public void setRepairLogs(List<Item> repairLogs) { mRepairLogs = repairLogs; }
}
