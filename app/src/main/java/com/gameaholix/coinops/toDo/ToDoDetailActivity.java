package com.gameaholix.coinops.toDo;

import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModel;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModelFactory;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class ToDoDetailActivity extends BaseActivity implements
        ToDoDetailFragment.OnFragmentInteractionListener,
        ToDoAddEditFragment.OnFragmentInteractionListener {
    private static final String TAG = ToDoDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

    private String mGameName;
    private String mGameId;
    private String mItemId;

    private ToDoItemViewModel mViewModel;

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
            Intent intent = getIntent();
            mGameName = intent.getStringExtra(EXTRA_GAME_NAME);
            mGameId = intent.getStringExtra(EXTRA_GAME_ID);
            mItemId = intent.getStringExtra(EXTRA_TODO_ID);

            ToDoDetailFragment fragment = ToDoDetailFragment.newInstance();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();

        } else {
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mItemId = savedInstanceState.getString(EXTRA_TODO_ID);
        }

        if (!TextUtils.isEmpty(mGameName)) {
            setTitle(mGameName);
        } else {
            setTitle(R.string.to_do_details_title);
        }

        // Create ViewModel with our custom factory using this Activity as the lifecycle owner
        mViewModel = ViewModelProviders
                .of(this, new ToDoItemViewModelFactory(mGameId, mItemId))
                .get(ToDoItemViewModel.class);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.todo_detail_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_container);
        if (currentFragment instanceof ToDoDetailFragment) {
            menu.findItem(R.id.menu_edit_todo).setVisible(true);
        } else {
            menu.findItem(R.id.menu_edit_todo).setVisible(false);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_todo:
                onToDoEditButtonPressed();
                return true;
            case R.id.menu_delete_todo:
                onToDoDeleteButtonPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_GAME_NAME, mGameName);
        outState.putString(EXTRA_GAME_ID, mGameId);
        outState.putString(EXTRA_TODO_ID, mItemId);
    }

    @Override
    public void onItemIdInvalid() {
        Log.e(TAG, "Failed to instantiate fragment! Item ID cannot be an empty string.");
        PromptUser.displayAlert(this,
                R.string.error_unable_to_load,
                R.string.error_item_id_empty,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
    }


    @Override
    public void onToDoEditButtonPressed() {
        // replace ToDoDetailFragment with ToDoAddEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoAddEditFragment.newInstance(null, true));
        ft.commit();
    }

    @Override
    public void onToDoDeleteButtonPressed() { showDeleteAlert(); }

    private void showDeleteAlert() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(R.string.really_delete_item)
                .setMessage(R.string.item_will_be_deleted)
                .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                })
                .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteItemData();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteItemData() {
        mViewModel.delete();
        finish();
    }

    @Override
    public void onToDoAddEditCompletedOrCancelled() {
        // replace ToDoAddEditFragment with ToDoDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoDetailFragment.newInstance());
        ft.commit();
    }
}
