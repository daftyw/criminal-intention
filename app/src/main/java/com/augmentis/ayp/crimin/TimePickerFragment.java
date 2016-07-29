package com.augmentis.ayp.crimin;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Rawin on 28-Jul-16.
 */
public class TimePickerFragment extends DialogFragment
        implements TimePickerDialog.OnTimeSetListener {

    protected static final String EXTRA_TIME = "EXTRA_TIME";
    protected static final String ARGUMENT_TIME = "ARG_TIME";

    private Calendar _calendar;

    // 1.
    public static TimePickerFragment newInstance(Date date) {
        TimePickerFragment tp = new TimePickerFragment();
        Bundle args = new Bundle();
        args.putSerializable(ARGUMENT_TIME, date);
        tp.setArguments(args);
        return tp;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the current time as the default values for the picker
        // 3.
        Date date = (Date) getArguments().getSerializable(ARGUMENT_TIME);

        _calendar = Calendar.getInstance();
        _calendar.setTime(date);

        int hour = _calendar.get(Calendar.HOUR_OF_DAY);
        int minute = _calendar.get(Calendar.MINUTE);

        // Create a new instance of TimePickerDialog and return it
        return new TimePickerDialog(getActivity(), this, hour, minute,
                DateFormat.is24HourFormat(getActivity()));
    }

    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        // Do something with the time chosen by the user

        _calendar.set(Calendar.HOUR, hourOfDay);
        _calendar.set(Calendar.MINUTE, minute);

        Date date = _calendar.getTime();
        sendResult(Activity.RESULT_OK, date);
    }

    private void sendResult(int resultCode, Date date) {
        if(getTargetFragment() == null) {
            return;
        }

        Intent intent = new Intent();
        intent.putExtra(EXTRA_TIME, date);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, intent);
    }

}
