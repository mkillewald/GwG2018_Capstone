package com.gameaholix.coinops.viewModel;


import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.FirebaseQueryLiveData;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Class used from https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
public class GameListViewModel extends ViewModel {
//    private static final DatabaseReference HOT_STOCK_REF =
//            FirebaseDatabase.getInstance().getReference("/hotstock");
//    private final FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(HOT_STOCK_REF);

    @NonNull
    public LiveData<DataSnapshot> getDataSnapshotLiveData(@NonNull String uid) {
        DatabaseReference gameListRef = FirebaseDatabase.getInstance().getReference()
                .child(Db.USER)
                .child(uid)
                .child(Db.GAME_LIST);
        return new FirebaseQueryLiveData(gameListRef.orderByValue());
    }

}
