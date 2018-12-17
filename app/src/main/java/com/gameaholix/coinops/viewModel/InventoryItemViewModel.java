package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;
    private InventoryItemRepository mRepository;

    InventoryItemViewModel(String itemId) {
        mRepository = new InventoryItemRepository(itemId);
        mItemLiveData = mRepository.getItemLiveData();
    }

    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    public boolean add() {
        return mRepository.add(getItemLiveData().getValue());
    }

    public boolean update() {
        return mRepository.update(getItemLiveData().getValue());
    }

    public boolean delete() {
        return mRepository.delete();
    }
}
