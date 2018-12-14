package com.gameaholix.coinops.repository;

import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ShoppingListGameRepository extends BaseListRepository {

    public ShoppingListGameRepository(@NonNull String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.GAME)
                    .child(user.getUid())
                    .child(gameId)
                    .child(Db.SHOP_LIST);

            fetchList(listRef.orderByValue());
//        } else {
//            // user is not signed in
        }
    }
}
