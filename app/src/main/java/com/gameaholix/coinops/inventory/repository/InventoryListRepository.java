package com.gameaholix.coinops.inventory.repository;

import com.gameaholix.coinops.firebase.Fb;
import com.gameaholix.coinops.BaseListRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class InventoryListRepository extends BaseListRepository {

    public InventoryListRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            super.fetchList(Fb.getInventoryListRef(uid).orderByValue());
//        } else {
//            // user is not signed in
        }
    }


}
