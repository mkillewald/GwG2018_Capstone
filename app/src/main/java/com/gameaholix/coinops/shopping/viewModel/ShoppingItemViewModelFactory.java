package com.gameaholix.coinops.shopping.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ShoppingItemViewModelFactory implements ViewModelProvider.Factory {
    private String mGameId;
    private String mItemId;

    public ShoppingItemViewModelFactory(@Nullable String gameId, @Nullable String itemId) {
        mGameId = gameId;
        mItemId = itemId;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ShoppingItemViewModel(mGameId, mItemId);
    }
}
