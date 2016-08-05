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
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeDateFormat;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.List;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeListFragment extends Fragment {

    private static final String TAG = "CrimeListFragment";
    private static final java.lang.String SUBTITLE_VISIBLE_STATE = "SUBTITLE_VISIBLE";

    private RecyclerView _crimeRecyclerView;
    private View _zeroItemView;

    private CrimeListAdapter _adapter;
    private boolean _subtitleVisible;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        _zeroItemView = v.findViewById(R.id.zero_item_view);

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

        // plurals
        String subtitle = getResources().getQuantityString(R.plurals.subtitle_format, crimeCount, crimeCount);

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
            _adapter = new CrimeListAdapter(crimes);
            _crimeRecyclerView.setAdapter(_adapter);
        } else {
            _adapter.setCrimes(crimeLab.getCrimes());
            _adapter.notifyDataSetChanged();
        }

        updateZeroView(crimes.size() == 0);
        updateSubtitle();
    }

    private void updateZeroView(boolean visible) {
        if(visible) {
            _zeroItemView.setVisibility(View.VISIBLE);
            _crimeRecyclerView.setVisibility(View.INVISIBLE);
        } else {
            _crimeRecyclerView.setVisibility(View.VISIBLE);
            _zeroItemView.setVisibility(View.INVISIBLE);
        }
    }


    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume list");
        updateUI();
    }


    public class CrimeListViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

        private static final String TAG = "CrimeListViewHolder";
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;
        public ImageView _crimeImage;

        UUID _crimeId;
        int _position;

        public CrimeListViewHolder(View itemView) {
            super(itemView);

            _titleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_crime_solved_check_box);

            _dateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_date_text_view);

            _crimeImage = (ImageView)
                    itemView.findViewById(R.id.list_item_crime_photo);

            _solvedCheckBox.setOnCheckedChangeListener(this);
            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime) {
            _crimeId = crime.getId();

            _titleTextView.setText(crime.getTitle());
            _dateTextView.setText(CrimeDateFormat.toFullDate(getActivity(), crime.getCrimeDate()));
            _solvedCheckBox.setChecked(crime.isSolved());

            File crimeImageFile = CrimeLab.getInstance(getActivity()).getPhotoFile(crime);
            if(crimeImageFile.exists()) {
                _crimeImage.setImageBitmap(PictureUtils.getScaledBitmap(crimeImageFile.getPath(), 200, 200));
                _crimeImage.setVisibility(View.VISIBLE);
            } else {
                _crimeImage.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "send position : " + _position);
            Intent intent = CrimePagerActivity.newIntent(getActivity(), _crimeId);
            startActivity(intent);
        }

        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            CrimeLab crimeLab = CrimeLab.getInstance(getActivity());
            Crime crime = crimeLab.getCrimeById(_crimeId);

            crime.setSolved(isChecked);
            crimeLab.updateCrime(crime);
        }
    }

    private class CrimeListAdapter
            extends RecyclerView.Adapter<CrimeListViewHolder> {
        private static final String TAG = "CrimeListAdapter";
        private List<Crime> _crimes;
        private int _viewCreatingCount;

        public CrimeListAdapter(List<Crime> crimes) {
            _crimes = crimes;
        }

        protected void setCrimes(List<Crime> crimes) {
            _crimes = crimes;
        }

        @Override
        public CrimeListViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            _viewCreatingCount++;
            Log.d(TAG, "Create view holder for CrimeList: creating view time= " + _viewCreatingCount);

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeListViewHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeListViewHolder holder, int position) {
            Log.d(TAG, "Bind view holder for CrimeList : position = " + position);

            Crime crime = _crimes.get(position);
            holder.bind(crime);
        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }
    }


}
