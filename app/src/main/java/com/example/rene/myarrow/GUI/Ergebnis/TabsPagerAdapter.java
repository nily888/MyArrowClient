package com.example.rene.myarrow.GUI.Ergebnis;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import java.util.ArrayList;

public class TabsPagerAdapter extends FragmentPagerAdapter {

    /** Kuerzel fuers Logging. */
    private static final String TAG = TabsPagerAdapter.class.getSimpleName();

    private ArrayList<Fragment> mFragments;

    public TabsPagerAdapter(FragmentManager fm, ArrayList<Fragment> fragments) {
        super(fm);
        mFragments = fragments;
    }

    @Override
    public Fragment getItem(int index) {
        return mFragments.get(index);
    }

    @Override
    public int getCount() {
        return mFragments.size();
    }

}