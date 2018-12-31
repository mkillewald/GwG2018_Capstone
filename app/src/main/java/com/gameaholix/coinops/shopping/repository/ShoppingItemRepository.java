package com.gameaholix.coinops.shopping.repository;

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
import com.gameaholix.coinops.model.Item;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;

import java.util.HashMap;
import java.util.Map;

public class ShoppingItemRepository {
    private static final String TAG = ShoppingItemRepository.class.getSimpleName();
    private LiveData<Item> mItemLiveData;
    private String mItemId;
    private String mGameId;

    /**
     * Constructor used for adding a new or retrieving an existing Item
     * @param gameId the ID of the parent Game entity
     * @param itemId the ID of the existing Item to retrieve. This will be null if
     *               we are adding a new Item.
     */
    public ShoppingItemRepository(@Nullable String gameId, @Nullable String itemId) {
        if (TextUtils.isEmpty(itemId)) {
            // we are adding a new Item
            mGameId = gameId;
            mItemLiveData = new MutableLiveData<>();
            ((MutableLiveData<Item>) mItemLiveData).setValue(new Item(gameId));
        } else {
            // we are editing an existing ToDoItem
            mGameId = gameId;
            mItemId = itemId;
            mItemLiveData = fetchData();
        }
    }

    /**
     * Fetch the Item data from Firebase
     * @return a LiveData<> object containing the Item retrieved from Firebase
     */
    private LiveData<Item> fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(Fb.getShopRef(uid, mItemId));

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            ((MutableLiveData<Item>) mItemLiveData).setValue(new Item());
            return mItemLiveData;
        }
    }

    private class Deserializer implements Function<DataSnapshot, Item> {
        @Override
        public Item apply(DataSnapshot dataSnapshot) {
            Item item = dataSnapshot.getValue(Item.class);
            if (item != null) {
                item.setId(mItemId);
                if (TextUtils.isEmpty(mGameId)) mGameId = item.getParentId();
            } else {
                Log.e(TAG, "Failed to read item details from database, the returned item is null!");
            }
            return item;
        }
    }

    public LiveData<Item> getItemLiveData() {
        return mItemLiveData;
    }

    /**
     * Add a new Item to Firebase
     * @param newItem the new Item to add
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean add(Item newItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            mItemId = Fb.getShopRootRef(uid).push().getKey();
            mGameId = newItem.getParentId();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) {
                return false;
            }

            DatabaseReference shopRef = Fb.getShopRef(uid, mItemId);
            DatabaseReference gameShopListRef = Fb.getGameShopListRef(uid, mGameId);
            DatabaseReference userShopListRef = Fb.getUserShopListRef(uid);

            Map<String, Object> valuesWithPath = new HashMap<>();
            valuesWithPath.put(shopRef.getPath().toString(), newItem);
            valuesWithPath.put(gameShopListRef.child(mItemId).getPath().toString(),
                    newItem.getName());
            valuesWithPath.put(userShopListRef.child(mItemId).getPath().toString(),
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
     * Update an existing Item to Firebase
     * @param item the existing Item instance to update
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean update(Item item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) return false;

            DatabaseReference shopRef = Fb.getShopRef(uid, mItemId);
            DatabaseReference gameShopListRef = Fb.getGameShopListRef(uid, mGameId);
            DatabaseReference userShopListRef = Fb.getUserShopListRef(uid);

            // convert item to Map so it can be iterated
            Map<String, Object> currentValues = item.getMap();

            // create new Map with full database paths as keys using values from item Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(shopRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Fb.NAME)) {
                    valuesWithPath.put(gameShopListRef.child(mItemId).getPath().toString(),
                            currentValues.get(key));
                    valuesWithPath.put(userShopListRef.child(mItemId).getPath().toString(),
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
     * Delete an Item from Firebase
     * @return a boolean indicating success (true) or failure (false)
     */
    public boolean delete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(mItemId) || TextUtils.isEmpty(mGameId)) return false;

            // delete to do item
            Fb.getShopRef(uid, mItemId).removeValue();

            // delete game to do list entry
            Fb.getGameShopListRef(uid, mGameId).child(mItemId).removeValue();

            // delete user to do list entry
            Fb.getUserShopListRef(uid).child(mItemId).removeValue();

            return true;
        } else {
            // user not signed in
            Log.e(TAG, "Failed to delete to do item, user was not signed in!");
            return false;
        }
    }
}
