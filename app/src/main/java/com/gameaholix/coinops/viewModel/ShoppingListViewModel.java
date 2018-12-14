package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ShoppingListGameRepository;
import com.gameaholix.coinops.repository.ShoppingListGlobalRepository;

import java.util.List;

public class ShoppingListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ShoppingListViewModel(@Nullable String gameId) {
        if (TextUtils.isEmpty(gameId)) {
            // use global repository
            ShoppingListGlobalRepository globalRepository = new ShoppingListGlobalRepository();
            mListLiveData = globalRepository.getListLiveData();
        } else {
            // use game specific repository
            ShoppingListGameRepository gameRepository = new ShoppingListGameRepository(gameId);
            mListLiveData = gameRepository.getListLiveData();
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }

}
