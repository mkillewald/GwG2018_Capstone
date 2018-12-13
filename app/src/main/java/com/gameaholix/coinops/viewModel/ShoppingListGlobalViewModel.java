package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ShoppingListGlobalRepository;

import java.util.List;

public class ShoppingListGlobalViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public ShoppingListGlobalViewModel() {
        ShoppingListGlobalRepository repository = new ShoppingListGlobalRepository();
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
