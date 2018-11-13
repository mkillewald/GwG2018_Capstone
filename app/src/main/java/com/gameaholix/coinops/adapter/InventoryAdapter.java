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
        final ListItemBinding mBinding;

        InventoryAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.getRoot().setOnClickListener(this);
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
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new InventoryAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull InventoryAdapterViewHolder holder, int position) {
        InventoryItem inventoryItem = mInventoryItems.get(position);

        holder.mBinding.tvName.setText(inventoryItem.getName());
        holder.mBinding.tvName.setMaxLines(1);
        holder.mBinding.tvName.setEllipsize(TextUtils.TruncateAt.END);
    }

    @Override
    public int getItemCount() {
        return mInventoryItems == null ? 0 : mInventoryItems.size();
    }

    public void setInventoryItems(List<InventoryItem> inventoryItems) { 
        mInventoryItems = inventoryItems; 
    }
    
}
