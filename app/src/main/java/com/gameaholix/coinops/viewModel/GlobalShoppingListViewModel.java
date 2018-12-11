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

public class GlobalShoppingListViewModel extends ViewModel {
    private LiveData<List<ListRow>> mShoppingListLiveData;
    private ListRepository mRepository;

    public GlobalShoppingListViewModel() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user != null) {
            // user is signed in

            DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

            // use global list reference
            DatabaseReference globalShopListRef = databaseReference
                    .child(Db.USER)
                    .child(user.getUid())
                    .child(Db.SHOP_LIST);

            mRepository = new ListRepository(globalShopListRef, globalShopListRef.orderByValue());
            mShoppingListLiveData = mRepository.getListLiveData();
        } else {
            // user is not signed in
        }
    }

    @NonNull
    public LiveData<List<ListRow>> getShoppingListLiveData() {
        return mShoppingListLiveData;
    }
}
