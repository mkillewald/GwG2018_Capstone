package com.gameaholix.coinops.inventory;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.InventoryItem;
import com.gameaholix.coinops.utility.Db;
import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class InventoryDetailActivity extends AppCompatActivity implements
        InventoryDetailFragment.OnFragmentInteractionListener,
        InventoryEditFragment.OnFragmentInteractionListener,
        NetworkUtils.CheckInternetConnection.TaskCompleted{
//    private static final String TAG = InventoryDetailActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";

    private InventoryItem mInventoryItem;
    private CoordinatorLayout mCoordinatorLayout;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mInventoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition slideIn = inflater.inflateTransition(R.transition.slide_in);
            getWindow().setEnterTransition(slideIn);
        }

        setContentView(R.layout.activity_fragment_host);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AdView adView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (savedInstanceState == null) {
            mInventoryItem = getIntent().getParcelableExtra(EXTRA_INVENTORY_ITEM);

            InventoryDetailFragment fragment = InventoryDetailFragment.newInstance(mInventoryItem);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            mInventoryItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
        }

        setTitle(R.string.inventory_details_title);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mInventoryRef = mDatabaseReference
                .child(Db.INVENTORY)
                .child(mUser.getUid())
                .child(mInventoryItem.getId());

        mCoordinatorLayout = findViewById(R.id.coordinator_layout);

        if (NetworkUtils.isNetworkEnabled(this)) {
            new NetworkUtils.CheckInternetConnection(this).execute();
        } else {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_unavailable);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof InventoryEditFragment) {
            menu.findItem(R.id.menu_edit_inventory).setVisible(false);
        } else {
            menu.findItem(R.id.menu_edit_inventory).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_inventory:
                // handled by InventoryDetailFragment
                return false;
            case R.id.menu_delete_inventory:
                showDeleteAlert();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_INVENTORY_ITEM, mInventoryItem);
    }

    @Override
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (!networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
        }
    }

    @Override
    public void onDeleteButtonPressed() {
        showDeleteAlert();
    }

    @Override
    public void onEditButtonPressed(InventoryItem inventoryItem) {
        // replace InventoryDetailFragment with InventoryEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, InventoryEditFragment.newInstance(inventoryItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace InventoryEditFragment with InventoryDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, InventoryDetailFragment.newInstance(mInventoryItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    private void showDeleteAlert() {
        if (mUser != null) {
            // user is signed in

            AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new AlertDialog.Builder(this);
            }
            builder.setTitle(R.string.really_delete_inventory_item)
                    .setMessage(R.string.inventory_item_will_be_deleted)
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                        deleteItemData();
                        finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteItemData() {
        // delete inventory item
        mInventoryRef.removeValue();

        // delete inventory list entry
        mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.INVENTORY_LIST)
                .child(mInventoryItem.getId())
                .removeValue();
    }

    // Hide keyboard after touch event occurs outside of EditText
    // Solution used from:
    // https://stackoverflow.com/questions/4828636/edittext-clear-focus-on-touch-outside
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            View view = getCurrentFocus();
            if ( view instanceof EditText) {
                Rect outRect = new Rect();
                view.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    view.clearFocus();
                    InputMethodManager imm =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent(event);
    }
}
