package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ListItemBinding;
import com.gameaholix.coinops.model.Entry;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingAdapterViewHolder> {
    private List<Entry> mShoppingItems;
    private final ShoppingAdapterOnClickHandler mClickHandler;

    public interface ShoppingAdapterOnClickHandler {
        void onClick(Entry shoppingItem);
    }

    public ShoppingAdapter (ShoppingAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ShoppingAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ListItemBinding mBinding;

        ShoppingAdapterViewHolder(ListItemBinding listItemBinding) {
            super(listItemBinding.getRoot());
            mBinding = listItemBinding;
            mBinding.tvName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            Entry shoppingItem = mShoppingItems.get(adapterPosition);
            mClickHandler.onClick(shoppingItem);
        }
    }

    @NonNull
    @Override
    public ShoppingAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.list_item, parent, false);

        return new ShoppingAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapterViewHolder holder, int position) {
        Entry shoppingItem = mShoppingItems.get(position);

//        holder.mBinding.tvName.setText(shoppingItem.getName());
    }

    @Override
    public int getItemCount() {
        return mShoppingItems == null ? 0 : mShoppingItems.size();
    }

    public void setShoppingItems(List<Entry> shoppingItems) {
        mShoppingItems = shoppingItems;
    }

}
