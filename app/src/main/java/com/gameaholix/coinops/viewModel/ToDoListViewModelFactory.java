package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class ToDoListViewModelFactory implements ViewModelProvider.Factory {
    private String mGameId;

    public ToDoListViewModelFactory(@Nullable String gameId) {
        mGameId = gameId;
    }

    @Override
    @NonNull
    @SuppressWarnings("unchecked")
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        return (T) new ToDoListViewModel(mGameId);
    }
}