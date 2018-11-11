package com.gameaholix.coinops.inventory;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
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
import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InventoryListActivity extends AppCompatActivity implements
        InventoryListFragment.OnFragmentInteractionListener,
        NetworkUtils.CheckInternetConnection.TaskCompleted {

//    private static final String TAG = InventoryListActivity.class.getSimpleName();
    private static final String EXTRA_INVENTORY = "com.gameaholix.coinops.model.InventoryItem";

    private CoordinatorLayout mCoordinatorLayout;
    private AdView mAdView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        mAdView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        setTitle(R.string.inventory_list_title);

        if (savedInstanceState == null) {
            InventoryListFragment fragment = new InventoryListFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }

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
        inflater.inflate(R.menu.inventory_list_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_add_inventory_item:
                showAddInventoryDialog();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (!networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
        }
    }

    @Override
    public void onInventoryItemSelected(InventoryItem inventoryItem) {
        Intent intent = new Intent(this, InventoryDetailActivity.class);
        intent.putExtra(EXTRA_INVENTORY, inventoryItem);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    private void showAddInventoryDialog() {
        FragmentManager fm = getSupportFragmentManager();
        InventoryAddFragment fragment = new InventoryAddFragment();
        fragment.show(fm, "fragment_inventory_add");
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
