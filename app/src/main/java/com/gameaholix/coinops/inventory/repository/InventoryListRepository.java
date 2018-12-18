package com.gameaholix.coinops.inventory.repository;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.BaseListRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class InventoryListRepository extends BaseListRepository {

    public InventoryListRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            super.fetchList(getListRef(user.getUid()).orderByValue());
//        } else {
//            // user is not signed in
        }
    }

    private DatabaseReference getListRef(String uid) {
        return super.getDatabaseReference()
                .child(Db.USER)
                .child(uid)
                .child(Db.INVENTORY_LIST);
    }
}
