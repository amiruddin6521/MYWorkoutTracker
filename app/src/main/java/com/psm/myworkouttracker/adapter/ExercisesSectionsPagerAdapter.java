package com.psm.myworkouttracker.adapter;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import com.psm.myworkouttracker.fragment.AddEditExercisesFragment;
import com.psm.myworkouttracker.fragment.ExercisesHistoryTabFragment;
import com.psm.myworkouttracker.fragment.ExercisesTabFragment;
import com.psm.myworkouttracker.fragment.GraphTabFragment;
import com.psm.myworkouttracker.fragment.HistoryTabFragment;
import com.psm.myworkouttracker.fragment.WorkoutTabFragment;

import java.util.HashMap;
import java.util.Map;

public class ExercisesSectionsPagerAdapter extends FragmentPagerAdapter {

    private Map<Integer, String> mFragmentTags;
    private FragmentManager mFragmentManager;
    private String exerciseName;

    public ExercisesSectionsPagerAdapter(FragmentManager fm, String exercise) {
        super(fm);
        mFragmentManager = fm;
        mFragmentTags = new HashMap<Integer, String>();
        this.exerciseName = exercise;
    }

    @Override
    public Fragment getItem(int position) {
        switch (position) {
            case 0:
                Bundle bundle = new Bundle();
                bundle.putString("exercise", exerciseName);
                ExercisesTabFragment fragment = new ExercisesTabFragment();
                fragment.setArguments(bundle);
                return fragment;

                /*ExercisesTabFragment tab1 = new ExercisesTabFragment();
                return tab1;*/
            case 1:
                Bundle bundle2 = new Bundle();
                bundle2.putString("exercise", exerciseName);
                ExercisesHistoryTabFragment fragment2 = new ExercisesHistoryTabFragment();
                fragment2.setArguments(bundle2);
                return fragment2;

                /*ExercisesHistoryTabFragment tab2 = new ExercisesHistoryTabFragment();
                return tab2;*/
        }
        return null;
    }

    @Override
    public int getCount() {
        // Show 2 total pages.
        return 2;
    }

    @Nullable
    @Override
    public CharSequence getPageTitle(int position) {
        switch (position) {
            case 0:
                return "Exercise";
            case 1:
                return "History";
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
