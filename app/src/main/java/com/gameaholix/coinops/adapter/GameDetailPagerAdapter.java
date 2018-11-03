package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.game.PlaceholderFragment;
import com.gameaholix.coinops.model.Game;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.todo.ToDoListFragment;

public class GameDetailPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabTitles;
    private Game mGame;

    public GameDetailPagerAdapter(Context context, FragmentManager fm, Game game) {
        super(fm);
        this.mGame = game;
        this.mTabTitles = context.getResources().getStringArray(R.array.game_details_tab_titles);
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            default:
            case 0:
                return PlaceholderFragment.newInstance(mGame);
            case 1:
                return RepairListFragment.newInstance(mGame.getId());
            case 2:
                return ToDoListFragment.newInstance(mGame.getId());
            case 3:
                return ShoppingListFragment.newInstance(mGame.getId());
        }
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
