package com.gameaholix.coinops.shopping.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.shopping.repository.ShoppingListRepository;

import java.util.List;

public class ShoppingListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ShoppingListViewModel(@Nullable String gameId) {
        ShoppingListRepository listRepository = new ShoppingListRepository(gameId);
        mListLiveData = listRepository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
