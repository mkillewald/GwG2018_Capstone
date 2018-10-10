package com.gameaholix.coinops.inventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;

public class InventoryListActivity extends AppCompatActivity implements
        InventoryListFragment.OnFragmentInteractionListener {

    private static final String TAG = InventoryListActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY = "com.gameaholix.coinops.inventory.InventoryItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory_list);

        setTitle(R.string.inventory_list_title);
    }

    @Override
    public void onInventoryItemSelected(InventoryItem inventoryItem) {
        Intent intent = new Intent(this, InventoryDetailActivity.class);
        intent.putExtra(EXTRA_INVENTORY, inventoryItem);
        startActivity(intent);
    }
}
