package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ListRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GameToDoListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mToDoListLiveData;
    private ListRepository mRepository;

    public GameToDoListViewModel() {
        // empty constructor,  is this needed?
    }

    public void init(String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // use game specific list reference
            DatabaseReference gameToDoListRef = databaseReference
                    .child(Db.GAME)
                    .child(user.getUid())
                    .child(gameId)
                    .child(Db.TODO_LIST);

            mRepository = new ListRepository(gameToDoListRef, gameToDoListRef.orderByValue());
            mToDoListLiveData = mRepository.getListLiveData();
//        } else {
//            // user is not signed in
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getToDoListLiveData() {
        return mToDoListLiveData;
    }
}
