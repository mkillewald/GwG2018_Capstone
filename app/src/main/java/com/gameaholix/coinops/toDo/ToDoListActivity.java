package com.gameaholix.coinops.toDo;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.ListRow;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ToDoListActivity extends BaseActivity implements
        ToDoListFragment.OnFragmentInteractionListener {
//    private static final String TAG = ToDoListActivity.class.getSimpleName();
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

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

        setTitle(R.string.to_do_list_title);

        if (savedInstanceState == null) {
            ToDoListFragment fragment = ToDoListFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        }
    }

    @Override
    public void onToDoFabPressed() {
        // no action
    }

    @Override
    public void onToDoItemSelected(ListRow item) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO_ID, item.getId());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }
}