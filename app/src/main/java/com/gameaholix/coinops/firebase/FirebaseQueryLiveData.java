package com.gameaholix.coinops.firebase;

import android.arch.lifecycle.LiveData;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

// Concepts and code used from 3 part series:
// https://firebase.googleblog.com/2017/12/using-android-architecture-components.html

public class FirebaseQueryLiveData extends LiveData<DataSnapshot> {
    private static final String TAG = FirebaseQueryLiveData.class.getSimpleName();

    private boolean listenerRemovePending = false;
    private final Query mQuery;
    private final MyValueEventListener mListener = new MyValueEventListener();
    private final Handler handler = new Handler();
    private final Runnable removeListener = new Runnable() {
        @Override
        public void run() {
            mQuery.removeEventListener(mListener);
            listenerRemovePending = false;
        }
    };

    public FirebaseQueryLiveData(Query query) {
        this.mQuery = query;
    }

    public FirebaseQueryLiveData(DatabaseReference ref) {
        this.mQuery = ref;
    }

    @Override
    protected void onActive() {
        if (listenerRemovePending) {
            handler.removeCallbacks(removeListener);
        }
        else {
            mQuery.addValueEventListener(mListener);
        }
        listenerRemovePending = false;
    }

    @Override
    protected void onInactive() {
        // Listener removal is scheduled on a two second delay to avoid listener removal and
        // re-add during configuration changes (device rotation) since that would trigger an unnecessary
        // re-download of data from the database.
        handler.postDelayed(removeListener, 2000);
        listenerRemovePending = true;
    }

    private class MyValueEventListener implements ValueEventListener {
        @Override
        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
            setValue(dataSnapshot);
        }

        @Override
        public void onCancelled(@NonNull DatabaseError databaseError) {
            Log.e(TAG, "Can't listen to query " + mQuery, databaseError.toException());
        }
    }
}
