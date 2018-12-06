package com.gameaholix.coinops.activity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.firebase.ui.auth.AuthUI;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.databinding.ActivityMainBinding;
import com.google.android.gms.ads.AdRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Arrays;

public class MainActivity extends BaseActivity {
//    private static final String TAG = MainActivity.class.getSimpleName();
    private static final int RC_SIGN_IN = 1;
    private FirebaseAuth mFirebaseAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            TransitionInflater inflater = TransitionInflater.from(this);
            Transition slideOut = inflater.inflateTransition(R.transition.slide_out);
            getWindow().setExitTransition(slideOut);
        }

        ActivityMainBinding bind = DataBindingUtil.setContentView(this, R.layout.activity_main);

        setSupportActionBar(bind.toolbar);

        // set CoordinatorLayout of BaseActivity for displaying Snackbar
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        setCoordinatorLayout(coordinatorLayout);

        AdRequest adRequest = new AdRequest.Builder().build();
        bind.avBanner.loadAd(adRequest);

        // Initialize Firebase components
        mFirebaseAuth = FirebaseAuth.getInstance();

        bind.gameList.tvCardTitle.setText(R.string.game_list_title);
        bind.gameList.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, GameListActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });

        bind.inventoryList.tvCardTitle.setText(R.string.inventory_list_title);
        bind.inventoryList.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, InventoryListActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();

                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });

        bind.globalToDoList.tvCardTitle.setText(R.string.to_do_list_title);
        bind.globalToDoList.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ToDoListActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });

        bind.globalShoppingList.tvCardTitle.setText(R.string.shopping_list_title);
        bind.globalShoppingList.card.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ShoppingListActivity.class);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
                    Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this).toBundle();
                    startActivity(intent, bundle);
                } else {
                    startActivity(intent);
                }
            }
        });

        Button signOut = findViewById(R.id.btn_sign_out);
        signOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AuthUI.getInstance().signOut(MainActivity.this);
            }
        });

        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // user is not signed in
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setIsSmartLockEnabled(false) // smartlock will save user credentials
                                    .setAvailableProviders(Arrays.asList(
        //                                                new AuthUI.IdpConfig.FacebookBuilder().build(),
                                            new AuthUI.IdpConfig.GoogleBuilder().build(),
                                            new AuthUI.IdpConfig.EmailBuilder().build()))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }

    @Override
    protected void onResume() {
        super.onResume();
        mFirebaseAuth.addAuthStateListener(mAuthStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mAuthStateListener != null) {
            mFirebaseAuth.removeAuthStateListener(mAuthStateListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_sign_out:
                // sign out
                AuthUI.getInstance().signOut(this);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
