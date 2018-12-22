package com.gameaholix.coinops.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.toDo.ToDoDetailActivity;
import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ToDoListProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ToDoListProvider.class.getSimpleName();
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";

    private final Context mContext;
    private final ArrayList<ToDoItem> mToDoList = new ArrayList<>();
    private DatabaseReference mToDoListRef;
    private ValueEventListener mToDoListener;
    private int appWidgetId;

    ToDoListProvider(Context mContext, Intent intent) {
        this.mContext = mContext;
        appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                AppWidgetManager.INVALID_APPWIDGET_ID);
    }

    @Override
    public void onCreate() {
        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();

        if (user != null) {
            // user is signed in
            // use global list reference
            mToDoListRef = databaseReference
                    .child(Fb.USER)
                    .child(user.getUid())
                    .child(Fb.TODO_LIST);

            // Setup event listener
            mToDoListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mToDoList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        ToDoItem toDoItem = new ToDoItem(id, null, name);
                        mToDoList.add(toDoItem);
                    }
                    AppWidgetManager.getInstance(mContext)
                            .notifyAppWidgetViewDataChanged(appWidgetId, R.id.lv_widget);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {
                    // Failed to read value
                    Log.d(TAG, "Failed to read from database.", databaseError.toException());
                }
            };
            mToDoListRef.addValueEventListener(mToDoListener);
//        } else {
//            // user is not signed in
        }
    }

    @Override
    public void onDestroy() {
        mToDoListRef.removeEventListener(mToDoListener);
    }

    @Override
    public int getCount() {
        return mToDoList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_row);
        ToDoItem toDoItem = mToDoList.get(position);
        remoteView.setTextViewText(R.id.tv_item_name, toDoItem.getName());

        Intent intent = new Intent(mContext, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO, toDoItem);
        remoteView.setOnClickFillInIntent(R.id.widget_list_row, intent);

        return remoteView;
    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public void onDataSetChanged() {
    }
}
