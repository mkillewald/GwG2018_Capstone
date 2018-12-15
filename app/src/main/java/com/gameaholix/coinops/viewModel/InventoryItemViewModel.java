package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;
    private InventoryItemRepository mRepository;

    public InventoryItemViewModel(String itemId) {
        mRepository = new InventoryItemRepository(itemId);
        mItemLiveData = mRepository.getItemLiveData();
    }

    @NonNull
    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    public boolean addUpdate() {
        return mRepository.addUpdate();
    }

    public boolean delete() {
        return mRepository.delete();
    }
}
