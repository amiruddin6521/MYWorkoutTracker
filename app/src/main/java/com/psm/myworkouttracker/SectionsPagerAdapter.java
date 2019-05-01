package com.psm.myworkouttracker;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class SectionsPagerAdapter extends FragmentPagerAdapter {

    public SectionsPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                ExercisesTabFragment tab1 = new ExercisesTabFragment();
                return tab1;
            case 1:
                HistoryTabFragment tab2 = new HistoryTabFragment();
                return tab2;
            case 2:
                GraphTabFragment tab3 = new GraphTabFragment();
                return tab3;
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 3 total pages.
        return 3;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Exercises";
            case 1:
                return "History";
            case 2:
                return "Graph";
        }
        return null;
    }
}
