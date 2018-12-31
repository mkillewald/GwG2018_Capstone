package com.gameaholix.coinops.shopping.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.shopping.repository.ShoppingItemRepository;

public class ShoppingItemViewModel extends ViewModel {
    private String mGameId;
    private String mItemId;
    private LiveData<Item> mItemLiveData;
    private ShoppingItemRepository mRepository;

    /**
     * Constructor used by ShoppingItemViewModelFactory to inject itemId and gameId
     * @param itemId the ID of the Item to inject
     * @param gameId the ID of the Game to inject
     */
    ShoppingItemViewModel(String gameId, String itemId) {
        mGameId = gameId;
        mItemId = itemId;
        mRepository = new ShoppingItemRepository(gameId, itemId);
        mItemLiveData = mRepository.getItemLiveData();
    }

    public String getGameId() {
        return mGameId;
    }

    public String getItemId() {
        return mItemId;
    }

    public LiveData<Item> getItemLiveData() {
        return mItemLiveData;
    }

    /**
     * Add the Item held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add() {
        return mRepository.add(mItemLiveData.getValue());
    }

    /**
     * Update the existing Item held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update() {
        return mRepository.update(mItemLiveData.getValue());
    }

    /**
     * Delete the Item held by this ViewModel instance from the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        return mRepository.delete();
    }
}
