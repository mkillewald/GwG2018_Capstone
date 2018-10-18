package com.gameaholix.coinops.inventory;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

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
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.inventory_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_inventory_item:
                Intent intent = new Intent(this, AddInventoryActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInventoryItemSelected(InventoryItem inventoryItem) {
        Intent intent = new Intent(this, InventoryDetailActivity.class);
        intent.putExtra(EXTRA_INVENTORY, inventoryItem);
        startActivity(intent);
    }
}
