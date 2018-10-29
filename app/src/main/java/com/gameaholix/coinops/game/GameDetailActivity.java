package com.gameaholix.coinops.game;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
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
import com.gameaholix.coinops.repair.RepairDetailActivity;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.ToDoListFragment;

import java.util.List;
import java.util.Vector;

public class GameDetailActivity extends AppCompatActivity implements
        GameDetailFragment.OnFragmentInteractionListener,
        RepairListFragment.OnFragmentInteractionListener,
        ToDoListFragment.OnFragmentInteractionListener,
        ShoppingListFragment.OnFragmentInteractionListener {

//    private static final String TAG = GameDetailActivity.class.getSimpleName();
    private static final String EXTRA_GAME = "com.gameaholix.coinops.model.Game";
    private static final String EXTRA_GAME_NAME = "CoinOpsGameName";
    private static final String EXTRA_REPAIR = "com.gameaholix.coinops.model.Item";

    private Game mGame;
    private ViewPager mViewPager;

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

        List<Fragment> fragments = new Vector<>();

        fragments.add(GameDetailFragment.newInstance(mGame));
        fragments.add(RepairListFragment.newInstance(mGame.getId()));
        fragments.add(ToDoListFragment.newInstance(mGame.getId()));
        fragments.add(ShoppingListFragment.newInstance(mGame.getId()));

        // Get the ViewPager and set it's PagerAdapter so that it can display items
        mViewPager = findViewById(R.id.viewpager);
        mViewPager.setAdapter(new GameDetailPagerAdapter(this, getSupportFragmentManager(),
                fragments));
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
//                menu.findItem(R.id.menu_add_repair).setVisible(false);
                break;
            case 1:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
//                menu.findItem(R.id.menu_add_repair).setVisible(true);
                break;
            default:
                menu.findItem(R.id.menu_edit_game).setVisible(false);
//                menu.findItem(R.id.menu_add_repair).setVisible(false);
                break;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_edit_game:
                return false;
//            case R.id.menu_add_repair:
//                return false;
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
//        Intent intent = new Intent(this, ToDoDetailActivity.class);
//        intent.putExtra(EXTRA_TODO, toDoItem);
//        startActivity(intent);
    }

    @Override
    public void onShoppingItemSelected(Item shoppingItem) {
//        Intent intent = new Intent(this, ShoppingDetailActivity.class);
//        intent.putExtra(EXTRA_SHOPPING, shoppingItem);
//        startActivity(intent);
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
