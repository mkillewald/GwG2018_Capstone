package com.gameaholix.coinops.inventory.repository;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.BaseListRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class InventoryListRepository  extends BaseListRepository {
    private static final String TAG = InventoryListRepository.class.getSimpleName();
    private DatabaseReference mDatabaseReference;

    public InventoryListRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabaseReference =  FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            // user is signed in
            DatabaseReference listRef = getInventoryListRef(user.getUid());
            fetchList(listRef.orderByValue());
//        } else {
//            // user is not signed in
        }
    }

    private DatabaseReference getInventoryListRef(String uid) {
        return mDatabaseReference
                .child(Db.USER)
                .child(uid)
                .child(Db.INVENTORY_LIST);
    }
}
