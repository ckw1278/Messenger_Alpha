package com.test.busanit.messenger_alpha.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.astuetz.PagerSlidingTabStrip;
import com.test.busanit.messenger_alpha.R;

import java.util.List;

public class MainPagerAdapter extends FragmentPagerAdapter implements PagerSlidingTabStrip.IconTabProvider {

    private List<Fragment> fragments;

    public static int[] tabIcons = {R.drawable.tab_chat, R.drawable.friend, R.drawable.config};

    public MainPagerAdapter(FragmentManager fm, List<Fragment> fragments) {
        super(fm);
        this.fragments = fragments;
    }

    @Override
    public Fragment getItem(int position) {
        return fragments.get(position);
    }

    @Override
    public int getCount() {
        return fragments.size();
    }


    @Override
    public int getPageIconResId(int i) {
        return tabIcons[i];
    }
}