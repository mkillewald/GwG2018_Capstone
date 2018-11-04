package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.GameDetailPagerAdapter;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.repair.AddRepairFragment;
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.AddShoppingFragment;
import com.gameaholix.coinops.shopping.EditShoppingFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.AddToDoFragment;
import com.gameaholix.coinops.todo.ToDoDetailActivity;
import com.gameaholix.coinops.todo.ToDoListFragment;
import com.gameaholix.coinops.utility.Db;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class GameDetailActivity extends AppCompatActivity implements
        GameDetailFragment.OnFragmentInteractionListener,
        RepairListFragment.OnFragmentInteractionListener,
        ToDoListFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener,
        EditGameFragment.OnFragmentInteractionListener {

//    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";

    private Game mGame;
    private ViewPager mViewPager;
    private FirebaseUser mUser;
    private DatabaseReference mDatabaseReference;
    private DatabaseReference mGameRef;
    private DatabaseReference mUserRef;
    private DatabaseReference mShopRef;
    private DatabaseReference mToDoRef;
    private ValueEventListener mDeleteTodoListener;
    private ValueEventListener mDeleteShopListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        if (getActionBar() != null) {
            getActionBar().setDisplayHomeAsUpEnabled(true);
        }

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        if (mGame != null) {
            setTitle(mGame.getName());
        }

//        mShowEditMenu = true;

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new GameDetailPagerAdapter(this, getSupportFragmentManager(),
                mGame));
        mViewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {
                invalidateOptionsMenu();
            }

            @Override
            public void onPageSelected(int i) {
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        // Give the TabLayout the ViewPager
        TabLayout tabLayout = findViewById(R.id.sliding_tabs);
        tabLayout.setupWithViewPager(mViewPager);

        // Initialize Firebase components
        FirebaseAuth firebaseAuth= FirebaseAuth.getInstance();
        mUser = firebaseAuth.getCurrentUser();
        mDatabaseReference = FirebaseDatabase.getInstance().getReference();
        mGameRef = mDatabaseReference
                .child(Db.GAME)
                .child(mUser.getUid())
                .child(mGame.getId());
        mUserRef = mDatabaseReference
                .child(Db.USER)
                .child(mUser.getUid());
        mToDoRef = mDatabaseReference
                .child(Db.TODO)
                .child(mUser.getUid());
        mShopRef = mDatabaseReference
                .child(Db.SHOP)
                .child(mUser.getUid());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mDeleteTodoListener != null) {
            mToDoRef.removeEventListener(mDeleteTodoListener);
        }

        if (mDeleteShopListener != null) {
            mShopRef.removeEventListener(mDeleteShopListener);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_detail_menu, menu);
        switch (mViewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(true);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(true);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                break;
            default:
            case 3:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(true);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewPager.getCurrentItem() == 0) {

            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
            if (currentFragment instanceof EditGameFragment) {
                menu.findItem(R.id.menu_edit_game).setVisible(false);
            } else {
                menu.findItem(R.id.menu_edit_game).setVisible(true);
            }
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_game:
                // handled by GameDetailFragment
                return false;
            case R.id.menu_add_repair:
                showAddRepairDialog();
                return true;
            case R.id.menu_add_todo:
                showAddToDoDialog();
                return true;
            case R.id.menu_add_shopping:
                showAddShoppingDialog();
                return true;
            case R.id.menu_delete_game:
                showDeleteAlert();
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(EXTRA_GAME, mGame);
    }

    @Override
    public void onGameNameChanged(String name) {
        setTitle(name);
    }

    @Override
    public void onRepairLogSelected(Item repairLog) {
        Intent intent = new Intent(this, RepairDetailActivity.class);
        intent.putExtra(EXTRA_REPAIR, repairLog);
        intent.putExtra(EXTRA_GAME_NAME, mGame.getName());
        startActivity(intent);
    }

    @Override
    public void onToDoItemSelected(ToDoItem toDoItem) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO, toDoItem);
        intent.putExtra(EXTRA_GAME_NAME, mGame.getName());
        startActivity(intent);
    }

    @Override
    public void onShoppingItemSelected(Item shoppingItem) {
        // show edit shopping dialog
        FragmentManager fm = getSupportFragmentManager();
        EditShoppingFragment fragment = EditShoppingFragment.newInstance(shoppingItem);
        fragment.show(fm, "fragment_edit_shopping");
    }

    private void showAddRepairDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddRepairFragment fragment = AddRepairFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_add_repair");
    }

    private void showAddToDoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddToDoFragment fragment = AddToDoFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_add_todo");
    }

    private void showAddShoppingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        AddShoppingFragment fragment = AddShoppingFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_add_shopping");
    }

    private void showDeleteAlert() {
        if (mUser != null) {
            //user is signed in

            android.support.v7.app.AlertDialog.Builder builder;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                builder = new android.support.v7.app.AlertDialog.Builder(this,
                        android.R.style.Theme_Material_Dialog_Alert);
            } else {
                builder = new android.support.v7.app.AlertDialog.Builder(this);
            }
            builder.setTitle(getString(R.string.really_delete_game))
                    .setMessage(getString(R.string.game_will_be_deleted))
                    .setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            deleteAllGameData();
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
//        } else {
//            // user is not signed in
        }
    }

    private void deleteAllGameData() {
        mDeleteTodoListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey() != null) {
                        String key = child.getKey();
                        child.getRef().removeValue();
                        mUserRef.child(Db.TODO_LIST).child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        mDeleteShopListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    if (child.getKey() != null) {
                        String key = child.getKey();
                        child.getRef().removeValue();
                        mUserRef.child(Db.SHOP_LIST).child(key).removeValue();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        };

        // delete game details
        mGameRef.removeValue();

        // remove user game_list entry
        mUserRef.child(Db.GAME_LIST)
                .child(mGame.getId())
                .removeValue();

        // delete repair logs and steps
        mDatabaseReference
                .child(Db.REPAIR)
                .child(mUser.getUid())
                .child(mGame.getId())
                .removeValue();

        // delete to do items
        mToDoRef.orderByChild(Db.PARENT_ID)
                .equalTo(mGame.getId())
                .addValueEventListener(mDeleteTodoListener);

        // delete shopping list items
        mShopRef.orderByChild(Db.PARENT_ID)
                .equalTo(mGame.getId())
                .addValueEventListener(mDeleteShopListener);
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
    public void onEditButtonPressed(Game game) {
        // replace GameDetailFragment with EditGameFragment
        Fragment editGameFragment = EditGameFragment.newInstance(game);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, editGameFragment);
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace EditGameFragment with GameDetailFragment
        Fragment gameDetailFragment = GameDetailFragment.newInstance(mGame);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, gameDetailFragment);
        ft.commit();

        invalidateOptionsMenu();
    }
}
