package com.gameaholix.coinops.inventory.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class InventoryItemViewModelFactory implements ViewModelProvider.Factory {
    private String mItemId;

    public InventoryItemViewModelFactory(@Nullable String itemId) {
        mItemId = itemId;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new InventoryItemViewModel(mItemId);
    }
}