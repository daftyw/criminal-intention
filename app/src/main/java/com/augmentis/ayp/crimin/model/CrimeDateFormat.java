package com.augmentis.ayp.crimin.model;

import android.content.Context;
import android.text.format.DateFormat;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rawin on 29-Jul-16.
 */
public class CrimeDateFormat {
    public static String toShortDate(Date date) {
        return new SimpleDateFormat("d MMMM yyyy").format(date);
    }

    public static String toTime(Context context, Date date) {
        String fmt;
        if(DateFormat.is24HourFormat(context)) {
            fmt = "HH:mm";
        } else {
            fmt = "hh:mm a";
        }
        return new SimpleDateFormat(fmt).format(date);
    }

    public static String toFullDate(Context context, Date date) {
        String fmt;
        if(DateFormat.is24HourFormat(context)) {
            fmt = "d MMMM yyyy HH:mm";
        } else {
            fmt = "d MMMM yyyy hh:mm a";
        }
        return new SimpleDateFormat(fmt).format(date);
    }
}
