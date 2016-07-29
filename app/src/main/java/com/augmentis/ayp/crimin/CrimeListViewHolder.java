package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeDateFormat;

/**
 * Created by Rawin on 28-Jul-16.
 */
public class CrimeListViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener  {
    private static final String TAG = "CrimeListViewHolder";
    public TextView _titleTextView;
    public TextView _dateTextView;
    public CheckBox _solvedCheckBox;

    Crime _crime;
    int _position;

    Fragment _f;

    public CrimeListViewHolder(Fragment f, View itemView) {
        super(itemView);

        _f = f;

        _titleTextView = (TextView)
                itemView.findViewById(R.id.list_item_crime_title_text_view);
        _solvedCheckBox = (CheckBox)
                itemView.findViewById(R.id.list_item_crime_solved_check_box);
        _solvedCheckBox.setEnabled(false);
        _dateTextView = (TextView)
                itemView.findViewById(R.id.list_item_crime_date_text_view);

        itemView.setOnClickListener(this);
    }

    public void bind(Crime crime, int position) {
        _crime = crime;
        _position = position;
        _titleTextView.setText(_crime.getTitle());
        _dateTextView.setText(CrimeDateFormat.toFullDate(_f.getActivity(), _crime.getCrimeDate()));
        _solvedCheckBox.setChecked(_crime.isSolved());
    }

    @Override
    public void onClick(View v) {
        Log.d(TAG, "send position : " + _position);
        Intent intent = CrimePagerActivity.newIntent(_f.getActivity(), _crime.getId());
        _f.startActivity(intent);
    }
}
