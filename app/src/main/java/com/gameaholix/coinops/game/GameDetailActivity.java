package com.gameaholix.coinops.game;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.Transition;
import android.transition.TransitionInflater;
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
import com.gameaholix.coinops.repair.RepairAddFragment;
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingAddFragment;
import com.gameaholix.coinops.shopping.ShoppingEditFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.ToDoAddFragment;
import com.gameaholix.coinops.todo.ToDoDetailActivity;
import com.gameaholix.coinops.todo.ToDoListFragment;
import com.gameaholix.coinops.utility.NetworkUtils;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class GameDetailActivity extends AppCompatActivity implements
        GameDetailFragment.OnFragmentInteractionListener,
        GameEditFragment.OnFragmentInteractionListener,
        RepairListFragment.OnFragmentInteractionListener,
        ToDoListFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener,
        NetworkUtils.CheckInternetConnection.TaskCompleted {
//    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_IMAGE_PATH = "CoinOpsImagePath";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_TODO = "com.gameaholix.coinops.model.ToDoItem";

    private Game mGame;
    private CoordinatorLayout mCoordinatorLayout;
    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
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

        AdView adView = findViewById(R.id.av_banner);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);

        if (savedInstanceState == null) {
            mGame = getIntent().getParcelableExtra(EXTRA_GAME);
        } else {
            mGame = savedInstanceState.getParcelable(EXTRA_GAME);
        }

        if (mGame != null) {
            setTitle(mGame.getName());
        }

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
        inflater.inflate(R.menu.game_detail_menu, menu);
        switch (mViewPager.getCurrentItem()) {
            case 0:
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                menu.findItem(R.id.menu_add_photo).setVisible(true);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_photo).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(true);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                break;
            case 2:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_photo).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(true);
                menu.findItem(R.id.menu_add_shopping).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                break;
            default:
            case 3:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_add_photo).setVisible(false);
                menu.findItem(R.id.menu_add_repair).setVisible(false);
                menu.findItem(R.id.menu_add_todo).setVisible(false);
                menu.findItem(R.id.menu_add_shopping).setVisible(true);
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
                menu.findItem(R.id.menu_add_photo).setVisible(false);
            } else {
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                menu.findItem(R.id.menu_add_photo).setVisible(true);

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
            case R.id.menu_add_photo:
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
                // handled by GameDetailFragment
                return false;
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
    public void onInternetCheckCompleted(boolean networkIsOnline) {
        if (!networkIsOnline) {
            PromptUser.displaySnackbar(mCoordinatorLayout, R.string.network_not_connected);
        }
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onToDoItemSelected(ToDoItem toDoItem) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_TODO, toDoItem);
        intent.putExtra(EXTRA_GAME_NAME, mGame.getName());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onShoppingItemSelected(Item shoppingItem) {
        // show edit shopping dialog
        FragmentManager fm = getSupportFragmentManager();
        ShoppingEditFragment fragment = ShoppingEditFragment.newInstance(shoppingItem);
        fragment.show(fm, "fragment_edit_shopping");
    }

    private void showAddRepairDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RepairAddFragment fragment = RepairAddFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_repair_add");
    }

    private void showAddToDoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ToDoAddFragment fragment = ToDoAddFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_add_todo");
    }

    private void showAddShoppingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShoppingAddFragment fragment = ShoppingAddFragment.newInstance(mGame.getId());
        fragment.show(fm, "fragment_item_add");
    }



    @Override
    public void onEditButtonPressed(Game game) {
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
        Fragment gameDetailFragment = GameDetailFragment.newInstance(mGame);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, gameDetailFragment);
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onImageClicked(String imagePath) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, imagePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void showSnackbar(int stringResourceId) {
        PromptUser.displaySnackbar(mCoordinatorLayout, stringResourceId);
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
