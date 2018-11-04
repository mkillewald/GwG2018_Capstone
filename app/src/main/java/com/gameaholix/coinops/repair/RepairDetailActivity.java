package com.gameaholix.coinops.repair;

import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.model.Item;

public class RepairDetailActivity extends AppCompatActivity implements
        RepairDetailFragment.OnFragmentInteractionListener,
        RepairEditFragment.OnFragmentInteractionListener {
    private static final String TAG = RepairDetailActivity.class.getSimpleName();
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";

    private Item mRepairLog;
    private String mGameName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragment_host);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGameName = getIntent().getStringExtra(EXTRA_GAME_NAME);
            mRepairLog = getIntent().getParcelableExtra(EXTRA_REPAIR);
        } else {
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
            mRepairLog = savedInstanceState.getParcelable(EXTRA_REPAIR);
        }

        setTitle(mGameName);

        Log.d(TAG, mRepairLog.getParentId());

        RepairDetailFragment fragment = RepairDetailFragment.newInstance(mRepairLog);
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, fragment)
                .commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.repair_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof RepairEditFragment) {
            menu.findItem(R.id.menu_edit_repair).setVisible(false);
        } else {
            menu.findItem(R.id.menu_edit_repair).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_repair:
                // handled by RepairDetailFragment
                return false;
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
    public void onEditButtonPressed(Item repairLog) {
        // replace RepairDetailFragment with RepairEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, RepairEditFragment.newInstance(repairLog));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace RepairEditFragment with RepairDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, RepairDetailFragment.newInstance(mRepairLog));
        ft.commit();

        invalidateOptionsMenu();
    }
}
