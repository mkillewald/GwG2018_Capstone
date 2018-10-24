package com.gameaholix.coinops.shopping;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ShoppingItem;

public class ShoppingListActivity extends AppCompatActivity implements
        ShoppingListFragment.OnFragmentInteractionListener {

    private static final String TAG = ShoppingListActivity.class.getSimpleName();
    private static final String EXTRA_SHOPPING = "com.gameaholix.coinops.model.ShoppingItem";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shopping_list);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        setTitle(R.string.shopping_list_title);
    }

    @Override
    public void onShoppingItemSelected(ShoppingItem shoppingItem) {
        Intent intent = new Intent(this, ShoppingDetailActivity.class);
        intent.putExtra(EXTRA_SHOPPING, shoppingItem);
        startActivity(intent);
    }
}
