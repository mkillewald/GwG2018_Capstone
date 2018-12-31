package com.gameaholix.coinops.inventory.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.inventory.repository.InventoryItemRepository;

public class InventoryItemViewModel extends ViewModel {
    private String mItemId;
    private LiveData<InventoryItem> mItemLiveData;
    private MutableLiveData<InventoryItem> mItemCopyLiveData;
    private InventoryItemRepository mRepository;

    /**
     * Default constructor used by default ViewModel factory when adding a new InventoryItem
     */
    public InventoryItemViewModel() {
        mRepository = new InventoryItemRepository(null);
        mItemLiveData = mRepository.getItemLiveData();
        mItemCopyLiveData = new MutableLiveData<>();
    }

    /**
     * Constructor used by InventoryItemViewModelFactory to inject the itemId
     * @param itemId the ID of the InventoryItem to inject
     */
    InventoryItemViewModel(String itemId) {
        mItemId = itemId;
        mRepository = new InventoryItemRepository(itemId);
        mItemLiveData = mRepository.getItemLiveData();
        mItemCopyLiveData = new MutableLiveData<>();
    }

    public String getItemId() {
        return mItemId;
    }

    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    /**
     * Returns a LiveData<> object which contains a duplicate copy of the InventoryItem
     * data contained in this instance. This is useful for editing details and maintaining state
     * before those changes have been saved, and so any changes can easily be reverted if cancelled.
     * @return the LiveData object containing a duplicate copy of the data held by this ViewModel
     * instance
     */
    public LiveData<InventoryItem> getItemCopyLiveData() {
        if (mItemCopyLiveData.getValue() == null) {
            InventoryItem itemCopy = new InventoryItem(getItemLiveData().getValue());
            mItemCopyLiveData.setValue(itemCopy);
        }
        return mItemCopyLiveData;
    }

    /**
     * Clears the duplicate LiveData object by setting the InventoryItem it holds to null.
     */
    public void clearItemCopyLiveData() {
        mItemCopyLiveData.setValue(null);
    }

    /**
     * Add the InventoryItem held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false
     */
    public boolean add() {
        return mRepository.add(mItemCopyLiveData.getValue());
    }

    /**
     * Update the existing InventoryItem held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update() {
        return mRepository.update(mItemCopyLiveData.getValue());
    }

    /**
     * Delete the InventoryItem held by this ViewModel instance from the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        return mRepository.delete();
    }
}
