package com.gameaholix.coinops.toDo.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.toDo.repository.ToDoListGameRepository;
import com.gameaholix.coinops.toDo.repository.ToDoListGlobalRepository;

import java.util.List;

public class ToDoListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    ToDoListViewModel(@Nullable String gameId) {
        BaseListRepository listRepository;
        if (TextUtils.isEmpty(gameId)) {
            // use global repository
            listRepository = new ToDoListGlobalRepository();
        } else {
            // use game specific repository
            listRepository = new ToDoListGameRepository(gameId);
        }
        mListLiveData = listRepository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
