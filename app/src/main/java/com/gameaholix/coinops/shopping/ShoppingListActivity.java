package com.gameaholix.coinops.shopping;

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

public class ShoppingListActivity extends BaseActivity implements
        ShoppingListFragment.OnFragmentInteractionListener,
        ShoppingAddEditFragment.OnFragmentInteractionListener {
//    private static final String TAG = ShoppingListActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
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

        // set CoordinatorLayout of BaseActivity for displaying Snackbar
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        setCoordinatorLayout(coordinatorLayout);

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
    }

    @Override
    public void onShoppingFabPressed() {
       // no operation for global shopping list view
    }

    @Override
    public void onShoppingItemSelected(ListRow item) {
        FragmentManager fm = getSupportFragmentManager();
        ShoppingAddEditFragment fragment = ShoppingAddEditFragment.newInstance(null, item.getId());
        fragment.show(fm, "fragment_add_edit_shopping");
    }

    @Override
    public void onShoppingAddEditCompletedOrCancelled() {
        // TODO: finish this
    }
}
