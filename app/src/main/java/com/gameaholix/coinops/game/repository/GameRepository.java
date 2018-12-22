package com.gameaholix.coinops.game.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameRepository {
    private static final String TAG = GameRepository.class.getSimpleName();
    private LiveData<Game> mGameLiveData;
    private String mGameId;

    public GameRepository(String gameId) {
        if (TextUtils.isEmpty(gameId)) {
            // we are adding a new Game
            mGameLiveData = new MutableLiveData<>();
            ((MutableLiveData<Game>) mGameLiveData).setValue(new Game());
        } else {
            // we are retrieving an existing InventoryItem
            mGameId = gameId;
            mGameLiveData = fetchGameDetails();
        }
    }

    private LiveData<Game> fetchGameDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(Db.getGameRef(uid, mGameId));

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            return null;
        }
    }

    private class Deserializer implements Function<DataSnapshot, Game> {
        @Override
        public Game apply(DataSnapshot dataSnapshot) {
            Game game = dataSnapshot.getValue(Game.class);
            if (game != null) {
                game.setId(mGameId);
            } else {
                Log.e(TAG, "Failed to read item details from database, the returned item is null!");
            }
            return game;
        }

    }

    @NonNull
    public LiveData<Game> getGameLiveData() {
        return mGameLiveData;
    }

    public boolean delete() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            final String uid = user.getUid();

            // delete repair logs
            Db.getRepairRef(uid, mGameId).removeValue();

            // delete to do items
            Query toDoQuery = Db.getToDoRef(uid)
                    .orderByChild(Db.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(toDoQuery, Db.getUserToDoListRef(uid));

            // delete shopping items
            Query shopQuery = Db.getShopRef(uid)
                    .orderByChild(Db.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(shopQuery, Db.getUserShopListRef(uid));

            // delete game details
            Db.getGameRef(uid, mGameId).removeValue();

            // remove user game_list entry
            Db.getUserGameListRef(uid).child(mGameId).removeValue();

            return false;
        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Deletes items matching a Firebase Query, and also removes the items from a list if provided.
     * @param query the Firebase realtime database query to perform
     * @param listRef the list reference to also remove items from
     */
    private void deleteQueryResults(Query query, @Nullable final DatabaseReference listRef) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (listRef != null && !TextUtils.isEmpty(item.getKey())) {
                            // remove item from list
                            listRef.child(item.getKey()).removeValue();
                        }

                        // remove item
                        item.getRef().removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
