package com.gameaholix.coinops.shopping;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ShoppingListActivity extends AppCompatActivity implements
        ShoppingListFragment.OnFragmentInteractionListener,
        NetworkUtils.CheckInternetConnection.TaskCompleted {
//    private static final String TAG = ShoppingListActivity.class.getSimpleName();

    private CoordinatorLayout mCoordinatorLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            TransitionInflater inflater = TransitionInflater.from(this);

            Transition slideIn = inflater.inflateTransition(R.transition.slide_in);
            getWindow().setEnterTransition(slideIn);

            Transition slideDown = inflater.inflateTransition(R.transition.slide_bottom);
            getWindow().setExitTransition(slideDown);
        }

        setContentView(R.layout.activity_fragment_host);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        AdView adView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        setTitle(R.string.shopping_list_title);

        if (savedInstanceState == null) {
            ShoppingListFragment fragment = ShoppingListFragment.newInstance();
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
    public void onShoppingItemSelected(Item shoppingItem) {
        FragmentManager fm = getSupportFragmentManager();
        ShoppingEditFragment fragment = ShoppingEditFragment.newInstance(shoppingItem);
        fragment.show(fm, "fragment_edit_shopping");
    }

    @Override
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (!networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
        }
    }


}
