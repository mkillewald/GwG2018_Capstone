package com.gameaholix.coinops.game.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameRepository {
    private LiveData<Game> mGameLiveData;

    public GameRepository(String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.GAME)
                    .child(user.getUid())
                    .child(gameId);

            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(inventoryRef.orderByValue());

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.

            mGameLiveData = Transformations.map(liveData, new Deserializer());
        } else {
            // user not signed in
        }

    }

    private class Deserializer implements Function<DataSnapshot, Game> {
        @Override
        public Game apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(Game.class);
        }
    }

    @NonNull
    public LiveData<Game> getItemLiveData() {
        return mGameLiveData;
    }
}
