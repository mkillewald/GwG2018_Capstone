package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ShoppingListGameRepository;

import java.util.List;

public class ShoppingListGameViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public ShoppingListGameViewModel() {
    }

    public void init(@NonNull String gameId) {
        ShoppingListGameRepository repository = new ShoppingListGameRepository(gameId);
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
