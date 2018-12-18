package com.gameaholix.coinops.game.repository;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class GameListRepository extends BaseListRepository {

    public GameListRepository() {
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
                .child(Db.GAME_LIST);
    }
}
