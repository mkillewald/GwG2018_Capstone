package com.gameaholix.coinops.repository;

import android.arch.core.util.Function;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Transformations;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.InventoryItem;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InventoryItemRepository {
    private static LiveData<InventoryItem> mItemLiveData;

    public InventoryItemRepository(String itemId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user signed in
            DatabaseReference inventoryRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.INVENTORY)
                    .child(user.getUid())
                    .child(itemId);

            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(inventoryRef.orderByValue());

            mItemLiveData = Transformations.map(liveData, new Deserializer());
        } else {
            // user not signed in
        }

    }

    private class Deserializer implements Function<DataSnapshot, InventoryItem> {
        @Override
        public InventoryItem apply(DataSnapshot dataSnapshot) {
            return dataSnapshot.getValue(InventoryItem.class);
        }
    }

    @NonNull
    public LiveData<InventoryItem> getItemLiveData() {
        return mItemLiveData;
    }
}
