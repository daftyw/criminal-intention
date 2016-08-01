package com.augmentis.ayp.crimin;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;

import java.util.UUID;

public class CrimeListPagerActivity extends AppCompatActivity {

    private ViewPager mViewPager;

    private CrimeListFragment crimeListFragment;

    private CrimeFragment crimeFragment;

    private UUID currentCrimeId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crime_list_pager);

        mViewPager = (ViewPager) findViewById(R.id.crime_list_pager);

        mViewPager.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return true;
            }
        });

        mViewPager.setAdapter(new FragmentStatePagerAdapter(
                getSupportFragmentManager()) {

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return getUpdatedCrimeListFragment();
                    case 1:
                        return getUpdatedCrimeFragment();
                }

                throw new ArrayIndexOutOfBoundsException();
            }
        });
    }

    private Fragment getUpdatedCrimeFragment() {
        if(crimeFragment == null) {
            crimeFragment = CrimeFragment.newInstance(currentCrimeId);
        }

        return crimeFragment;
    }

    private Fragment getUpdatedCrimeListFragment() {
        if(this.crimeListFragment == null) {
            crimeListFragment = new CrimeListFragment();
        }
        return crimeListFragment;
    }

    protected void gotoCrime(UUID crimeId) {
        currentCrimeId = crimeId;

        if(crimeFragment != null) {
            crimeFragment.setCurrentCrimeId(currentCrimeId);
        }

        mViewPager.setCurrentItem(1);
    }

    protected void gotoCrime() {
        gotoCrime(null);
    }

    public void gotoList() {
        crimeListFragment.updateUI();
        mViewPager.setCurrentItem(0);
    }
}
