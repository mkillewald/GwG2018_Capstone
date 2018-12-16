package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;
    private InventoryItemRepository mRepository;

    public InventoryItemViewModel() {
        mRepository = new InventoryItemRepository();
    }

    InventoryItemViewModel(String itemId) {
        mRepository = new InventoryItemRepository(itemId);
        mItemLiveData = mRepository.getItemLiveData();
    }

    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    public boolean add(InventoryItem newItem) {
        return mRepository.add(newItem);
    }

    public boolean edit() {
        return mRepository.edit(mItemLiveData.getValue());
    }

    public boolean delete() {
        return mRepository.delete();
    }
}
