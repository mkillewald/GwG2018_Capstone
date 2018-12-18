package com.gameaholix.coinops.inventory.repository;

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
import com.gameaholix.coinops.model.InventoryItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;

import java.util.HashMap;
import java.util.Map;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class InventoryItemRepository {
    private static final String TAG = InventoryItemRepository.class.getSimpleName();
    private LiveData<InventoryItem> mItemLiveData;
    private String mItemId;
    private DatabaseReference mDatabaseReference;

    public InventoryItemRepository(String itemId) {
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();

        if (TextUtils.isEmpty(itemId)) {
            // we are adding a new InventoryItem
            mItemLiveData = new MutableLiveData<>();
            ((MutableLiveData<InventoryItem>) mItemLiveData).setValue(new InventoryItem());
        } else {
            // we are retrieving an existing InventoryItem
            mItemId = itemId;
            mItemLiveData = fetchData();
        }
    }

    private DatabaseReference getInventoryRootRef(String uid) {
        return mDatabaseReference
                .child(Db.INVENTORY)
                .child(uid);
    }

    private DatabaseReference getInventoryRef(String uid) {
        return getInventoryRootRef(uid)
                .child(mItemId);
    }

    private DatabaseReference getUserInventoryListRootRef(String uid) {
        return mDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.INVENTORY_LIST);
    }

    private DatabaseReference getUserInventoryListRef(String uid) {
        return getUserInventoryListRootRef(uid)
                .child(mItemId);
    }

    private LiveData<InventoryItem> fetchData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            Query query = getInventoryRef(uid).orderByValue();
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(query);

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.
            return Transformations.map(liveData, new Deserializer());
        } else {
            // user is not signed in
            return null;
        }
    }

    private class Deserializer implements Function<DataSnapshot, InventoryItem> {
        @Override
        public InventoryItem apply(DataSnapshot dataSnapshot) {
            Log.d(TAG, "Deserializer called");
            InventoryItem inventoryItem = dataSnapshot.getValue(InventoryItem.class);
            if (inventoryItem != null) {
                inventoryItem.setId(mItemId);
            } else {
//                inventoryItem = new InventoryItem();
                Log.e(TAG, "Failed to read item details from database, the returned item is null!");
            }
            return inventoryItem;
        }
    }

    @NonNull
    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }

    public boolean add(InventoryItem newItem) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            String itemId = getInventoryRootRef(user.getUid()).push().getKey();

            if (itemId == null) return false;

            DatabaseReference inventoryRef = getInventoryRootRef(uid).child(itemId);
            DatabaseReference userInventoryListRef = getUserInventoryListRootRef(uid);

            Map<String, Object> valuesWithPath = new HashMap<>();
            valuesWithPath.put(inventoryRef.getPath().toString(), newItem);
            valuesWithPath.put(userInventoryListRef.child(itemId).getPath().toString(),
                    newItem.getName());

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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
            Log.e(TAG, "Failed to add inventory item, user was not signed in!");
            return false;
        }
    }

    public boolean update(InventoryItem item) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            String uid = user.getUid();

            DatabaseReference inventoryRef = getInventoryRef(uid);
            DatabaseReference userInventoryListRef = getUserInventoryListRef(uid);

            // convert item to Map so it can be iterated
            Map<String, Object> currentValues = item.getMap();

            // create new Map with full database paths as keys using values from item Map created above
            Map<String, Object> valuesWithPath = new HashMap<>();
            for (String key : currentValues.keySet()) {
                valuesWithPath.put(inventoryRef.child(key).getPath().toString(), currentValues.get(key));
                if (key.equals(Db.NAME)) {
                    valuesWithPath.put(userInventoryListRef.getPath().toString(), currentValues.get(key));
                }
            }

            // perform atomic update to firebase using Map with database paths as keys
            mDatabaseReference.updateChildren(valuesWithPath, new DatabaseReference.CompletionListener() {
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
            // user not signed in
            Log.e(TAG, "Failed to update inventory item, user was not signed in!");
            return false;
        }
    }

    public boolean delete() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            String uid = user.getUid();

            // delete inventory item
            getInventoryRef(uid).removeValue();

            // delete inventory list entry
            getUserInventoryListRef(uid).removeValue();

            return true;
        } else {
            // user not signed in
            Log.e(TAG, "Failed to delete inventory item, user was not signed in!");
            return false;
        }
    }
}
