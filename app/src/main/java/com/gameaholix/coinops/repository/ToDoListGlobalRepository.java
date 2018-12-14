package com.gameaholix.coinops.repository;

import com.gameaholix.coinops.firebase.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ToDoListGlobalRepository extends BaseListRepository {

    public ToDoListGlobalRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            DatabaseReference listRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.USER)
                    .child(user.getUid())
                    .child(Db.TODO_LIST);

            fetchList(listRef.orderByValue());
//        } else {
//            // user is not signed in
        }
    }
}
