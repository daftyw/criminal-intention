package com.augmentis.ayp.crimin;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.augmentis.ayp.crimin.model.Crime;

public class CrimeListActivity extends SingleFragmentActivity
        implements CrimeListFragment.Callbacks, CrimeFragment.Callbacks {

    private static final String TAG = "CrimeListAct";

    @Override
    protected Fragment onCreateFragment() {

        return new CrimeListFragment();
    }

    @Override
    public void onCrimeSelected(Crime crime) {
        if(findViewById(R.id.detail_fragment_container) == null) {
            // single pane
            Intent intent = CrimePagerActivity.newIntent(this, crime.getId());
            startActivity(intent);
        } else {

            Log.d(TAG, "On crime selected: " + crime);

            CrimeFragment currentDetailFragment = (CrimeFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.detail_fragment_container);

            if(currentDetailFragment == null || !currentDetailFragment.getCrimeId().equals(crime.getId())) {

                Log.d(TAG, "Difference in ID --- Replace new crimeFragment: " + crime);
                // two pane
                Fragment newDetailFragment = CrimeFragment.newInstance(crime.getId());

                // replace old fragment with new one
                getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.detail_fragment_container, newDetailFragment)
                        .commit();
            } else {
                currentDetailFragment.updateUI();
            }
        }
    }

    @Override
    public void onCrimeUpdated(Crime crime) {
        // Update List
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        listFragment.updateUI();
    }

    @Override
    public void onCrimeDeleted() {
        CrimeListFragment listFragment = (CrimeListFragment) getSupportFragmentManager()
                .findFragmentById(R.id.fragment_container);

        CrimeFragment detailFragment = (CrimeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.detail_fragment_container);

        listFragment.updateUI();

        /// clear
        getSupportFragmentManager()
                .beginTransaction()
                .detach(detailFragment)
                .commit();
    }
}
