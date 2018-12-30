package com.gameaholix.coinops.toDo.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ToDoItemViewModelFactory implements ViewModelProvider.Factory {
    private String mItemId;
    private String mGameId;

    public ToDoItemViewModelFactory(@Nullable String gameId, @Nullable String itemId) {
        mItemId = itemId;
        mGameId = gameId;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ToDoItemViewModel(mGameId, mItemId);
    }
}
