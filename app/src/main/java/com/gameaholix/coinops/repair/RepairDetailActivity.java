package com.gameaholix.coinops.repair;

import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RepairDetailActivity extends BaseActivity implements
        RepairDetailFragment.OnFragmentInteractionListener,
        RepairAddEditFragment.OnFragmentInteractionListener,
        StepAddEditFragment.OnFragmentInteractionListener {
//    private static final String TAG = RepairDetailActivity.class.getSimpleName();
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";

    private Item mRepairLog;
    private String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition slideIn = inflater.inflateTransition(R.transition.slide_in);
            getWindow().setEnterTransition(slideIn);
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

        if (savedInstanceState == null) {
            mGameName = getIntent().getStringExtra(EXTRA_GAME_NAME);
            mRepairLog = getIntent().getParcelableExtra(EXTRA_REPAIR);

            RepairDetailFragment fragment = RepairDetailFragment.newInstance(mRepairLog);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        setTitle(mGameName);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.repair_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_delete_repair:
                // handled by RepairDetailFragment
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_NAME, mGameName);
        outState.putParcelable(EXTRA_REPAIR, mRepairLog);
    }

    @Override
    public void onDescriptionSelected() {
        FragmentManager fm = getSupportFragmentManager();
        RepairAddEditFragment fragment = RepairAddEditFragment.newInstance(mRepairLog);
        fragment.show(fm, "fragment_repair_add_edit");
    }

    @Override
    public void onRepairAddEditCompletedOrCancelled() {
        // TODO: finish this
    }

    @Override
    public void onStepAddEditCompletedOrCancelled() {
        // TODO: finish this
    }

    @Override
    public void onStepSelected(Item repairStep) {
        FragmentManager fm = getSupportFragmentManager();
        StepAddEditFragment fragment =
                StepAddEditFragment.newInstance(mRepairLog.getParentId(), repairStep);
        fragment.show(fm, "fragment_step_edit");
    }

    @Override
    public void onAddStepPressed() {
        FragmentManager fm = getSupportFragmentManager();
        StepAddEditFragment fragment =
                StepAddEditFragment.newInstance(mRepairLog.getParentId(), mRepairLog.getId());
        fragment.show(fm, "fragment_step_add");
    }
}
