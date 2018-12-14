package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ToDoListGameRepository;
import com.gameaholix.coinops.repository.ToDoListGlobalRepository;

import java.util.List;

public class ToDoListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ToDoListViewModel(@Nullable String gameId) {
        if (TextUtils.isEmpty(gameId)) {
            // use global repository
            ToDoListGlobalRepository globalRepository = new ToDoListGlobalRepository();
            mListLiveData = globalRepository.getListLiveData();
        } else {
            // use game specific repository
            ToDoListGameRepository gameRepository = new ToDoListGameRepository(gameId);
            mListLiveData = gameRepository.getListLiveData();
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
