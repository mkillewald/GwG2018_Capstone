package com.gameaholix.coinops.inventory;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.InventoryItem;

public class InventoryDetailActivity extends AppCompatActivity implements
        InventoryDetailFragment.OnFragmentInteractionListener,
        EditInventoryFragment.OnFragmentInteractionListener {
//    private static final String TAG = InventoryDetailActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ITEM = "com.gameaholix.coinops.model.InventoryItem";

    private InventoryItem mInventoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mInventoryItem = getIntent().getParcelableExtra(EXTRA_INVENTORY_ITEM);
        } else {
            mInventoryItem = savedInstanceState.getParcelable(EXTRA_INVENTORY_ITEM);
        }

        setTitle(R.string.inventory_details_title);

        InventoryDetailFragment fragment = InventoryDetailFragment.newInstance(mInventoryItem);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
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
        if (currentFragment instanceof EditInventoryFragment) {
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
                // handled by InventoryDetailFragment
                return false;
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
    public void onEditButtonPressed(InventoryItem inventoryItem) {
        // replace InventoryDetailFragment with EditInventoryFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, EditInventoryFragment.newInstance(inventoryItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace EditInventoryFragment with InventoryDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, InventoryDetailFragment.newInstance(mInventoryItem));
        ft.commit();

        invalidateOptionsMenu();
    }

}
