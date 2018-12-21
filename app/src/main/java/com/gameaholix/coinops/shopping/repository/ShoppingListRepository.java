package com.gameaholix.coinops.shopping.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ShoppingListRepository extends BaseListRepository {
    private String TAG = ShoppingListRepository.class.getSimpleName();

    public ShoppingListRepository(@Nullable String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(gameId)) {
                // use User (global) database reference
                super.fetchList(getUserShopListRef(uid));
            } else {
                // user Game specific database reference
                super.fetchList(getShopListRef(uid, gameId));
            }
//        } else {
//            // user is not signed in
        }
    }

    private DatabaseReference getShopListRef(@NonNull String uid, @NonNull String gameId) {
        return super.getDatabaseReference()
                .child(Db.GAME)
                .child(uid)
                .child(gameId)
                .child(Db.SHOP_LIST);
    }

    private DatabaseReference getUserShopListRef(@NonNull String uid) {
        return super.getDatabaseReference()
                .child(Db.USER)
                .child(uid)
                .child(Db.SHOP_LIST);
    }
}
