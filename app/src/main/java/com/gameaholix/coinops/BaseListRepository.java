package com.gameaholix.coinops;

import android.arch.lifecycle.MediatorLiveData;
import android.arch.lifecycle.Observer;
import android.support.annotation.NonNull;

import com.gameaholix.coinops.firebase.FirebaseQueryLiveData;
import com.gameaholix.coinops.model.ListRow;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.Query;
import com.google.firebase.database.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public abstract class BaseListRepository {
    private final MediatorLiveData<List<ListRow>> mListLiveData = new MediatorLiveData<>();

    @NonNull
    public MediatorLiveData<List<ListRow>> getListLiveData() {
        return mListLiveData;
    }

    public void fetchList(Query query) {
        FirebaseQueryLiveData liveData = new FirebaseQueryLiveData(query);

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

        // Set up the MediatorLiveData to convert DataSnapshot object into List<ListRow>
        mListLiveData.addSource(liveData, new Observer<DataSnapshot>() {
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
                            mListLiveData.postValue(items);
                        }
                    }).start();
                } else {
                    mListLiveData.setValue(null);
                }
            }
        });
    }
}
