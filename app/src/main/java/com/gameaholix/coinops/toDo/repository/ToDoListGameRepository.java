package com.gameaholix.coinops.toDo.repository;

import android.support.annotation.NonNull;

import com.gameaholix.coinops.BaseListRepository;
import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

public class ToDoListGameRepository extends BaseListRepository {

    public ToDoListGameRepository(@NonNull String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            super.fetchList(getListRef(user.getUid(), gameId).orderByValue());
//        } else {
//            // user is not signed in
        }
    }

    private DatabaseReference getListRef(String uid, String gameId) {
        return super.getDatabaseReference()
                .child(Db.GAME)
                .child(uid)
                .child(gameId)
                .child(Db.TODO_LIST);
    }
}
