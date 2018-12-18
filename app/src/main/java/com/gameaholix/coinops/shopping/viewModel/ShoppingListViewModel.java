package com.gameaholix.coinops.shopping.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.shopping.repository.ShoppingListGameRepository;
import com.gameaholix.coinops.shopping.repository.ShoppingListGlobalRepository;

import java.util.List;

public class ShoppingListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ShoppingListViewModel(@Nullable String gameId) {
        BaseListRepository listRepository;
        if (TextUtils.isEmpty(gameId)) {
            // use global repository
            listRepository = new ShoppingListGlobalRepository();
        } else {
            // use game specific repository
            listRepository = new ShoppingListGameRepository(gameId);
        }
        mListLiveData = listRepository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }

}
