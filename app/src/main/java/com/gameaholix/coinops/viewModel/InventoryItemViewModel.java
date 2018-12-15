package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;

    public InventoryItemViewModel(String itemId) {
        InventoryItemRepository repository = new InventoryItemRepository(itemId);
        mItemLiveData = repository.getItemLiveData();
    }

    @NonNull
    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }
}
