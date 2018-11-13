package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.game.PlaceholderFragment;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.ToDoListFragment;

public class GameDetailPagerAdapter extends FragmentPagerAdapter {
    private final Game mGame;
    private final String[] mTabTitles;
    private final Fragment[] mFragments;

    public GameDetailPagerAdapter(Context context, FragmentManager fm, Game game) {
        super(fm);
        mGame = game;
        mTabTitles = context.getResources().getStringArray(R.array.game_details_tab_titles);
        mFragments = new Fragment[mTabTitles.length];
        mFragments[0] = PlaceholderFragment.newInstance(mGame);
        mFragments[1] = RepairListFragment.newInstance(mGame.getId());
        mFragments[2] = ToDoListFragment.newInstance(mGame.getId());
        mFragments[3] = ShoppingListFragment.newInstance(mGame.getId());
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments[position];
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }

    // Solution used from:
    // https://stackoverflow.com/questions/19393076/how-to-properly-handle-screen-rotation-with-a-viewpager-and-nested-fragments

    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        mFragments[position] = (Fragment) super.instantiateItem(container, position);
        return mFragments[position];
    }
}
