package com.gameaholix.coinops.repair;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.RepairListItemBinding;

import java.util.List;

public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RepairAdapterViewHolder> {
    private List<RepairLog> mRepairLogs;
    private final Context mContext;
    private final RepairAdapterOnClickHandler mClickHandler;

    public interface RepairAdapterOnClickHandler {
        void onClick(RepairLog repairLog);
    }

    public RepairAdapter (Context context, RepairAdapterOnClickHandler clickHandler) {
        mContext = context;
        mClickHandler = clickHandler;
    }

    public class RepairAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final RepairListItemBinding mBinding;

        RepairAdapterViewHolder(RepairListItemBinding repairListItemBinding) {
            super(repairListItemBinding.getRoot());
            mBinding = repairListItemBinding;
            mBinding.tvRepairName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            RepairLog repairLog = mRepairLogs.get(adapterPosition);
            mClickHandler.onClick(repairLog);
        }
    }

    @Override
    public RepairAdapterViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RepairListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.repair_list_item, parent, false);

        RepairAdapterViewHolder viewHolder = new RepairAdapterViewHolder(binding);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RepairAdapterViewHolder holder, int position) {
        RepairLog repairLog = mRepairLogs.get(position);

        holder.mBinding.setRepairLog(repairLog);
        holder.mBinding.tvRepairName.setText(repairLog.getName());
    }

    @Override
    public int getItemCount() {
        return mRepairLogs == null ? 0 : mRepairLogs.size();
    }

    public void setRepairLogs(List<RepairLog> repairLogs) { mRepairLogs = repairLogs; }
}
