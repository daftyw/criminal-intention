package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeLab;

import java.util.List;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeListFragment extends Fragment {

    private static final String TAG = "CrimeListFragment";
    private static final java.lang.String SUBTITLE_VISIBLE_STATE = "SUBTITLE_VISIBLE";

    private RecyclerView _crimeRecyclerView;

    private CrimeListAdapter _adapter;
    private boolean _subtitleVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        if(savedInstanceState != null) {
            _subtitleVisible = savedInstanceState.getBoolean(SUBTITLE_VISIBLE_STATE);
        } else {
            _subtitleVisible = false;
        }

        Log.d(TAG, "Subtitle show = " + String.valueOf(_subtitleVisible));

        updateUI();

        return v;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        inflater.inflate(R.menu.crime_list_menu, menu);

        MenuItem menuItem = menu.findItem(R.id.menu_item_show_subtitle);

        Log.d(TAG, "Creation of Menu");
        if(_subtitleVisible) {
            menuItem.setIcon(R.drawable.ic_hide_subtitle);
            menuItem.setTitle(R.string.hide_subtitle);
        } else {
            menuItem.setIcon(R.drawable.ic_show_subtitle);
            menuItem.setTitle(R.string.show_subtitle);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_new_crime:

                Crime crime = new Crime();
                CrimeLab.getInstance(getActivity()).addCrime(crime);
                Intent intent = CrimePagerActivity.newIntent(getActivity(), crime.getId());
                startActivity(intent);
                return true;

            case R.id.menu_item_show_subtitle:
                _subtitleVisible = !_subtitleVisible;
                getActivity().invalidateOptionsMenu();

                updateSubtitle();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void updateSubtitle() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        int crimeCount = crimeLab.getCrimes().size();
        String subtitle = getString(R.string.subtitle_format, crimeCount);

        if(!_subtitleVisible) {
            subtitle = null;
        }
        AppCompatActivity appCompatActivity = (AppCompatActivity) getActivity();
        ActionBar actionBar = appCompatActivity.getSupportActionBar();
        if(actionBar != null) {
            actionBar.setSubtitle(subtitle);
        }
    }

    @Override
    public void onSaveInstanceState(Bundle whatever) {
        super.onSaveInstanceState(whatever);

        whatever.putBoolean(SUBTITLE_VISIBLE_STATE, _subtitleVisible);
    }

    /**
     * Update UI
     */
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
        List<Crime> crimes = crimeLab.getCrimes();

        if(_adapter == null) {
            _adapter = new CrimeListAdapter(this, crimes);
            _crimeRecyclerView.setAdapter(_adapter);
        } else {
            _adapter.setCrimes(crimeLab.getCrimes());
            _adapter.notifyDataSetChanged();
        }

        updateSubtitle();
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume list");
        updateUI();
    }

}
