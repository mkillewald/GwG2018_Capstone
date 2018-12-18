package com.gameaholix.coinops.inventory;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ListRow;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InventoryListActivity extends BaseActivity implements
        InventoryListFragment.OnFragmentInteractionListener,
        InventoryAddEditFragment.OnFragmentInteractionListener {

//    private static final String TAG = InventoryListActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY_ID = "CoinOpsInventoryId";
    private static final String EXTRA_INVENTORY_NAME = "CoinOpsInventoryName";

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

        AdView adView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        setTitle(R.string.inventory_list_title);

        if (savedInstanceState == null) {
            InventoryListFragment fragment = new InventoryListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onFabPressed() {
        showAddInventoryDialog();
    }

    @Override
    public void onInventoryItemSelected(ListRow row) {
        Intent intent = new Intent(this, InventoryDetailActivity.class);
        intent.putExtra(EXTRA_INVENTORY_ID, row.getId());
        intent.putExtra(EXTRA_INVENTORY_NAME, row.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    private void showAddInventoryDialog() {
        FragmentManager fm = getSupportFragmentManager();
        InventoryAddEditFragment fragment = new InventoryAddEditFragment();
        fragment.show(fm, "fragment_inventory_add");
    }

    @Override
    public void onInventoryAddEditCompletedOrCancelled() {
        // no operation
    }
}
