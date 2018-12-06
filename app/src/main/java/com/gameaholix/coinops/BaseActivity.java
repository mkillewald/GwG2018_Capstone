package com.gameaholix.coinops;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.CoordinatorLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public abstract class BaseActivity extends AppCompatActivity implements
    NetworkUtils.CheckInternetConnection.TaskCompleted {
    private static final String EXTRA_NETWORK_DISABLED = "CoinOpsNetworkDisabledShown";
    private static final String EXTRA_NETWORK_NOT_CONNECTED = "CoinOpsNetworkNotConnectedShown";

    private ProgressDialog mProgressDialog;
    private CoordinatorLayout mCoordinatorLayout;
    private boolean mNetworkDisabledMessageShown;
    private boolean mNetworkNotConnectedMessageShown;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState != null) {
            mNetworkDisabledMessageShown = savedInstanceState.getBoolean(EXTRA_NETWORK_DISABLED);
            mNetworkNotConnectedMessageShown =
                    savedInstanceState.getBoolean(EXTRA_NETWORK_NOT_CONNECTED);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCoordinatorLayout != null && NetworkUtils.isNetworkEnabled(this)) {
            new NetworkUtils.CheckInternetConnection(this).execute();
        } else if (!mNetworkDisabledMessageShown) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_unavailable);
            mNetworkDisabledMessageShown = true;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(EXTRA_NETWORK_DISABLED, mNetworkDisabledMessageShown);
        outState.putBoolean(EXTRA_NETWORK_NOT_CONNECTED, mNetworkNotConnectedMessageShown);
    }

    public CoordinatorLayout getCoordinatorLayout() {
        return mCoordinatorLayout;
    }

    public void setCoordinatorLayout(CoordinatorLayout coordinatorLayout) {
        mCoordinatorLayout = coordinatorLayout;
    }

    public void showProgressDialog() {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage("Loading...");
        }

        mProgressDialog.show();
    }

    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    public String getUid() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        return user != null ? user.getUid() : null;
    }

    @Override
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (mCoordinatorLayout != null && !mNetworkNotConnectedMessageShown && !networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
            mNetworkNotConnectedMessageShown = true;
        }
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
