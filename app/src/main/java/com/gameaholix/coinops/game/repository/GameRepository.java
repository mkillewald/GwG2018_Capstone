package com.gameaholix.coinops.game.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
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
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameRepository {
    private static final String TAG = GameRepository.class.getSimpleName();
    private final DatabaseReference mDatabaseReference = FirebaseDatabase.getInstance().getReference();
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

    private DatabaseReference getGameRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.GAME)
                .child(uid)
                .child(mGameId);
    }

    private DatabaseReference getUserGameListRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.GAME_LIST);
    }

    private DatabaseReference getRepairRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.REPAIR)
                .child(uid)
                .child(mGameId);
    }

    private DatabaseReference getShopRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.SHOP)
                .child(uid);
    }

    private DatabaseReference getUserShopListRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.SHOP_LIST);
    }

    private DatabaseReference getToDoRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.TODO)
                .child(uid);
    }

    private DatabaseReference getUserTodoListRef(@NonNull String uid) {
        return mDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.TODO_LIST);
    }

    private LiveData<Game> fetchGameDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(getGameRef(uid));

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
            getRepairRef(uid).removeValue();

            // delete to do items
            Query toDoQuery = getToDoRef(uid)
                    .orderByChild(Db.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(toDoQuery, getUserTodoListRef(uid));

            // delete shopping items
            Query shopQuery = getShopRef(uid)
                    .orderByChild(Db.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(shopQuery, getUserShopListRef(uid));

            // delete game details
            getGameRef(uid).removeValue();

            // remove user game_list entry

            return false;
        } else {
            // user is not signed in
            return false;
        }
    }

    private void deleteQueryResults(Query query, final DatabaseReference globalListRef) {
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChildren()) {
                    for (DataSnapshot item : dataSnapshot.getChildren()) {
                        if (!TextUtils.isEmpty(item.getKey())) {
                            // remove item from User (global) list
                            globalListRef.child(item.getKey()).removeValue();
//                            Log.d(TAG, "Deleted: " + globalListRef.child(item.getKey()).toString());
                            // remove item
                            item.getRef().removeValue();
//                            Log.d(TAG, "Deleted: " + item.getRef().toString());
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

}
