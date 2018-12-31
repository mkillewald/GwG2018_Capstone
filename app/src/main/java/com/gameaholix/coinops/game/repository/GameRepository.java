package com.gameaholix.coinops.game.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.Game;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameRepository {
    private static final String TAG = GameRepository.class.getSimpleName();
    private LiveData<Game> mGameLiveData;
    private String mGameId;

    /**
     * Constructor used for adding a new or retrieving an existing Game
     * @param gameId the ID of the existing Game to retrieve. This will be null if
     *               we are adding a new Game.
     */
    public GameRepository(@Nullable String gameId) {
        if (gameId == null) {
            // we are adding a new Game
            mGameLiveData = new MutableLiveData<>();
            ((MutableLiveData<Game>) mGameLiveData).setValue(new Game());
        } else {
            // we are retrieving an existing InventoryItem
            mGameId = gameId;
            mGameLiveData = fetchData();
        }
    }

    /**
     * Fetch the Game data from Firebase
     * @return a LiveData<> object containing the Game retrieved from firebase
     */
    private LiveData<Game> fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(Fb.getGameRef(uid, mGameId));

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            ((MutableLiveData<Game>) mGameLiveData).setValue(new Game());
            return mGameLiveData;
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

    /**
     * Add a new Game to Firebase
     * @param newGame the new Game to add
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add(Game newGame) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            mGameId = Fb.getGameRootRef(uid).push().getKey();

            if (TextUtils.isEmpty(mGameId)) return false;

            DatabaseReference gameRef = Fb.getGameRef(uid, mGameId);
            DatabaseReference gameListRef = Fb.getGameListRef(uid);

            Map<String, Object> valuesWithPath = new HashMap<>();
            valuesWithPath.put(gameRef.getPath().toString(), newGame);
            valuesWithPath.put(gameListRef.child(mGameId).getPath().toString(),
                    newGame.getName());

            // perform atomic update to firebase using Map with database paths as keys
            Fb.getDatabaseReference().updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            return true;
        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Update an existing Game to Firebase
     * @param game the existing Game instance to update
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update(Game game) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            DatabaseReference gameRef = Fb.getGameRef(uid, mGameId);
            DatabaseReference gameListRef = Fb.getGameListRef(uid);

            // convert item to Map so it can be iterated
            Map<String, Object> currentValues = game.getMap();

            // create new Map with full database paths as keys using values from item Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(gameRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Fb.NAME)) {
                    valuesWithPath.put(gameListRef.child(game.getId()).getPath().toString(),
                            currentValues.get(key));
                }
            }

            // perform atomic update to firebase using Map with database paths as keys
            Fb.getDatabaseReference().updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
                @Override
                public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
                    if (databaseError != null) {
                        Log.e(TAG, "DatabaseError: " + databaseError.getMessage() +
                                " Code: " + databaseError.getCode() +
                                " Details: " + databaseError.getDetails());
                    }
                }
            });

            return true;

        } else {
            // user is not signed in
            return false;
        }
    }

    /**
     * Delete a Game from Firebase
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            final String uid = user.getUid();

            // delete repair logs
            Fb.getRepairRef(uid, mGameId).removeValue();

            // delete to do items
            Query toDoQuery = Fb.getToDoRootRef(uid)
                    .orderByChild(Fb.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(toDoQuery, Fb.getUserToDoListRef(uid));

            // delete shopping items
            Query shopQuery = Fb.getShopRootRef(uid)
                    .orderByChild(Fb.PARENT_ID)
                    .equalTo(mGameId);
            deleteQueryResults(shopQuery, Fb.getUserShopListRef(uid));

            // delete game details
            Fb.getGameRef(uid, mGameId).removeValue();

            // remove user game_list entry
            Fb.getGameListRef(uid).child(mGameId).removeValue();

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
