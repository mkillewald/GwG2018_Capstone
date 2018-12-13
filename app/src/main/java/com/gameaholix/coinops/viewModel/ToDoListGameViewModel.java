package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ToDoListGameRepository;

import java.util.List;

public class ToDoListGameViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public ToDoListGameViewModel() {
    }

    public void init(@NonNull String gameId) {
        ToDoListGameRepository repository = new ToDoListGameRepository(gameId);
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
