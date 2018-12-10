package com.gameaholix.coinops.activity;

import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.fragment.InventoryAddEditFragment;
import com.gameaholix.coinops.fragment.InventoryDetailFragment;
import com.gameaholix.coinops.model.InventoryItem;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InventoryDetailActivity extends BaseActivity implements
        InventoryDetailFragment.OnFragmentInteractionListener,
        InventoryAddEditFragment.OnFragmentInteractionListener {
//    private static final String TAG = InventoryDetailActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";
    private static final String EXTRA_INVENTORY_NAME = "CoinOpsInventoryName";

    private String mItemId;
    private String mItemName;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            TransitionInflater inflater = TransitionInflater.from(this);

            Transition slideIn = inflater.inflateTransition(R.transition.slide_in);
            getWindow().setEnterTransition(slideIn);

            Transition slideOut = inflater.inflateTransition(R.transition.slide_out);
            getWindow().setExitTransition(slideOut);
        }

        setContentView(R.layout.activity_fragment_host);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set CoordinatorLayout of BaseActivity for displaying Snackbar
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        setCoordinatorLayout(coordinatorLayout);

        mAdView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mItemId = intent.getStringExtra(EXTRA_INVENTORY_ID);
            mItemName = intent.getStringExtra(EXTRA_INVENTORY_NAME);

            InventoryDetailFragment fragment = InventoryDetailFragment.newInstance(mItemId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            mItemId = savedInstanceState.getString(EXTRA_INVENTORY_ID);
            mItemName = savedInstanceState.getString(EXTRA_INVENTORY_NAME);
        }

        setTitle(R.string.inventory_details_title);
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
        if (currentFragment instanceof InventoryAddEditFragment) {
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

        outState.putString(EXTRA_INVENTORY_ID, mItemId);
        outState.putString(EXTRA_INVENTORY_NAME, mItemName);
    }

    @Override
    public void onDeleteButtonPressed() {
        showDeleteAlert();
    }

    @Override
    public void onEditButtonPressed(InventoryItem inventoryItem) {
        mAdView.setVisibility(View.GONE);
        // replace InventoryDetailFragment with InventoryAddEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, InventoryAddEditFragment.newInstance(inventoryItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onInventoryAddEditCompletedOrCancelled() {
        // replace InventoryAddEditFragment with InventoryDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, InventoryDetailFragment.newInstance(mItemId));
        ft.commit();

        invalidateOptionsMenu();
        mAdView.setVisibility(View.VISIBLE);
    }

    private void showDeleteAlert() {
//        if (mUser != null) {
//            // user is signed in
//
//            AlertDialog.Builder builder;
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
//            } else {
//                builder = new AlertDialog.Builder(this);
//            }
//            builder.setTitle(R.string.really_delete_inventory_item)
//                    .setMessage(R.string.inventory_item_will_be_deleted)
//                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
//                        @Override
//                        public void onClick(DialogInterface dialogInterface, int i) {
//                        dialogInterface.dismiss();
//                        }
//                    })
//                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
//                        public void onClick(DialogInterface dialog, int which) {
//                        deleteItemData();
//                        finish();
//                        }
//                    })
//                    .setIcon(android.R.drawable.ic_dialog_alert)
//                    .show();
////        } else {
////            // user is not signed in
//        }
    }

    private void deleteItemData() {
//        // delete inventory item
//        mInventoryRef.removeValue();
//
//        // delete inventory list entry
//        mDatabaseReference
//                .child(Db.USER)
//                .child(mUser.getUid())
//                .child(Db.INVENTORY_LIST)
//                .child(mInventoryItem.getId())
//                .removeValue();
    }
}
