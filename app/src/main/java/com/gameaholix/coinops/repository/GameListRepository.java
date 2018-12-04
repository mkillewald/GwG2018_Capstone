package com.gameaholix.coinops.repository;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.ListRow;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class GameListRepository {
    private final MediatorLiveData<List<ListRow>> gameListLiveData = new MediatorLiveData<>();

    public GameListRepository() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            DatabaseReference gameListRef = FirebaseDatabase.getInstance().getReference()
                .child(Db.USER)
                .child(user.getUid())
                .child(Db.GAME_LIST);
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(gameListRef.orderByValue());

            // NOTE: I don't recommend starting up a new thread like this in your production app.
            // This is not an example of "best practice" threading behavior. Optimally, you might want
            // to use an Executor with a pool of reusable threads (for example) for a job like this.

            // Here, we see that addSource() is being called on the MediatorLiveData instance with a
            // source LiveData object and an Observer that gets invoked whenever that source publishes
            // a change. During onChanged(), it offloads the work of deserialization to a new thread.
            // This threaded work is using postValue() to update the MediatorLiveData object, whereas
            // the non-threaded work when (dataSnapshot is null) is using setValue(). This is an
            // important distinction to make, because postValue() is the thread-safe way of
            // performing the update, whereas setValue() may only be called on the main thread.

            // Set up the MediatorLiveData to convert DataSnapshot object into List<Game>
            gameListLiveData.addSource(liveData, new Observer<DataSnapshot>() {
                @Override
                public void onChanged(@Nullable final DataSnapshot dataSnapshot) {
                    if (dataSnapshot != null) {
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                ArrayList<ListRow> items = new ArrayList<>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String id = child.getKey();
                                    String name = (String) child.getValue();
                                    ListRow item = new ListRow(id, name);
                                    items.add(item);
                                }
                                gameListLiveData.postValue(items);
                            }
                        }).start();
                    } else {
                        gameListLiveData.setValue(null);
                    }
                }
            });
//        } else {
//            // user is not signed in
        }
    }

    @NonNull
    public MediatorLiveData<List<ListRow>> getGameListLiveData() {
        return gameListLiveData;
    }
}
