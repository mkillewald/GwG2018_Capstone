package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.InventoryListItemBinding;
import com.gameaholix.coinops.model.InventoryItem;

import java.util.List;

public class InventoryAdapter extends RecyclerView.Adapter<InventoryAdapter.InventoryAdapterViewHolder> {
    private List<InventoryItem> mInventoryItems;
    private final InventoryAdapterOnClickHandler mClickHandler;

    public interface InventoryAdapterOnClickHandler {
        void onClick(InventoryItem inventoryItem);
    }

    public InventoryAdapter (InventoryAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class InventoryAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final InventoryListItemBinding mBinding;

        InventoryAdapterViewHolder(InventoryListItemBinding inventoryListItemBinding) {
            super(inventoryListItemBinding.getRoot());
            mBinding = inventoryListItemBinding;
            mBinding.tvInventoryName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            InventoryItem inventoryItem = mInventoryItems.get(adapterPosition);
            mClickHandler.onClick(inventoryItem);
        }
    }

    @NonNull
    @Override
    public InventoryAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        InventoryListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.inventory_list_item, parent, false);

        return new InventoryAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapterViewHolder holder, int position) {
        InventoryItem inventoryItem = mInventoryItems.get(position);

        holder.mBinding.setInventoryItem(inventoryItem);
        holder.mBinding.tvInventoryName.setText(inventoryItem.getName());
    }

    @Override
    public int getItemCount() {
        return mInventoryItems == null ? 0 : mInventoryItems.size();
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) { 
        mInventoryItems = inventoryItems; 
    }
    
}
