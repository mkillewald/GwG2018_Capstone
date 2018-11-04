package com.gameaholix.coinops.shopping;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;

public class ShoppingDetailActivity extends AppCompatActivity implements
        ShoppingDetailFragment.OnFragmentInteractionListener {
//    private static final String TAG = ShoppingDetailActivity.class.getSimpleName();
    private static final String EXTRA_SHOPPING = "CoinOpsShoppingItem";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";

    private Item mShoppingItem;
    private String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mShoppingItem = getIntent().getParcelableExtra(EXTRA_SHOPPING);
            mGameName = getIntent().getStringExtra(EXTRA_GAME_NAME);
        } else {
            mShoppingItem = savedInstanceState.getParcelable(EXTRA_SHOPPING);
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
        }

        if (!TextUtils.isEmpty(mGameName)) {
            setTitle(mGameName);
        } else {
            setTitle(R.string.shopping_details_title);
        }

        ShoppingDetailFragment fragment = ShoppingDetailFragment.newInstance(mShoppingItem);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.shopping_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof EditShoppingFragment) {
            menu.findItem(R.id.menu_edit_shopping).setVisible(false);
        } else {
            menu.findItem(R.id.menu_edit_shopping).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_shopping:
                // handled by ShoppingDetailFragment
                return false;
            case R.id.menu_delete_shopping:
                // handled by ShoppingDetailFragment
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putParcelable(EXTRA_SHOPPING, mShoppingItem);
        outState.putString(EXTRA_GAME_NAME, mGameName);
    }

    @Override
    public void onEditButtonPressed(Item item) {
        // replace ShoppingDetailFragment with EditShoppingFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, EditShoppingFragment.newInstance(item));
        ft.commit();

        invalidateOptionsMenu();
    }


}
