package com.gameaholix.coinops.game.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.game.repository.GameRepository;

public class GameViewModel extends ViewModel {
    private LiveData<Game> mGameLiveData;
    private GameRepository mRepository;

    public GameViewModel() {
        mRepository = new GameRepository();
        mGameLiveData = mRepository.getGameLiveData();
    }

    GameViewModel(String gameId) {
        mRepository = new GameRepository(gameId);
        mGameLiveData = mRepository.getGameLiveData();
    }

    @NonNull
    public LiveData<Game> getGameLiveData() {
        return mGameLiveData;
    }

    public boolean add() { return mRepository.add(mGameLiveData.getValue()); }

    public boolean update() { return mRepository.update(mGameLiveData.getValue()); }

    public boolean delete() {
        return mRepository.delete();
    }
}
