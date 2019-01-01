package com.gameaholix.coinops.game;

import android.app.ActivityOptions;
import android.arch.lifecycle.ViewModelProviders;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.gameaholix.coinops.BaseActivity;
import com.gameaholix.coinops.R;
import com.gameaholix.coinops.DisplayImageActivity;
import com.gameaholix.coinops.databinding.ActivityGameDetailBinding;
import com.gameaholix.coinops.game.viewModel.GameViewModel;
import com.gameaholix.coinops.game.viewModel.GameViewModelFactory;
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.toDo.ToDoDetailActivity;
import com.gameaholix.coinops.adapter.GameDetailPagerAdapter;
import com.gameaholix.coinops.model.Item;
import com.gameaholix.coinops.model.ListRow;
import com.gameaholix.coinops.repair.RepairAddEditFragment;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingAddEditFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.toDo.ToDoAddEditFragment;
import com.gameaholix.coinops.toDo.ToDoListFragment;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModel;
import com.gameaholix.coinops.toDo.viewModel.ToDoItemViewModelFactory;
import com.gameaholix.coinops.utility.PromptUser;
import com.google.android.gms.ads.AdRequest;

public class GameDetailActivity extends BaseActivity implements
        GameDetailFragment.OnFragmentInteractionListener,
        GameAddEditFragment.OnFragmentInteractionListener,
        RepairListFragment.OnFragmentInteractionListener,
        RepairAddEditFragment.OnFragmentInteractionListener,
        ToDoListFragment.OnFragmentInteractionListener,
        ToDoAddEditFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener,
        ShoppingAddEditFragment.OnFragmentInteractionListener {
    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME_ID = "CoinOpsGameId";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_IMAGE_PATH = "CoinOpsImagePath";
    private static final String EXTRA_REPAIR = "CoinOpsRepairLog";
    private static final String EXTRA_TODO_ID = "CoinOpsToDoId";

    private String mGameId;
    private String mGameName;
    private ActivityGameDetailBinding mBind;

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

        mBind = DataBindingUtil.setContentView(this, R.layout.activity_game_detail);

        setSupportActionBar(mBind.toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // set CoordinatorLayout of BaseActivity for displaying Snackbar
        setCoordinatorLayout(mBind.coordinatorLayout);

        AdRequest adRequest = new AdRequest.Builder().build();
        mBind.avBanner.loadAd(adRequest);

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

        // Instantiate ViewModels with our custom factory methods using this Activity as the
        // lifecycle owner, so the same ViewModel instances can be retrieved by the necessary
        // Fragments.
        ViewModelProviders
                .of(this, new GameViewModelFactory(mGameId))
                .get(GameViewModel.class);

        ViewModelProviders
                .of(this, new ToDoItemViewModelFactory(mGameId, null))
                .get(ToDoItemViewModel.class);

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mBind.viewpager.setAdapter(new GameDetailPagerAdapter(this, getSupportFragmentManager(),
                mGameId));
        mBind.viewpager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
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
        mBind.slidingTabs.setupWithViewPager(mBind.viewpager);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.game_detail_menu, menu);
        switch (mBind.viewpager.getCurrentItem()) {
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
        if (mBind.viewpager.getCurrentItem() == 0) {
            Fragment currentFragment = getSupportFragmentManager().findFragmentById(R.id.fragment_placeholder);
            if (currentFragment instanceof GameDetailFragment) {
                menu.findItem(R.id.menu_edit_game).setVisible(true);
                // TODO: allow delete game from any tab
                menu.findItem(R.id.menu_delete_game).setVisible(true);
                mBind.avBanner.setVisibility(View.VISIBLE);
            } else {
                menu.findItem(R.id.menu_edit_game).setVisible(false);
                menu.findItem(R.id.menu_delete_game).setVisible(false);
                mBind.avBanner.setVisibility(View.GONE);
            }
        } else {
            if (mBind.avBanner.getVisibility() == View.GONE) {
                mBind.avBanner.setVisibility(View.VISIBLE);
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

    // Game callbacks

    @Override
    public void onGameIdInvalid() {
        Log.e(TAG, "Failed to instantiate fragment! Game ID cannot be an empty string.");
        PromptUser.displayAlert(this,
                R.string.error_unable_to_load,
                R.string.error_game_id_empty,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();
                    }
                });
    }

    @Override
    public void onGameNameChanged(String name) {
        setTitle(name);
        mGameName = name;
    }

    @Override
    public void onGameEditButtonPressed() {
        mBind.avBanner.setVisibility(View.GONE);
        // replace GameDetailFragment with GameAddEditFragment
        Fragment addEditGameFragment = GameAddEditFragment.newEditInstance(mGameId);
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, addEditGameFragment);
        ft.commit();

        invalidateOptionsMenu();
    }

    @Override
    public void onGameAddEditCompletedOrCancelled() {
        // replace GameAddEditFragment with GameDetailFragment
        Fragment gameDetailFragment = GameDetailFragment.newInstance();
        final FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.fragment_placeholder, gameDetailFragment);
        ft.commit();

        invalidateOptionsMenu();
        mBind.avBanner.setVisibility(View.VISIBLE);
    }

    @Override
    public void onGameDeleteCompleted() {
        finish();
    }

    @Override
    public void onGameImageClicked(String imagePath) {
        Intent intent = new Intent(this, DisplayImageActivity.class);
        intent.putExtra(EXTRA_IMAGE_PATH, imagePath);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    // Repair callbacks

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
    public void onRepairAddEditCompletedOrCancelled() {
        // no operation
    }

    @Override
    public void onRepairFabPressed() {
        showAddRepairDialog();
    }

    private void showAddRepairDialog() {
        FragmentManager fm = getSupportFragmentManager();
        RepairAddEditFragment fragment = RepairAddEditFragment.newInstance(mGameId);
        fragment.show(fm, "fragment_repair_add");
    }

    // To Do callbacks

    @Override
    public void onToDoItemSelected(ListRow item) {
        Intent intent = new Intent(this, ToDoDetailActivity.class);
        intent.putExtra(EXTRA_GAME_NAME, mGameName);
        intent.putExtra(EXTRA_TODO_ID, item.getId());
        intent.putExtra(EXTRA_GAME_ID, mGameId);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Bundle bundle = ActivityOptions.makeSceneTransitionAnimation(this).toBundle();
            startActivity(intent, bundle);
        } else {
            startActivity(intent);
        }
    }

    @Override
    public void onToDoAddEditCompletedOrCancelled() {
        // TODO: finish this
    }

    @Override
    public void onToDoFabPressed() {
        showAddToDoDialog();
    }

    private void showAddToDoDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ToDoAddEditFragment fragment = ToDoAddEditFragment.newInstance(false);
        fragment.show(fm, "fragment_add_todo");
    }

    // Shopping callbacks

    @Override
    public void onShoppingItemSelected(ListRow item) {
        // show update shopping dialog
        FragmentManager fm = getSupportFragmentManager();
        ShoppingAddEditFragment fragment =
                ShoppingAddEditFragment.newInstance(mGameId, item.getId(), true);
        fragment.show(fm, "fragment_add_edit_shopping");
    }

    @Override
    public void onShoppingAddEditCompletedOrCancelled() {
        // TODO: finish this
    }

    @Override
    public void onShoppingFabPressed() {
        showAddShoppingDialog();
    }

    private void showAddShoppingDialog() {
        FragmentManager fm = getSupportFragmentManager();
        ShoppingAddEditFragment fragment =
                ShoppingAddEditFragment.newInstance(mGameId,null, false);
        fragment.show(fm, "fragment_add_edit_shopping");
    }

    @Override
    public void onShowSnackbar(int stringResourceId) {
        PromptUser.displaySnackbar(getCoordinatorLayout(), stringResourceId);
    }
}
