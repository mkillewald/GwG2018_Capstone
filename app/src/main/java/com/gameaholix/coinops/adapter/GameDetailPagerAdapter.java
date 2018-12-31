package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.gameaholix.coinops.R;
import com.gameaholix.coinops.game.PlaceholderFragment;
import com.gameaholix.coinops.repair.RepairListFragment;
import com.gameaholix.coinops.shopping.ShoppingListFragment;
import com.gameaholix.coinops.toDo.ToDoListFragment;

public class GameDetailPagerAdapter extends FragmentPagerAdapter {
//    private static final String TAG = GameDetailPagerAdapter.class.getSimpleName();
    private final String[] mTabTitles;
    private final Fragment[] mFragments;

    public GameDetailPagerAdapter(Context context, FragmentManager fragmentManager, String gameId) {
        super(fragmentManager);
        mTabTitles = context.getResources().getStringArray(R.array.game_details_tab_titles);
        mFragments = new Fragment[mTabTitles.length];
        mFragments[0] = PlaceholderFragment.newInstance();
        mFragments[1] = RepairListFragment.newInstance(gameId);
        mFragments[2] = ToDoListFragment.newInstance(gameId);
        mFragments[3] = ShoppingListFragment.newInstance(gameId);
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
