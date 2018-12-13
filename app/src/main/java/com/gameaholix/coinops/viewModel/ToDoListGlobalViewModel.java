package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ToDoListGlobalRepository;

import java.util.List;

public class ToDoListGlobalViewModel extends ViewModel {
    private LiveData<List<ListRow>> mListLiveData;

    public ToDoListGlobalViewModel() {
        ToDoListGlobalRepository repository = new ToDoListGlobalRepository();
        mListLiveData = repository.getListLiveData();
    }

    @NonNull
    public LiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }
}
