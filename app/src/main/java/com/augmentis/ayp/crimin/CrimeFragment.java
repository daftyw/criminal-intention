package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;

import com.augmentis.ayp.crimin.model.Crime;
import com.augmentis.ayp.crimin.model.CrimeDateFormat;
import com.augmentis.ayp.crimin.model.CrimeLab;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * Created by Rawin on 18-Jul-16.
 */
public class CrimeFragment extends Fragment {

    private static final String CRIME_ID = "CrimeFragment.CRIME_ID";

    private static final String DIALOG_DATE = "CrimeFragment.DIALOG_DATE";
    private static final String DIALOG_TIME = "CrimeFragment.DIALOG_DATE";

    private static final int REQUEST_DATE = 2222;
    private static final int REQUEST_TIME = 2221;
    private static final String TAG = "CrimeFragment";

    private Crime crime;

    private EditText editText;
    private Button crimeDateButton;
    private Button crimeTimeButton;
    private CheckBox crimeSolvedCheckbox;

    public CrimeFragment() {}

    public static CrimeFragment newInstance(UUID crimeId) {
        Bundle args = new Bundle();
        args.putSerializable(CRIME_ID, crimeId);

        CrimeFragment crimeFragment = new CrimeFragment();
        crimeFragment.setArguments(args);
        return crimeFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);

        CrimeLab crimeLab = CrimeLab.getInstance(getActivity());

        UUID crimeId =(UUID) getArguments().getSerializable(CRIME_ID);
        crime = crimeLab.getCrimeById(crimeId);

        Log.d(TAG, "crime.getTitle()=" + crime.getTitle());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_crime, container, false);

        editText = (EditText) v.findViewById(R.id.crime_title);
        editText.setText(crime.getTitle());
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                crime.setTitle(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        crimeDateButton = (Button) v.findViewById(R.id.crime_date);
        crimeDateButton.setText(CrimeDateFormat.toShortDate(crime.getCrimeDate()));
        crimeDateButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                ///
                FragmentManager fm = getFragmentManager();
                DatePickerFragment dialogFragment =
                        DatePickerFragment.newInstance(crime.getCrimeDate());
                dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_DATE);
                dialogFragment.show(fm, DIALOG_DATE);
            }
        });

        crimeTimeButton = (Button) v.findViewById(R.id.crime_time);
        crimeTimeButton.setText(CrimeDateFormat.toTime(getActivity(), crime.getCrimeDate()));
        crimeTimeButton.setOnClickListener(new View.OnClickListener() {

            @Override
           public void onClick(View v) {
               ///
               FragmentManager fm = getFragmentManager();
               TimePickerFragment dialogFragment =
                       TimePickerFragment.newInstance(crime.getCrimeDate());
               dialogFragment.setTargetFragment(CrimeFragment.this, REQUEST_TIME);
               dialogFragment.show(fm, DIALOG_TIME);
           }
        });

        crimeSolvedCheckbox = (CheckBox) v.findViewById(R.id.crime_solved);
        crimeSolvedCheckbox.setChecked(crime.isSolved());
        crimeSolvedCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                crime.setSolved(isChecked);
                Log.d(TAG, "Crime:" + crime.toString());
            }
        });

        return v;
    }

    @Override
    public void onActivityResult(int requestCode, int result, Intent data) {
        if(result != Activity.RESULT_OK) {
            return;
        }

        if(requestCode == REQUEST_DATE) {
            Date date = (Date) data.getSerializableExtra(DatePickerFragment.EXTRA_DATE);

            // set
            crime.setCrimeDate(date);
            crimeDateButton.setText(CrimeDateFormat.toShortDate(crime.getCrimeDate()));
        }

        if(requestCode == REQUEST_TIME) {
            Date date = (Date) data.getSerializableExtra(TimePickerFragment.EXTRA_TIME);

            // set
            crime.setCrimeDate(date);
            crimeTimeButton.setText(CrimeDateFormat.toTime(getActivity(), crime.getCrimeDate()));
        }
    }

    @Override
    public void onPause() {
        super.onPause();

        CrimeLab.getInstance(getActivity()).updateCrime(crime); // update crime in db
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.crime_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_item_delete_crime:
                CrimeLab.getInstance(getActivity()).deleteCrime(crime.getId());
                getActivity().finish();
                return true;
            default:

        }

        return super.onOptionsItemSelected(item);
    }
}
