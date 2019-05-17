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
import android.widget.Toast;

import com.psm.myworkouttracker.R;
import com.psm.myworkouttracker.adapter.ExercisesSectionsPagerAdapter;

public class AddEditExercisesFragment extends Fragment {

    private ExercisesSectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private String exerciseName;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_addeditexercises, container, false);

        Bundle bundle = getArguments();
        exerciseName = bundle.getString("exercise");

        mSectionsPagerAdapter = new ExercisesSectionsPagerAdapter(getChildFragmentManager(), exerciseName);

        mViewPager = v.findViewById(R.id.container2);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        final TabLayout tabLayout = v.findViewById(R.id.tabs2);
        tabLayout.setupWithViewPager(mViewPager);

        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i1) {

            }

            @Override
            public void onPageSelected(int i) {
                Fragment fragment = ((ExercisesSectionsPagerAdapter)mViewPager.getAdapter()).getFragment(i);
                if(i == 0 && fragment != null) {
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

    public String getExerciseName() {
        return exerciseName;
    }
}
