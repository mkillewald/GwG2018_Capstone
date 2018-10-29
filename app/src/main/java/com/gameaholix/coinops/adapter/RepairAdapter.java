package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ListItemMoreBinding;
import com.gameaholix.coinops.model.Entry;

import java.util.List;

public class RepairAdapter extends RecyclerView.Adapter<RepairAdapter.RepairAdapterViewHolder> {
    private List<Entry> mRepairLogs;
    private final RepairAdapterOnClickHandler mClickHandler;

    public interface RepairAdapterOnClickHandler {
        void onClick(Entry repairLog);
    }

    public RepairAdapter (RepairAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class RepairAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemMoreBinding mBinding;

        RepairAdapterViewHolder(ListItemMoreBinding listItemMoreBinding) {
            super(listItemMoreBinding.getRoot());
            mBinding = listItemMoreBinding;
            mBinding.tvName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            Entry repairLog = mRepairLogs.get(adapterPosition);
            mClickHandler.onClick(repairLog);
        }
    }

    @NonNull
    @Override
    public RepairAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemMoreBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item_more, parent, false);

        return new RepairAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RepairAdapterViewHolder holder, int position) {
        Entry repairLog = mRepairLogs.get(position);

        holder.mBinding.tvName.setText(repairLog.getEntry());
    }

    @Override
    public int getItemCount() {
        return mRepairLogs == null ? 0 : mRepairLogs.size();
    }

    public void setRepairLogs(List<Entry> repairLogs) { mRepairLogs = repairLogs; }
}
