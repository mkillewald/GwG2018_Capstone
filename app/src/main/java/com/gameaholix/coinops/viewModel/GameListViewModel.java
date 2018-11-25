package com.gameaholix.coinops.viewModel;


import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.model.Game;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

// Concepts used from https://firebase.googleblog.com/2017/12/using-android-architecture-components.html
public class GameListViewModel extends ViewModel {
    private static LiveData<List<Game>> gameListLiveData;

    public void init(@NonNull String uid) {
        DatabaseReference gameListRef = FirebaseDatabase.getInstance().getReference()
                .child(Db.USER)
                .child(uid)
                .child(Db.GAME_LIST);
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(gameListRef.orderByValue());
        gameListLiveData = Transformations.map(liveData, new Deserializer());
    }

    private class Deserializer implements Function<DataSnapshot, List<Game>> {
        @Override
        public List<Game> apply(DataSnapshot dataSnapshot) {
            ArrayList<Game> games = new ArrayList<>();
            for (DataSnapshot child : dataSnapshot.getChildren()) {
                String gameId = child.getKey();
                String name = (String) child.getValue();
                Game game = new Game(gameId, name);
                games.add(game);
            }
            return games;
        }
    }

    @NonNull
    public LiveData<List<Game>> getGameListLiveData() {
        return gameListLiveData;
    }

}
