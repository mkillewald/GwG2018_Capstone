package com.gameaholix.coinops.shopping;

import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;

public class ShoppingDetailActivity extends AppCompatActivity implements
        ShoppingDetailFragment.OnFragmentInteractionListener {

    private static final String TAG = ShoppingDetailActivity.class.getSimpleName();
    private static final String EXTRA_SHOPPING = "com.gameaholix.coinops.model.Item";

    private Item mShoppingItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mShoppingItem = getIntent().getParcelableExtra(EXTRA_SHOPPING);
        } else {
            mShoppingItem = savedInstanceState.getParcelable(EXTRA_SHOPPING);
        }

        setTitle(R.string.shopping_details_title);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_SHOPPING, mShoppingItem);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
