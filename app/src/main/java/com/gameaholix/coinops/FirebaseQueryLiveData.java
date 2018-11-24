package com.gameaholix.coinops;

import android.arch.lifecycle.LiveData;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Class used from https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = FirebaseQueryLiveData.class.getSimpleName();

    private final Query mQuery;
    private final MyValueEventListener mListener = new MyValueEventListener();

    public FirebaseQueryLiveData(Query query) {
        this.mQuery = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.mQuery = ref;
    }

    @Override
    protected void onActive() {
        Log.d(TAG, "onActive");
        mQuery.addValueEventListener(mListener);
    }

    @Override
    protected void onInactive() {
        Log.d(TAG, "onInactive");
        mQuery.removeEventListener(mListener);
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + mQuery, databaseError.toException());
        }
    }
}
