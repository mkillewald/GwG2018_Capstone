package com.gameaholix.coinops.toDo.repository;

import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ToDoListRepository extends BaseListRepository {
    private static final String TAG = ToDoListRepository.class.getSimpleName();

    public ToDoListRepository(@Nullable String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(gameId)) {
                // use User (global) database reference
                super.fetchList(Db.getUserToDoListRef(uid));
            } else {
                // use Game specific database reference
                super.fetchList(Db.getGameToDoListRef(uid, gameId));
            }
//        } else {
//            // user is not signed in
        }
    }
}
