package com.gameaholix.coinops.inventory;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class InventoryDetailActivity extends AppCompatActivity implements
        InventoryDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = InventoryDetailActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY = "com.gameaholix.coinops.inventory.InventoryItem";

    private InventoryItem mInventoryItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mInventoryItem = getIntent().getParcelableExtra(EXTRA_INVENTORY);
        } else {
            mInventoryItem = savedInstanceState.getParcelable(EXTRA_INVENTORY);
        }

        setTitle(mInventoryItem.getName());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_INVENTORY, mInventoryItem);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
