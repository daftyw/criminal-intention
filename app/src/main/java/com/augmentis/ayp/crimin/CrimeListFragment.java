package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeListFragment extends Fragment {

    private static final int REQUEST_UPDATED_CRIME = 200;

    private RecyclerView _crimeRecyclerView;

    private CrimeAdapter _adapter;

    protected static final String TAG = "CRIME_LIST";
    private int crimePos;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime_list, container, false);

        _crimeRecyclerView = (RecyclerView) v.findViewById(R.id.crime_recycler_view);
        _crimeRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        updateUI();

        return v;
    }

    /**
     * Update UI
     */
    private void updateUI() {
        CrimeLab crimeLab = CrimeLab.getInstance();
        List<Crime> crimes = crimeLab.getCrimes();

        if(_adapter == null) {
            _adapter = new CrimeAdapter(crimes);
            _crimeRecyclerView.setAdapter(_adapter);
        } else {
            //_adapter.notifyDataSetChanged();
            _adapter.notifyItemChanged(crimePos);
            Log.d(TAG, "notify change at " + crimePos);
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "Resume list");
        updateUI();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_UPDATED_CRIME) {
            if(resultCode == Activity.RESULT_OK) {
                crimePos = (int) data.getExtras().get("position");
                Log.d(TAG, "get crimePos = " + crimePos);
            }
            // Blah blah
            Log.d(TAG, "Return from CrimeFragment");
        }
    }

    /**
     *
     */
    private class CrimeHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView _titleTextView;
        public TextView _dateTextView;
        public CheckBox _solvedCheckBox;

        Crime _crime;
        int _position;

        public CrimeHolder(View itemView) {
            super(itemView);

            _titleTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_title_text_view);
            _solvedCheckBox = (CheckBox)
                    itemView.findViewById(R.id.list_item_crime_solved_check_box);
            _dateTextView = (TextView)
                    itemView.findViewById(R.id.list_item_crime_date_text_view);

            itemView.setOnClickListener(this);
        }

        public void bind(Crime crime, int position) {
            _crime = crime;
            _position = position;
            _titleTextView.setText(_crime.getTitle());
            _dateTextView.setText(_crime.getCrimeDate().toString());
            _solvedCheckBox.setChecked(_crime.isSolved());
        }

        @Override
        public void onClick(View v) {
            Log.d(TAG, "send position : " + _position);
            Intent intent = CrimeActivity.newIntent(getActivity(), _crime.getId(), _position);
            startActivityForResult(intent, REQUEST_UPDATED_CRIME);
        }
    }

    private class CrimeAdapter extends RecyclerView.Adapter<CrimeHolder> {
        private List<Crime> _crimes;
        private int _viewCreatingCount;
        public CrimeAdapter(List<Crime> crimes) {
            this._crimes = crimes;
        }

        @Override
        public CrimeHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            _viewCreatingCount++;
            Log.d(TAG, "Create view holder for CrimeList: creating view time= " + _viewCreatingCount);

            LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
            View v = layoutInflater.inflate(R.layout.list_item_crime, parent, false);
            return new CrimeHolder(v);
        }

        @Override
        public void onBindViewHolder(CrimeHolder holder, int position) {
            Log.d(TAG, "Bind view holder for CrimeList : position = " + position);

            Crime crime = _crimes.get(position);
            holder.bind(crime, position);
        }

        @Override
        public int getItemCount() {
            return _crimes.size();
        }
    }
}
