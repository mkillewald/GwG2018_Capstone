package com.gameaholix.coinops.game;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.adapter.GameDetailPagerAdapter;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.model.ToDoItem;
import com.gameaholix.coinops.repair.RepairAddFragment;
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingAddFragment;
import com.gameaholix.coinops.shopping.ShoppingEditFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.ToDoAddFragment;
import com.gameaholix.coinops.todo.ToDoDetailActivity;
import com.gameaholix.coinops.todo.ToDoListFragment;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GameDetailActivity extends BaseActivity implements
        GameDetailFragment.OnFragmentInteractionListener,
        GameEditFragment.OnFragmentInteractionListener,
        RepairListFragment.OnFragmentInteractionListener,
        ToDoListFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener {
//    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_IMAGE_PATH = "CoinOpsImagePath";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";

    private String mGameId;
    private String mGameName;
    private ViewPager mViewPager;
    private AdView mAdView;

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

        setContentView(R.layout.activity_game_detail);

        Toolbar myToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set CoordinatorLayout of BaseActivity for displaying Snackbar
        CoordinatorLayout coordinatorLayout = findViewById(R.id.coordinator_layout);
        setCoordinatorLayout(coordinatorLayout);

        mAdView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);


        if (savedInstanceState == null) {
            Intent intent = getIntent();
            mGameId = intent.getStringExtra(EXTRA_GAME_ID);
            mGameName = intent.getStringExtra(EXTRA_GAME_NAME);
        } else {
            mGameId = savedInstanceState.getString(EXTRA_GAME_ID);
            mGameName = savedInstanceState.getString(EXTRA_GAME_NAME);
        }

        if (mGameName != null) {
            setTitle(mGameName);
        }

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new GameDetailPagerAdapter(this, getSupportFragmentManager(),
                mGameId));
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_detail_menu, menu);
        switch (mViewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                break;
            case 1:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                break;
            default:
            case 3:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mViewPager.getCurrentItem() == 0) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
            if (currentFragment instanceof GameEditFragment) {
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                mAdView.setVisibility(View.GONE);
            } else {
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                menu.findItem(R.id.menu_delete_game).setVisible(true);
                mAdView.setVisibility(View.VISIBLE);
            }
        } else {
            if (mAdView.getVisibility() == View.GONE) {
                mAdView.setVisibility(View.VISIBLE);
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
            case R.id.menu_delete_game:
                // handled by GameDetailFragment
                return false;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString(EXTRA_GAME_ID, mGameId);
    }

    @Override
    public void onGameNameChanged(String name) {
        setTitle(name);
    }

    @Override
    public void onRepairLogSelected(Item repairLog) {
        Intent intent = new Intent(this, RepairDetailActivity.class);
        intent.putExtra(EXTRA_REPAIR, repairLog);
        intent.putExtra(EXTRA_GAME_NAME, mGameName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onToDoFabPressed() {
        showAddToDoDialog();
    }

    @Override
    public void onToDoItemSelected(ToDoItem toDoItem) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO, toDoItem);
        intent.putExtra(EXTRA_GAME_NAME, mGameName);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onShoppingFabPressed() {
        showAddShoppingDialog();
    }

    @Override
    public void onShoppingItemSelected(Item shoppingItem) {
        // show edit shopping dialog
        FragmentManager fm = getSupportFragmentManager();
        ShoppingEditFragment fragment = ShoppingEditFragment.newInstance(shoppingItem);
        fragment.show(fm, "fragment_edit_shopping");
    }

    @Override
    public void onRepairFabPressed() {
        showAddRepairDialog();
    }

    private void showAddRepairDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RepairAddFragment fragment = RepairAddFragment.newInstance(mGameId);
        fragment.show(fm, "fragment_repair_add");
    }

    private void showAddToDoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ToDoAddFragment fragment = ToDoAddFragment.newInstance(mGameId);
        fragment.show(fm, "fragment_add_todo");
    }

    private void showAddShoppingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShoppingAddFragment fragment = ShoppingAddFragment.newInstance(mGameId);
        fragment.show(fm, "fragment_item_add");
    }



    @Override
    public void onEditButtonPressed(Game game) {
        mAdView.setVisibility(View.GONE);
        // replace GameDetailFragment with GameEditFragment
        Fragment editGameFragment = GameEditFragment.newInstance(game);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, editGameFragment);
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onEditCompletedOrCancelled() {
        // replace GameEditFragment with GameDetailFragment
        Fragment gameDetailFragment = GameDetailFragment.newInstance(mGameId);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, gameDetailFragment);
        ft.commit();

        invalidateOptionsMenu();
        mAdView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onImageClicked(String imagePath) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, imagePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void showSnackbar(int stringResourceId) {
        PromptUser.displaySnackbar(getCoordinatorLayout(), stringResourceId);
    }
}
