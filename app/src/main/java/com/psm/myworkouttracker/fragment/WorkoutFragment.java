package com.psm.myworkouttracker.fragment;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.adapter.WorkoutSectionsPagerAdapter;

public class WorkoutFragment extends Fragment {

    private WorkoutSectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View v = inflater.inflate(R.layout.fragment_workout, container, false);

        mSectionsPagerAdapter = new WorkoutSectionsPagerAdapter(getChildFragmentManager());

        mViewPager = v.findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = v.findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Fragment fragment = ((WorkoutSectionsPagerAdapter)mViewPager.getAdapter()).getFragment(i);
                if(i == 0 && fragment != null) {
                    fragment.onResume();
                } else if(i == 1 && fragment != null) {
                    fragment.onResume();
                } else {
                    fragment.onResume();
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        return v;
    }
}
