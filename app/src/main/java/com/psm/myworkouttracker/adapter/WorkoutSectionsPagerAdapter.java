package com.psm.myworkouttracker.adapter;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.psm.myworkouttracker.fragment.GraphTabFragment;
import com.psm.myworkouttracker.fragment.HistoryTabFragment;
import com.psm.myworkouttracker.fragment.WorkoutTabFragment;

import java.util.HashMap;
import java.util.Map;

public class WorkoutSectionsPagerAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;

    public WorkoutSectionsPagerAdapter(FragmentManager fm) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                WorkoutTabFragment tab1 = new WorkoutTabFragment();
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
                return "Workout";
            case 1:
                return "History";
            case 2:
                return "Graph";
        }
        return null;
    }

    public Object instantiateItem(ViewGroup container, int position) {
        Object obj = super.instantiateItem(container,position);
        if(obj instanceof Fragment) {
            Fragment f = (Fragment) obj;
            String tag = f.getTag();
            mFragmentTags.put(position, tag);
        }
        return obj;
    }

    public Fragment getFragment(int position) {
        String tag = mFragmentTags.get(position);
        if(tag == null) {
            return null;
        }
        return mFragmentManager.findFragmentByTag(tag);
    }
}
