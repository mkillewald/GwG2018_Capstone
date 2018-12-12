package com.gameaholix.coinops.activity;

import android.content.DialogInterface;
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
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.fragment.ToDoAddEditFragment;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.firebase.Db;
import com.gameaholix.coinops.fragment.ToDoDetailFragment;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class ToDoDetailActivity extends BaseActivity implements
        ToDoDetailFragment.OnFragmentInteractionListener,
        ToDoAddEditFragment.OnFragmentInteractionListener {
//    private static final String TAG = ToDoDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

    private String mGameName;
    private String mItemId;
    private FirebaseUser mUser;

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
            mItemId = getIntent().getStringExtra(EXTRA_TODO_ID);

            ToDoDetailFragment fragment = ToDoDetailFragment.newInstance(mItemId);
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.fragment_container, fragment)
                    .commit();

        } else {
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
            mItemId = savedInstanceState.getString(EXTRA_TODO_ID);
        }

        if (!TextUtils.isEmpty(mGameName)) {
            setTitle(mGameName);
        } else {
            setTitle(R.string.to_do_details_title);
        }

        // Initialize Firebase components
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
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
        if (currentFragment instanceof ToDoAddEditFragment) {
            menu.findItem(R.id.menu_edit_todo).setVisible(false);
        } else {
            menu.findItem(R.id.menu_edit_todo).setVisible(true);
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_todo:
                // handled by ToDoDetailFragment
                return false;
            case R.id.menu_delete_todo:
                // handled by ToDoDetailFragment or ToDoEditFragment listener callback
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(EXTRA_TODO_ID, mItemId);
        outState.putString(EXTRA_GAME_NAME, mGameName);
    }

    @Override
    public void onToDoEditButtonPressed(ToDoItem toDoItem) {
        // replace ToDoDetailFragment with ToDoAddEditFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoAddEditFragment.newInstance(toDoItem));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onToDoEditCompletedOrCancelled() {
        // replace ToDoEditFragment with ToDoDetailFragment
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_container, ToDoDetailFragment.newInstance(mItemId));
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onToDoDeleteButtonPressed(ToDoItem toDoItem) {
        showDeleteAlert(toDoItem);
    }

    private void showDeleteAlert(final ToDoItem toDoItem) {
        if (mUser != null) {
            // user is signed in

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
                            deleteItemData(toDoItem);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteItemData(ToDoItem toDoItem) {
        // delete to do item
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference
                .child(Db.TODO)
                .child(mUser.getUid())
                .child(toDoItem.getId())
                .removeValue();

        // delete game to do list entry
        databaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(toDoItem.getParentId())
                .child(Db.TODO_LIST)
                .child(toDoItem.getId())
                .removeValue();

        // delete user to do list entry (global list)
        databaseReference
                .child(Db.USER)
                .child(mUser.getUid())
                .child(Db.TODO_LIST)
                .child(toDoItem.getId())
                .removeValue();
    }
}
