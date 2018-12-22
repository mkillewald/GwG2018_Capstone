package com.gameaholix.coinops.widget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.shopping.ShoppingListActivity;
import com.gameaholix.coinops.firebase.Fb;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

class ShoppingListProvider implements RemoteViewsService.RemoteViewsFactory {
    private static final String TAG = ShoppingListProvider.class.getSimpleName();

    private final Context mContext;
    private final ArrayList<Item> mShoppingList = new ArrayList<>();
    private DatabaseReference mShopListRef;
    private ValueEventListener mShoppingListener;
    private int appWidgetId;

    ShoppingListProvider(Context mContext, Intent intent) {
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
            mShopListRef = databaseReference
                    .child(Fb.USER)
                    .child(user.getUid())
                    .child(Fb.SHOP_LIST);

            // Setup event listener
            mShoppingListener = new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    mShoppingList.clear();
                    for (DataSnapshot child : dataSnapshot.getChildren()) {
                        String id = child.getKey();
                        String name = (String) child.getValue();
                        Item item = new Item(id, null, name);
                        mShoppingList.add(item);
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
            mShopListRef.addValueEventListener(mShoppingListener);
//        } else {
//            // user is not signed in
        }
    }

    @Override
    public void onDestroy() {
        mShopListRef.removeEventListener(mShoppingListener);
    }

    @Override
    public int getCount() {
        return mShoppingList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public RemoteViews getViewAt(int position) {
        final RemoteViews remoteView = new RemoteViews(mContext.getPackageName(), R.layout.widget_list_row);
        Item item = mShoppingList.get(position);
        remoteView.setTextViewText(R.id.tv_item_name, item.getName());

        Intent intent = new Intent(mContext, ShoppingListActivity.class);
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
