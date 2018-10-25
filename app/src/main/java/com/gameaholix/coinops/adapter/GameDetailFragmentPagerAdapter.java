package com.gameaholix.coinops.adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.gameaholix.coinops.game.PageFragment;

// SampleFragmentPagerAdapter used from:
// https://github.com/codepath/android_guides/wiki/Google-Play-Style-Tabs-using-TabLayout

public class GameDetailFragmentPagerAdapter extends FragmentPagerAdapter {
    private String tabTitles[] = new String[] { "Info", "To Do", "Shopping", "Repair Log" };
    private Context context;

    public GameDetailFragmentPagerAdapter(FragmentManager fm, Context context) {
        super(fm);
        this.context = context;
    }

    @Override
    public int getCount() {
        return tabTitles.length;
    }

    @Override
    public Fragment getItem(int position) {
        return PageFragment.newInstance(position + 1);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        // Generate title based on item position
        return tabTitles[position];
    }
}
