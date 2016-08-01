package com.augmentis.ayp.crimin.model;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.augmentis.ayp.crimin.model.CrimeDbSchema.CrimeTable;

/**
 * Created by Rawin on 01-Aug-16.
 */
public class CrimesBaseHelper extends SQLiteOpenHelper {

    private static final int VERSION = 1;

    private static final String DATABASE_NAME = "crimeBase.db";
    private static final String TAG = "CrimesBaseHelper";

    public CrimesBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Create Database");

        db.execSQL("create table " + CrimeTable.NAME
                + "("
                + "_id integer primary key autoincrement, "
                + CrimeTable.Cols.UUID + ","
                + CrimeTable.Cols.TITLE + ","
                + CrimeTable.Cols.DATE + ","
                + CrimeTable.Cols.SOLVED + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
