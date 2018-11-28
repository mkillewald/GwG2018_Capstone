package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.model.Game;
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

public class GameListViewModel extends ViewModel {
//    private static final String TAG = GameListViewModel.class.getSimpleName();
//    private static LiveData<List<Game>> gameListLiveData;
    private final MediatorLiveData<List<Game>> gameListLiveData = new MediatorLiveData<>();


    public GameListViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in
            DatabaseReference gameListRef = FirebaseDatabase.getInstance().getReference()
                    .child(Db.USER)
                    .child(user.getUid())
                    .child(Db.GAME_LIST);
            FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(gameListRef.orderByValue());

            // NOTE: Transformations run synchronously on the main thread, if the total time it takes
            // to perform this conversion is over 16 ms, "jank" will occur. A MediatorLiveData can be used
            // instead to execute off of the main thread.

//        gameListLiveData = Transformations.map(liveData, new Deserializer());

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
                                ArrayList<Game> games = new ArrayList<>();
                                for (DataSnapshot child : dataSnapshot.getChildren()) {
                                    String gameId = child.getKey();
                                    String name = (String) child.getValue();
                                    Game game = new Game(gameId, name);
                                    games.add(game);
                                }
                                gameListLiveData.postValue(games);
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

//    private class Deserializer implements Function<DataSnapshot, List<Game>> {
//        @Override
//        public List<Game> apply(DataSnapshot dataSnapshot) {
////            long start = System.currentTimeMillis();
//            ArrayList<Game> games = new ArrayList<>();
//            for (DataSnapshot child : dataSnapshot.getChildren()) {
//                String gameId = child.getKey();
//                String name = (String) child.getValue();
//                Game game = new Game(gameId, name);
//                games.add(game);
//            }
////            long end = System.currentTimeMillis();
////            Log.d(TAG, "Transformation took: " + (end - start) + "ms");
//            return games;
//        }
//    }

//    @NonNull
//    public LiveData<List<Game>> getGameListLiveData() {
//        return gameListLiveData;
//    }

    @NonNull
    public MediatorLiveData<List<Game>> getGameListLiveData() {
        return gameListLiveData;
    }

}
