package com.gameaholix.coinops.game.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.game.repository.GameListRepository;

import java.util.List;

public class GameListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public GameListViewModel() {
        GameListRepository repository = new GameListRepository();
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
