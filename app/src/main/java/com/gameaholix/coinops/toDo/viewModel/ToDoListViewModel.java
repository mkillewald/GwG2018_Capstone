package com.gameaholix.coinops.toDo.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.toDo.repository.ToDoListRepository;

import java.util.List;

public class ToDoListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ToDoListViewModel(@Nullable String gameId) {
        ToDoListRepository listRepository = new ToDoListRepository(gameId);
        mListLiveData = listRepository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
