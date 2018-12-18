package com.gameaholix.coinops;

import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class DisplayImageActivity extends BaseActivity {
//    private static final String TAG = DisplayImageActivity.class.getSimpleName();
    private static final String EXTRA_IMAGE_PATH = "CoinOpsImagePath";

    private String mImagePath;

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

        if (savedInstanceState == null) {
            mImagePath = getIntent().getStringExtra(EXTRA_IMAGE_PATH);

            DisplayImageFragment fragment = DisplayImageFragment.newInstance(mImagePath);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();
        } else {
            mImagePath = savedInstanceState.getString(EXTRA_IMAGE_PATH);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_IMAGE_PATH, mImagePath);
    }
}
