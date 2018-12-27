package com.gameaholix.coinops.inventory.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.inventory.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private LiveData<InventoryItem> mItemLiveData;
    private MutableLiveData<InventoryItem> mItemCopyLiveData;
    private InventoryItemRepository mRepository;

    /**
     * Constructor used to create an InventoryItemViewModel instance
     * @param itemId the itemId of the InventoryItem that will be injected by the
     *               InventoryItemViewModelFactory. It will be null if we are adding a new
     *               InventoryItem and non-null if we are editing an existing InventoryItem
     */
    InventoryItemViewModel(String itemId) {
        mRepository = new InventoryItemRepository(itemId);
        mItemLiveData = mRepository.getItemLiveData();
    }

    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    /**
     * Returns a LiveData<InventoryItem> object which contains a duplicate copy of the InventoryItem
     * data contained in this instance. This is useful for editing details and maintaining state
     * before those changes have been saved, and so any changes can easily be reverted.
     * @return the LiveData object containing a duplicate copy of the data held by this instance
     */
    public LiveData<InventoryItem> getItemCopyLiveData() {
        if (mItemCopyLiveData == null) {
            mItemCopyLiveData = new MutableLiveData<>();
            InventoryItem itemCopy = new InventoryItem(getItemLiveData().getValue());
            mItemCopyLiveData.setValue(itemCopy);
        }
        return mItemCopyLiveData;
    }

    /**
     * Clears the duplicate LiveData object by setting it to null. This is used when an edit
     * operation is cancelled by the user.
     */
    public void clearItemCopyLiveData() {
        mItemCopyLiveData = null;
    }

    /**
     * Add the InventoryItem held by this instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add() {
        return mRepository.add(mItemCopyLiveData.getValue());
    }

    /**
     * Update the existing InventoryItem held by this instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update() {
        return mRepository.update(mItemCopyLiveData.getValue());
    }

    /**
     * Delete the InventoryItem held by this instance from the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        return mRepository.delete();
    }
}
