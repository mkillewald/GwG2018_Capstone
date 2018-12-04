package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.GameListRepository;

import java.util.List;

public class GameListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mGameListLiveData;
    private GameListRepository mRepository;

    public GameListViewModel() {
        mRepository = new GameListRepository();
        mGameListLiveData = mRepository.getGameListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getGameListLiveData() {
        return mGameListLiveData;
    }

}
