package com.gameaholix.coinops.toDo.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.toDo.repository.ToDoItemRepository;

public class ToDoItemViewModel extends ViewModel {
    private String mGameId;
    private String mItemId;
    private LiveData<ToDoItem> mItemLiveData;
    private MutableLiveData<ToDoItem> mItemCopyLiveData;
    private ToDoItemRepository mRepository;

    /**
     * Constructor used by ToDoItemViewModelFactory to inject itemId and gameId
     * @param itemId the ID of the ToDoItem to inject
     * @param gameId the ID of the Game to inject
     */
    ToDoItemViewModel(String gameId, String itemId) {
        mGameId = gameId;
        mItemId = itemId;
        mRepository = new ToDoItemRepository(gameId, itemId);
        mItemLiveData = mRepository.getItemLiveData();
        mItemCopyLiveData = new MutableLiveData<>();
    }

    public String getGameId() {
        return mGameId;
    }

    public String getItemId() {
        return mItemId;
    }

    public LiveData<ToDoItem> getItemLiveData() {
        return mItemLiveData;
    }

    /**
     * Returns a LiveData<> object which contains a duplicate copy of the ToDoItem data contained
     * in this instance. This is useful for editing details and maintaining state before those
     * changes have been saved, and so any changes can easily be reverted if cancelled.
     * @return the LiveData object containing a duplicate copy of the data held by this ViewModel
     * instance
     */
    public LiveData<ToDoItem> getItemCopyLiveData() {
        if (mItemCopyLiveData.getValue() == null) {
            ToDoItem itemCopy = new ToDoItem(getItemLiveData().getValue());
            mItemCopyLiveData.setValue(itemCopy);
        }
        return mItemCopyLiveData;
    }

    /**
     * Clears the duplicate LiveData object by setting the Item it holds to null.
     */
    public void clearItemCopyLiveData() {
        mItemCopyLiveData.setValue(null);
    }

    /**
     * Add the ToDoItem held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add() {
        return mRepository.add(mItemCopyLiveData.getValue());
    }

    /**
     * Update the existing ToDoItem held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update() {
        return mRepository.update(mItemCopyLiveData.getValue());
    }

    /**
     * Delete the ToDoItem held by this ViewModel instance from the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        return mRepository.delete();
    }
}
