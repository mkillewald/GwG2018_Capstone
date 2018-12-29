package com.gameaholix.coinops.toDo.repository;

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
import com.gameaholix.coinops.model.ToDoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ToDoItemRepository {
    private static final String TAG = ToDoItemRepository.class.getSimpleName();
    private LiveData<ToDoItem> mItemLiveData;
    private String mItemId;
    private String mGameId;

    /**
     * Constructor used for adding a new or retrieving an existing ToDoItem
     * @param itemId the ID of the existing ToDoItem to retrieve. This will be null if
     *               we are adding a new ToDoItem.
     */
    public ToDoItemRepository(@Nullable String itemId, @Nullable String gameId) {
        if (TextUtils.isEmpty(itemId)) {
            // we are adding a new ToDoItem
            mGameId = gameId;
            mItemLiveData = new MutableLiveData<>();
            ((MutableLiveData<ToDoItem>) mItemLiveData).setValue(new ToDoItem(mGameId));

        } else {
            // we are editing an existing ToDoItem
            mGameId = gameId;
            mItemId = itemId;
            mItemLiveData = fetchData();
        }
    }

    /**
     * Fetch the ToDoItem data from Firebase
     * @return a LiveData<> object containing the ToDoItem retrieved from Firebase
     */
    private LiveData<ToDoItem> fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(Fb.getToDoRef(uid, mItemId));

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            ((MutableLiveData<ToDoItem>) mItemLiveData).setValue(new ToDoItem());
            return mItemLiveData;
        }
    }

    private class Deserializer implements Function<DataSnapshot, ToDoItem> {
        @Override
        public ToDoItem apply(DataSnapshot dataSnapshot) {
            ToDoItem toDoItem = dataSnapshot.getValue(ToDoItem.class);
            if (toDoItem != null) {
                toDoItem.setId(mItemId);
                if (TextUtils.isEmpty(mGameId)) mGameId = toDoItem.getParentId();
            } else {
                Log.e(TAG, "Failed to read item details from database, the returned item is null!");
            }
            return toDoItem;
        }
    }

    public LiveData<ToDoItem> getItemLiveData() {
        return mItemLiveData;
    }


    /**
     * Add a new ToDoItem to Firebase
     * @param newItem the new ToDoItem to add
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add(ToDoItem newItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            mItemId = Fb.getToDoRootRef(uid).push().getKey();
            mGameId = newItem.getParentId();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) {
                Log.d(TAG, "mItemId: " + mItemId);
                Log.d(TAG, "mGameId: " + mGameId);
                return false;
            }

            DatabaseReference toDoRef = Fb.getToDoRef(uid, mItemId);
            DatabaseReference gameToDoListRef = Fb.getGameToDoListRef(uid, mGameId);
            DatabaseReference userToDoListRef = Fb.getUserToDoListRef(uid);

            Map<String, Object> valuesWithPath = new HashMap<>();
            valuesWithPath.put(toDoRef.getPath().toString(), newItem);
            valuesWithPath.put(gameToDoListRef.child(mItemId).getPath().toString(),
                    newItem.getName());
            valuesWithPath.put(userToDoListRef.child(mItemId).getPath().toString(),
                    newItem.getName());

            // perform atomic update to Firebase using Map with database paths as keys
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
            Log.e(TAG, "Failed to add to do item, user was not signed in!");
            return false;
        }
    }

    /**
     * Update an existing ToDoItem to Firebase
     * @param item the existing ToDoItem instance to update
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update(ToDoItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) return false;

            DatabaseReference toDoRef = Fb.getToDoRef(uid, mItemId);
            DatabaseReference gameToDoListRef = Fb.getGameToDoListRef(uid, mGameId);
            DatabaseReference userToDoListRef = Fb.getUserToDoListRef(uid);

            // convert item to Map so it can be iterated
            Map<String, Object> currentValues = item.getMap();

            // create new Map with full database paths as keys using values from item Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(toDoRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Fb.NAME)) {
                    valuesWithPath.put(gameToDoListRef.child(mItemId).getPath().toString(),
                            currentValues.get(key));
                    valuesWithPath.put(userToDoListRef.child(mItemId).getPath().toString(),
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
            Log.e(TAG, "Failed to update to do item, user was not signed in!");
            return false;
        }
    }

    /**
     * Delete an ToDoItem from Firebase
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) return false;

            // delete to do item
            Fb.getToDoRef(uid, mItemId).removeValue();

            // delete game to do list entry
            Fb.getGameToDoListRef(uid, mGameId).child(mItemId).removeValue();

            // delete user to do list entry
            Fb.getUserToDoListRef(uid).child(mItemId).removeValue();

            return true;
        } else {
            // user not signed in
            Log.e(TAG, "Failed to delete to do item, user was not signed in!");
            return false;
        }
    }
}
