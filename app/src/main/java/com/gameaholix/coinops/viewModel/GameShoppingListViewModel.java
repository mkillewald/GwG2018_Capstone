package com.gameaholix.coinops.viewModel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repository.ListRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class GameShoppingListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mShoppingListLiveData;
    private ListRepository mRepository;

    public GameShoppingListViewModel() {
        // empty constructor,  is this needed?
    }

    public void init(String gameId) {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // use game specific list reference
            DatabaseReference gameShopListRef = databaseReference
                    .child(Db.GAME)
                    .child(user.getUid())
                    .child(gameId)
                    .child(Db.SHOP_LIST);

            mRepository = new ListRepository(gameShopListRef, gameShopListRef.orderByValue());
            mShoppingListLiveData = mRepository.getListLiveData();
//        } else {
//            // user is not signed in
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getShoppingListLiveData() {
        return mShoppingListLiveData;
    }
}
