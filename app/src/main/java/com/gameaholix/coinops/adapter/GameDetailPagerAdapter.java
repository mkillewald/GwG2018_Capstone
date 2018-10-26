package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gameaholix.coinops.R;

import java.util.List;

// TODO: finish this

public class GameDetailPagerAdapter extends FragmentPagerAdapter {
    private String[] mTabTitles;
    private List<Fragment> mFragments;

    public GameDetailPagerAdapter(Context context, FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.mFragments = fragments;
        this.mTabTitles = context.getResources().getStringArray(R.array.game_details_tab_titles);
    }

    @Override
    public int getCount() {
        return mTabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return mFragments.get(position);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTabTitles[position];
    }
}
