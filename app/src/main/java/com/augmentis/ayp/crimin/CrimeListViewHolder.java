package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeDateFormat;
import com.augmentis.ayp.crimin.model.CrimeLab;
import com.augmentis.ayp.crimin.model.PictureUtils;

import java.io.File;
import java.util.UUID;

/**
 * Created by Rawin on 28-Jul-16.
 */
public class CrimeListViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private static final String TAG = "CrimeListViewHolder";
    public TextView _titleTextView;
    public TextView _dateTextView;
    public CheckBox _solvedCheckBox;
    public ImageView _crimeImage;

    UUID _crimeId;
    int _position;

    Fragment _f;

    public CrimeListViewHolder(Fragment f, View itemView) {
        super(itemView);

        _f = f;

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
        _dateTextView.setText(CrimeDateFormat.toFullDate(_f.getActivity(), crime.getCrimeDate()));
        _solvedCheckBox.setChecked(crime.isSolved());

        File crimeImageFile = CrimeLab.getInstance(_f.getActivity()).getPhotoFile(crime);
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
        Intent intent = CrimePagerActivity.newIntent(_f.getActivity(), _crimeId);
        _f.startActivity(intent);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        CrimeLab crimeLab = CrimeLab.getInstance(_f.getActivity());
        Crime crime = crimeLab.getCrimeById(_crimeId);

        crime.setSolved(isChecked);
        crimeLab.updateCrime(crime);
    }
}
