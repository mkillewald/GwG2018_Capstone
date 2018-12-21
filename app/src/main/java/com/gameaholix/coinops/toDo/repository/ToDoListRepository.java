package com.gameaholix.coinops.toDo.repository;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ToDoListRepository extends BaseListRepository {
    private static final String TAG = ToDoListRepository.class.getSimpleName();

    public ToDoListRepository(@Nullable String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            String uid = user.getUid();

            if (TextUtils.isEmpty(gameId)) {
                // use User (global) database reference
                super.fetchList(getUserListRef(uid));
            } else {
                // use Game specific database reference
                super.fetchList(getGameListRef(user.getUid(), gameId).orderByValue());
            }
//        } else {
//            // user is not signed in
        }
    }

    private DatabaseReference getGameListRef(@NonNull String uid, @NonNull String gameId) {
        return super.getDatabaseReference()
                .child(Db.GAME)
                .child(uid)
                .child(gameId)
                .child(Db.TODO_LIST);
    }

    private DatabaseReference getUserListRef(@NonNull String uid) {
        return super.getDatabaseReference()
                .child(Db.USER)
                .child(uid)
                .child(Db.TODO_LIST);
    }
}
