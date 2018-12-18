package com.gameaholix.coinops.inventory.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.inventory.repository.InventoryListRepository;

import java.util.List;

public class InventoryListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public InventoryListViewModel() {
        InventoryListRepository repository = new InventoryListRepository();
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
