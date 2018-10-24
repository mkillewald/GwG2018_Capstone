package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.RepairStepListItemBinding;
import com.gameaholix.coinops.model.RepairStep;

import java.util.List;

public class StepAdapter extends RecyclerView.Adapter<StepAdapter.StepAdapterViewHolder> {
    private List<RepairStep> mRepairSteps;
    private final StepAdapterOnClickHandler mClickHandler;

    public interface StepAdapterOnClickHandler {
        void onClick(RepairStep repairStep);
    }

    public StepAdapter(StepAdapterOnClickHandler clickHandler) {
//        mContext = context;
        mClickHandler = clickHandler;
    }

    public class StepAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final RepairStepListItemBinding mBinding;

        StepAdapterViewHolder(RepairStepListItemBinding stepListItemBinding) {
            super(stepListItemBinding.getRoot());
            mBinding = stepListItemBinding;
//            mBinding.tvRepairDescription.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            RepairStep repairStep = mRepairSteps.get(adapterPosition);
            mClickHandler.onClick(repairStep);
        }
    }

    @NonNull
    @Override
    public StepAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RepairStepListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.repair_step_list_item, parent, false);

        return new StepAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull StepAdapterViewHolder holder, int position) {
        RepairStep repairStep = mRepairSteps.get(position);

        holder.mBinding.setRepairStep(repairStep);
        holder.mBinding.tvRepairStepEntry.setText(repairStep.getEntry());
    }

    @Override
    public int getItemCount() {
        return mRepairSteps == null ? 0 : mRepairSteps.size();
    }

    public void setRepairSteps(List<RepairStep> repairSteps) { mRepairSteps = repairSteps; }
}
