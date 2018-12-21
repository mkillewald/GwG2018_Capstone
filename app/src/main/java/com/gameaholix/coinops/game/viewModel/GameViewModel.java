package com.gameaholix.coinops.game.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.game.repository.GameRepository;

public class GameViewModel extends ViewModel {
    private LiveData<Game> mGameLiveData;
    private GameRepository mRepository;

    public GameViewModel(String gameId) {
        mRepository = new GameRepository(gameId);
        mGameLiveData = mRepository.getGameLiveData();
    }

    @NonNull
    public LiveData<Game> getGameLiveData() {
        return mGameLiveData;
    }

    public boolean delete() {
        return mRepository.delete();
    }
}
