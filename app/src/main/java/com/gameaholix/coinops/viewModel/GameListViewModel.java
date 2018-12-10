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

public class GameListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mGameListLiveData;
    private ListRepository mRepository;

    public GameListViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            DatabaseReference gameListRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.USER)
                    .child(user.getUid())
                    .child(Db.GAME_LIST);

            mRepository = new ListRepository(gameListRef, gameListRef.orderByValue());
            mGameListLiveData = mRepository.getListLiveData();
        } else {
            // user is not signed in
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getGameListLiveData() {
        return mGameListLiveData;
    }

}
