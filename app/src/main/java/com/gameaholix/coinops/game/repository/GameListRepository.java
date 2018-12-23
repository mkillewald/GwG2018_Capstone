package com.gameaholix.coinops.game.repository;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class GameListRepository extends BaseListRepository {

    public GameListRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();
            super.fetchList(Fb.getGameListRef(uid).orderByValue());
//        } else {
//            // user is not signed in
        }
    }
}
