package com.gameaholix.coinops.repair;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class RepairDetailActivity extends AppCompatActivity implements
        RepairDetailFragment.OnFragmentInteractionListener,
        NetworkUtils.CheckInternetConnection.TaskCompleted {
//    private static final String TAG = RepairDetailActivity.class.getSimpleName();
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";

    private Item mRepairLog;
    private CoordinatorLayout mCoordinatorLayout;
    private String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        AdView adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

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

    @Override
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (!networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
        }
    }

    @Override
    public void onDescriptionSelected(Item repairLog) {
        FragmentManager fm = getSupportFragmentManager();
        RepairEditFragment fragment = RepairEditFragment.newInstance(mRepairLog);
        fragment.show(fm, "fragment_step_edit");
    }

    @Override
    public void onStepSelected(Item repairStep) {
        FragmentManager fm = getSupportFragmentManager();
        StepEditFragment fragment = StepEditFragment.newInstance(mRepairLog.getParentId(), repairStep);
        fragment.show(fm, "fragment_step_edit");
    }
}
