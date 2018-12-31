package com.gameaholix.coinops.game.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.game.repository.GameRepository;

public class GameViewModel extends ViewModel {
    private String mGameId;
    private LiveData<Game> mGameLiveData;
    private MutableLiveData<Game> mGameCopyLiveData;
    private GameRepository mRepository;

    /**
     * Constructor used to create a GameViewModel instance
     * @param gameId the ID of the Game instance that will be injected by the
     *               GameViewModelFactory. It will be null if we are adding a new
     *               Game and non-null if we are displaying an existing Game
     */
    GameViewModel(String gameId) {
        mGameId = gameId;
        mRepository = new GameRepository(gameId);
        mGameLiveData = mRepository.getGameLiveData();
        mGameCopyLiveData = new MutableLiveData<>();
    }

    public String getGameId() {
        return mGameId;
    }

    public LiveData<Game> getGameLiveData() {
        return mGameLiveData;
    }

    /**
     * Returns a LiveData<> object which contains a duplicate copy of the Game data contained in
     * this instance. This is useful for editing details and maintaining state before those changes
     * have been saved, and so any changes can easily be reverted if cancelled.
     * @return the LiveData object containing a duplicate copy of the data held by this ViewModel
     * instance
     */
    public LiveData<Game> getGameCopyLiveData() {
        if (mGameCopyLiveData.getValue() == null) {
            // TODO: why does this warning occur?
            // InventoryItemViewModel does not have same warning
            // ToDoItemViewModel does not have the same warning either.
            Game gameCopy = new Game(getGameLiveData().getValue());
            mGameCopyLiveData.setValue(gameCopy);
        }
        return mGameCopyLiveData;
    }

    /**
     * Clears the duplicate LiveData object by setting it to null.
     */
    public void clearGameCopyLiveData() {
        mGameCopyLiveData.setValue(null);
    }

    /**
     * Add the Game held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add() { return mRepository.add(mGameCopyLiveData.getValue()); }

    /**
     * Update the existing Game held by this ViewModel instance to the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update() { return mRepository.update(mGameCopyLiveData.getValue()); }

    /**
     * Delete the Game held by this ViewModel instance from the repository
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        return mRepository.delete();
    }
}
