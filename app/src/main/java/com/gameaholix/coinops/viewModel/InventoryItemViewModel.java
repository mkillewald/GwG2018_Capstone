package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.text.TextUtils;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;
    private InventoryItemRepository mRepository;

    // used for detail view and editing
    InventoryItemViewModel(String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            // used to add a new inventory item
            mRepository = new InventoryItemRepository();
        } else {
            // used to display existing inventory item
            mRepository = new InventoryItemRepository(itemId);
            mItemLiveData = mRepository.getItemLiveData();
        }
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
