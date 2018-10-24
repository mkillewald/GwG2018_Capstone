package com.gameaholix.coinops.adapter;

import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ShoppingListItemBinding;
import com.gameaholix.coinops.model.ShoppingItem;

import java.util.List;

public class ShoppingAdapter extends RecyclerView.Adapter<ShoppingAdapter.ShoppingAdapterViewHolder> {
    private List<ShoppingItem> mShoppingItems;
    private final ShoppingAdapterOnClickHandler mClickHandler;

    public interface ShoppingAdapterOnClickHandler {
        void onClick(ShoppingItem shoppingItem);
    }

    public ShoppingAdapter (ShoppingAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    public class ShoppingAdapterViewHolder extends RecyclerView.ViewHolder implements
            View.OnClickListener {
        final ShoppingListItemBinding mBinding;

        ShoppingAdapterViewHolder(ShoppingListItemBinding shoppingListItemBinding) {
            super(shoppingListItemBinding.getRoot());
            mBinding = shoppingListItemBinding;
            mBinding.tvShoppingName.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition  = getAdapterPosition();
            ShoppingItem shoppingItem = mShoppingItems.get(adapterPosition);
            mClickHandler.onClick(shoppingItem);
        }
    }

    @NonNull
    @Override
    public ShoppingAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ShoppingListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.shopping_list_item, parent, false);

        return new ShoppingAdapterViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ShoppingAdapterViewHolder holder, int position) {
        ShoppingItem shoppingItem = mShoppingItems.get(position);

        holder.mBinding.setShoppingItem(shoppingItem);
        holder.mBinding.tvShoppingName.setText(shoppingItem.getName());
    }

    @Override
    public int getItemCount() {
        return mShoppingItems == null ? 0 : mShoppingItems.size();
    }

    public void setShoppingItems(List<ShoppingItem> shoppingItems) {
        mShoppingItems = shoppingItems;
    }

}
