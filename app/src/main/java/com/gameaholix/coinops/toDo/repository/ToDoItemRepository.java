package com.gameaholix.coinops.toDo.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;

import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.ToDoItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;

public class ToDoItemRepository {
    private static final String TAG = ToDoItemRepository.class.getSimpleName();
    private LiveData<ToDoItem> mItemLiveData;
    private String mItemId;

    /**
     * Constructor used for adding a new or retrieving an existing ToDoItem
     * @param itemId the ID of the existing ToDoItem to retrieve. This will be null if
     *               we are adding a new ToDoItem.
     */
    public ToDoItemRepository(@Nullable String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            // we are adding a new ToDoItem
            mItemLiveData = new MutableLiveData<>();
            ((MutableLiveData<ToDoItem>) mItemLiveData).setValue(new ToDoItem());
        } else {
            // we are editing an existing ToDoItem
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

            return true;
        } else {
            // user not signed in
            Log.e(TAG, "Failed to delete to do item, user was not signed in!");
            return false;
        }
    }
}
