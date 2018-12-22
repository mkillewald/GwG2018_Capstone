package com.gameaholix.coinops.shopping.repository;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ShoppingListRepository extends BaseListRepository {

    public ShoppingListRepository(@Nullable String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(gameId)) {
                // use User (global) database reference
                super.fetchList(Fb.getUserShopListRef(uid));
            } else {
                // user Game specific database reference
                super.fetchList(Fb.getGameShopListRef(uid, gameId));
            }
//        } else {
//            // user is not signed in
        }
    }
}
