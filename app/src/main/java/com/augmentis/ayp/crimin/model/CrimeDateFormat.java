package com.augmentis.ayp.crimin.model;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Rawin on 29-Jul-16.
 */
public class CrimeDateFormat {
    public static String toShortDate(Date date) {
        return new SimpleDateFormat("dd MMMM yyyy").format(date);
    }

    public static String toTime(Date date) {
        return new SimpleDateFormat("HH:mm").format(date);
    }
}
